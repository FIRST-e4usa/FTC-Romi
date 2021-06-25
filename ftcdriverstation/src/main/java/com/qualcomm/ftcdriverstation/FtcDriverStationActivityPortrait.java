package com.qualcomm.ftcdriverstation;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.qualcomm.robotcore.util.BatteryChecker;
import com.qualcomm.robotcore.util.RobotLog;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.ui.UILocation;

public class FtcDriverStationActivityPortrait extends FtcDriverStationActivityBase {
   protected EditText matchNumField;

   protected void clearMatchNumber() {
      this.matchNumField.setText("");
   }

   protected void disableMatchLoggingUI() {
      RobotLog.ii("DriverStation", "Hide match logging UI");
      this.matchNumField.setVisibility(View.INVISIBLE);
      this.matchNumField.setEnabled(false);
      this.findViewById(R.id.matchNumLabel).setVisibility(View.GONE);
   }

   protected void doMatchNumFieldBehaviorInit() {
      this.matchNumField.setText("");
      this.matchNumField.setOnClickListener(new OnClickListener() {
         public void onClick(View var1) {
            FtcDriverStationActivityPortrait.this.matchNumField.setText("");
         }
      });
      this.matchNumField.setOnEditorActionListener(new OnEditorActionListener() {
         public boolean onEditorAction(TextView var1, int var2, KeyEvent var3) {
            if (var2 == 6) {
               var2 = FtcDriverStationActivityPortrait.this.validateMatchEntry(var1.getText().toString());
               if (var2 == -1) {
                  AppUtil.getInstance().showToast(UILocation.ONLY_LOCAL, FtcDriverStationActivityPortrait.this.getString(R.string.invalidMatchNumber));
                  FtcDriverStationActivityPortrait.this.matchNumField.setText("");
               } else {
                  FtcDriverStationActivityPortrait.this.sendMatchNumber(var2);
               }
            }

            return false;
         }
      });
      this.findViewById(R.id.buttonInit).requestFocus();
      if (!this.preferencesHelper.readBoolean(this.getString(R.string.pref_match_logging_on_off), false)) {
         this.disableMatchLoggingUI();
      }

   }

   protected void enableMatchLoggingUI() {
      RobotLog.ii("DriverStation", "Show match logging UI");
      this.matchNumField.setVisibility(View.VISIBLE);
      this.matchNumField.setEnabled(true);
      this.findViewById(R.id.matchNumLabel).setVisibility(View.VISIBLE);
   }

   protected int getMatchNumber() {
      return Integer.parseInt(this.matchNumField.getText().toString());
   }

   public View getPopupMenuAnchor() {
      return this.buttonMenu;
   }

   protected void pingStatus(String var1) {
      this.setTextView(this.textPingStatus, var1);
   }

   public void subclassOnCreate() {
      this.setContentView(R.layout.activity_ftc_driver_station);
      this.matchNumField = (EditText)this.findViewById(R.id.matchNumTextField);
   }

   public void updateBatteryStatus(BatteryChecker.BatteryStatus var1) {
      TextView var2 = this.dsBatteryInfo;
      StringBuilder var3 = new StringBuilder();
      var3.append(Double.toString(var1.percent));
      var3.append("%");
      this.setTextView(var2, var3.toString());
      this.setBatteryIcon(var1, this.dsBatteryIcon);
   }

   protected void updateRcBatteryStatus(BatteryChecker.BatteryStatus var1) {
      TextView var2 = this.rcBatteryTelemetry;
      StringBuilder var3 = new StringBuilder();
      var3.append(Double.toString(var1.percent));
      var3.append("%");
      this.setTextView(var2, var3.toString());
      this.setBatteryIcon(var1, this.rcBatteryIcon);
   }
}
