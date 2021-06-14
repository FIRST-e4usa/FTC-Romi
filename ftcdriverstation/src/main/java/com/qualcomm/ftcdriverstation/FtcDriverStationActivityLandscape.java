package com.qualcomm.ftcdriverstation;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qualcomm.robotcore.util.BatteryChecker;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.wifi.DriverStationAccessPointAssistant;
import com.qualcomm.robotcore.wifi.NetworkConnection;
import com.qualcomm.robotcore.wifi.NetworkType;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.ui.UILocation;

public class FtcDriverStationActivityLandscape extends FtcDriverStationActivityBase implements DriverStationAccessPointAssistant.ConnectedNetworkHealthListener {
   View configAndTimerRegion;
   View dividerRcBatt12vBatt;
   ImageView headerColorLeft;
   LinearLayout headerColorRight;
   LinearLayout layoutPingChan;
   View matchLoggingContainer;
   TextView matchNumTxtView;
   ImageView networkSignalLevel;
   TextView network_ssid;
   PracticeTimerManager practiceTimerManager;
   View telemetryRegion;
   TextView textDbmLink;
   FtcDriverStationActivityLandscape.WiFiStatsView wiFiStatsView;

   public FtcDriverStationActivityLandscape() {
      this.wiFiStatsView = FtcDriverStationActivityLandscape.WiFiStatsView.PING_CHAN;
   }

   protected void assumeClientConnect(FtcDriverStationActivityBase.ControlPanelBack var1) {
      RobotLog.vv("DriverStation", "Assuming client connected");
      this.setClientConnected(true);
      this.uiRobotControllerIsConnected(var1);
   }

   protected void clearMatchNumber() {
      this.matchNumTxtView.setText("NONE");
   }

   protected void dimAndDisableAllControls() {
      super.dimAndDisableAllControls();
      this.setOpacity(this.configAndTimerRegion, 0.3F);
      this.setOpacity(this.telemetryRegion, 0.3F);
   }

   protected void disableMatchLoggingUI() {
      RobotLog.ii("DriverStation", "Hide match logging UI");
      this.matchLoggingContainer.setVisibility(View.GONE);
   }

   protected void displayRcBattery(boolean var1) {
      super.displayRcBattery(var1);
      View var2 = this.dividerRcBatt12vBatt;
      byte var3;
      if (var1) {
         var3 = 0;
      } else {
         var3 = 8;
      }

      var2.setVisibility(var3);
   }

   public void doMatchNumFieldBehaviorInit() {
      this.matchLoggingContainer.setOnClickListener(new View.OnClickListener() {
         public void onClick(View view) {
            new ManualKeyInDialog(FtcDriverStationActivityLandscape.this.context, "Enter Match Number", new ManualKeyInDialog.Listener() {
               public void onInput(String str) {
                  int validateMatchEntry = FtcDriverStationActivityLandscape.this.validateMatchEntry(str);
                  if (validateMatchEntry == -1) {
                     AppUtil.getInstance().showToast(UILocation.ONLY_LOCAL, FtcDriverStationActivityLandscape.this.getString(R.string.invalidMatchNumber));
                     FtcDriverStationActivityLandscape.this.clearMatchNumber();
                     return;
                  }
                  FtcDriverStationActivityLandscape.this.matchNumTxtView.setText(Integer.toString(validateMatchEntry));
                  FtcDriverStationActivityLandscape.this.sendMatchNumber(validateMatchEntry);
               }
            }).show();
         }
      });
      if (!this.preferencesHelper.readBoolean(getString(R.string.pref_match_logging_on_off), false)) {
         disableMatchLoggingUI();
      }
   }


   protected void enableAndBrightenForConnected(FtcDriverStationActivityBase.ControlPanelBack var1) {
      super.enableAndBrightenForConnected(var1);
      this.setOpacity(this.configAndTimerRegion, 1.0F);
      this.setOpacity(this.telemetryRegion, 1.0F);
   }

   protected void enableMatchLoggingUI() {
      RobotLog.ii("DriverStation", "Show match logging UI");
      this.matchLoggingContainer.setVisibility(View.VISIBLE);
   }

   protected int getMatchNumber() throws NumberFormatException {
      return Integer.parseInt(this.matchNumTxtView.getText().toString());
   }

   public View getPopupMenuAnchor() {
      return this.wifiInfo;
   }

   public int linkSpeedToWiFiSignal(int var1, int var2) {
      float var3 = (float)var1;
      if (var3 <= 6.0F) {
         return 0;
      } else {
         return var3 >= 54.0F ? var2 : Math.round((var3 - 6.0F) / (48.0F / (float)(var2 - 1)));
      }
   }

   public void onNetworkHealthUpdate(final int i, final int i2) {
      int round = (int) Math.round(((double) ((float) (rssiToWiFiSignal(i, 5) + linkSpeedToWiFiSignal(i2, 5)))) / 2.0d);
      final int i3 = round != 0 ? round != 1 ? round != 2 ? round != 3 ? round != 4 ? round != 5 ? 0 : R.drawable.ic_signal_bars_5 : R.drawable.ic_signal_bars_4 : R.drawable.ic_signal_bars_3 : R.drawable.ic_signal_bars_2 : R.drawable.ic_signal_bars_1 : R.drawable.ic_signal_bars_0;
      runOnUiThread(new Runnable() {
         public void run() {
            FtcDriverStationActivityLandscape.this.networkSignalLevel.setBackgroundResource(i3);
            FtcDriverStationActivityLandscape.this.textDbmLink.setText(String.format("%ddBm Link %dMb", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
         }
      });
   }


   protected void onPause() {
      super.onPause();
      this.practiceTimerManager.reset();
      NetworkConnection var1 = this.networkConnectionHandler.getNetworkConnection();
      if (var1.getNetworkType() == NetworkType.WIRELESSAP) {
         ((DriverStationAccessPointAssistant)var1).unregisterNetworkHealthListener(this);
      }

   }

   protected void onResume() {
      super.onResume();
      NetworkConnection var1 = this.networkConnectionHandler.getNetworkConnection();
      if (var1.getNetworkType() == NetworkType.WIRELESSAP) {
         ((DriverStationAccessPointAssistant)var1).registerNetworkHealthListener(this);
      }

   }

   protected void pingStatus(String var1) {
      TextView var2 = this.textPingStatus;
      StringBuilder var3 = new StringBuilder();
      var3.append("Ping: ");
      var3.append(var1);
      var3.append(" - ");
      this.setTextView(var2, var3.toString());
   }

   public int rssiToWiFiSignal(int var1, int var2) {
      float var3 = (float)var1;
      if (var3 <= -90.0F) {
         return 0;
      } else {
         return var3 >= -55.0F ? var2 : Math.round((var3 + 90.0F) / (35.0F / (float)(var2 - 1)));
      }
   }

   protected void showWifiStatus(final boolean z, final String str) {
      runOnUiThread(new Runnable() {
         public void run() {
            FtcDriverStationActivityLandscape.this.textWifiDirectStatusShowingRC = z;
            FtcDriverStationActivityLandscape.this.textWifiDirectStatus.setText(str);
            if (str.equals(FtcDriverStationActivityLandscape.this.getString(R.string.wifiStatusDisconnected)) || str.equals(FtcDriverStationActivityLandscape.this.getString(R.string.actionlistenerfailure_busy)) || str.equals(FtcDriverStationActivityLandscape.this.getString(R.string.wifiStatusNotPaired))) {
               FtcDriverStationActivityLandscape.this.headerColorRight.setBackground(FtcDriverStationActivityLandscape.this.getResources().getDrawable(R.drawable.lds_header_shadow_disconnected));
               FtcDriverStationActivityLandscape.this.headerColorLeft.setBackgroundColor(FtcDriverStationActivityLandscape.this.getResources().getColor(R.color.lds_header_red_gradient_start));
            } else if (str.equals(FtcDriverStationActivityLandscape.this.getString(R.string.wifiStatusConnecting)) || str.equals(FtcDriverStationActivityLandscape.this.getString(R.string.wifiStatusSearching))) {
               FtcDriverStationActivityLandscape.this.headerColorRight.setBackground(FtcDriverStationActivityLandscape.this.getResources().getDrawable(R.drawable.lds_header_shadow_connecting));
               FtcDriverStationActivityLandscape.this.headerColorLeft.setBackgroundColor(FtcDriverStationActivityLandscape.this.getResources().getColor(R.color.lds_header_yellow_gradient_start));
            } else {
               FtcDriverStationActivityLandscape.this.textWifiDirectStatus.setText("Robot Connected");
               String str2 = str;
               if (str.contains("DIRECT-") && str.contains("RC")) {
                  str2 = str.substring(10);
               }
               TextView textView = FtcDriverStationActivityLandscape.this.network_ssid;
               textView.setText("Network: " + str);
               FtcDriverStationActivityLandscape.this.headerColorRight.setBackground(FtcDriverStationActivityLandscape.this.getResources().getDrawable(R.drawable.lds_header_shadow_connected));
               FtcDriverStationActivityLandscape.this.headerColorLeft.setBackgroundColor(FtcDriverStationActivityLandscape.this.getResources().getColor(R.color.lds_header_green_gradient_start));
            }
         }
      });
   }


   public void subclassOnCreate() {
      setContentView(R.layout.activity_ds_land_main);
      this.headerColorLeft = (ImageView) findViewById(R.id.headerColorLeft);
      this.headerColorRight = (LinearLayout) findViewById(R.id.headerColorRight);
      this.configAndTimerRegion = findViewById(R.id.configAndTimerRegion);
      this.telemetryRegion = findViewById(R.id.telemetryRegion);
      this.practiceTimerManager = new PracticeTimerManager(this, (ImageView) findViewById(R.id.practiceTimerStartStopBtn), (TextView) findViewById(R.id.practiceTimerTimeView));
      this.matchLoggingContainer = findViewById(R.id.matchNumContainer);
      this.matchNumTxtView = (TextView) findViewById(R.id.matchNumTextField);
      this.networkSignalLevel = (ImageView) findViewById(R.id.networkSignalLevel);
      this.layoutPingChan = (LinearLayout) findViewById(R.id.layoutPingChan);
      this.textDbmLink = (TextView) findViewById(R.id.textDbmLink);
      this.network_ssid = (TextView) findViewById(R.id.network_ssid);
      this.dividerRcBatt12vBatt = findViewById(R.id.dividerRcBatt12vBatt);
   }



   public void toggleWifiStatsView(View var1) {
      if (this.wiFiStatsView == FtcDriverStationActivityLandscape.WiFiStatsView.PING_CHAN) {
         this.wiFiStatsView = FtcDriverStationActivityLandscape.WiFiStatsView.DBM_LINK;
         this.textDbmLink.setVisibility(View.VISIBLE);
         this.layoutPingChan.setVisibility(View.GONE);
      } else if (this.wiFiStatsView == FtcDriverStationActivityLandscape.WiFiStatsView.DBM_LINK) {
         this.wiFiStatsView = FtcDriverStationActivityLandscape.WiFiStatsView.PING_CHAN;
         this.layoutPingChan.setVisibility(View.VISIBLE);
         this.textDbmLink.setVisibility(View.GONE );
      }

   }

   public void uiRobotControllerIsConnected(FtcDriverStationActivityBase.ControlPanelBack controlPanelBack) {
      super.uiRobotControllerIsConnected(controlPanelBack);
      runOnUiThread(new Runnable() {
         public void run() {
            FtcDriverStationActivityLandscape.this.headerColorRight.setBackground(FtcDriverStationActivityLandscape.this.getResources().getDrawable(R.drawable.lds_header_shadow_connected));
            FtcDriverStationActivityLandscape.this.headerColorLeft.setBackgroundColor(FtcDriverStationActivityLandscape.this.getResources().getColor(R.color.lds_header_green_gradient_start));
            FtcDriverStationActivityLandscape.this.textWifiDirectStatus.setText("Robot Connected");
         }
      });
   }


   public void uiRobotControllerIsDisconnected() {
      super.uiRobotControllerIsDisconnected();
      runOnUiThread(new Runnable() {
         public void run() {
            FtcDriverStationActivityLandscape.this.headerColorRight.setBackground(FtcDriverStationActivityLandscape.this.getResources().getDrawable(R.drawable.lds_header_shadow_disconnected));
            FtcDriverStationActivityLandscape.this.headerColorLeft.setBackgroundColor(FtcDriverStationActivityLandscape.this.getResources().getColor(R.color.lds_header_red_gradient_start));
            FtcDriverStationActivityLandscape.this.textWifiDirectStatus.setText("Disconnected");
         }
      });
   }


   public void updateBatteryStatus(BatteryChecker.BatteryStatus var1) {
      TextView var2 = this.dsBatteryInfo;
      StringBuilder var3 = new StringBuilder();
      var3.append("DS: ");
      var3.append(Math.round(var1.percent));
      var3.append("%");
      this.setTextView(var2, var3.toString());
      this.setBatteryIcon(var1, this.dsBatteryIcon);
   }

   protected void updateRcBatteryStatus(BatteryChecker.BatteryStatus var1) {
      TextView var2 = this.rcBatteryTelemetry;
      StringBuilder var3 = new StringBuilder();
      var3.append("RC: ");
      var3.append(Math.round(var1.percent));
      var3.append("%");
      this.setTextView(var2, var3.toString());
      this.setBatteryIcon(var1, this.rcBatteryIcon);
   }

   static enum WiFiStatsView {
      DBM_LINK,
      PING_CHAN;

   }
}
