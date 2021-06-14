package com.qualcomm.ftcdriverstation;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.FrameLayout;
import org.firstinspires.ftc.robotcore.internal.network.DeviceNameManagerFactory;
import org.firstinspires.ftc.robotcore.internal.system.PreferencesHelper;
import org.firstinspires.ftc.robotcore.internal.ui.BaseActivity;

public class FtcDriverStationInspectionReportsActivity extends BaseActivity {
   protected static final String CLIENT_CONNECTED = "CLIENT_CONNECTED";
   public static final String TAG = "FtcDriverStationInspectionReportsActivity";

   protected FrameLayout getBackBar() {
      return (FrameLayout)this.findViewById(R.id.backbar);
   }

   public String getTag() {
      return "FtcDriverStationInspectionReportsActivity";
   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(R.layout.activity_generic_settings);
      DeviceNameManagerFactory.getInstance().initializeDeviceNameIfNecessary();
      FtcDriverStationInspectionReportsActivity.SettingsFragment var2 = new FtcDriverStationInspectionReportsActivity.SettingsFragment();
      var1 = new Bundle();
      var1.putBoolean("CLIENT_CONNECTED", (new PreferencesHelper("FtcDriverStationInspectionReportsActivity", this)).readBoolean(this.getString(R.string.pref_rc_connected), false));
      var2.setArguments(var1);
      this.getFragmentManager().beginTransaction().replace(R.id.container, var2).commit();
   }

   public static class SettingsFragment extends PreferenceFragment {
      protected boolean clientConnected = false;

      public void onCreate(Bundle var1) {
         super.onCreate(var1);
         this.clientConnected = this.getArguments().getBoolean("CLIENT_CONNECTED");
         this.addPreferencesFromResource(R.xml.inspection);
         if (!this.clientConnected) {
            this.findPreference(this.getString(R.string.pref_launch_inspect_rc)).setEnabled(false);
         }

      }
   }
}
