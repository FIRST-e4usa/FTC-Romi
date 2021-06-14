package com.qualcomm.ftcdriverstation;

import org.firstinspires.ftc.robotcore.external.android.*;

import com.google.gson.reflect.TypeToken;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.robot.*;
import org.firstinspires.ftc.ftccommon.internal.*;

import android.annotation.SuppressLint;
import android.app.admin.NetworkEvent;
import android.content.*;
import org.firstinspires.ftc.robotcore.internal.opmode.*;
import com.qualcomm.robotcore.hardware.*;
import com.google.gson.*;
import com.qualcomm.robotcore.hardware.configuration.*;
import org.firstinspires.ftc.robotcore.internal.system.*;
import org.firstinspires.ftc.robotcore.external.*;
import android.content.res.*;
import android.preference.*;
import org.firstinspires.ftc.robotcore.external.stream.*;
import org.firstinspires.ftc.robotcore.internal.ui.*;
import android.graphics.drawable.*;
import org.firstinspires.ftc.driverstation.internal.*;
import org.firstinspires.ftc.robotcore.internal.network.*;
import android.net.wifi.*;
import org.firstinspires.ftc.ftccommon.external.*;
import android.hardware.input.InputManager;

import com.qualcomm.robotcore.wifi.*;
import android.view.*;
import android.app.*;
import com.qualcomm.ftccommon.configuration.*;
import com.qualcomm.ftccommon.*;
import android.hardware.input.*;
import android.os.*;
import com.qualcomm.robotcore.exception.*;
import android.text.*;
import android.widget.*;
import com.qualcomm.robotcore.util.*;
import android.graphics.*;
import com.qualcomm.robotcore.robocol.*;
import java.util.*;

public abstract class FtcDriverStationActivityBase extends ThemedActivity implements NetworkConnection.NetworkConnectionCallback, RecvLoopRunnable.RecvLoopCallback, SharedPreferences.OnSharedPreferenceChangeListener, OpModeSelectionDialogFragment.OpModeSelectionDialogListener, BatteryChecker.BatteryWatcher, PeerStatusCallback, WifiMuteStateMachine.Callback
{
   protected static final float FULLY_OPAQUE = 1.0f;
   protected static final int MATCH_NUMBER_LOWER_BOUND = 0;
   protected static final int MATCH_NUMBER_UPPER_BOUND = 1000;
   protected static final float PARTLY_OPAQUE = 0.3f;
   public static final String TAG = "DriverStation";
   protected static final boolean debugBattery = false;
   protected static boolean permissionsValidated = false;
   protected double V12BatteryMin;
   protected String V12BatteryMinString;
   protected TextView activeConfigText;
   private final AndroidTextToSpeech androidTextToSpeech;
   protected AppUtil appUtil;
   protected BatteryChecker batteryChecker;
   protected View batteryInfo;
   protected Button buttonAutonomous;
   protected View buttonInit;
   protected View buttonInitStop;
   protected ImageButton buttonMenu;
   protected View buttonStart;
   protected ImageButton buttonStop;
   protected Button buttonTeleOp;
   protected ImageView cameraStreamImageView;
   protected LinearLayout cameraStreamLayout;
   protected boolean cameraStreamOpen;
   protected View chooseOpModePrompt;
   protected boolean clientConnected;
   protected String connectionOwner;
   protected String connectionOwnerPassword;
   protected Context context;
   protected View controlPanelBack;
   protected TextView currentOpModeName;
   protected boolean debugLogging;
   protected final OpModeMeta defaultOpMode;
   protected FtcDriverStationActivityBase.DeviceNameManagerCallback deviceNameManagerCallback;
   protected StartResult deviceNameManagerStartResult;
   protected boolean disconnectFromPeerOnActivityStop;
   protected ImageView dsBatteryIcon;
   protected TextView dsBatteryInfo;
   protected Map<GamepadUser, GamepadIndicator> gamepadIndicators;
   protected GamepadManager gamepadManager;
   protected Heartbeat heartbeatRecv;
   protected ImmersiveMode immersion;
   protected ElapsedTime lastUiUpdate;
   private InputManager mInputManager;
   protected NetworkConnectionHandler networkConnectionHandler;
   protected FtcDriverStationActivityBase.OpModeCountDownTimer opModeCountDown;
   protected boolean opModeUseTimer;
   protected List<OpModeMeta> opModes;
   protected RollingAverage pingAverage;
   protected StartResult prefRemoterStartResult;
   protected SharedPreferences preferences;
   protected PreferencesHelper preferencesHelper;
   protected boolean processUserActivity;
   protected OpModeMeta queuedOpMode;
   protected OpModeMeta queuedOpModeWhenMuted;
   protected View rcBatteryContainer;
   protected ImageView rcBatteryIcon;
   protected TextView rcBatteryTelemetry;
   protected boolean rcHasIndependentBattery;
   protected TextView robotBatteryMinimum;
   protected TextView robotBatteryTelemetry;
   protected RobotConfigFileManager robotConfigFileManager;
   protected RobotState robotState;
   protected TextView systemTelemetry;
   protected int systemTelemetryOriginalColor;
   protected Telemetry.DisplayFormat telemetryMode;
   protected TextView textBytesPerSecond;
   protected TextView textDeviceName;
   protected TextView textDsUiStateIndicator;
   protected TextView textPingStatus;
   protected TextView textTelemetry;
   protected TextView textWifiChannel;
   protected TextView textWifiDirectStatus;
   protected boolean textWifiDirectStatusShowingRC;
   protected View timerAndTimerSwitch;
   protected FtcDriverStationActivityBase.UIState uiState;
   protected Thread uiThread;
   protected Utility utility;
   protected View wifiInfo;
   protected WifiMuteStateMachine wifiMuteStateMachine;

   protected enum ControlPanelBack {
      NO_CHANGE,
      DIM,
      BRIGHT
   }

   protected class DeviceNameManagerCallback implements DeviceNameListener {
      protected DeviceNameManagerCallback() {
      }

      public void onDeviceNameChanged(String str) {
         FtcDriverStationActivityBase.this.displayDeviceName(str);
      }
   }


   public FtcDriverStationActivityBase() {
      super();
      this.gamepadIndicators = new HashMap<GamepadUser, GamepadIndicator>();
      this.heartbeatRecv = new Heartbeat();
      final OpModeMeta queuedOpModeWhenMuted = new OpModeMeta.Builder().setName(".Stop.Robot.").build();
      this.defaultOpMode = queuedOpModeWhenMuted;
      this.queuedOpMode = queuedOpModeWhenMuted;
      this.queuedOpModeWhenMuted = queuedOpModeWhenMuted;
      this.opModes = new LinkedList<OpModeMeta>();
      this.opModeUseTimer = false;
      this.pingAverage = new RollingAverage(10);
      this.lastUiUpdate = new ElapsedTime();
      this.uiState = FtcDriverStationActivityBase.UIState.UNKNOWN;
      this.telemetryMode = Telemetry.DisplayFormat.CLASSIC;
      this.debugLogging = false;
      this.networkConnectionHandler = NetworkConnectionHandler.getInstance();
      this.appUtil = AppUtil.getInstance();
      this.deviceNameManagerStartResult = new StartResult();
      this.prefRemoterStartResult = new StartResult();
      this.deviceNameManagerCallback = new FtcDriverStationActivityBase.DeviceNameManagerCallback(/*this*/);
      this.processUserActivity = false;
      this.disconnectFromPeerOnActivityStop = true;
      this.androidTextToSpeech = new AndroidTextToSpeech();
   }

   private void checkRcIndependentBattery(final SharedPreferences sharedPreferences) {
      this.rcHasIndependentBattery = sharedPreferences.getBoolean(this.getString(R.string.pref_has_independent_phone_battery_rc), true);
   }

   private String getBestRobotControllerName() {
      return this.networkConnectionHandler.getConnectionOwnerName();
   }

   private CallbackResult handleCommandSetTelemetryDisplayFormat(final String s) {
      try {
         final Telemetry.DisplayFormat value = Telemetry.DisplayFormat.valueOf(s);
         if (value != this.telemetryMode) {
            final int n = this.telemetryMode.ordinal();
            if (n != 1) {
               if (n == 2 || n == 3) {
                  this.textTelemetry.setTypeface(Typeface.DEFAULT);
               }
            }
            else {
               this.textTelemetry.setTypeface(Typeface.MONOSPACE);
            }
         }
         this.telemetryMode = value;
         return CallbackResult.HANDLED;
      }
      catch (IllegalArgumentException ex) {
         return CallbackResult.HANDLED;
      }
   }

   private CallbackResult handleCommandStartProgramAndManageResp(final String s) {
      if (s != null && !s.isEmpty()) {
         final Intent intent = new Intent(AppUtil.getDefContext(), (Class)ProgramAndManageActivity.class);
         intent.putExtra("RC_WEB_INFO", s);
         this.startActivityForResult(intent, LaunchActivityConstantsList.RequestCode.PROGRAM_AND_MANAGE.ordinal());
      }
      return CallbackResult.HANDLED;
   }

   private CallbackResult handleCommandTextToSpeech(String text) {
      final RobotCoreCommandList.TextToSpeech deserialize = RobotCoreCommandList.TextToSpeech.deserialize(text);
      text = deserialize.getText();
      final String languageCode = deserialize.getLanguageCode();
      final String countryCode = deserialize.getCountryCode();
      if (languageCode != null && !languageCode.isEmpty()) {
         if (countryCode != null && !countryCode.isEmpty()) {
            this.androidTextToSpeech.setLanguageAndCountry(languageCode, countryCode);
         }
         else {
            this.androidTextToSpeech.setLanguage(languageCode);
         }
      }
      this.androidTextToSpeech.speak(text);
      return CallbackResult.HANDLED;
   }

   private void onPeersAvailableSoftAP() {
      if (this.networkConnectionHandler.connectionMatches(this.getString(R.string.connection_owner_default))) {
         this.showWifiStatus(false, this.getString(R.string.wifiStatusNotPaired));
      }
      else {
         this.showWifiStatus(false, this.getString(R.string.wifiStatusSearching));
      }
      this.networkConnectionHandler.handlePeersAvailable();
   }

   private void onPeersAvailableWifiDirect() {
      if (this.networkConnectionHandler.connectingOrConnected()) {
         return;
      }
      this.onPeersAvailableSoftAP();
   }

   public static void setPermissionsValidated() {
      FtcDriverStationActivityBase.permissionsValidated = true;
   }

   private void updateRcBatteryIndependence(final SharedPreferences sharedPreferences) {
      this.updateRcBatteryIndependence(sharedPreferences, true);
   }

   private void updateRcBatteryIndependence(final SharedPreferences sharedPreferences, final boolean b) {
      this.checkRcIndependentBattery(sharedPreferences);
      RobotLog.vv("DriverStation", "updateRcBatteryIndependence(%s)", new Object[] { this.rcHasIndependentBattery });
      if (b) {
         this.displayRcBattery(this.rcHasIndependentBattery);
      }
   }

   protected void assertUiThread() {
      Assert.assertTrue(Thread.currentThread() == this.uiThread);
   }

   protected void assumeClientConnect(final FtcDriverStationActivityBase.ControlPanelBack controlPanelBack) {
      RobotLog.vv("DriverStation", "Assuming client connected");
      if (this.uiState == FtcDriverStationActivityBase.UIState.UNKNOWN || this.uiState == FtcDriverStationActivityBase.UIState.DISCONNECTED || this.uiState == FtcDriverStationActivityBase.UIState.CANT_CONTINUE) {
         this.setClientConnected(true);
         this.uiRobotControllerIsConnected(controlPanelBack);
      }
   }

   protected void assumeClientConnectAndRefreshUI(final FtcDriverStationActivityBase.ControlPanelBack controlPanelBack) {
      this.assumeClientConnect(controlPanelBack);
      this.requestUIState();
   }

   protected void assumeClientDisconnect() {
      RobotLog.vv("DriverStation", "Assuming client disconnected");
      this.setClientConnected(false);
      this.enableAndResetTimer(false);
      this.opModeCountDown.disable();
      this.queuedOpMode = this.defaultOpMode;
      this.opModes.clear();
      this.pingStatus(2131624391);
      this.stopKeepAlives();
      this.networkConnectionHandler.clientDisconnect();
      RobocolParsableBase.initializeSequenceNumber(10000);
      RobotLog.clearGlobalErrorMsg();
      this.setRobotState(RobotState.UNKNOWN);
      this.uiRobotControllerIsDisconnected();
   }

   protected void brightenControlPanelBack() {
      this.setOpacity(this.controlPanelBack, 1.0f);
   }

   protected void checkConnectedEnableBrighten(final FtcDriverStationActivityBase.ControlPanelBack controlPanelBack) {
      if (!this.clientConnected) {
         RobotLog.vv("DriverStation", "auto-rebrightening for connected state");
         this.enableAndBrightenForConnected(controlPanelBack);
         this.setClientConnected(true);
         this.requestUIState();
      }
   }

   protected abstract void clearMatchNumber();

   protected void clearMatchNumberIfNecessary() {
      if (this.queuedOpMode.flavor == OpModeMeta.Flavor.TELEOP) {
         this.clearMatchNumber();
      }
   }

   protected void clearSystemTelemetry() {
      this.setVisibility((View)this.systemTelemetry, 8);
      this.setTextView(this.systemTelemetry, "");
      this.setTextColor(this.systemTelemetry, this.systemTelemetryOriginalColor);
      RobotLog.clearGlobalErrorMsg();
      RobotLog.clearGlobalWarningMsg();
   }

   protected void clearUserTelemetry() {
      this.setTextView(this.textTelemetry, "");
   }

   public CallbackResult commandEvent(final Command command) {
      final CallbackResult not_HANDLED = CallbackResult.NOT_HANDLED;
      CallbackResult callbackResult = null;
      try {
         final String name = command.getName();
         final String extra = command.getExtra();
         switch (name) {
            default:
               callbackResult = not_HANDLED;
               break;
            case "CMD_SET_TELEM_DISPL_FORMAT":
               callbackResult = this.handleCommandSetTelemetryDisplayFormat(extra);
               break;
            case "CMD_TEXT_TO_SPEECH":
               callbackResult = this.handleCommandTextToSpeech(extra);
               break;
            case "CMD_RECEIVE_FRAME_CHUNK":
               callbackResult = CameraStreamClient.getInstance().handleReceiveFrameChunk(extra);
               break;
            case "CMD_RECEIVE_FRAME_BEGIN":
               callbackResult = CameraStreamClient.getInstance().handleReceiveFrameBegin(extra);
               break;
            case "CMD_STREAM_CHANGE":
               callbackResult = CameraStreamClient.getInstance().handleStreamChange(extra);
               break;
            case "CMD_STOP_PLAYING_SOUNDS":
               callbackResult = SoundPlayer.getInstance().handleCommandStopPlayingSounds(command);
               break;
            case "CMD_REQUEST_SOUND":
               callbackResult = SoundPlayer.getInstance().handleCommandRequestSound(command);
               break;
            case "CMD_PLAY_SOUND":
               callbackResult = SoundPlayer.getInstance().handleCommandPlaySound(extra);
               break;
            case "CMD_ROBOT_CONTROLLER_PREFERENCE":
               callbackResult = PreferenceRemoterDS.getInstance().handleCommandRobotControllerPreference(extra);
               break;
            case "CMD_START_DS_PROGRAM_AND_MANAGE_RESP":
               callbackResult = this.handleCommandStartProgramAndManageResp(extra);
               break;
            case "CMD_DISMISS_ALL_DIALOGS":
               callbackResult = this.handleCommandDismissAllDialogs(command);
               break;
            case "CMD_DISMISS_DIALOG":
               callbackResult = this.handleCommandDismissDialog(command);
               break;
            case "CMD_SHOW_DIALOG":
               callbackResult = this.handleCommandShowDialog(extra);
               break;
            case "CMD_DISMISS_PROGRESS":
               callbackResult = this.handleCommandDismissProgress();
               break;
            case "CMD_SHOW_PROGRESS":
               callbackResult = this.handleCommandShowProgress(extra);
               break;
            case "CMD_SHOW_TOAST":
               callbackResult = this.handleCommandShowToast(extra);
               break;
            case "CMD_NOTIFY_RUN_OP_MODE":
               callbackResult = this.handleCommandNotifyStartOpMode(extra);
               break;
            case "CMD_NOTIFY_INIT_OP_MODE":
               callbackResult = this.handleCommandNotifyInitOpMode(extra);
               break;
            case "CMD_NOTIFY_ACTIVE_CONFIGURATION":
               callbackResult = this.handleCommandNotifyActiveConfig(extra);
               break;
            case "CMD_NOTIFY_USER_DEVICE_LIST":
               callbackResult = this.handleCommandNotifyUserDeviceList(extra);
               break;
            case "CMD_NOTIFY_OP_MODE_LIST":
               callbackResult = this.handleCommandNotifyOpModeList(extra);
               break;
            case "CMD_NOTIFY_ROBOT_STATE":
               callbackResult = this.handleNotifyRobotState(extra);
               break;
         }
      }
      catch (Exception ex) {
         RobotLog.logStackTrace((Throwable)ex);
         callbackResult = not_HANDLED;
      }
      return callbackResult;
   }

   protected void dimAndDisableAllControls() {
      this.dimControlPanelBack();
      this.setOpacity(this.wifiInfo, 0.3f);
      this.setOpacity(this.batteryInfo, 0.3f);
      this.disableAndDimOpModeMenu();
      this.disableOpModeControls();
   }

   protected void dimControlPanelBack() {
      this.setOpacity(this.controlPanelBack, 0.3f);
   }

   protected void disableAndDim(final View view) {
      this.setOpacity(view, 0.3f);
      this.setEnabled(view, false);
   }

   protected void disableAndDimOpModeMenu() {
      this.disableAndDim((View)this.buttonAutonomous);
      this.disableAndDim((View)this.buttonTeleOp);
      this.disableAndDim((View)this.currentOpModeName);
      this.disableAndDim(this.chooseOpModePrompt);
   }

   protected abstract void disableMatchLoggingUI();

   protected void disableOpModeControls() {
      this.setEnabled(this.buttonInit, false);
      this.setVisibility(this.buttonInit, 0);
      this.setVisibility(this.buttonStart, 4);
      this.setVisibility((View)this.buttonStop, 4);
      this.setVisibility(this.buttonInitStop, 4);
      this.setVisibility(this.timerAndTimerSwitch, 4);
      this.hideCameraStream();
   }

   public boolean dispatchGenericMotionEvent(final MotionEvent motionEvent) {
      if (Gamepad.isGamepadDevice(motionEvent.getDeviceId())) {
         this.gamepadManager.handleGamepadEvent(motionEvent);
         return true;
      }
      return super.dispatchGenericMotionEvent(motionEvent);
   }

   public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
      if (Gamepad.isGamepadDevice(keyEvent.getDeviceId())) {
         this.gamepadManager.handleGamepadEvent(keyEvent);
         return true;
      }
      return super.dispatchKeyEvent(keyEvent);
   }

   public void displayDeviceName(final String str) {
      runOnUiThread(new Runnable() {
         public void run() {
            FtcDriverStationActivityBase.this.textDeviceName.setText(str);
         }
      });
   }


   protected void displayRcBattery(final boolean b) {
      final View rcBatteryContainer = this.rcBatteryContainer;
      int visibility;
      if (b) {
         visibility = 0;
      }
      else {
         visibility = 8;
      }
      rcBatteryContainer.setVisibility(visibility);
   }

   protected abstract void doMatchNumFieldBehaviorInit();

   public CallbackResult emptyEvent(final RobocolDatagram robocolDatagram) {
      return CallbackResult.NOT_HANDLED;
   }

   protected void enableAndBrighten(final View view) {
      this.setOpacity(view, 1.0f);
      this.setEnabled(view, true);
   }

   protected void enableAndBrightenForConnected(final FtcDriverStationActivityBase.ControlPanelBack controlPanelBack) {
      this.setControlPanelBack(controlPanelBack);
      this.setOpacity(this.wifiInfo, 1.0f);
      this.setOpacity(this.batteryInfo, 1.0f);
      this.enableAndBrightenOpModeMenu();
   }

   protected void enableAndBrightenOpModeMenu() {
      this.enableAndBrighten((View)this.buttonAutonomous);
      this.enableAndBrighten((View)this.buttonTeleOp);
      this.setOpacity((View)this.currentOpModeName, 1.0f);
      this.setOpacity(this.chooseOpModePrompt, 1.0f);
   }

   protected void enableAndResetTimer(final boolean opModeUseTimer) {
      if (!opModeUseTimer) {
         this.opModeCountDown.disable();
      }
      else {
         this.stopTimerAndReset();
         this.opModeCountDown.enable();
      }
      this.opModeUseTimer = opModeUseTimer;
   }

   protected void enableAndResetTimerForQueued() {
      this.enableAndResetTimer(this.queuedOpMode.flavor == OpModeMeta.Flavor.AUTONOMOUS);
   }

   protected abstract void enableMatchLoggingUI();

   protected void enforcePermissionValidator() {
      if (!FtcDriverStationActivityBase.permissionsValidated) {
         RobotLog.vv("DriverStation", "Redirecting to permission validator");
         this.startActivity(new Intent(AppUtil.getDefContext(), (Class)PermissionValidatorWrapper.class));
         this.finish();
      }
      else {
         RobotLog.vv("DriverStation", "Permissions validated already");
      }
   }

   public List<OpModeMeta> filterOpModes(Predicate<OpModeMeta> predicate) {
      LinkedList linkedList = new LinkedList();
      for (OpModeMeta next : this.opModes) {
         if (predicate.test(next)) {
            linkedList.add(next);
         }
      }
      return linkedList;
   }


   public CallbackResult gamepadEvent(final RobocolDatagram robocolDatagram) {
      return CallbackResult.NOT_HANDLED;
   }

   protected abstract int getMatchNumber() throws NumberFormatException;

   protected OpModeMeta getOpModeMeta(final String anObject) {
      synchronized (this.opModes) {
         for (final OpModeMeta opModeMeta : this.opModes) {
            if (opModeMeta.name.equals(anObject)) {
               return opModeMeta;
            }
         }
         // monitorexit(this.opModes)
         return new OpModeMeta.Builder().setName(anObject).build();
      }
   }

   public abstract View getPopupMenuAnchor();

   public String getTag() {
      return "DriverStation";
   }

   protected CallbackResult handleCommandDismissAllDialogs(final Command command) {
      this.appUtil.dismissAllDialogs(UILocation.ONLY_LOCAL);
      return CallbackResult.HANDLED;
   }

   protected CallbackResult handleCommandDismissDialog(final Command command) {
      this.appUtil.dismissDialog(UILocation.ONLY_LOCAL, RobotCoreCommandList.DismissDialog.deserialize(command.getExtra()));
      return CallbackResult.HANDLED;
   }

   protected CallbackResult handleCommandDismissProgress() {
      this.appUtil.dismissProgress(UILocation.ONLY_LOCAL);
      return CallbackResult.HANDLED;
   }

   public CallbackResult handleCommandNotifyActiveConfig(String str) {
      RobotLog.vv(TAG, "%s.handleCommandRequestActiveConfigResp(%s)", getClass().getSimpleName(), str);
      final RobotConfigFile configFromString = this.robotConfigFileManager.getConfigFromString(str);
      this.robotConfigFileManager.setActiveConfig(configFromString);
      this.appUtil.runOnUiThread(this, new Runnable() {
         public void run() {
            FtcDriverStationActivityBase.this.activeConfigText.setText(configFromString.getName());
         }
      });
      return CallbackResult.HANDLED_CONTINUE;
   }


   public CallbackResult handleCommandNotifyInitOpMode(String str) {
      if (this.uiState == UIState.CANT_CONTINUE) {
         return CallbackResult.HANDLED;
      }
      RobotLog.vv(TAG, "Robot Controller initializing op mode: " + str);
      stopTimerPreservingRemainingTime();
      if (isDefaultOpMode(str)) {
         this.androidTextToSpeech.stop();
         stopKeepAlives();
         runOnUiThread(new Runnable() {
            public void run() {
               FtcDriverStationActivityBase.this.telemetryMode = Telemetry.DisplayFormat.CLASSIC;
               FtcDriverStationActivityBase.this.textTelemetry.setTypeface(Typeface.DEFAULT);
            }
         });
         handleDefaultOpModeInitOrStart(false);
      } else {
         clearUserTelemetry();
         startKeepAlives();
         if (setQueuedOpModeIfDifferent(str)) {
            RobotLog.vv(TAG, "timer: init new opmode");
            enableAndResetTimerForQueued();
         } else if (this.opModeCountDown.isEnabled()) {
            RobotLog.vv(TAG, "timer: init w/ timer enabled");
            this.opModeCountDown.resetCountdown();
         } else {
            RobotLog.vv(TAG, "timer: init w/o timer enabled");
         }
         uiWaitingForStartEvent();
      }
      return CallbackResult.HANDLED;
   }


   public CallbackResult handleCommandNotifyOpModeList(String str) {
      assumeClientConnect(ControlPanelBack.NO_CHANGE);
      this.opModes = (List) new Gson().fromJson(str, new TypeToken<Collection<OpModeMeta>>() {
      }.getType());
      RobotLog.vv(TAG, "Received the following op modes: " + this.opModes.toString());
      return CallbackResult.HANDLED;
   }


   protected CallbackResult handleCommandNotifyStartOpMode(final String s) {
      if (this.uiState == FtcDriverStationActivityBase.UIState.CANT_CONTINUE) {
         return CallbackResult.HANDLED;
      }
      final StringBuilder sb = new StringBuilder();
      sb.append("Robot Controller starting op mode: ");
      sb.append(s);
      RobotLog.vv("DriverStation", sb.toString());
      if (this.isDefaultOpMode(s)) {
         this.androidTextToSpeech.stop();
         this.stopKeepAlives();
         this.handleDefaultOpModeInitOrStart(true);
      }
      else {
         if (this.setQueuedOpModeIfDifferent(s)) {
            RobotLog.vv("DriverStation", "timer: started new opmode: auto-initing timer");
            this.enableAndResetTimerForQueued();
         }
         this.uiWaitingForStopEvent();
         if (this.opModeUseTimer) {
            this.opModeCountDown.start();
         }
         else {
            this.stopTimerAndReset();
         }
      }
      return CallbackResult.HANDLED;
   }

   protected CallbackResult handleCommandNotifyUserDeviceList(final String s) {
      ConfigurationTypeManager.getInstance().deserializeUserDeviceTypes(s);
      return CallbackResult.HANDLED;
   }

   protected CallbackResult handleCommandShowDialog(final String s) {
      final RobotCoreCommandList.ShowDialog deserialize = RobotCoreCommandList.ShowDialog.deserialize(s);
      final AppUtil.DialogParams dialogParams = new AppUtil.DialogParams(UILocation.ONLY_LOCAL, deserialize.title, deserialize.message);
      dialogParams.uuidString = deserialize.uuidString;
      this.appUtil.showDialog(dialogParams);
      return CallbackResult.HANDLED;
   }

   protected CallbackResult handleCommandShowProgress(final String s) {
      final RobotCoreCommandList.ShowProgress deserialize = RobotCoreCommandList.ShowProgress.deserialize(s);
      this.appUtil.showProgress(UILocation.ONLY_LOCAL, deserialize.message, (ProgressParameters)deserialize);
      return CallbackResult.HANDLED;
   }

   protected CallbackResult handleCommandShowToast(final String s) {
      final RobotCoreCommandList.ShowToast deserialize = RobotCoreCommandList.ShowToast.deserialize(s);
      this.appUtil.showToast(UILocation.ONLY_LOCAL, deserialize.message, deserialize.duration);
      return CallbackResult.HANDLED;
   }

   protected void handleDefaultOpModeInitOrStart(final boolean b) {
      if (this.isDefaultOpMode(this.queuedOpMode)) {
         this.uiWaitingForOpModeSelection();
      }
      else {
         this.uiWaitingForInitEvent();
         if (!b) {
            this.runDefaultOpMode();
         }
      }
   }

   protected CallbackResult handleNotifyRobotState(final String s) {
      this.setRobotState(RobotState.fromByte((int)Integer.valueOf(s)));
      return CallbackResult.HANDLED;
   }

   protected void handleOpModeInit() {
      if (this.uiState != FtcDriverStationActivityBase.UIState.WAITING_FOR_INIT_EVENT) {
         return;
      }
      this.traceUiStateChange("ui:uiWaitingForAck", FtcDriverStationActivityBase.UIState.WAITING_FOR_ACK);
      this.sendMatchNumberIfNecessary();
      this.networkConnectionHandler.sendCommand(new Command("CMD_INIT_OP_MODE", this.queuedOpMode.name));
      if (!this.queuedOpMode.name.equals(this.defaultOpMode.name)) {
         this.wifiMuteStateMachine.consumeEvent((Event)WifiMuteEvent.RUNNING_OPMODE);
      }
      this.hideCameraStream();
   }

   protected void handleOpModeQueued(final OpModeMeta queuedOpModeIfDifferent) {
      if (this.setQueuedOpModeIfDifferent(queuedOpModeIfDifferent)) {
         this.enableAndResetTimerForQueued();
      }
      this.uiWaitingForInitEvent();
   }

   protected void handleOpModeStart() {
      if (this.uiState != FtcDriverStationActivityBase.UIState.WAITING_FOR_START_EVENT) {
         return;
      }
      this.traceUiStateChange("ui:uiWaitingForAck", FtcDriverStationActivityBase.UIState.WAITING_FOR_ACK);
      this.networkConnectionHandler.sendCommand(new Command("CMD_RUN_OP_MODE", this.queuedOpMode.name));
   }

   protected void handleOpModeStop() {
      if (this.uiState != FtcDriverStationActivityBase.UIState.WAITING_FOR_START_EVENT && this.uiState != FtcDriverStationActivityBase.UIState.WAITING_FOR_STOP_EVENT) {
         return;
      }
      this.traceUiStateChange("ui:uiWaitingForAck", FtcDriverStationActivityBase.UIState.WAITING_FOR_ACK);
      this.clearMatchNumberIfNecessary();
      this.initDefaultOpMode();
      this.wifiMuteStateMachine.consumeEvent((Event)WifiMuteEvent.STOPPED_OPMODE);
   }

   protected CallbackResult handleReportGlobalError(final String s) {
      final StringBuilder sb = new StringBuilder();
      sb.append("Received error from robot controller: ");
      sb.append(s);
      RobotLog.ee("DriverStation", sb.toString());
      RobotLog.setGlobalErrorMsg(s);
      return CallbackResult.HANDLED;
   }

   public CallbackResult heartbeatEvent(final RobocolDatagram robocolDatagram, final long n) {
      try {
         this.heartbeatRecv.fromByteArray(robocolDatagram.getData());
         RobotLog.processTimeSynch(this.heartbeatRecv.t0, this.heartbeatRecv.t1, this.heartbeatRecv.t2, n);
         final double elapsedSeconds = this.heartbeatRecv.getElapsedSeconds();
         this.heartbeatRecv.getSequenceNumber();
         this.setRobotState(RobotState.fromByte((int)this.heartbeatRecv.getRobotState()));
         this.pingAverage.addNumber((int)(elapsedSeconds * 1000.0));
         if (this.lastUiUpdate.time() > 0.5) {
            this.lastUiUpdate.reset();
            this.networkStatus();
         }
      }
      catch (RobotCoreException ex) {
         RobotLog.logStackTrace((Throwable)ex);
      }
      return CallbackResult.HANDLED;
   }

   protected void hideCameraStream() {
      this.cameraStreamOpen = false;
      this.gamepadManager.setEnabled(true);
      this.setVisibility((View)this.cameraStreamLayout, 4);
      this.setVisibility(this.buttonStart, 0);
   }

   protected void initDefaultOpMode() {
      this.networkConnectionHandler.sendCommand(new Command("CMD_INIT_OP_MODE", this.defaultOpMode.name));
   }

   public void initializeNetwork() {
      updateLoggingPrefs();
      NetworkType defaultNetworkType = NetworkConnectionHandler.getDefaultNetworkType(this);
      this.connectionOwner = this.preferences.getString(getString(R.string.pref_connection_owner_identity), getString(R.string.connection_owner_default));
      this.connectionOwnerPassword = this.preferences.getString(getString(R.string.pref_connection_owner_password), getString(R.string.connection_owner_password_default));
      this.networkConnectionHandler.init(NetworkConnectionHandler.newWifiLock(), defaultNetworkType, this.connectionOwner, this.connectionOwnerPassword, this, this.gamepadManager);
      if (this.networkConnectionHandler.isNetworkConnected()) {
         RobotLog.vv("Robocol", "Spoofing a Network Connection event...");
         onNetworkConnectionEvent(NetworkConnection.NetworkEvent.CONNECTION_INFO_AVAILABLE);
      }
   }


   protected boolean isDefaultOpMode(final String anObject) {
      return this.defaultOpMode.name.equals(anObject);
   }

   protected boolean isDefaultOpMode(final OpModeMeta opModeMeta) {
      return this.isDefaultOpMode(opModeMeta.name);
   }

   protected void networkStatus() {
      this.pingStatus(String.format("%dms", this.pingAverage.getAverage()));
      final long bytesPerSecond = this.networkConnectionHandler.getBytesPerSecond();
      if (bytesPerSecond > 0L) {
         this.showBytesPerSecond(bytesPerSecond);
      }
   }

   public void onActivityResult(final int i, final int n, final Intent intent) {
      RobotLog.vv("DriverStation", "onActivityResult(request=%d)", new Object[] { i });
      if (i == LaunchActivityConstantsList.RequestCode.SETTINGS_DRIVER_STATION.ordinal()) {
         if (intent != null) {
            final FtcDriverStationSettingsActivity.Result deserialize = FtcDriverStationSettingsActivity.Result.deserialize(intent.getExtras().getString("RESULT"));
            if (deserialize.prefLogsClicked) {
               this.updateLoggingPrefs();
            }
            if (deserialize.prefPairingMethodChanged) {
               RobotLog.ii("DriverStation", "Pairing method changed in settings activity, shutdown network to force complete restart");
               this.startOrRestartNetwork();
            }
            if (deserialize.prefPairClicked) {
               this.startOrRestartNetwork();
            }
            if (deserialize.prefAdvancedClicked) {
               this.networkConnectionHandler.sendCommand(new Command("CMD_RESTART_ROBOT"));
            }
         }
      }
      else if (i == LaunchActivityConstantsList.RequestCode.CONFIGURE_DRIVER_STATION.ordinal()) {
         this.requestUIState();
         this.networkConnectionHandler.sendCommand(new Command("CMD_RESTART_ROBOT"));
      }
   }

   public void onClickButtonAutonomous(View view) {
      showOpModeDialog(filterOpModes(new Predicate<OpModeMeta>() {
         public boolean test(OpModeMeta opModeMeta) {
            return opModeMeta.flavor == OpModeMeta.Flavor.AUTONOMOUS;
         }
      }), R.string.opmodeDialogTitleAutonomous);
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

   public void onClickButtonTeleOp(View view) {
      showOpModeDialog(filterOpModes(new Predicate<OpModeMeta>() {
         public boolean test(OpModeMeta opModeMeta) {
            return opModeMeta.flavor == OpModeMeta.Flavor.TELEOP;
         }
      }), R.string.opmodeDialogTitleTeleOp);
   }


   public void onClickRCBatteryToast(View view) {
      showToast(getString(R.string.toastRobotControllerBattery));
   }



   public void onClickRobotBatteryToast(View view) {
      resetBatteryStats();
      showToast(getString(R.string.toastRobotBattery));
   }

   public void resetBatteryStats() {
      this.V12BatteryMin = Double.POSITIVE_INFINITY;
      this.V12BatteryMinString = "";
   }


   public void onClickTimer(final View view) {
      this.enableAndResetTimer(this.opModeUseTimer ^= true);
   }

   public void onConfigurationChanged(final Configuration configuration) {
      super.onConfigurationChanged(configuration);
   }

   @SuppressLint("ServiceCast")
   protected void onCreate(final Bundle bundle) {

         super.onCreate(bundle);
         enforcePermissionValidator();
         this.uiThread = Thread.currentThread();
         subclassOnCreate();
         this.gamepadManager = new GamepadManager(this);
         this.context = this;
         this.utility = new Utility(this);
         this.opModeCountDown = new OpModeCountDownTimer();
         this.rcHasIndependentBattery = false;
         PreferenceManager.setDefaultValues(this, R.xml.app_settings, false);
         this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
         this.preferencesHelper = new PreferencesHelper(TAG, this.preferences);
         DeviceNameManagerFactory.getInstance().start(this.deviceNameManagerStartResult);
         PreferenceRemoterDS.getInstance().start(this.prefRemoterStartResult);
         NetworkConnectionHandler.getInstance().registerPeerStatusCallback(this);
         setClientConnected(false);
         if (permissionsValidated) {
            RobotLog.ii(TAG, "Processing all classes through class filter");
            ClassManagerFactory.registerResourceFilters();
            ClassManagerFactory.processAllClasses();
         }
         this.robotConfigFileManager = new RobotConfigFileManager(this);
         this.textDeviceName = (TextView) findViewById(R.id.textDeviceName);
         this.textDsUiStateIndicator = (TextView) findViewById(R.id.textDsUiStateIndicator);
         this.textWifiDirectStatus = (TextView) findViewById(R.id.textWifiDirectStatus);
         this.textWifiDirectStatusShowingRC = false;
         this.textWifiChannel = (TextView) findViewById(R.id.wifiChannel);
         this.textPingStatus = (TextView) findViewById(R.id.textPingStatus);
         this.textBytesPerSecond = (TextView) findViewById(R.id.bps);
         this.textTelemetry = (TextView) findViewById(R.id.textTelemetry);
         TextView textView = (TextView) findViewById(R.id.textSystemTelemetry);
         this.systemTelemetry = textView;
         this.systemTelemetryOriginalColor = textView.getCurrentTextColor();
         this.rcBatteryContainer = findViewById(R.id.rcBatteryContainer);
         this.rcBatteryTelemetry = (TextView) findViewById(R.id.rcBatteryTelemetry);
         this.robotBatteryMinimum = (TextView) findViewById(R.id.robotBatteryMinimum);
         this.rcBatteryIcon = (ImageView) findViewById(R.id.rc_battery_icon);
         this.dsBatteryInfo = (TextView) findViewById(R.id.dsBatteryInfo);
         this.robotBatteryTelemetry = (TextView) findViewById(R.id.robotBatteryTelemetry);
         this.dsBatteryIcon = (ImageView) findViewById(R.id.DS_battery_icon);
         this.immersion = new ImmersiveMode(getWindow().getDecorView());
         doMatchNumFieldBehaviorInit();
         LinearLayout linearLayout = (LinearLayout) findViewById(R.id.cameraStreamLayout);
         this.cameraStreamLayout = linearLayout;
         linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               FtcDriverStationActivityBase.this.networkConnectionHandler.sendCommand(new Command(RobotCoreCommandList.CMD_REQUEST_FRAME));
            }
         });
         this.cameraStreamImageView = (ImageView) findViewById(R.id.cameraStreamImageView);
         CameraStreamClient.getInstance().setListener(new CameraStreamClient.Listener() {
            public void onStreamAvailableChange(boolean z) {
               FtcDriverStationActivityBase.this.invalidateOptionsMenu();
               if (FtcDriverStationActivityBase.this.cameraStreamOpen && !z) {
                  FtcDriverStationActivityBase.this.hideCameraStream();
               }
            }

            public void onFrameBitmap(final Bitmap bitmap) {
               FtcDriverStationActivityBase.this.runOnUiThread(new Runnable() {
                  public void run() {
                     FtcDriverStationActivityBase.this.cameraStreamImageView.setImageBitmap(bitmap);
                  }
               });
            }
         });
         this.buttonInit = findViewById(R.id.buttonInit);
         this.buttonInitStop = findViewById(R.id.buttonInitStop);
         this.buttonStart = findViewById(R.id.buttonStart);
         this.controlPanelBack = findViewById(R.id.controlPanel);
         this.batteryInfo = findViewById(R.id.battery_info_layout);
         this.wifiInfo = findViewById(R.id.wifi_info_layout);
         ((ImageButton) findViewById(R.id.buttonStartArrow)).setImageDrawable(new FilledPolygonDrawable(((ColorDrawable) findViewById(R.id.buttonStartArrowColor).getBackground()).getColor(), 3));
         ((ImageView) findViewById(R.id.timerStopWatch)).setImageDrawable(new StopWatchDrawable(((ColorDrawable) findViewById(R.id.timerStopWatchColorHolder).getBackground()).getColor()));
         this.gamepadIndicators.put(GamepadUser.ONE, new GamepadIndicator(this, R.id.user1_icon_clicked, R.id.user1_icon_base));
         this.gamepadIndicators.put(GamepadUser.TWO, new GamepadIndicator(this, R.id.user2_icon_clicked, R.id.user2_icon_base));
         this.gamepadManager.setGamepadIndicators(this.gamepadIndicators);
         TextView textView2 = (TextView) findViewById(R.id.activeConfigName);
         this.activeConfigText = textView2;
         textView2.setText(" ");
         this.timerAndTimerSwitch = findViewById(R.id.timerAndTimerSwitch);
         this.buttonAutonomous = (Button) findViewById(R.id.buttonAutonomous);
         this.buttonTeleOp = (Button) findViewById(R.id.buttonTeleOp);
         this.currentOpModeName = (TextView) findViewById(R.id.currentOpModeName);
         this.chooseOpModePrompt = findViewById(R.id.chooseOpModePrompt);
         this.buttonStop = (ImageButton) findViewById(R.id.buttonStop);
         ImageButton imageButton = (ImageButton) findViewById(R.id.menu_buttons);
         this.buttonMenu = imageButton;
         imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               FtcDriverStationActivityBase ftcDriverStationActivityBase = FtcDriverStationActivityBase.this;
               PopupMenu popupMenu = new PopupMenu(ftcDriverStationActivityBase, ftcDriverStationActivityBase.getPopupMenuAnchor());
               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                  public boolean onMenuItemClick(MenuItem menuItem) {
                     return FtcDriverStationActivityBase.this.onOptionsItemSelected(menuItem);
                  }
               });
               FtcDriverStationActivityBase.this.onCreateOptionsMenu(popupMenu.getMenu());
               popupMenu.show();
            }
         });
         this.preferences.registerOnSharedPreferenceChangeListener(this);
         this.gamepadManager.open();
         BatteryChecker batteryChecker2 = new BatteryChecker(this, (long) 300000);
         this.batteryChecker = batteryChecker2;
         batteryChecker2.startBatteryMonitoring();
         resetBatteryStats();
         pingStatus((int) R.string.ping_status_no_heartbeat);
         this.mInputManager = (InputManager) this.getSystemService(Context.INPUT_SERVICE);
         this.networkConnectionHandler.pushNetworkConnectionCallback(this);
         this.networkConnectionHandler.pushReceiveLoopCallback(this);
         startOrRestartNetwork();
         DeviceNameManagerFactory.getInstance().registerCallback(this.deviceNameManagerCallback);
         ((WifiManager) AppUtil.getDefContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(true);
         WifiMuteStateMachine wifiMuteStateMachine2 = new WifiMuteStateMachine();
         this.wifiMuteStateMachine = wifiMuteStateMachine2;
         wifiMuteStateMachine2.initialize();
         this.wifiMuteStateMachine.start();
         this.wifiMuteStateMachine.registerCallback(this);
         this.processUserActivity = true;
         SoundPlayingRobotMonitor.prefillSoundCache();
         RobotLog.logBuildConfig(BuildConfig.class);
         RobotLog.logDeviceInfo();
         this.androidTextToSpeech.initialize();


      }

   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.ftc_driver_station, menu);
      if (this.uiState != UIState.WAITING_FOR_START_EVENT || !CameraStreamClient.getInstance().isStreamAvailable()) {
         menu.findItem(R.id.action_camera_stream).setVisible(false);
      } else {
         menu.findItem(R.id.action_camera_stream).setVisible(true);
      }
      return true;
   }


   protected void onDestroy() {
      super.onDestroy();
      RobotLog.vv("DriverStation", "onDestroy()");
      this.androidTextToSpeech.close();
      this.gamepadManager.close();
      DeviceNameManagerFactory.getInstance().unregisterCallback((DeviceNameListener)this.deviceNameManagerCallback);
      this.networkConnectionHandler.removeNetworkConnectionCallback((NetworkConnection.NetworkConnectionCallback)this);
      this.networkConnectionHandler.removeReceiveLoopCallback((RecvLoopRunnable.RecvLoopCallback)this);
      this.shutdown();
      PreferenceRemoterDS.getInstance().stop(this.prefRemoterStartResult);
      DeviceNameManagerFactory.getInstance().stop(this.deviceNameManagerStartResult);
      RobotLog.cancelWriteLogcatToDisk();
   }

   public CallbackResult onNetworkConnectionEvent(NetworkConnection.NetworkEvent networkEvent) {
      CallbackResult callbackResult = CallbackResult.NOT_HANDLED;
      RobotLog.i("Received networkConnectionEvent: " + networkEvent.toString());
      switch (networkEvent.ordinal()) {
         case 1:
            if (this.networkConnectionHandler.isWifiDirect()) {
               onPeersAvailableWifiDirect();
            } else {
               onPeersAvailableSoftAP();
            }
            return CallbackResult.HANDLED;
         case 2:
            RobotLog.ee(TAG, "Wifi Direct - connected as Group Owner, was expecting Peer");
            showWifiStatus(false, getString(R.string.wifiStatusErrorConnectedAsGroupOwner));
            ConfigWifiDirectActivity.launch(getBaseContext(), ConfigWifiDirectActivity.Flag.WIFI_DIRECT_DEVICE_NAME_INVALID);
            return CallbackResult.HANDLED;
         case 3:
            showWifiStatus(false, getString(R.string.wifiStatusConnecting));
            return CallbackResult.HANDLED;
         case 4:
            showWifiStatus(false, getString(R.string.wifiStatusConnected));
            return CallbackResult.HANDLED;
         case 5:
            showWifiStatus(true, getBestRobotControllerName());
            showWifiChannel();
            if (!NetworkConnection.isDeviceNameValid(this.networkConnectionHandler.getDeviceName())) {
               RobotLog.ee(TAG, "Wifi-Direct device name contains non-printable characters");
               ConfigWifiDirectActivity.launch(getBaseContext(), ConfigWifiDirectActivity.Flag.WIFI_DIRECT_DEVICE_NAME_INVALID);
            } else if (this.networkConnectionHandler.connectedWithUnexpectedDevice()) {
               showWifiStatus(false, getString(R.string.wifiStatusErrorWrongDevice));
               if (this.networkConnectionHandler.isWifiDirect()) {
                  ConfigWifiDirectActivity.launch(getBaseContext(), ConfigWifiDirectActivity.Flag.WIFI_DIRECT_FIX_CONFIG);
               } else if (this.connectionOwner == null && this.connectionOwnerPassword == null) {
                  showWifiStatus(false, getString(R.string.wifiStatusNotPaired));
                  return CallbackResult.HANDLED;
               } else {
                  this.networkConnectionHandler.startConnection(this.connectionOwner, this.connectionOwnerPassword);
               }
               return CallbackResult.HANDLED;
            }
            this.networkConnectionHandler.handleConnectionInfoAvailable();
            this.networkConnectionHandler.cancelConnectionSearch();
            assumeClientConnectAndRefreshUI(ControlPanelBack.NO_CHANGE);
            return CallbackResult.HANDLED;
         case 6:
            String string = getString(R.string.wifiStatusDisconnected);
            showWifiStatus(false, string);
            RobotLog.vv(TAG, "Network Connection - " + string);
            this.networkConnectionHandler.discoverPotentialConnections();
            assumeClientDisconnect();
            return CallbackResult.HANDLED;
         case 7:
            String string2 = getString(R.string.dsErrorMessage, new Object[]{this.networkConnectionHandler.getFailureReason()});
            showWifiStatus(false, string2);
            RobotLog.vv(TAG, "Network Connection - " + string2);
            return callbackResult;
         default:
            return callbackResult;
      }
   }


   public void onOpModeSelectionClick(final OpModeMeta opModeMeta) {
      this.handleOpModeQueued(opModeMeta);
   }

   public boolean onOptionsItemSelected(final MenuItem menuItem) {
      this.wifiMuteStateMachine.consumeEvent((Event)WifiMuteEvent.ACTIVITY_OTHER);
      this.wifiMuteStateMachine.maskEvent((Event)WifiMuteEvent.STOPPED_OPMODE);
      switch (menuItem.getItemId()) {
         default:
            return super.onOptionsItemSelected(menuItem);
         case 2131230784:
            this.startActivityForResult(new Intent(this.getBaseContext(), (Class)FtcDriverStationSettingsActivity.class), LaunchActivityConstantsList.RequestCode.SETTINGS_DRIVER_STATION.ordinal());
            return true;
         case 2131230783:
            this.networkConnectionHandler.sendCommand(new Command("CMD_RESTART_ROBOT"));
            this.wifiMuteStateMachine.consumeEvent((Event)WifiMuteEvent.ACTIVITY_START);
            this.wifiMuteStateMachine.maskEvent((Event)WifiMuteEvent.STOPPED_OPMODE);
            return true;
         case 2131230782:
            RobotLog.vv("DriverStation", "action_program_and_manage clicked");
            this.networkConnectionHandler.sendCommand(new Command("CMD_START_DS_PROGRAM_AND_MANAGE"));
            return true;
         case 2131230776:
            this.startActivityForResult(new Intent(this.getBaseContext(), (Class)FtcDriverStationInspectionReportsActivity.class), LaunchActivityConstantsList.RequestCode.INSPECTIONS.ordinal());
            return true;
         case 2131230773: {
            this.finishAffinity();
            final Iterator<ActivityManager.AppTask> iterator = (Iterator<ActivityManager.AppTask>)((ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE)).getAppTasks().iterator();
            while (iterator.hasNext()) {
               iterator.next().finishAndRemoveTask();
            }
            AppUtil.getInstance().exitApplication();
            return true;
         }
         case 2131230769: {
            final EditParameters editParameters = new EditParameters();
            final Intent intent = new Intent(AppUtil.getDefContext(), (Class)FtcLoadFileActivity.class);
            editParameters.putIntent(intent);
            this.startActivityForResult(intent, LaunchActivityConstantsList.RequestCode.CONFIGURE_DRIVER_STATION.ordinal());
            return true;
         }
         case 2131230768:
            if (this.cameraStreamOpen) {
               this.hideCameraStream();
            }
            else {
               this.showCameraStream();
            }
            return true;
         case 2131230760:
            this.startActivity(new Intent(AppUtil.getDefContext(), (Class)FtcAboutActivity.class));
            return true;
      }
   }

   protected void onPause() {
      super.onPause();
      RobotLog.vv(TAG, "onPause()");
      this.gamepadManager.clearGamepadAssignments();
      this.gamepadManager.clearTrackedGamepads();
      this.mInputManager.unregisterInputDeviceListener(this.gamepadManager);
      initDefaultOpMode();
   }


   public void onPeerConnected() {
      RobotLog.vv("DriverStation", "robot controller connected");
      this.assumeClientConnectAndRefreshUI(FtcDriverStationActivityBase.ControlPanelBack.NO_CHANGE);
      PreferenceRemoterDS.getInstance().sendInformationalPrefsToRc();
   }

   public void onPeerDisconnected() {
      RobotLog.logStackTrace(new Throwable("Peer disconnected"));
      RobotLog.vv("DriverStation", "robot controller disconnected");
      this.assumeClientDisconnect();
   }

   public void onPendingCancel() {
      this.processUserActivity = true;
      final StringBuilder sb = new StringBuilder();
      sb.append("Pending Wifi Cancel: ");
      sb.append(this.queuedOpMode.name);
      RobotLog.ii("DriverStation", sb.toString());
   }

   public void onPendingOn() {
      this.processUserActivity = false;
      final StringBuilder sb = new StringBuilder();
      sb.append("Pending Wifi Off: ");
      sb.append(this.queuedOpMode.name);
      RobotLog.ii("DriverStation", sb.toString());
   }

   protected void onResume() {
      super.onResume();
      RobotLog.vv(TAG, "onResume()");
      this.disconnectFromPeerOnActivityStop = true;
      updateRcBatteryIndependence(this.preferences);
      resetBatteryStats();
      this.mInputManager.registerInputDeviceListener(this.gamepadManager, (Handler) null);
      pingStatus((int) R.string.ping_status_no_heartbeat);
   }


   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
      RobotLog.vv(TAG, "onSharedPreferenceChanged() pref=%s", str);
      if (str.equals(this.context.getString(R.string.pref_device_name_rc_display))) {
         final String string = sharedPreferences.getString(str, "");
         if (string.length() > 0) {
            runOnUiThread(new Runnable() {
               public void run() {
                  if (FtcDriverStationActivityBase.this.textWifiDirectStatusShowingRC) {
                     FtcDriverStationActivityBase.this.textWifiDirectStatus.setText(string);
                  }
               }
            });
         }
      } else if (str.equals(getString(R.string.pref_has_independent_phone_battery_rc))) {
         updateRcBatteryIndependence(this.preferences);
      } else if (!str.equals(getString(R.string.pref_app_theme)) && str.equals("pref_wifip2p_channel")) {
         RobotLog.vv(TAG, "pref_wifip2p_channel changed.");
         showWifiChannel();
      }
      updateLoggingPrefs();
   }


   protected void onStart() {
      super.onStart();
      RobotLog.onApplicationStart();
      RobotLog.vv("DriverStation", "onStart()");
      final Iterator<GamepadIndicator> iterator = this.gamepadIndicators.values().iterator();
      while (iterator.hasNext()) {
         iterator.next().setState(GamepadIndicator.State.INVISIBLE);
      }
      this.wifiMuteStateMachine.consumeEvent((Event)WifiMuteEvent.ACTIVITY_START);
      this.wifiMuteStateMachine.unMaskEvent((Event)WifiMuteEvent.STOPPED_OPMODE);
      FtcAboutActivity.setBuildTimeFromBuildConfig("2020-09-21T09:05:36.688-0700");
   }

   protected void onStop() {
      super.onStop();
      RobotLog.vv("DriverStation", "onStop()");
      this.pingStatus(2131624392);
      this.wifiMuteStateMachine.consumeEvent((Event)WifiMuteEvent.ACTIVITY_STOP);
      if (this.disconnectFromPeerOnActivityStop) {
         RobotLog.ii("DriverStation", "App appears to be exiting. Destroying activity so that another DS can connect");
         this.finish();
      }
   }

   public void onUserInteraction() {
      if (this.processUserActivity) {
         this.wifiMuteStateMachine.consumeEvent((Event)WifiMuteEvent.USER_ACTIVITY);
      }
   }

   public void onWifiOff() {
      this.queuedOpModeWhenMuted = this.queuedOpMode;
      final StringBuilder sb = new StringBuilder();
      sb.append("Wifi Off: ");
      sb.append(this.queuedOpMode.name);
      RobotLog.ii("DriverStation", sb.toString());
   }

   public void onWifiOn() {
      this.queuedOpMode = this.queuedOpModeWhenMuted;
      this.processUserActivity = true;
      final StringBuilder sb = new StringBuilder();
      sb.append("Wifi On: ");
      sb.append(this.queuedOpMode.name);
      RobotLog.ii("DriverStation", sb.toString());
   }

   public void onWindowFocusChanged(final boolean b) {
      super.onWindowFocusChanged(b);
      if (b) {
         this.immersion.hideSystemUI();
         this.getWindow().setFlags(134217728, 134217728);
      }
   }

   public CallbackResult packetReceived(final RobocolDatagram robocolDatagram) throws RobotCoreException {
      return CallbackResult.NOT_HANDLED;
   }

   public CallbackResult peerDiscoveryEvent(RobocolDatagram robocolDatagram) throws RobotCoreException {
      try {
         PeerDiscovery forReceive = PeerDiscovery.forReceive();
         forReceive.fromByteArray(robocolDatagram.getData());
         if (forReceive.getPeerType() == PeerDiscovery.PeerType.NOT_CONNECTED_DUE_TO_PREEXISTING_CONNECTION) {
            reportGlobalError(getString(R.string.anotherDsIsConnectedError), false);
            showRobotBatteryVoltage(FtcEventLoopHandler.NO_VOLTAGE_SENSOR);
         } else {
            this.networkConnectionHandler.updateConnection(robocolDatagram);
         }
      } catch (RobotProtocolException e) {
         reportGlobalError(e.getMessage(), false);
         this.networkConnectionHandler.stopPeerDiscovery();
         RobotLog.setGlobalErrorMsgSticky(true);
         Thread.currentThread().interrupt();
         showRobotBatteryVoltage(FtcEventLoopHandler.NO_VOLTAGE_SENSOR);
      }
      return CallbackResult.HANDLED;
   }


   protected void pingStatus(final int n) {
      this.pingStatus(this.context.getString(n));
   }

   protected abstract void pingStatus(final String p0);

   public CallbackResult reportGlobalError(String str, boolean z) {
      if (!RobotLog.getGlobalErrorMsg().equals(str)) {
         RobotLog.ee(TAG, "System telemetry error: " + str);
         RobotLog.clearGlobalErrorMsg();
         RobotLog.setGlobalErrorMsg(str);
      }
      TextView textView = this.systemTelemetry;
      AppUtil.getInstance();
      setTextColor(textView, AppUtil.getColor(R.color.text_error));
      setVisibility(this.systemTelemetry, 0);
      StringBuilder sb = new StringBuilder();
      RobotState robotState2 = this.robotState;
      if (!(robotState2 == null || robotState2 == RobotState.UNKNOWN)) {
         sb.append(String.format(getString(R.string.dsRobotStatus), new Object[]{this.robotState.toString(this)}));
      }
      if (z) {
         sb.append(getString(R.string.dsToAttemptRecovery));
      }
      sb.append(String.format(getString(R.string.dsErrorMessage), new Object[]{str}));
      setTextView(this.systemTelemetry, sb.toString());
      stopTimerAndReset();
      uiRobotCantContinue();
      return CallbackResult.HANDLED;
   }



   public void reportGlobalWarning(String str) {
      if (!RobotLog.getGlobalWarningMessage().equals(str)) {
         RobotLog.ee(TAG, "System telemetry warning: " + str);
         RobotLog.clearGlobalWarningMsg();
         RobotLog.setGlobalWarningMessage(str);
      }
      TextView textView = this.systemTelemetry;
      AppUtil.getInstance();
      setTextColor(textView, AppUtil.getColor(R.color.text_warning));
      setVisibility(this.systemTelemetry, 0);
      setTextView(this.systemTelemetry, String.format(getString(R.string.dsWarningMessage), new Object[]{str}));
   }



   protected void requestUIState() {
      this.networkConnectionHandler.sendCommand(new Command("CMD_REQUEST_UI_STATE"));
   }


      protected void runDefaultOpMode() {
      this.networkConnectionHandler.sendCommand(new Command("CMD_RUN_OP_MODE", this.defaultOpMode.name));
      this.wifiMuteStateMachine.consumeEvent((Event)WifiMuteEvent.STOPPED_OPMODE);
   }

   protected void sendMatchNumber(final int i) {
      this.sendMatchNumber(String.valueOf(i));
   }

   protected void sendMatchNumber(final String s) {
      this.networkConnectionHandler.sendCommand(new Command("CMD_SET_MATCH_NUMBER", s));
   }

   protected void sendMatchNumberIfNecessary() {
      try {
         this.sendMatchNumber(this.getMatchNumber());
      }
      catch (NumberFormatException ex) {
         this.sendMatchNumber(0);
      }
   }

   public void setBG(final View view, final Drawable drawable) {
      runOnUiThread(new Runnable() {
         public void run() {
            view.setBackground(drawable);
         }
      });
   }


   public void setBGColor(final View view, final int i) {
      runOnUiThread(new Runnable() {
         public void run() {
            view.setBackgroundColor(i);
         }
      });
   }


   public void setBatteryIcon(final BatteryChecker.BatteryStatus batteryStatus, final ImageView imageView) {
      runOnUiThread(new Runnable() {
         public void run() {
            if (batteryStatus.percent <= 15.0d) {
               imageView.setImageResource(batteryStatus.isCharging ? R.drawable.icon_battery0_charging : R.drawable.icon_battery0);
               imageView.setColorFilter(FtcDriverStationActivityBase.this.getResources().getColor(R.color.phoneBatteryCritical), PorterDuff.Mode.MULTIPLY);
            } else if (batteryStatus.percent > 15.0d && batteryStatus.percent <= 45.0d) {
               imageView.setImageResource(batteryStatus.isCharging ? R.drawable.icon_battery25_charging : R.drawable.icon_battery25);
               if (batteryStatus.percent <= 30.0d) {
                  imageView.setColorFilter(FtcDriverStationActivityBase.this.getResources().getColor(R.color.phoneBatteryLow), PorterDuff.Mode.MULTIPLY);
               } else {
                  imageView.setColorFilter(FtcDriverStationActivityBase.this.getResources().getColor(R.color.text_white), PorterDuff.Mode.MULTIPLY);
               }
            } else if (batteryStatus.percent > 45.0d && batteryStatus.percent <= 65.0d) {
               imageView.setImageResource(batteryStatus.isCharging ? R.drawable.icon_battery50_charging : R.drawable.icon_battery50);
               imageView.setColorFilter(FtcDriverStationActivityBase.this.getResources().getColor(R.color.text_white), PorterDuff.Mode.MULTIPLY);
            } else if (batteryStatus.percent <= 65.0d || batteryStatus.percent > 85.0d) {
               imageView.setImageResource(batteryStatus.isCharging ? R.drawable.icon_battery100_charging : R.drawable.icon_battery100);
               imageView.setColorFilter(FtcDriverStationActivityBase.this.getResources().getColor(R.color.text_white), PorterDuff.Mode.MULTIPLY);
            } else {
               imageView.setImageResource(batteryStatus.isCharging ? R.drawable.icon_battery75_charging : R.drawable.icon_battery75);
               imageView.setColorFilter(FtcDriverStationActivityBase.this.getResources().getColor(R.color.text_white), PorterDuff.Mode.MULTIPLY);
            }
         }
      });
   }


   public void setButtonText(final Button button, final String str) {
      runOnUiThread(new Runnable() {
         public void run() {
            button.setText(str);
         }
      });
   }


   protected boolean setClientConnected(final boolean clientConnected) {
      final boolean clientConnected2 = this.clientConnected;
      this.clientConnected = clientConnected;
      this.preferencesHelper.writeBooleanPrefIfDifferent(this.getString(R.string.pref_rc_connected), clientConnected);
      return clientConnected2;
   }

   public void setControlPanelBack(ControlPanelBack controlPanelBack2) {
      int i = controlPanelBack2.ordinal();
      if (i == 2) {
         dimControlPanelBack();
      } else if (i == 3) {
         brightenControlPanelBack();
      }
   }

   public void setEnabled(final View view, final boolean z) {
      runOnUiThread(new Runnable() {
         public void run() {
            view.setEnabled(z);
         }
      });
   }


   public void setImageResource(final ImageButton imageButton, final int i) {
      runOnUiThread(new Runnable() {
         public void run() {
            imageButton.setImageResource(i);
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

   protected void setRobotState(final RobotState robotState) {
      if (this.robotState != robotState) {
         if ((this.robotState = robotState) == RobotState.STOPPED) {
            this.traceUiStateChange("ui:uiRobotStopped", FtcDriverStationActivityBase.UIState.ROBOT_STOPPED);
            this.disableAndDimOpModeMenu();
            this.disableOpModeControls();
            this.dimControlPanelBack();
         }
         if (robotState == RobotState.EMERGENCY_STOP) {
            final WifiMuteStateMachine wifiMuteStateMachine = this.wifiMuteStateMachine;
            if (wifiMuteStateMachine != null) {
               wifiMuteStateMachine.consumeEvent((Event)WifiMuteEvent.STOPPED_OPMODE);
            }
         }
      }
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


   public void setTimerButtonEnabled(boolean z) {
      setEnabled(this.timerAndTimerSwitch, z);
      setEnabled(findViewById(R.id.timerBackground), z);
      setEnabled(findViewById(R.id.timerStopWatch), z);
      setEnabled(findViewById(R.id.timerText), z);
      setEnabled(findViewById(R.id.timerSwitchOn), z);
      setEnabled(findViewById(R.id.timerSwitchOff), z);
   }


   public void setUserTelemetry(String str) {
      int i = this.telemetryMode.ordinal();
      if (i == 1 || i == 2) {
         setTextView(this.textTelemetry, str);
      } else if (i == 3) {
         setTextView(this.textTelemetry, Html.fromHtml(str.replace("\n", "<br>")));
      }
   }


   public void setVisibility(final View view, final int i) {
      runOnUiThread(new Runnable() {
         public void run() {
            view.setVisibility(i);
         }
      });
   }

   public void showBytesPerSecond(final long j) {
      runOnUiThread(new Runnable() {
         public void run() {
            FtcDriverStationActivityBase.this.textBytesPerSecond.setText(String.valueOf(j));
         }
      });
   }


   public void showCameraStream() {
      this.cameraStreamOpen = true;
      this.gamepadManager.setEnabled(false);
      setVisibility(this.cameraStreamLayout, 0);
      setVisibility(this.buttonStart, 4);
      this.networkConnectionHandler.sendCommand(new Command(RobotCoreCommandList.CMD_REQUEST_FRAME));
      showToast(getString(R.string.toastDisableGamepadsStream));
   }

   protected void showOpModeDialog(final List<OpModeMeta> opModes, final int title) {
      this.stopTimerPreservingRemainingTime();
      this.initDefaultOpMode();
      final OpModeSelectionDialogFragment opModeSelectionDialogFragment = new OpModeSelectionDialogFragment();
      opModeSelectionDialogFragment.setOnSelectionDialogListener((OpModeSelectionDialogFragment.OpModeSelectionDialogListener)this);
      opModeSelectionDialogFragment.setOpModes((List)opModes);
      opModeSelectionDialogFragment.setTitle(title);
      opModeSelectionDialogFragment.show(this.getFragmentManager(), "op_mode_selection");
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

   public void showRobotBatteryVoltage(String str) {
      String str2 = str;
      RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.robot_battery_background);
      View findViewById = findViewById(R.id.rc_battery_layout);
      TextView textView = (TextView) findViewById(R.id.rc_no_voltage_sensor);
      if (str2.equals(FtcEventLoopHandler.NO_VOLTAGE_SENSOR)) {
         setVisibility(findViewById, 8);
         setVisibility(textView, 0);
         resetBatteryStats();
         setBG(relativeLayout, findViewById(R.id.rcBatteryBackgroundReference).getBackground());
         return;
      }
      setVisibility(findViewById, 0);
      setVisibility(textView, 8);
      double doubleValue = Double.valueOf(str).doubleValue();
      if (doubleValue < this.V12BatteryMin) {
         this.V12BatteryMin = doubleValue;
         this.V12BatteryMinString = str2;
      }
      TextView textView2 = this.robotBatteryTelemetry;
      setTextView(textView2, str2 + " V");
      TextView textView3 = this.robotBatteryMinimum;
      setTextView(textView3, "( " + this.V12BatteryMinString + " V )");
      double d = (double) 10.0f;
      double d2 = (double) 14.0f;
      setBGColor(relativeLayout, Color.HSVToColor(new float[]{(float) Range.scale(Range.clip(doubleValue, d, d2), d, d2, (double) 0.0f, (double) 128.0f), 1.0f, 0.6f}));
   }


   public void showToast(final String s) {
      this.appUtil.showToast(UILocation.ONLY_LOCAL, s);
   }

   public void showWifiChannel() {
      runOnUiThread(new Runnable() {
         public void run() {
            if (FtcDriverStationActivityBase.this.networkConnectionHandler.getWifiChannel() > 0) {
               FtcDriverStationActivityBase.this.textWifiChannel.setText("ch " + FtcDriverStationActivityBase.this.networkConnectionHandler.getWifiChannel());
               FtcDriverStationActivityBase.this.textWifiChannel.setVisibility(View.VISIBLE);
               return;
            }
            int i = FtcDriverStationActivityBase.this.preferences.getInt(FtcDriverStationActivityBase.this.getString(R.string.pref_wifip2p_channel), -1);
            if (i == -1) {
               RobotLog.vv(FtcDriverStationActivityBase.TAG, "pref_wifip2p_channel: showWifiChannel prefChannel not found");
               FtcDriverStationActivityBase.this.textWifiChannel.setVisibility(View.GONE);
               return;
            }
            RobotLog.vv(FtcDriverStationActivityBase.TAG, "pref_wifip2p_channel: showWifiChannel prefChannel = %d", Integer.valueOf(i));
            FtcDriverStationActivityBase.this.textWifiChannel.setText("ch " + Integer.toString(i));
            FtcDriverStationActivityBase.this.textWifiChannel.setVisibility(View.VISIBLE);
         }
      });
   }


   protected void showWifiStatus(final boolean z, final String str) {
      runOnUiThread(new Runnable() {
         public void run() {
            FtcDriverStationActivityBase.this.textWifiDirectStatusShowingRC = z;
            FtcDriverStationActivityBase.this.textWifiDirectStatus.setText(str);
         }
      });
   }


   protected void shutdown() {
      this.networkConnectionHandler.stop();
      this.networkConnectionHandler.shutdown();
   }

   public void startActivity(final Intent intent, final Bundle bundle) {
      this.disconnectFromPeerOnActivityStop = false;
      super.startActivity(intent, bundle);
   }

   public void startActivityForResult(final Intent intent, final int n, final Bundle bundle) {
      this.disconnectFromPeerOnActivityStop = false;
      super.startActivityForResult(intent, n, bundle);
   }

   protected void startKeepAlives() {
      final NetworkConnectionHandler networkConnectionHandler = this.networkConnectionHandler;
      if (networkConnectionHandler != null) {
         networkConnectionHandler.startKeepAlives();
      }
   }

   public void startOrRestartNetwork() {
      RobotLog.vv(TAG, "startOrRestartNetwork()");
      assumeClientDisconnect();
      showWifiStatus(false, getString(R.string.wifiStatusDisconnected));
      initializeNetwork();
   }

   protected void stopKeepAlives() {
      final NetworkConnectionHandler networkConnectionHandler = this.networkConnectionHandler;
      if (networkConnectionHandler != null) {
         networkConnectionHandler.stopKeepAlives();
      }
   }

   void stopTimerAndReset() {
      this.opModeCountDown.stop();
      this.opModeCountDown.resetCountdown();
   }

   void stopTimerPreservingRemainingTime() {
      this.opModeCountDown.stopPreservingRemainingTime();
   }

   public abstract void subclassOnCreate();

   public CallbackResult telemetryEvent(RobocolDatagram robocolDatagram) {
      try {
         TelemetryMessage telemetryMessage = new TelemetryMessage(robocolDatagram.getData());
         if (telemetryMessage.getRobotState() != RobotState.UNKNOWN) {
            setRobotState(telemetryMessage.getRobotState());
         }
         Map<String, String> dataStrings = telemetryMessage.getDataStrings();
         String str = "";
         boolean z = false;
         for (String next : telemetryMessage.isSorted() ? new TreeSet<>(dataStrings.keySet()) : dataStrings.keySet()) {
            if (next.equals(EventLoopManager.ROBOT_BATTERY_LEVEL_KEY)) {
               showRobotBatteryVoltage(dataStrings.get(next));
            } else {
               if (next.length() > 0 && next.charAt(0) != 0) {
                  str = str + next + ": ";
               }
               str = str + dataStrings.get(next) + "\n";
               z = true;
            }
         }
         String str2 = str + "\n";
         Map<String, Float> dataNumbers = telemetryMessage.getDataNumbers();
         for (String next2 : telemetryMessage.isSorted() ? new TreeSet<>(dataNumbers.keySet()) : dataNumbers.keySet()) {
            if (next2.length() > 0 && next2.charAt(0) != 0) {
               str2 = str2 + next2 + ": ";
            }
            str2 = str2 + dataNumbers.get(next2) + "\n";
            z = true;
         }
         String tag = telemetryMessage.getTag();
         if (tag.equals(EventLoopManager.SYSTEM_NONE_KEY)) {
            clearSystemTelemetry();
         } else if (tag.equals(EventLoopManager.SYSTEM_ERROR_KEY)) {
            reportGlobalError(dataStrings.get(tag), true);
         } else if (tag.equals(EventLoopManager.SYSTEM_WARNING_KEY)) {
            reportGlobalWarning(dataStrings.get(tag));
         } else if (tag.equals(EventLoopManager.RC_BATTERY_STATUS_KEY)) {
            updateRcBatteryStatus(BatteryChecker.BatteryStatus.deserialize(dataStrings.get(tag)));
         } else if (tag.equals(EventLoopManager.ROBOT_BATTERY_LEVEL_KEY)) {
            showRobotBatteryVoltage(dataStrings.get(tag));
         } else if (z) {
            setUserTelemetry(str2);
         }
         return CallbackResult.HANDLED;
      } catch (RobotCoreException e) {
         RobotLog.logStackTrace(e);
         return CallbackResult.HANDLED;
      }
   }


   protected void traceUiStateChange(final String s, final FtcDriverStationActivityBase.UIState uiState) {
      RobotLog.vv("DriverStation", s);
      this.uiState = uiState;
      this.setTextView(this.textDsUiStateIndicator, uiState.indicator);
      this.invalidateOptionsMenu();
   }

   protected void uiRobotCantContinue() {
      this.traceUiStateChange("ui:uiRobotCantContinue", FtcDriverStationActivityBase.UIState.CANT_CONTINUE);
      this.disableAndDimOpModeMenu();
      this.disableOpModeControls();
      this.dimControlPanelBack();
   }

   protected void uiRobotControllerIsConnected(final FtcDriverStationActivityBase.ControlPanelBack controlPanelBack) {
      this.traceUiStateChange("ui:uiRobotControllerIsConnected", FtcDriverStationActivityBase.UIState.CONNNECTED);
      this.enableAndBrightenForConnected(controlPanelBack);
      AppUtil.getInstance().dismissAllDialogs(UILocation.ONLY_LOCAL);
      AppUtil.getInstance().dismissProgress(UILocation.ONLY_LOCAL);
      this.setTextView(this.rcBatteryTelemetry, "");
      this.setTextView(this.robotBatteryTelemetry, "");
      this.showWifiChannel();
      this.hideCameraStream();
   }

   protected void uiRobotControllerIsDisconnected() {
      this.traceUiStateChange("ui:uiRobotControllerIsDisconnected", FtcDriverStationActivityBase.UIState.DISCONNECTED);
      this.dimAndDisableAllControls();
   }

   protected void uiWaitingForInitEvent() {
      this.traceUiStateChange("ui:uiWaitingForInitEvent", FtcDriverStationActivityBase.UIState.WAITING_FOR_INIT_EVENT);
      this.checkConnectedEnableBrighten(FtcDriverStationActivityBase.ControlPanelBack.BRIGHT);
      this.brightenControlPanelBack();
      this.showQueuedOpModeName();
      this.enableAndBrightenOpModeMenu();
      this.setEnabled(this.buttonInit, true);
      this.setVisibility(this.buttonInit, 0);
      this.setVisibility(this.buttonStart, 4);
      this.setVisibility((View)this.buttonStop, 4);
      this.setVisibility(this.buttonInitStop, 4);
      this.setTimerButtonEnabled(true);
      this.setVisibility(this.timerAndTimerSwitch, 0);
      this.hideCameraStream();
   }

   protected void uiWaitingForOpModeSelection() {
      this.traceUiStateChange("ui:uiWaitingForOpModeSelection", FtcDriverStationActivityBase.UIState.WAITING_FOR_OPMODE_SELECTION);
      this.checkConnectedEnableBrighten(FtcDriverStationActivityBase.ControlPanelBack.DIM);
      this.dimControlPanelBack();
      this.enableAndBrightenOpModeMenu();
      this.showQueuedOpModeName();
      this.disableOpModeControls();
   }

   protected void uiWaitingForStartEvent() {
      this.traceUiStateChange("ui:uiWaitingForStartEvent", FtcDriverStationActivityBase.UIState.WAITING_FOR_START_EVENT);
      this.checkConnectedEnableBrighten(FtcDriverStationActivityBase.ControlPanelBack.BRIGHT);
      this.showQueuedOpModeName();
      this.enableAndBrightenOpModeMenu();
      this.setVisibility(this.buttonStart, 0);
      this.setVisibility(this.buttonInit, 4);
      this.setVisibility((View)this.buttonStop, 4);
      this.setVisibility(this.buttonInitStop, 0);
      this.setTimerButtonEnabled(true);
      this.setVisibility(this.timerAndTimerSwitch, 0);
      this.hideCameraStream();
   }

   protected void uiWaitingForStopEvent() {
      this.traceUiStateChange("ui:uiWaitingForStopEvent", FtcDriverStationActivityBase.UIState.WAITING_FOR_STOP_EVENT);
      this.checkConnectedEnableBrighten(FtcDriverStationActivityBase.ControlPanelBack.BRIGHT);
      this.showQueuedOpModeName();
      this.enableAndBrightenOpModeMenu();
      this.setVisibility((View)this.buttonStop, 0);
      this.setVisibility(this.buttonInit, 4);
      this.setVisibility(this.buttonStart, 4);
      this.setVisibility(this.buttonInitStop, 4);
      this.setTimerButtonEnabled(false);
      this.setVisibility(this.timerAndTimerSwitch, 0);
      this.hideCameraStream();
   }

   public void updateLoggingPrefs() {
      boolean z = this.preferences.getBoolean(getString(R.string.pref_debug_driver_station_logs), false);
      this.debugLogging = z;
      this.gamepadManager.setDebug(z);
      if (this.preferences.getBoolean(getString(R.string.pref_match_logging_on_off), false)) {
         enableMatchLoggingUI();
      } else {
         disableMatchLoggingUI();
      }
   }


   protected abstract void updateRcBatteryStatus(final BatteryChecker.BatteryStatus p0);

   protected int validateMatchEntry(final String s) {
      try {
         final int int1 = Integer.parseInt(s);
         if (int1 >= 0 && int1 <= 1000) {
            return int1;
         }
      }
      catch (NumberFormatException ex) {
         RobotLog.logStackTrace((Throwable)ex);
      }
      return -1;
   }

   private class OpModeCountDownTimer {
      public static final long MS_COUNTDOWN_INTERVAL = 30000L;
      public static final long MS_PER_S = 1000L;
      public static final long MS_TICK = 1000L;
      public static final long TICK_INTERVAL = 1L;
      private CountDownTimer countDownTimer = null;
      private boolean enabled = false;
      private long msRemaining = 30000L;
      private View timerStopWatch = FtcDriverStationActivityBase.this.findViewById(R.id.timerStopWatch);
      private View timerSwitchOff = FtcDriverStationActivityBase.this.findViewById(R.id.timerSwitchOff);
      private View timerSwitchOn = FtcDriverStationActivityBase.this.findViewById(R.id.timerSwitchOn);
      private TextView timerText = (TextView)FtcDriverStationActivityBase.this.findViewById(R.id.timerText);

      public OpModeCountDownTimer() {
      }

      private void displaySecondsRemaining(long var1) {
         if (this.enabled) {
            FtcDriverStationActivityBase.this.setTextView(this.timerText, String.valueOf(var1));
         }

      }

      public void disable() {
         FtcDriverStationActivityBase.this.setTextView(this.timerText, "");
         FtcDriverStationActivityBase.this.setVisibility(this.timerText, 8);
         FtcDriverStationActivityBase.this.setVisibility(this.timerStopWatch, 0);
         FtcDriverStationActivityBase.this.setVisibility(this.timerSwitchOn, 8);
         FtcDriverStationActivityBase.this.setVisibility(this.timerSwitchOff, 0);
         this.enabled = false;
      }

      public void enable() {
         if (!this.enabled) {
            FtcDriverStationActivityBase.this.setVisibility(this.timerText, 0);
            FtcDriverStationActivityBase.this.setVisibility(this.timerStopWatch, 8);
            FtcDriverStationActivityBase.this.setVisibility(this.timerSwitchOn, 0);
            FtcDriverStationActivityBase.this.setVisibility(this.timerSwitchOff, 8);
            this.enabled = true;
            this.displaySecondsRemaining(this.getSecondsRemaining());
         }

      }

      public long getSecondsRemaining() {
         return this.msRemaining / 1000L;
      }

      public boolean isEnabled() {
         return this.enabled;
      }

      public void resetCountdown() {
         this.setMsRemaining(30000L);
      }

      public void setMsRemaining(long var1) {
         this.msRemaining = var1;
         if (this.enabled) {
            this.displaySecondsRemaining(var1 / 1000L);
         }

      }

      public void start() {
         if (this.enabled) {
            StringBuilder var1 = new StringBuilder();
            var1.append("Starting to run current op mode for ");
            var1.append(this.getSecondsRemaining());
            var1.append(" seconds");
            RobotLog.vv("DriverStation", var1.toString());
            FtcDriverStationActivityBase.this.appUtil.synchronousRunOnUiThread(new Runnable() {
               public void run() {
                  CountDownTimer var1 = OpModeCountDownTimer.this.countDownTimer;
                  if (var1 != null) {
                     var1.cancel();
                  }

                  OpModeCountDownTimer.this.countDownTimer = (new CountDownTimer(OpModeCountDownTimer.this.msRemaining, 1000L) {
                     public void onFinish() {
                        FtcDriverStationActivityBase.this.assertUiThread();
                        RobotLog.vv("DriverStation", "Stopping current op mode, timer expired");
                        OpModeCountDownTimer.this.resetCountdown();
                        FtcDriverStationActivityBase.this.handleOpModeStop();
                     }

                     public void onTick(long var1) {
                        FtcDriverStationActivityBase.this.assertUiThread();
                        OpModeCountDownTimer.this.setMsRemaining(var1);
                        StringBuilder var3 = new StringBuilder();
                        var3.append("Running current op mode for ");
                        var3.append(var1 / 1000L);
                        var3.append(" seconds");
                        RobotLog.vv("DriverStation", var3.toString());
                     }
                  }).start();
               }
            });
         }

      }

      public void stop() {
         FtcDriverStationActivityBase.this.appUtil.synchronousRunOnUiThread(new Runnable() {
            public void run() {
               if (OpModeCountDownTimer.this.countDownTimer != null) {
                  OpModeCountDownTimer.this.countDownTimer.cancel();
                  OpModeCountDownTimer.this.countDownTimer = null;
               }

            }
         });
      }

      public void stopPreservingRemainingTime()
      {
         CountDownTimer localCountDownTimer = this.countDownTimer;
         long l = this.msRemaining;
         if (localCountDownTimer != null) {
            try
            {
               l = this.msRemaining;
            }
            finally {}
         }
         stop();
         setMsRemaining(l);
      }

   }

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

}



