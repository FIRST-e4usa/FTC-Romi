package com.qualcomm.ftcdriverstation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.Heartbeat;
import com.qualcomm.robotcore.robocol.RobocolDatagram;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.wifi.DriverStationAccessPointAssistant;
import com.qualcomm.robotcore.wifi.NetworkConnection;
import org.firstinspires.ftc.robotcore.internal.network.CallbackResult;
import org.firstinspires.ftc.robotcore.internal.network.NetworkConnectionHandler;
import org.firstinspires.ftc.robotcore.internal.network.RecvLoopRunnable;
import org.firstinspires.ftc.robotcore.internal.ui.BaseActivity;

public class FtcWirelessApNetworkConnectionActivity extends BaseActivity implements OnClickListener {
   private static final String TAG = "FtcWirelessApNetworkConnection";
   private Heartbeat heartbeat = new Heartbeat();
   private NetworkConnection networkConnection;
   private NetworkConnectionHandler networkConnectionHandler = NetworkConnectionHandler.getInstance();
   private RobotState robotState;
   private TextView textViewCurrentAp;
   private TextView textViewWirelessApStatus;

   protected FrameLayout getBackBar() {
      return (FrameLayout)this.findViewById(R.id.backbar);
   }

   public String getTag() {
      return "FtcWirelessApNetworkConnection";
   }

   public void onClick(View var1) {
   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(R.layout.activity_ftc_wireless_ap_connection);
      this.networkConnection = DriverStationAccessPointAssistant.getDriverStationAccessPointAssistant(this.getBaseContext());
      this.textViewCurrentAp = (TextView)this.findViewById(R.id.textViewCurrentAp);
      ((Button)this.findViewById(R.id.buttonWirelessApSettings)).setOnClickListener(new OnClickListener() {
         public void onClick(View var1) {
            FtcWirelessApNetworkConnectionActivity.this.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
         }
      });
   }

   public void onStart() {
      super.onStart();
      this.textViewCurrentAp.setText(this.networkConnection.getConnectionOwnerName());
      this.networkConnection.discoverPotentialConnections();
   }

   public void onStop() {
      super.onStop();
      this.networkConnection.cancelPotentialConnections();
   }

   protected void setTextView(final TextView var1, final String var2) {
      this.runOnUiThread(new Runnable() {
         public void run() {
            var1.setText(var2);
         }
      });
   }

   protected class RecvLoopCallback extends RecvLoopRunnable.DegenerateCallback {
      public CallbackResult heartbeatEvent(RobocolDatagram var1, long var2) {
         try {
            FtcWirelessApNetworkConnectionActivity.this.heartbeat.fromByteArray(var1.getData());
            FtcWirelessApNetworkConnectionActivity.this.robotState = RobotState.fromByte(FtcWirelessApNetworkConnectionActivity.this.heartbeat.getRobotState());
            FtcWirelessApNetworkConnectionActivity.this.setTextView(FtcWirelessApNetworkConnectionActivity.this.textViewWirelessApStatus, FtcWirelessApNetworkConnectionActivity.this.robotState.toString());
         } catch (RobotCoreException var4) {
            RobotLog.logStackTrace(var4);
         }

         return CallbackResult.HANDLED;
      }
   }
}
