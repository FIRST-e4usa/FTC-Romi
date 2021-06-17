/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package org.firstinspires.ftc.robotcontroller.internal;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import ftcdriverstation.OpModeSelectionDialogFragment;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dekaresearch.simulation.SimulationConstants;
import com.google.blocks.ftcrobotcontroller.ProgrammingWebHandlers;
import com.google.blocks.ftcrobotcontroller.runtime.BlocksOpMode;
import com.qualcomm.ftccommon.ClassManagerFactory;
import com.qualcomm.ftccommon.FtcAboutActivity;
import com.qualcomm.ftccommon.FtcEventLoop;
import com.qualcomm.ftccommon.FtcEventLoopIdle;
import com.qualcomm.ftccommon.FtcRobotControllerService;
import com.qualcomm.ftccommon.FtcRobotControllerService.FtcRobotControllerBinder;
import com.qualcomm.ftccommon.FtcRobotControllerSettingsActivity;
import com.qualcomm.ftccommon.LaunchActivityConstantsList;
import com.qualcomm.ftccommon.LaunchActivityConstantsList.RequestCode;
import com.qualcomm.ftccommon.Restarter;
import com.qualcomm.ftccommon.UpdateUI;
import com.qualcomm.ftccommon.configuration.EditParameters;
import com.qualcomm.ftccommon.configuration.FtcLoadFileActivity;
import com.qualcomm.ftccommon.configuration.RobotConfigFile;
import com.qualcomm.ftccommon.configuration.RobotConfigFileManager;
import com.qualcomm.ftcrobotcontroller.BuildConfig;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.hardware.HardwareFactory;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.opmode.FtcRobotControllerServiceState;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.configuration.LynxConstants;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.TelemetryMessage;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.BatteryChecker;
import com.qualcomm.robotcore.util.Device;
import com.qualcomm.robotcore.util.Dimmer;
import com.qualcomm.robotcore.util.ImmersiveMode;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.WebServer;
import com.qualcomm.robotcore.wifi.NetworkConnection;
import com.qualcomm.robotcore.wifi.NetworkConnectionFactory;
import com.qualcomm.robotcore.wifi.NetworkType;

import org.firstinspires.ftc.ftccommon.external.SoundPlayingRobotMonitor;
import org.firstinspires.ftc.ftccommon.internal.FtcRobotControllerWatchdogService;
import org.firstinspires.ftc.ftccommon.internal.ProgramAndManageActivity;
import org.firstinspires.ftc.onbotjava.OnBotJavaHelperImpl;
import org.firstinspires.ftc.onbotjava.OnBotJavaProgrammingMode;
import org.firstinspires.ftc.robotcore.external.Event;
import org.firstinspires.ftc.robotcore.external.Predicate;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.MotionDetection;
import org.firstinspires.ftc.robotcore.internal.hardware.android.AndroidBoard;
import org.firstinspires.ftc.robotcore.internal.network.DeviceNameManagerFactory;
import org.firstinspires.ftc.robotcore.internal.network.PreferenceRemoterRC;
import org.firstinspires.ftc.robotcore.internal.network.StartResult;
import org.firstinspires.ftc.robotcore.internal.network.WifiDirectChannelChanger;
import org.firstinspires.ftc.robotcore.internal.network.WifiMuteEvent;
import org.firstinspires.ftc.robotcore.internal.network.WifiMuteStateMachine;
import org.firstinspires.ftc.robotcore.internal.opmode.ClassManager;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;
import org.firstinspires.ftc.robotcore.internal.system.AppAliveNotifier;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.robotcore.internal.system.Assert;
import org.firstinspires.ftc.robotcore.internal.system.PreferencesHelper;
import org.firstinspires.ftc.robotcore.internal.system.ServiceController;
import org.firstinspires.ftc.robotcore.internal.ui.ThemedActivity;
import org.firstinspires.ftc.robotcore.internal.ui.UILocation;
import org.firstinspires.ftc.robotcore.internal.webserver.RobotControllerWebInfo;
import org.firstinspires.ftc.robotserver.internal.programmingmode.ProgrammingModeManager;
import org.firstinspires.inspection.RcInspectionActivity;

import java.security.Key;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("WeakerAccess")
public class FtcRobotControllerActivity extends Activity implements OpModeSelectionDialogFragment.OpModeSelectionDialogListener
  {
  public static final String TAG = "RCActivity";
  public String getTag() { return TAG; }

  private static final int REQUEST_CONFIG_WIFI_CHANNEL = 1;
  private static final int NUM_GAMEPADS = 2;

  protected WifiManager.WifiLock wifiLock;
  protected RobotConfigFileManager cfgFileMgr;

  protected ProgrammingModeManager programmingModeManager;

  protected UpdateUI.Callback callback;
  protected Context context;
  protected Utility utility;
  protected StartResult prefRemoterStartResult = new StartResult();
  protected StartResult deviceNameStartResult = new StartResult();
  protected PreferencesHelper preferencesHelper;
  protected final SharedPreferencesListener sharedPreferencesListener = new SharedPreferencesListener();

  protected ImageButton buttonMenu;
  protected TextView textDeviceName;
  protected TextView textNetworkConnectionStatus;
  protected TextView textRobotStatus;
  protected TextView[] textGamepad = new TextView[NUM_GAMEPADS];
  protected TextView textOpMode;
  protected TextView textErrorMessage;
  protected ImmersiveMode immersion;

  protected UpdateUI updateUI;
  protected Dimmer dimmer;
  protected LinearLayout entireScreenLayout;

  protected FtcRobotControllerService controllerService;
  protected NetworkType networkType;

  protected FtcEventLoop eventLoop;
  protected Queue<UsbDevice> receivedUsbAttachmentNotifications;

  protected WifiMuteStateMachine wifiMuteStateMachine;
  protected MotionDetection motionDetection;

  private static boolean permissionsValidated = false;

  private WifiDirectChannelChanger wifiDirectChannelChanger;

    protected class RobotRestarter implements Restarter {

    public void requestRestart() {
      requestRobotRestart();
    }

  }

  protected boolean serviceShouldUnbind = false;
  protected ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      FtcRobotControllerBinder binder = (FtcRobotControllerBinder) service;
      onServiceBind(binder.getService());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      RobotLog.vv(FtcRobotControllerService.TAG, "%s.controllerService=null", TAG);
      controllerService = null;
    }
  };

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
      UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
      RobotLog.vv(TAG, "ACTION_USB_DEVICE_ATTACHED: %s", usbDevice.getDeviceName());

      if (usbDevice != null) {  // paranoia
        // We might get attachment notifications before the event loop is set up, so
        // we hold on to them and pass them along only when we're good and ready.
        if (receivedUsbAttachmentNotifications != null) { // *total* paranoia
          receivedUsbAttachmentNotifications.add(usbDevice);
          passReceivedUsbAttachmentsToEventLoop();
        }
      }
    }
  }

  protected void passReceivedUsbAttachmentsToEventLoop() {
    if (this.eventLoop != null) {
      for (;;) {
        UsbDevice usbDevice = receivedUsbAttachmentNotifications.poll();
        if (usbDevice == null)
          break;
        this.eventLoop.onUsbDeviceAttached(usbDevice);
      }
    }
    else {
      // Paranoia: we don't want the pending list to grow without bound when we don't
      // (yet) have an event loop
      while (receivedUsbAttachmentNotifications.size() > 100) {
        receivedUsbAttachmentNotifications.poll();
      }
    }
  }

  /**
   * There are cases where a permission may be revoked and the system restart will restart the
   * FtcRobotControllerActivity, instead of the launch activity.  Detect when that happens, and throw
   * the device back to the permission validator activity.
   */
  protected boolean enforcePermissionValidator() {
    if (!permissionsValidated) {
      RobotLog.vv(TAG, "Redirecting to permission validator");
      Intent permissionValidatorIntent = new Intent(AppUtil.getDefContext(), PermissionValidatorWrapper.class);
      startActivity(permissionValidatorIntent);
      finish();
      return true;
    } else {
      RobotLog.vv(TAG, "Permissions validated already");
      return false;
    }
  }

  public static void setPermissionsValidated() {
    permissionsValidated = true;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (enforcePermissionValidator()) {
      return;
    }

    RobotLog.onApplicationStart();  // robustify against onCreate() following onDestroy() but using the same app instance, which apparently does happen
    RobotLog.vv(TAG, "onCreate()");
    ThemedActivity.appAppThemeToActivity(getTag(), this); // do this way instead of inherit to help AppInventor

    // Oddly, sometimes after a crash & restart the root activity will be something unexpected, like from the before crash? We don't yet understand
    RobotLog.vv(TAG, "rootActivity is of class %s", AppUtil.getInstance().getRootActivity().getClass().getSimpleName());
    RobotLog.vv(TAG, "launchActivity is of class %s", FtcRobotControllerWatchdogService.launchActivity());
    Assert.assertTrue(FtcRobotControllerWatchdogService.isLaunchActivity(AppUtil.getInstance().getRootActivity()));
    Assert.assertTrue(AppUtil.getInstance().isRobotController());

    // Quick check: should we pretend we're not here, and so allow the Lynx to operate as
    // a stand-alone USB-connected module?
    if (LynxConstants.isRevControlHub()) {
      // Double-sure check that we can talk to the DB over the serial TTY
      AndroidBoard.getInstance().getAndroidBoardIsPresentPin().setState(true);
    }

    context = this;
    utility = new Utility(this);

    DeviceNameManagerFactory.getInstance().start(deviceNameStartResult);

    PreferenceRemoterRC.getInstance().start(prefRemoterStartResult);

    receivedUsbAttachmentNotifications = new ConcurrentLinkedQueue<UsbDevice>();
    eventLoop = null;

    setContentView(R.layout.activity_ftc_controller);

    driverStationOnCreate();

    preferencesHelper = new PreferencesHelper(TAG, context);
    preferencesHelper.writeBooleanPrefIfDifferent(context.getString(R.string.pref_rc_connected), true);
    preferencesHelper.getSharedPreferences().registerOnSharedPreferenceChangeListener(sharedPreferencesListener);

    entireScreenLayout = (LinearLayout) findViewById(R.id.entire_screen);
    buttonMenu = (ImageButton) findViewById(R.id.menu_buttons);
    buttonMenu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PopupMenu popupMenu = new PopupMenu(FtcRobotControllerActivity.this, v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(MenuItem item) {
            return onOptionsItemSelected(item); // Delegate to the handler for the hardware menu button
          }
        });
        popupMenu.inflate(R.menu.ftc_robot_controller);
        if(SimulationConstants.isSimulation) {
          popupMenu.getMenu().findItem(R.id.action_configure_robot).setVisible(false);
          // TODO(Romi) make simulation related buttons visible
        }
        popupMenu.show();
      }
    });

    updateMonitorLayout(getResources().getConfiguration());

    BlocksOpMode.setActivityAndWebView(this, (WebView) findViewById(R.id.webViewBlocksRuntime));

    /*
     * Paranoia as the ClassManagerFactory requires EXTERNAL_STORAGE permissions
     * and we've seen on the DS where the finish() call above does not short-circuit
     * the onCreate() call for the activity and then we crash here because we don't
     * have permissions. So...
     */
    if (permissionsValidated) {
      ClassManager.getInstance().setOnBotJavaClassHelper(new OnBotJavaHelperImpl());
      ClassManagerFactory.registerFilters();
      ClassManagerFactory.processAllClasses();
    }

    cfgFileMgr = new RobotConfigFileManager(this);

    // Clean up 'dirty' status after a possible crash
    RobotConfigFile configFile = cfgFileMgr.getActiveConfig();
    if (configFile.isDirty()) {
      configFile.markClean();
      cfgFileMgr.setActiveConfig(false, configFile);
    }

    textDeviceName = (TextView) findViewById(R.id.textDeviceName);
    textNetworkConnectionStatus = (TextView) findViewById(R.id.textNetworkConnectionStatus);
    textRobotStatus = (TextView) findViewById(R.id.textRobotStatus);
    textOpMode = (TextView) findViewById(R.id.textOpMode);
    textErrorMessage = (TextView) findViewById(R.id.textErrorMessage);
    textGamepad[0] = (TextView) findViewById(R.id.textGamepad1);
    textGamepad[1] = (TextView) findViewById(R.id.textGamepad2);
    immersion = new ImmersiveMode(getWindow().getDecorView());
    dimmer = new Dimmer(this);
    dimmer.longBright();

    programmingModeManager = new ProgrammingModeManager();
    programmingModeManager.register(new ProgrammingWebHandlers());
    programmingModeManager.register(new OnBotJavaProgrammingMode());

    updateUI = createUpdateUI();
    callback = createUICallback(updateUI);

    PreferenceManager.setDefaultValues(this, R.xml.app_settings, false);

    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "");

    hittingMenuButtonBrightensScreen();

    wifiLock.acquire();
    callback.networkConnectionUpdate(NetworkConnection.NetworkEvent.DISCONNECTED);
    readNetworkType();
    ServiceController.startService(FtcRobotControllerWatchdogService.class);
    bindToService();
    logPackageVersions();
    logDeviceSerialNumber();
    AndroidBoard.getInstance().logAndroidBoardInfo();
    RobotLog.logDeviceInfo();

    if (preferencesHelper.readBoolean(getString(R.string.pref_wifi_automute), false)) {
      initWifiMute(true);
    }

    FtcAboutActivity.setBuildTimeFromBuildConfig(BuildConfig.BUILD_TIME);

    // check to see if there is a preferred Wi-Fi to use.
    checkPreferredChannel();
  }

  protected UpdateUI createUpdateUI() {
    Restarter restarter = new RobotRestarter();
    UpdateUI result = new UpdateUI(this, dimmer);
    result.setRestarter(restarter);
    result.setTextViews(textNetworkConnectionStatus, textRobotStatus, textGamepad, textOpMode, textErrorMessage, textDeviceName);
    if(SimulationConstants.isSimulation) {
      result.setExtraTextViews(textTelemetry);
    }
    return result;
  }

  protected UpdateUI.Callback createUICallback(UpdateUI updateUI) {
    UpdateUI.Callback result = updateUI.new Callback();
    result.setStateMonitor(new SoundPlayingRobotMonitor());
    return result;
  }

  @Override
  protected void onStart() {
    super.onStart();
    RobotLog.vv(TAG, "onStart()");

    entireScreenLayout.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        dimmer.handleDimTimer();
        return false;
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    RobotLog.vv(TAG, "onResume()");
  }

  @Override
  protected void onPause() {
    super.onPause();
    RobotLog.vv(TAG, "onPause()");
  }

  @Override
  protected void onStop() {
    // Note: this gets called even when the configuration editor is launched. That is, it gets
    // called surprisingly often. So, we don't actually do much here.
    super.onStop();
    RobotLog.vv(TAG, "onStop()");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    RobotLog.vv(TAG, "onDestroy()");

    shutdownRobot();  // Ensure the robot is put away to bed
    if (callback != null) callback.close();

    PreferenceRemoterRC.getInstance().stop(prefRemoterStartResult);
    DeviceNameManagerFactory.getInstance().stop(deviceNameStartResult);

    unbindFromService();
    // If the app manually (?) is stopped, then we don't need the auto-starting function (?)
    ServiceController.stopService(FtcRobotControllerWatchdogService.class);
    if (wifiLock != null) wifiLock.release();
    if (preferencesHelper != null) preferencesHelper.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener);

    RobotLog.cancelWriteLogcatToDisk();
  }

  protected void bindToService() {
    readNetworkType();
    Intent intent = new Intent(this, FtcRobotControllerService.class);
    intent.putExtra(NetworkConnectionFactory.NETWORK_CONNECTION_TYPE, networkType);
    serviceShouldUnbind = bindService(intent, connection, Context.BIND_AUTO_CREATE);
  }

  protected void unbindFromService() {
    if (serviceShouldUnbind) {
      unbindService(connection);
      serviceShouldUnbind = false;
    }
  }

  protected void logPackageVersions() {
    RobotLog.logBuildConfig(com.qualcomm.ftcrobotcontroller.BuildConfig.class);
    RobotLog.logBuildConfig(com.qualcomm.robotcore.BuildConfig.class);
    RobotLog.logBuildConfig(com.qualcomm.hardware.BuildConfig.class);
    RobotLog.logBuildConfig(com.qualcomm.ftccommon.BuildConfig.class);
    RobotLog.logBuildConfig(com.google.blocks.BuildConfig.class);
    RobotLog.logBuildConfig(org.firstinspires.inspection.BuildConfig.class);
  }

  protected void logDeviceSerialNumber() {
    RobotLog.ii(TAG, "Android device serial number: " + Device.getSerialNumberOrUnknown());
  }

  protected void readNetworkType() {

    // The code here used to defer to the value found in a configuration file
    // to configure the network type. If the file was absent, then it initialized
    // it with a default.
    //
    // However, bugs have been reported with that approach (empty config files, specifically).
    // Moreover, the non-Wifi-Direct networking is end-of-life, so the simplest and most robust
    // (e.g.: no one can screw things up by messing with the contents of the config file) fix is
    // to do away with configuration file entirely.
    //
    // Control hubs are always running the access point model.  Everything else, for the time
    // being always runs the wifi direct model.
    if (Device.isRevControlHub() == true) {
      networkType = NetworkType.RCWIRELESSAP;
    } else {
      networkType = NetworkType.fromString(preferencesHelper.readString(context.getString(R.string.pref_pairing_kind), NetworkType.globalDefaultAsString()));
    }

    // update the app_settings
    preferencesHelper.writeStringPrefIfDifferent(context.getString(R.string.pref_pairing_kind), networkType.toString());
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);

    if (hasFocus) {
      immersion.hideSystemUI();
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.ftc_robot_controller, menu);
    return true;
  }

  private boolean isRobotRunning() {
    if (controllerService == null) {
      return false;
    }

    Robot robot = controllerService.getRobot();

    if ((robot == null) || (robot.eventLoopManager == null)) {
      return false;
    }

    RobotState robotState = robot.eventLoopManager.state;

    if (robotState != RobotState.RUNNING) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_program_and_manage) {
      if (isRobotRunning()) {
        Intent programmingModeIntent = new Intent(AppUtil.getDefContext(), ProgramAndManageActivity.class);
        RobotControllerWebInfo webInfo = programmingModeManager.getWebServer().getConnectionInformation();
        programmingModeIntent.putExtra(LaunchActivityConstantsList.RC_WEB_INFO, webInfo.toJson());
        startActivity(programmingModeIntent);
      } else {
        AppUtil.getInstance().showToast(UILocation.ONLY_LOCAL, context.getString(R.string.toastWifiUpBeforeProgrammingMode));
      }
    } else if (id == R.id.action_inspection_mode) {
      Intent inspectionModeIntent = new Intent(AppUtil.getDefContext(), RcInspectionActivity.class);
      startActivity(inspectionModeIntent);
      return true;
    } else if (id == R.id.action_restart_robot) {
      dimmer.handleDimTimer();
      AppUtil.getInstance().showToast(UILocation.BOTH, context.getString(R.string.toastRestartingRobot));
      requestRobotRestart();
      return true;
    }
    else if (id == R.id.action_configure_robot) {
      EditParameters parameters = new EditParameters();
      Intent intentConfigure = new Intent(AppUtil.getDefContext(), FtcLoadFileActivity.class);
      parameters.putIntent(intentConfigure);
      startActivityForResult(intentConfigure, RequestCode.CONFIGURE_ROBOT_CONTROLLER.ordinal());
    }
    else if (id == R.id.action_settings) {
	  // historical: this once erroneously used FTC_CONFIGURE_REQUEST_CODE_ROBOT_CONTROLLER
      Intent settingsIntent = new Intent(AppUtil.getDefContext(), FtcRobotControllerSettingsActivity.class);
      startActivityForResult(settingsIntent, RequestCode.SETTINGS_ROBOT_CONTROLLER.ordinal());
      return true;
    }
    else if (id == R.id.action_about) {
      Intent intent = new Intent(AppUtil.getDefContext(), FtcAboutActivity.class);
      startActivity(intent);
      return true;
    }
    else if (id == R.id.action_exit_app) {

      //Clear backstack and everything to prevent edge case where VM might be
      //restarted (after it was exited) if more than one activity was on the
      //backstack for some reason.
      finishAffinity();

      //For lollipop and up, we can clear ourselves from the recents list too
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> tasks = manager.getAppTasks();

        for (ActivityManager.AppTask task : tasks) {
          task.finishAndRemoveTask();
        }
      }

      // Allow the user to use the Control Hub operating system's UI, instead of relaunching the app
      AppAliveNotifier.getInstance().disableAppWatchdogUntilNextAppStart();

      //Finally, nuke the VM from orbit
      AppUtil.getInstance().exitApplication();

      return true;
    }

   return super.onOptionsItemSelected(item);
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // don't destroy assets on screen rotation
    updateMonitorLayout(newConfig);
  }

  /**
   * Updates the orientation of monitorContainer (which contains cameraMonitorView and
   * tfodMonitorView) based on the given configuration. Makes the children split the space.
   */
  private void updateMonitorLayout(Configuration configuration) {
    LinearLayout monitorContainer = (LinearLayout) findViewById(R.id.monitorContainer);
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      // When the phone is landscape, lay out the monitor views horizontally.
      monitorContainer.setOrientation(LinearLayout.HORIZONTAL);
      for (int i = 0; i < monitorContainer.getChildCount(); i++) {
        View view = monitorContainer.getChildAt(i);
        view.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1 /* weight */));
      }
    } else {
      // When the phone is portrait, lay out the monitor views vertically.
      monitorContainer.setOrientation(LinearLayout.VERTICAL);
      for (int i = 0; i < monitorContainer.getChildCount(); i++) {
        View view = monitorContainer.getChildAt(i);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1 /* weight */));
      }
    }
    monitorContainer.requestLayout();
  }

  @Override
  protected void onActivityResult(int request, int result, Intent intent) {
    if (request == REQUEST_CONFIG_WIFI_CHANNEL) {
      if (result == RESULT_OK) {
        AppUtil.getInstance().showToast(UILocation.BOTH, context.getString(R.string.toastWifiConfigurationComplete));
      }
    }
    // was some historical confusion about launch codes here, so we err safely
    if (request == RequestCode.CONFIGURE_ROBOT_CONTROLLER.ordinal() || request == RequestCode.SETTINGS_ROBOT_CONTROLLER.ordinal()) {
      // We always do a refresh, whether it was a cancel or an OK, for robustness
      shutdownRobot();
      cfgFileMgr.getActiveConfigAndUpdateUI();
      updateUIAndRequestRobotSetup();
    }
  }

  public void onServiceBind(final FtcRobotControllerService service) {
    RobotLog.vv(FtcRobotControllerService.TAG, "%s.controllerService=bound", TAG);
    controllerService = service;
    updateUI.setControllerService(controllerService);

    updateUIAndRequestRobotSetup();
    programmingModeManager.setState(new FtcRobotControllerServiceState() {
      @NonNull
      @Override
      public WebServer getWebServer() {
        return service.getWebServer();
      }

      @Override
      public EventLoopManager getEventLoopManager() {
        return service.getRobot().eventLoopManager;
      }
    });
  }

  private void updateUIAndRequestRobotSetup() {
    if (controllerService != null) {
      callback.networkConnectionUpdate(controllerService.getNetworkConnectionStatus());
      callback.updateRobotStatus(controllerService.getRobotStatus());
      // Only show this first-time toast on headless systems: what we have now on non-headless suffices
      requestRobotSetup(LynxConstants.isRevControlHub()
        ? new Runnable() {
            @Override public void run() {
              showRestartRobotCompleteToast(R.string.toastRobotSetupComplete);
            }
          }
        : null);
    }
  }

  private void requestRobotSetup(@Nullable Runnable runOnComplete) {
    if (controllerService == null) return;

    RobotConfigFile file = cfgFileMgr.getActiveConfigAndUpdateUI();
    HardwareFactory hardwareFactory = new HardwareFactory(context);
    try {
      hardwareFactory.setXmlPullParser(file.getXml());
    } catch (Resources.NotFoundException e) {
      file = RobotConfigFile.noConfig(cfgFileMgr);
      hardwareFactory.setXmlPullParser(file.getXml());
      cfgFileMgr.setActiveConfigAndUpdateUI(false, file);
    }

    OpModeRegister userOpModeRegister = createOpModeRegister();
    eventLoop = new FtcEventLoop(hardwareFactory, userOpModeRegister, callback, this);
    FtcEventLoopIdle idleLoop = new FtcEventLoopIdle(hardwareFactory, userOpModeRegister, callback, this);

    controllerService.setCallback(callback);
    controllerService.setupRobot(eventLoop, idleLoop, runOnComplete);

    passReceivedUsbAttachmentsToEventLoop();
    AndroidBoard.showErrorIfUnknownControlHub();
  }

  protected OpModeRegister createOpModeRegister() {
    return new FtcOpModeRegister();
  }

  private void shutdownRobot() {
    if (controllerService != null) controllerService.shutdownRobot();
  }

  private void requestRobotRestart() {
    AppUtil.getInstance().showToast(UILocation.BOTH, AppUtil.getDefContext().getString(R.string.toastRestartingRobot));
    //
    RobotLog.clearGlobalErrorMsg();
    RobotLog.clearGlobalWarningMsg();
    shutdownRobot();
    requestRobotSetup(new Runnable() {
      @Override public void run() {
        showRestartRobotCompleteToast(R.string.toastRestartRobotComplete);
        }
      });
  }

  private void showRestartRobotCompleteToast(@StringRes int resid) {
    AppUtil.getInstance().showToast(UILocation.BOTH, AppUtil.getDefContext().getString(resid));
  }

  private void checkPreferredChannel() {
    // For P2P network, check to see what preferred channel is.
    if (networkType ==  NetworkType.WIFIDIRECT) {
      int prefChannel = preferencesHelper.readInt(getString(com.qualcomm.ftccommon.R.string.pref_wifip2p_channel), -1);
      if (prefChannel == -1) {
        prefChannel = 0;
        RobotLog.vv(TAG, "pref_wifip2p_channel: No preferred channel defined. Will use a default value of %d", prefChannel);
      } else {
        RobotLog.vv(TAG, "pref_wifip2p_channel: Found existing preferred channel (%d).", prefChannel);
      }

      // attempt to set the preferred channel.
      RobotLog.vv(TAG, "pref_wifip2p_channel: attempting to set preferred channel...");
      wifiDirectChannelChanger = new WifiDirectChannelChanger();
      wifiDirectChannelChanger.changeToChannel(prefChannel);
    }
  }

  protected void hittingMenuButtonBrightensScreen() {
    ActionBar actionBar = getActionBar();
    if (actionBar != null) {
      actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
        @Override
        public void onMenuVisibilityChanged(boolean isVisible) {
          if (isVisible) {
            dimmer.handleDimTimer();
          }
        }
      });
    }
  }

  protected class SharedPreferencesListener implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      if (key.equals(context.getString(R.string.pref_app_theme))) {
        ThemedActivity.restartForAppThemeChange(getTag(), getString(R.string.appThemeChangeRestartNotifyRC));
      } else if (key.equals(context.getString(R.string.pref_wifi_automute))) {
        if (preferencesHelper.readBoolean(context.getString(R.string.pref_wifi_automute), false)) {
          initWifiMute(true);
        } else {
          initWifiMute(false);
        }
      }
    }
  }

  protected void initWifiMute(boolean enable) {
    if (enable) {
      wifiMuteStateMachine = new WifiMuteStateMachine();
      wifiMuteStateMachine.initialize();
      wifiMuteStateMachine.start();

      motionDetection = new MotionDetection(2.0, 10);
      motionDetection.startListening();
      motionDetection.registerListener(new MotionDetection.MotionDetectionListener() {
        @Override
        public void onMotionDetected(double vector)
        {
          wifiMuteStateMachine.consumeEvent(WifiMuteEvent.USER_ACTIVITY);
        }
      });
    } else {
      wifiMuteStateMachine.stop();
      wifiMuteStateMachine = null;
      motionDetection.stopListening();
      motionDetection.purgeListeners();
      motionDetection = null;
    }
  }

  @Override
  public void onUserInteraction() {
    if (wifiMuteStateMachine != null) {
      wifiMuteStateMachine.consumeEvent(WifiMuteEvent.USER_ACTIVITY);
    }
  }

  // DRIVER STATION
  //protected View batteryInfo;
  protected Button buttonAutonomous;
  protected View buttonInit;
  protected View buttonInitStop;
  protected View buttonStart;
  protected ImageButton buttonStop;
  protected Button buttonTeleOp;
  protected View controlPanelBack;
  //protected ImageView cameraStreamImageView;
  //protected LinearLayout cameraStreamLayout;
  //protected boolean cameraStreamOpen;
  protected View chooseOpModePrompt;
  //protected boolean clientConnected;
  //protected String connectionOwner;
  //protected String connectionOwnerPassword;
  //protected View controlPanelBack;
  protected TextView currentOpModeName;
  protected View timerAndTimerSwitch;

  protected TextView systemTelemetry;
  protected int systemTelemetryOriginalColor;
  protected TextView textTelemetry;

  protected UIState uiState;

  protected OpModeMeta queuedOpMode;
  protected final OpModeMeta defaultOpMode;

  public FtcRobotControllerActivity() {
    final OpModeMeta queuedOpModeWhenMuted = new OpModeMeta.Builder().setName(".Stop.Robot.").build();
    this.defaultOpMode = queuedOpModeWhenMuted;
    this.queuedOpMode = queuedOpModeWhenMuted;
  }

  public void driverStationOnCreate() {
    this.timerAndTimerSwitch = findViewById(R.id.timerAndTimerSwitch);
    this.buttonAutonomous = (Button) findViewById(R.id.buttonAutonomous);
    this.buttonTeleOp = (Button) findViewById(R.id.buttonTeleOp);
    this.currentOpModeName = (TextView) findViewById(R.id.currentOpModeName);
    this.chooseOpModePrompt = findViewById(R.id.chooseOpModePrompt);
    this.buttonInit = findViewById(R.id.buttonInit);
    this.buttonInitStop = findViewById(R.id.buttonInitStop);
    this.buttonStart = findViewById(R.id.buttonStart);
    this.buttonStop = (ImageButton) findViewById(R.id.buttonStop);
    this.controlPanelBack = findViewById(R.id.controlPanel);

    this.textTelemetry = (TextView) findViewById(R.id.textTelemetry);
    //TextView textView = (TextView) findViewById(R.id.textSystemTelemetry);
    //this.systemTelemetry = textView;
    //this.systemTelemetryOriginalColor = textView.getCurrentTextColor();

    handleDefaultOpModeInitOrStart(false);
  }

  //region Gamepad events
  @Override
  public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
    eventLoop.updateLocalGamepad(keyEvent);
    if (Gamepad.isGamepadDevice(keyEvent.getDeviceId())) {
      //this.gamepadManager.handleGamepadEvent(keyEvent);
      eventLoop.updateLocalGamepad(keyEvent);
      return true;
    }
    return super.dispatchKeyEvent(keyEvent);
  }

  @Override
  public boolean dispatchGenericMotionEvent(final MotionEvent motionEvent) {
    if (Gamepad.isGamepadDevice(motionEvent.getDeviceId())) {
      //this.gamepadManager.handleGamepadEvent(motionEvent);
      eventLoop.updateLocalGamepad(motionEvent);
      return true;
    }
    return super.dispatchGenericMotionEvent(motionEvent);
  }
  //endregion

  //region Button clicks
  public void onClickButtonAutonomous(View view) {
    showOpModeDialog(filterOpModes(new Predicate<OpModeMeta>() {
      public boolean test(OpModeMeta opModeMeta) {
        return opModeMeta.flavor == OpModeMeta.Flavor.AUTONOMOUS;
      }
    }), R.string.opmodeDialogTitleAutonomous);
  }

  public void onClickButtonTeleOp(View view) {
    showOpModeDialog(filterOpModes(new Predicate<OpModeMeta>() {
      public boolean test(OpModeMeta opModeMeta) {
        return opModeMeta.flavor == OpModeMeta.Flavor.TELEOP;
      }
    }), R.string.opmodeDialogTitleTeleOp);
  }

  public void onClickButtonInit(final View view) {
    this.handleOpModeInit();
  }

  public void onClickButtonStart(final View view) {
    this.handleOpModeStart();
  }

  public void onClickButtonStop(final View view) {
    this.handleOpModeStop();
  }

  public void onClickTimer(final View view) {
    //this.enableAndResetTimer(this.opModeUseTimer ^= true);
  }
  //endregion

  //region OpMode lifecycle
  protected void handleDefaultOpModeInitOrStart(final boolean b) {

    if (this.isDefaultOpMode(this.queuedOpMode)) {
      this.uiWaitingForOpModeSelection();
    }
    else {
      this.uiWaitingForInitEvent();
      /*
      if (!b) {
        this.runDefaultOpMode();
      }
      */
    }
  }

  protected void handleOpModeInit() {
    if (this.uiState != UIState.WAITING_FOR_INIT_EVENT) {
      return;
    }
    this.traceUiStateChange("ui:uiWaitingForAck", UIState.WAITING_FOR_ACK);
    //this.sendMatchNumberIfNecessary();
    //this.networkConnectionHandler.sendCommand(new Command("CMD_INIT_OP_MODE", this.queuedOpMode.name));
    /*if (!this.queuedOpMode.name.equals(this.defaultOpMode.name)) {
      this.wifiMuteStateMachine.consumeEvent((Event)WifiMuteEvent.RUNNING_OPMODE);
    }*/
    //this.hideCameraStream();
    if (isDefaultOpMode(queuedOpMode)) {
      //this.androidTextToSpeech.stop();
      //stopKeepAlives();
      runOnUiThread(new Runnable() {
        public void run() {
          //FtcDriverStationActivityBase.this.telemetryMode = Telemetry.DisplayFormat.CLASSIC;
          //FtcDriverStationActivityBase.this.textTelemetry.setTypeface(Typeface.DEFAULT);
        }
      });
      handleDefaultOpModeInitOrStart(false);
    } else {
      callback.clearUserTelemetry();
      //startKeepAlives();
      if (setQueuedOpModeIfDifferent(queuedOpMode)) {
        RobotLog.vv(TAG, "timer: init new opmode");
        //enableAndResetTimerForQueued();
      } else if (/*this.opModeCountDown.isEnabled()*/false) {
        RobotLog.vv(TAG, "timer: init w/ timer enabled");
        //this.opModeCountDown.resetCountdown();
      } else {
        RobotLog.vv(TAG, "timer: init w/o timer enabled");
      }

      eventLoop.getOpModeManager().initActiveOpMode(queuedOpMode.name);
      uiWaitingForStartEvent();
    }
  }

  protected void handleOpModeStart() {
    if (this.uiState != UIState.WAITING_FOR_START_EVENT) {
      return;
    }
    this.traceUiStateChange("ui:uiWaitingForAck", UIState.WAITING_FOR_ACK);

    if (this.isDefaultOpMode(queuedOpMode)) {
      //this.androidTextToSpeech.stop();
      //this.stopKeepAlives();
      this.handleDefaultOpModeInitOrStart(true);
    }
    else {
      if (this.setQueuedOpModeIfDifferent(queuedOpMode)) {
        //RobotLog.vv("DriverStation", "timer: started new opmode: auto-initing timer");
        //this.enableAndResetTimerForQueued();
      }
      eventLoop.getOpModeManager().startActiveOpMode();
      this.uiWaitingForStopEvent();
      /*
      if (this.opModeUseTimer) {
        this.opModeCountDown.start();
      }
      else {
        this.stopTimerAndReset();
      }
      */
    }
  }

  protected void handleOpModeStop() {
    if (this.uiState != UIState.WAITING_FOR_START_EVENT && this.uiState != UIState.WAITING_FOR_STOP_EVENT) {
      return;
    }
    this.traceUiStateChange("ui:uiWaitingForAck", UIState.WAITING_FOR_ACK);
    //this.clearMatchNumberIfNecessary();
    this.initDefaultOpMode();
  }
  //endregion

  //region OpMode selection
  protected void showOpModeDialog(final List<OpModeMeta> opModes, final int title) {
    //this.stopTimerPreservingRemainingTime();
    this.initDefaultOpMode();
    final OpModeSelectionDialogFragment opModeSelectionDialogFragment = new OpModeSelectionDialogFragment();
    opModeSelectionDialogFragment.setOnSelectionDialogListener((OpModeSelectionDialogFragment.OpModeSelectionDialogListener)this);
    opModeSelectionDialogFragment.setOpModes((List)opModes);
    opModeSelectionDialogFragment.setTitle(title);
    opModeSelectionDialogFragment.show(this.getFragmentManager(), "op_mode_selection");
  }

  protected void initDefaultOpMode() {
    //this.networkConnectionHandler.sendCommand(new Command("CMD_INIT_OP_MODE", this.defaultOpMode.name));
    eventLoop.getOpModeManager().initActiveOpMode(defaultOpMode.name);
    handleDefaultOpModeInitOrStart(false);
  }

  public List<OpModeMeta> filterOpModes(Predicate<OpModeMeta> predicate) {
    LinkedList linkedList = new LinkedList();
    for (OpModeMeta next : RegisteredOpModes.getInstance().getOpModes()) {
      if (predicate.test(next)) {
        linkedList.add(next);
      }
    }
    return linkedList;
  }

  @Override
  public void onOpModeSelectionClick(OpModeMeta var1) {
    this.handleOpModeQueued(var1);
  }

  protected void handleOpModeQueued(final OpModeMeta queuedOpModeIfDifferent) {
    if (this.setQueuedOpModeIfDifferent(queuedOpModeIfDifferent)) {
      //this.enableAndResetTimerForQueued();
    }
    this.uiWaitingForInitEvent();
  }

  protected boolean setQueuedOpModeIfDifferent(final String s) {
    return this.setQueuedOpModeIfDifferent(this.getOpModeMeta(s));
  }

  protected boolean setQueuedOpModeIfDifferent(final OpModeMeta queuedOpMode) {
    if (!queuedOpMode.name.equals(this.queuedOpMode.name)) {
      this.queuedOpMode = queuedOpMode;
      this.showQueuedOpModeName();
      return true;
    }
    return false;
  }

  protected void showQueuedOpModeName() {
    this.showQueuedOpModeName(this.queuedOpMode);
  }

  protected void showQueuedOpModeName(final OpModeMeta opModeMeta) {
    if (this.isDefaultOpMode(opModeMeta)) {
      this.setVisibility((View)this.currentOpModeName, 8);
      this.setVisibility(this.chooseOpModePrompt, 0);
    }
    else {
      this.setTextView(this.currentOpModeName, opModeMeta.name);
      this.setVisibility((View)this.currentOpModeName, 0);
      this.setVisibility(this.chooseOpModePrompt, 8);
    }
  }

  protected boolean isDefaultOpMode(final OpModeMeta opModeMeta) {
    return this.isDefaultOpMode(opModeMeta.name);
  }

  protected boolean isDefaultOpMode(final String anObject) {
    return this.defaultOpMode.name.equals(anObject);
  }

  protected OpModeMeta getOpModeMeta(final String anObject) {
    synchronized (RegisteredOpModes.getInstance().getOpModes()) {
      for (final OpModeMeta opModeMeta : RegisteredOpModes.getInstance().getOpModes()) {
        if (opModeMeta.name.equals(anObject)) {
          return opModeMeta;
        }
      }
      // monitorexit(this.opModes)
      return new OpModeMeta.Builder().setName(anObject).build();
    }
  }
  //endregion

  //region Telemetry

  /*
  protected void clearSystemTelemetry() {
    this.setVisibility((View)this.systemTelemetry, 8);
    this.setTextView(this.systemTelemetry, "");
    this.setTextColor(this.systemTelemetry, this.systemTelemetryOriginalColor);
    RobotLog.clearGlobalErrorMsg();
    RobotLog.clearGlobalWarningMsg();
  }
  */

  //endregion

  //region UI state
  protected void traceUiStateChange(final String s, final UIState uiState) {
    RobotLog.vv("DriverStation", s);
    this.uiState = uiState;
    //this.setTextView(this.textDsUiStateIndicator, uiState.indicator);
    this.invalidateOptionsMenu();
  }

  protected void uiWaitingForOpModeSelection() {
    this.traceUiStateChange("ui:uiWaitingForOpModeSelection", UIState.WAITING_FOR_OPMODE_SELECTION);
    this.checkConnectedEnableBrighten(ControlPanelBack.DIM);
    this.dimControlPanelBack();
    this.enableAndBrightenOpModeMenu();
    this.showQueuedOpModeName();
    this.disableOpModeControls();
  }

  protected void uiWaitingForInitEvent() {
    this.traceUiStateChange("ui:uiWaitingForInitEvent", UIState.WAITING_FOR_INIT_EVENT);
    this.checkConnectedEnableBrighten(ControlPanelBack.BRIGHT);
    this.brightenControlPanelBack();
    this.showQueuedOpModeName();
    this.enableAndBrightenOpModeMenu();
    this.setEnabled(this.buttonInit, true);
    this.setVisibility(this.buttonInit, 0);
    this.setVisibility(this.buttonStart, 4);
    this.setVisibility((View)this.buttonStop, 4);
    this.setVisibility(this.buttonInitStop, 4);
    this.setTimerButtonEnabled(true);
    //this.setVisibility(this.timerAndTimerSwitch, 0);
    //this.hideCameraStream();
  }

  protected void uiWaitingForStartEvent() {
    this.traceUiStateChange("ui:uiWaitingForStartEvent", UIState.WAITING_FOR_START_EVENT);
    this.checkConnectedEnableBrighten(ControlPanelBack.BRIGHT);
    this.showQueuedOpModeName();
    this.enableAndBrightenOpModeMenu();
    this.setVisibility(this.buttonStart, 0);
    this.setVisibility(this.buttonInit, 4);
    this.setVisibility((View)this.buttonStop, 4);
    this.setVisibility(this.buttonInitStop, 0);
    this.setTimerButtonEnabled(true);
    //this.setVisibility(this.timerAndTimerSwitch, 0);
    //this.hideCameraStream();
  }

  protected void uiWaitingForStopEvent() {
    this.traceUiStateChange("ui:uiWaitingForStopEvent", UIState.WAITING_FOR_STOP_EVENT);
    this.checkConnectedEnableBrighten(ControlPanelBack.BRIGHT);
    this.showQueuedOpModeName();
    this.enableAndBrightenOpModeMenu();
    this.setVisibility((View)this.buttonStop, 0);
    this.setVisibility(this.buttonInit, 4);
    this.setVisibility(this.buttonStart, 4);
    this.setVisibility(this.buttonInitStop, 4);
    this.setTimerButtonEnabled(false);
    //this.setVisibility(this.timerAndTimerSwitch, 0);
    //this.hideCameraStream();
  }
  //endregion

  //region UI util
  protected void disableOpModeControls() {
    this.setEnabled(this.buttonInit, false);
    this.setVisibility(this.buttonInit, 0);
    this.setVisibility(this.buttonStart, 4);
    this.setVisibility((View)this.buttonStop, 4);
    this.setVisibility(this.buttonInitStop, 4);
    //this.setVisibility(this.timerAndTimerSwitch, 4);
    //this.hideCameraStream();
  }

  public void setTimerButtonEnabled(boolean z) {
    setEnabled(this.timerAndTimerSwitch, z);
    setEnabled(findViewById(R.id.timerBackground), z);
    setEnabled(findViewById(R.id.timerStopWatch), z);
    setEnabled(findViewById(R.id.timerText), z);
    setEnabled(findViewById(R.id.timerSwitchOn), z);
    setEnabled(findViewById(R.id.timerSwitchOff), z);
  }

  public void setControlPanelBack(ControlPanelBack controlPanelBack2) {
    if (controlPanelBack2 == ControlPanelBack.DIM) {
      dimControlPanelBack();
    } else if (controlPanelBack2 == ControlPanelBack.BRIGHT) {
      brightenControlPanelBack();
    }
  }

  protected void dimControlPanelBack() {
    this.setOpacity(this.controlPanelBack, 0.3f);
  }

  protected void brightenControlPanelBack() {
    this.setOpacity(this.controlPanelBack, 1.0f);
  }

  protected void checkConnectedEnableBrighten(final ControlPanelBack controlPanelBack) {
    if (true) {
      RobotLog.vv("DriverStation", "auto-rebrightening for connected state");
      this.enableAndBrightenForConnected(controlPanelBack);
      //this.setClientConnected(true);
      //this.requestUIState();
    }
  }

  protected void enableAndBrightenForConnected(final ControlPanelBack controlPanelBack) {
    this.setControlPanelBack(controlPanelBack);
    //this.setOpacity(this.wifiInfo, 1.0f);
    //this.setOpacity(this.batteryInfo, 1.0f);
    this.enableAndBrightenOpModeMenu();
  }

  protected void enableAndBrightenOpModeMenu() {
    this.enableAndBrighten((View)this.buttonAutonomous);
    this.enableAndBrighten((View)this.buttonTeleOp);
    this.setOpacity((View)this.currentOpModeName, 1.0f);
    this.setOpacity(this.chooseOpModePrompt, 1.0f);
  }

  protected void enableAndBrighten(final View view) {
    this.setOpacity(view, 1.0f);
    this.setEnabled(view, true);
  }

  public void setEnabled(final View view, final boolean z) {
    runOnUiThread(new Runnable() {
      public void run() {
        view.setEnabled(z);
      }
    });
  }

  public void setVisibility(final View view, final int i) {
    runOnUiThread(new Runnable() {
      public void run() {
        view.setVisibility(i);
      }
    });
  }

  public void setTextColor(final TextView textView, final int i) {
    runOnUiThread(new Runnable() {
      public void run() {
        textView.setTextColor(i);
      }
    });
  }

  public void setTextView(final TextView textView, final CharSequence charSequence) {
    runOnUiThread(new Runnable() {
      public void run() {
        textView.setText(charSequence);
      }
    });
  }

  public void setOpacity(final View view, final float f) {
    runOnUiThread(new Runnable() {
      public void run() {
        view.setAlpha(f);
      }
    });
  }
  //endregion

  protected enum UIState {
    UNKNOWN("U"),
    CANT_CONTINUE("E"),
    DISCONNECTED("X"),
    CONNNECTED("C"),
    WAITING_FOR_OPMODE_SELECTION("M"),
    WAITING_FOR_INIT_EVENT("K"),
    WAITING_FOR_ACK("KW"),
    WAITING_FOR_START_EVENT("S"),
    WAITING_FOR_STOP_EVENT("P"),
    ROBOT_STOPPED("Z");

    public final String indicator;

    private UIState(String str) {
      this.indicator = str;
    }
  }

  protected enum ControlPanelBack {
    NO_CHANGE,
    DIM,
    BRIGHT
  }
}
