package com.qualcomm.ftcdriverstation;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.ScanResult;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.ThreadPool;
import com.qualcomm.robotcore.wifi.NetworkConnection;
import com.qualcomm.robotcore.wifi.NetworkConnectionFactory;
import com.qualcomm.robotcore.wifi.NetworkType;
import com.qualcomm.robotcore.wifi.SoftApAssistant;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.firstinspires.ftc.robotcore.internal.network.CallbackResult;
import org.firstinspires.ftc.robotcore.internal.network.PreferenceRemoterDS;
import org.firstinspires.ftc.robotcore.internal.ui.BaseActivity;

public class FtcPairNetworkConnectionActivity extends BaseActivity implements OnClickListener, NetworkConnection.NetworkConnectionCallback {
   public static final String TAG = "FtcPairNetworkConnection";
   private String connectionOwnerIdentity;
   private String connectionOwnerPassword;
   private ScheduledFuture discoveryFuture;
   private EditText editTextSoftApPassword;
   private boolean filterForTeam = true;
   private NetworkConnection networkConnection;
   private SharedPreferences sharedPref;
   private int teamNum;
   private TextView textViewSoftApPasswordLabel;

   private int getTeamNumber(String name)
   {
      int dashPos = name.indexOf("-");
      if (dashPos == -1)
      {
         return 0;
      }

      try
      {
         return Integer.parseInt(name.substring(0, dashPos));
      }
      catch (NumberFormatException e)
      {
         return 0;
      }
   }

   private void updateDevicesList()
   {
      RadioGroup rg = findViewById(R.id.radioGroupDevices);
      rg.clearCheck();
      rg.removeAllViews();
      PeerRadioButton b = new PeerRadioButton(this);
      String none = getString(R.string.connection_owner_default);
      b.setId(0);
      b.setText("None\nDo not pair with any device");
      b.setPadding(0, 0, 0, 24);
      b.setOnClickListener(this);
      b.setDeviceIdentity(none);
      if (connectionOwnerIdentity.equalsIgnoreCase(none))
      {
         b.setChecked(true);
      }
      rg.addView(b);
      int i = 1;
      Map<String, String> namesAndAddresses = new TreeMap<>();
      if (networkConnection.getNetworkType() == NetworkType.WIFIDIRECT)
      {
         namesAndAddresses = buildMap(((WifiDirectAssistant) networkConnection).getPeers());
      }
      else if (networkConnection.getNetworkType() == NetworkType.SOFTAP)
      {
         namesAndAddresses = buildResultsMap(((SoftApAssistant) networkConnection).getScanResults());
      }
      for (String deviceName : namesAndAddresses.keySet())
      {
         if (!filterForTeam || deviceName.contains(teamNum + "-") || deviceName.startsWith("0000-"))
         {
            String deviceIdentity = namesAndAddresses.get(deviceName);
            b = new PeerRadioButton(this);
            int i2 = i + 1;
            b.setId(i);
            String display = "";
            if (networkConnection.getNetworkType() == NetworkType.WIFIDIRECT)
            {
               display = deviceName + "\n" + deviceIdentity;
            }
            else if (networkConnection.getNetworkType() == NetworkType.SOFTAP)
            {
               display = deviceName;
            }
            b.setText(display);
            b.setPadding(0, 0, 0, 24);
            b.setDeviceIdentity(deviceIdentity);
            if (deviceIdentity.equalsIgnoreCase(connectionOwnerIdentity))
            {
               b.setChecked(true);
            }
            b.setOnClickListener(this);
            rg.addView(b);
            i = i2;
         }
      }
   }

   public Map<String, String> buildMap(List<WifiP2pDevice> peers)
   {
      Map<String, String> map = new TreeMap<>();
      for (WifiP2pDevice peer : peers)
      {
         map.put(PreferenceRemoterDS.getInstance().getDeviceNameForWifiP2pGroupOwner(peer.deviceName), peer.deviceAddress);
      }
      return map;
   }

   public Map<String, String> buildResultsMap(List<ScanResult> results)
   {
      Map<String, String> map = new TreeMap<>();
      for (ScanResult result : results)
      {
         map.put(result.SSID, result.SSID);
      }
      return map;
   }

   protected FrameLayout getBackBar() {
      return (FrameLayout)this.findViewById(R.id.backbar);
   }

   public String getTag() {
      return "FtcPairNetworkConnection";
   }

   public void onClick(View var1) {
      if (var1 instanceof FtcPairNetworkConnectionActivity.PeerRadioButton) {
         FtcPairNetworkConnectionActivity.PeerRadioButton var2 = (FtcPairNetworkConnectionActivity.PeerRadioButton)var1;
         if (var2.getId() == 0) {
            this.connectionOwnerIdentity = this.getString(R.string.connection_owner_default);
            this.connectionOwnerPassword = this.getString(R.string.connection_owner_password_default);
         } else {
            this.connectionOwnerIdentity = var2.getDeviceIdentity();
         }

         Editor var3 = this.sharedPref.edit();
         var3.putString(this.getString(R.string.pref_connection_owner_identity), this.connectionOwnerIdentity);
         var3.apply();
         StringBuilder var4 = new StringBuilder();
         var4.append("Setting Driver Station name to ");
         var4.append(this.connectionOwnerIdentity);
         RobotLog.ii("FtcPairNetworkConnection", var4.toString());
      }

   }

   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_ftc_network_connection);
      String networkType = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString(NetworkConnectionFactory.NETWORK_CONNECTION_TYPE, NetworkType.WIFIDIRECT.toString());
      editTextSoftApPassword = findViewById(R.id.editTextSoftApPassword);
      textViewSoftApPasswordLabel = findViewById(R.id.textViewSoftApPasswordLabel);
      networkConnection = NetworkConnectionFactory.getNetworkConnection(NetworkConnectionFactory.getTypeFromString(networkType), getBaseContext());
      String deviceName = networkConnection.getDeviceName();
      if (deviceName.isEmpty())
      {
         teamNum = 0;
         deviceName = getString(R.string.wifi_direct_name_unknown);
      }
      else
      {
         teamNum = getTeamNumber(deviceName);
      }
      TextView instructions = findViewById(R.id.textWifiInstructions);
      TextView wifiName = findViewById(R.id.textViewWifiName);
      TextView label = findViewById(R.id.textViewWifiNameLabel);
      if (networkType.equalsIgnoreCase(NetworkType.WIFIDIRECT.toString()))
      {
         instructions.setText(getString(R.string.pair_instructions));
         wifiName.setVisibility(View.VISIBLE);
         wifiName.setText(deviceName);
         label.setVisibility(View.VISIBLE);
      }
      else if (networkType.equalsIgnoreCase(NetworkType.SOFTAP.toString()))
      {
         instructions.setText(getString(R.string.softap_instructions));
         wifiName.setVisibility(View.INVISIBLE);
         label.setVisibility(View.INVISIBLE);
      }
      ((Switch) findViewById(R.id.wifi_filter)).setOnCheckedChangeListener(new OnCheckedChangeListener()
      {
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
         {
            filterForTeam = isChecked;
            updateDevicesList();
         }
      });
   }

   public CallbackResult onNetworkConnectionEvent(NetworkConnection.NetworkEvent event)
   {
      CallbackResult result = CallbackResult.NOT_HANDLED;
      switch (event)
      {
         case PEERS_AVAILABLE:
            updateDevicesList();
            return CallbackResult.HANDLED;
         default:
            return result;
      }
   }

   public void onStart()
   {
      super.onStart();
      RobotLog.ii(TAG, "Starting Pairing with Driver Station activity");
      sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
      connectionOwnerIdentity = sharedPref.getString(getString(R.string.pref_connection_owner_identity), getString(R.string.connection_owner_default));
      TextView softApPasswordInstructions = findViewById(R.id.textViewSoftApPasswordInstructions);
      if (networkConnection.getNetworkType() == NetworkType.SOFTAP)
      {
         connectionOwnerPassword = sharedPref.getString(getString(R.string.pref_connection_owner_password), getString(R.string.connection_owner_password_default));
         textViewSoftApPasswordLabel.setVisibility(View.VISIBLE);
         editTextSoftApPassword.setVisibility(View.VISIBLE);
         editTextSoftApPassword.setText(connectionOwnerPassword);
         softApPasswordInstructions.setVisibility(View.VISIBLE);
      }
      else
      {
         textViewSoftApPasswordLabel.setVisibility(View.INVISIBLE);
         editTextSoftApPassword.setVisibility(View.INVISIBLE);
         softApPasswordInstructions.setVisibility(View.INVISIBLE);
      }
      networkConnection.enable();
      networkConnection.setCallback(this);
      updateDevicesList();
      discoveryFuture = ThreadPool.getDefaultScheduler().scheduleAtFixedRate(new Runnable()
      {
         public void run()
         {
            networkConnection.discoverPotentialConnections();
         }
      }, 0, 10000, TimeUnit.MILLISECONDS);
   }

   public void onStop()
   {
      super.onStop();
      discoveryFuture.cancel(false);
      networkConnection.cancelPotentialConnections();
      networkConnection.disable();
      connectionOwnerPassword = editTextSoftApPassword.getText().toString();
      Editor editor = sharedPref.edit();
      editor.putString(getString(R.string.pref_connection_owner_password), connectionOwnerPassword);
      editor.apply();
   }

   public static class PeerRadioButton extends RadioButton {
      private String deviceIdentity = "";

      public PeerRadioButton(Context var1) {
         super(var1);
      }

      public String getDeviceIdentity() {
         return this.deviceIdentity;
      }

      public void setDeviceIdentity(String var1) {
         this.deviceIdentity = var1;
      }
   }
}
