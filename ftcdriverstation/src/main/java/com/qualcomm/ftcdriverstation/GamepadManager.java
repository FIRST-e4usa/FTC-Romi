package com.qualcomm.ftcdriverstation;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.input.InputManager;
import android.preference.PreferenceManager;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.ftcdriverstation.GamepadIndicator;
import com.qualcomm.ftcdriverstation.GamepadTypeOverrideMapper;
import com.qualcomm.hardware.logitech.LogitechGamepadF310;
import com.qualcomm.hardware.microsoft.MicrosoftGamepadXbox360;
import com.qualcomm.hardware.sony.SonyGamepadPS4;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.RobotLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.firstinspires.ftc.robotcore.internal.ui.GamepadUser;
import org.firstinspires.ftc.robotcore.internal.ui.RobotCoreGamepadManager;

public class GamepadManager implements RobotCoreGamepadManager, InputManager.InputDeviceListener {
   protected static int SOUND_ID_GAMEPAD_CONNECT = 2131558404;
   protected static int SOUND_ID_GAMEPAD_DISCONNECT = 2131558405;
   public static final String TAG = "GamepadManager";
   protected final Context context;
   protected boolean debug = false;
   protected boolean enabled = true;
   protected Map<Integer, Gamepad> gamepadIdToGamepadMap = new ConcurrentHashMap();
   protected Map<Integer, GamepadPlaceholder> gamepadIdToPlaceholderMap = new ConcurrentHashMap();
   protected Map<GamepadUser, GamepadIndicator> gamepadIndicators = null;
   protected SharedPreferences preferences;
   protected Set<GamepadUser> recentlyUnassignedUsers = Collections.newSetFromMap(new ConcurrentHashMap());
   protected ArrayList<RemovedGamepadMemory> removedGamepadMemories = new ArrayList<>();
   protected GamepadTypeOverrideMapper typeOverrideMapper;
   protected Map<GamepadUser, Integer> userToGamepadIdMap = new ConcurrentHashMap();

   public void close() {
   }

   class GamepadPlaceholder {
      boolean key105depressed = false;
      boolean key108depressed = false;
      boolean key96depressed = false;
      boolean key97depressed = false;
      boolean key98depressed = false;

      GamepadPlaceholder() {
      }

      /* access modifiers changed from: package-private */
      public void update(KeyEvent keyEvent) {
         if (keyEvent.getKeyCode() == 105) {
            this.key105depressed = pressed(keyEvent);
         } else if (keyEvent.getKeyCode() == 108) {
            this.key108depressed = pressed(keyEvent);
         } else if (keyEvent.getKeyCode() == 96) {
            this.key96depressed = pressed(keyEvent);
         } else if (keyEvent.getKeyCode() == 97) {
            this.key97depressed = pressed(keyEvent);
         } else if (keyEvent.getKeyCode() == 98) {
            this.key98depressed = pressed(keyEvent);
         }
      }

      /* access modifiers changed from: package-private */
      public boolean pressed(KeyEvent keyEvent) {
         return keyEvent.getAction() == 0;
      }
   }

   class GamepadUserPointer {
      GamepadUser val;

      GamepadUserPointer() {
      }
   }

   class RemovedGamepadMemory {
      int othergamepad_pid;
      int othergamepad_vid;
      int pid;
      Gamepad.Type type;
      GamepadUser user;
      int vid;

      RemovedGamepadMemory() {
      }

      /* access modifiers changed from: package-private */
      public boolean memoriesAmbiguousIfBothPositionsEmpty() {
         return this.vid == this.othergamepad_vid && this.pid == this.othergamepad_pid;
      }

      /* access modifiers changed from: package-private */
      public boolean isTargetForAutomagicReassignment(InputDevice inputDevice) {
         return inputDevice.getVendorId() == this.vid && inputDevice.getProductId() == this.pid;
      }
   }

   public GamepadManager(Context context2) {
      this.context = context2;
      this.typeOverrideMapper = new GamepadTypeOverrideMapper(context2);
      SoundPlayer.getInstance().preload(context2, SOUND_ID_GAMEPAD_CONNECT);
      SoundPlayer.getInstance().preload(context2, SOUND_ID_GAMEPAD_DISCONNECT);
   }

   public void setGamepadIndicators(Map<GamepadUser, GamepadIndicator> map) {
      this.gamepadIndicators = map;
   }

   public void setDebug(boolean z) {
      this.debug = z;
   }

   public void setEnabled(boolean z) {
      this.enabled = z;
   }

   public void open() {
      this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
   }

   public synchronized void handleGamepadEvent(MotionEvent motionEvent) {
      Gamepad gamepadById = getGamepadById(Integer.valueOf(motionEvent.getDeviceId()));
      if (gamepadById != null) {
         gamepadById.update(motionEvent);
         indicateGamepad(getAssignedGamepadById(Integer.valueOf(motionEvent.getDeviceId())));
      }
   }

   public void handleGamepadEvent(final KeyEvent keyEvent) {
      synchronized (this) {
         final InputDevice device = InputDevice.getDevice(keyEvent.getDeviceId());
         final Gamepad gamepad = this.gamepadIdToGamepadMap.get(device.getId());
         if (gamepad != null) {
            if (gamepad.start && (gamepad.a || gamepad.b)) {
               if (gamepad.a) {
                  this.assignGamepad(device.getId(), GamepadUser.ONE);
               }
               if (gamepad.b) {
                  this.assignGamepad(device.getId(), GamepadUser.TWO);
               }
            }
            gamepad.update(keyEvent);
            this.indicateGamepad(this.getAssignedGamepadById(keyEvent.getDeviceId()));
         }
         else {
            final Gamepad knownInputDeviceToGamepad = this.knownInputDeviceToGamepad(device);
            if (knownInputDeviceToGamepad != null) {
               knownInputDeviceToGamepad.update(keyEvent);
               knownInputDeviceToGamepad.setVidPid(device.getVendorId(), device.getProductId());
               this.gamepadIdToGamepadMap.put(device.getId(), knownInputDeviceToGamepad);
               RobotLog.vv("GamepadManager", "Input device %d has been autodetected based on USB VID and PID as %s", new Object[] { device.getId(), knownInputDeviceToGamepad.getClass().getSimpleName() });
               return;
            }
            final Gamepad overriddenInputDeviceToGamepad = this.overriddenInputDeviceToGamepad(device);
            if (overriddenInputDeviceToGamepad != null) {
               overriddenInputDeviceToGamepad.update(keyEvent);
               overriddenInputDeviceToGamepad.setVidPid(device.getVendorId(), device.getProductId());
               this.gamepadIdToGamepadMap.put(device.getId(), overriddenInputDeviceToGamepad);
               RobotLog.vv("GamepadManager", "Input device %d has a USB VID and PID that has an entry in override list; treating as %s", new Object[] { device.getId(), overriddenInputDeviceToGamepad.getClass().getSimpleName() });
               return;
            }
            final GamepadManager.GamepadUserPointer gamepadUserPointer = new GamepadManager.GamepadUserPointer();
            final Gamepad guessGamepadType = this.guessGamepadType(keyEvent, gamepadUserPointer);
            if (guessGamepadType != null) {
               guessGamepadType.setVidPid(device.getVendorId(), device.getProductId());
               this.gamepadIdToGamepadMap.put(device.getId(), guessGamepadType);
               RobotLog.vv("GamepadManager", "Using quantum superposition to guess that input device %d should be treated as %s", new Object[] { device.getId(), guessGamepadType.getClass().getSimpleName() });
               if (gamepadUserPointer.val == GamepadUser.ONE) {
                  this.assignGamepad(device.getId(), GamepadUser.ONE);
               }
               else {
                  this.assignGamepad(device.getId(), GamepadUser.TWO);
               }
            }
         }
      }
   }

   public synchronized Gamepad knownInputDeviceToGamepad(InputDevice inputDevice) {
      int vendorId = inputDevice.getVendorId();
      int productId = inputDevice.getProductId();
      if (MicrosoftGamepadXbox360.matchesVidPid(vendorId, productId)) {
         return new MicrosoftGamepadXbox360();
      } else if (LogitechGamepadF310.matchesVidPid(vendorId, productId)) {
         return new LogitechGamepadF310();
      } else if (!SonyGamepadPS4.matchesVidPid(vendorId, productId)) {
         return null;
      } else {
         return new SonyGamepadPS4();
      }
   }

   public synchronized Gamepad overriddenInputDeviceToGamepad(InputDevice inputDevice) {
      GamepadTypeOverrideMapper.GamepadTypeOverrideEntry entryFor = this.typeOverrideMapper.getEntryFor(inputDevice.getVendorId(), inputDevice.getProductId());
      if (entryFor == null) {
         return null;
      }
      return entryFor.createGamepad();
   }

   public synchronized Gamepad guessGamepadType(KeyEvent keyEvent, GamepadUserPointer gamepadUserPointer) {
      GamepadPlaceholder gamepadPlaceholder = this.gamepadIdToPlaceholderMap.get(Integer.valueOf(keyEvent.getDeviceId()));
      if (gamepadPlaceholder == null) {
         gamepadPlaceholder = new GamepadPlaceholder();
         this.gamepadIdToPlaceholderMap.put(Integer.valueOf(keyEvent.getDeviceId()), gamepadPlaceholder);
      }
      gamepadPlaceholder.update(keyEvent);
      if (gamepadPlaceholder.key105depressed) {
         if (gamepadPlaceholder.key97depressed || gamepadPlaceholder.key98depressed) {
            this.gamepadIdToPlaceholderMap.remove(Integer.valueOf(keyEvent.getDeviceId()));
            if (gamepadPlaceholder.key97depressed) {
               gamepadUserPointer.val = GamepadUser.ONE;
            } else {
               gamepadUserPointer.val = GamepadUser.TWO;
            }
            return new SonyGamepadPS4();
         }
      } else if (gamepadPlaceholder.key108depressed && (gamepadPlaceholder.key96depressed || gamepadPlaceholder.key97depressed)) {
         this.gamepadIdToPlaceholderMap.remove(Integer.valueOf(keyEvent.getDeviceId()));
         if (gamepadPlaceholder.key96depressed) {
            gamepadUserPointer.val = GamepadUser.ONE;
         } else {
            gamepadUserPointer.val = GamepadUser.TWO;
         }
         return new MicrosoftGamepadXbox360();
      }
      return null;
   }

   public synchronized void removeGamepad(int i) {
      Gamepad assignedGamepadById = getAssignedGamepadById(Integer.valueOf(i));
      if (assignedGamepadById != null) {
         internalUnassignUser(assignedGamepadById.getUser());
      }
      this.gamepadIdToGamepadMap.remove(Integer.valueOf(i));
      if (this.gamepadIdToPlaceholderMap.get(Integer.valueOf(i)) != null) {
         this.gamepadIdToPlaceholderMap.remove(Integer.valueOf(i));
      }
   }

   public synchronized void unassignUser(GamepadUser gamepadUser) {
      Integer num = this.userToGamepadIdMap.get(gamepadUser);
      if (num != null) {
         this.gamepadIdToGamepadMap.remove(num);
      }
      internalUnassignUser(gamepadUser);
   }

   public synchronized List<Gamepad> getGamepadsForTransmission() {
      if (!this.enabled) {
         return Collections.emptyList();
      }
      ArrayList arrayList = new ArrayList(2);
      for (Map.Entry<GamepadUser, Integer> value : this.userToGamepadIdMap.entrySet()) {
         arrayList.add(getGamepadById((Integer) value.getValue()));
      }
      for (GamepadUser next : this.recentlyUnassignedUsers) {
         RobotLog.vv(TAG, "transmitting synthetic gamepad user=%s", next);
         Gamepad gamepad = new Gamepad();
         gamepad.setGamepadId(-2);
         gamepad.refreshTimestamp();
         gamepad.setUser(next);
         arrayList.add(gamepad);
         this.recentlyUnassignedUsers.remove(next);
      }
      return arrayList;
   }

   public synchronized Gamepad getGamepadById(Integer num) {
      if (num == null) {
         return null;
      }
      return this.gamepadIdToGamepadMap.get(num);
   }

   public synchronized Gamepad getAssignedGamepadById(Integer num) {
      if (num != null) {
         for (Map.Entry next : this.userToGamepadIdMap.entrySet()) {
            if (((Integer) next.getValue()).equals(num)) {
               return getGamepadById((Integer) next.getValue());
            }
         }
      }
      return null;
   }

   public synchronized void clearGamepadAssignments() {
      for (GamepadUser unassignUser : this.userToGamepadIdMap.keySet()) {
         unassignUser(unassignUser);
      }
      this.removedGamepadMemories.clear();
   }

   public synchronized void clearTrackedGamepads() {
      this.gamepadIdToGamepadMap.clear();
      this.gamepadIdToPlaceholderMap.clear();
   }

   /* access modifiers changed from: protected */
   public void internalUnassignUser(GamepadUser gamepadUser) {
      this.gamepadIndicators.get(gamepadUser).setState(GamepadIndicator.State.INVISIBLE);
      this.userToGamepadIdMap.remove(gamepadUser);
      this.recentlyUnassignedUsers.add(gamepadUser);
   }

   public synchronized void assignGamepad(int i, GamepadUser gamepadUser) {
      if (this.debug) {
         RobotLog.dd(TAG, "assigning gampadId=%d user=%s", Integer.valueOf(i), gamepadUser);
      }
      Iterator<RemovedGamepadMemory> it = this.removedGamepadMemories.iterator();
      while (true) {
         if (!it.hasNext()) {
            break;
         }
         RemovedGamepadMemory next = it.next();
         if (next.user == gamepadUser) {
            this.removedGamepadMemories.remove(next);
            break;
         }
      }
      for (Map.Entry next2 : this.userToGamepadIdMap.entrySet()) {
         if (((Integer) next2.getValue()).intValue() == i) {
            Integer num = this.userToGamepadIdMap.get(gamepadUser);
            if (num == null || num.intValue() != i) {
               internalUnassignUser((GamepadUser) next2.getKey());
            } else {
               return;
            }
         }
      }
      this.userToGamepadIdMap.put(gamepadUser, Integer.valueOf(i));
      this.recentlyUnassignedUsers.remove(gamepadUser);
      this.gamepadIndicators.get(gamepadUser).setState(GamepadIndicator.State.VISIBLE);
      Gamepad gamepad = this.gamepadIdToGamepadMap.get(Integer.valueOf(i));
      gamepad.setUser(gamepadUser);
      gamepad.refreshTimestamp();
      RobotLog.vv(TAG, "assigned id=%d user=%s type=%s class=%s", Integer.valueOf(i), gamepadUser, gamepad.type(), gamepad.getClass().getSimpleName());
      SoundPlayer.getInstance().play(this.context, SOUND_ID_GAMEPAD_CONNECT, 1.0f, 0, 1.0f);
   }

   /* access modifiers changed from: protected */
   public void indicateGamepad(Gamepad gamepad) {
      if (gamepad != null) {
         this.gamepadIndicators.get(gamepad.getUser()).setState(GamepadIndicator.State.INDICATE);
      }
   }

   public synchronized void onInputDeviceAdded(int i) {
      RobotLog.vv(TAG, String.format("New input device (id = %d) detected.", new Object[]{Integer.valueOf(i)}));
      considerInputDeviceForAutomagicReassignment(InputDevice.getDevice(i));
   }

   public synchronized void onInputDeviceRemoved(int i) {
      RobotLog.vv(TAG, String.format("Input device (id = %d) removed.", new Object[]{Integer.valueOf(i)}));
      Gamepad assignedGamepadById = getAssignedGamepadById(Integer.valueOf(i));
      if (assignedGamepadById != null) {
         onAssignedGamepadDropped(assignedGamepadById);
      }
      removeGamepad(i);
   }

   public synchronized void onInputDeviceChanged(int i) {
      RobotLog.vv(TAG, String.format("Input device (id = %d) modified.", new Object[]{Integer.valueOf(i)}));
   }

   public void onAssignedGamepadDropped(Gamepad gamepad) {
      Gamepad gamepad2;
      SoundPlayer.getInstance().play(this.context, SOUND_ID_GAMEPAD_DISCONNECT, 1.0f, 0, 1.0f);
      Toast.makeText(this.context, String.format("Gamepad %d connection lost", new Object[]{Byte.valueOf(gamepad.getUser().id)}), Toast.LENGTH_SHORT).show();
      RemovedGamepadMemory removedGamepadMemory = new RemovedGamepadMemory();
      removedGamepadMemory.vid = gamepad.vid;
      removedGamepadMemory.pid = gamepad.pid;
      removedGamepadMemory.user = gamepad.getUser();
      removedGamepadMemory.type = gamepad.type();
      if (gamepad.getUser() == GamepadUser.ONE) {
         gamepad2 = getAssignedGamepadById(this.userToGamepadIdMap.get(GamepadUser.TWO));
      } else {
         gamepad2 = getAssignedGamepadById(this.userToGamepadIdMap.get(GamepadUser.ONE));
      }
      if (gamepad2 != null) {
         removedGamepadMemory.othergamepad_vid = gamepad2.vid;
         removedGamepadMemory.othergamepad_pid = gamepad2.pid;
      }
      this.removedGamepadMemories.add(removedGamepadMemory);
   }

   public void considerInputDeviceForAutomagicReassignment(InputDevice inputDevice) {
      RemovedGamepadMemory removedGamepadMemory;
      Iterator<RemovedGamepadMemory> it = this.removedGamepadMemories.iterator();
      while (true) {
         if (!it.hasNext()) {
            removedGamepadMemory = null;
            break;
         }
         removedGamepadMemory = it.next();
         if (removedGamepadMemory.isTargetForAutomagicReassignment(inputDevice)) {
            break;
         }
      }
      if (removedGamepadMemory != null) {
         try {
            automagicallyReassignIfPossible(inputDevice, removedGamepadMemory);
         } catch (IllegalStateException e) {
            e.printStackTrace();
         }
         this.removedGamepadMemories.remove(removedGamepadMemory);
      }
   }

   public void automagicallyReassignIfPossible(InputDevice inputDevice, RemovedGamepadMemory removedGamepadMemory) {
      Gamepad gamepad;
      if (removedGamepadMemory.memoriesAmbiguousIfBothPositionsEmpty() && this.userToGamepadIdMap.isEmpty()) {
         this.removedGamepadMemories.clear();
         RobotLog.vv(TAG, "Input device %d was considered for automatic recovery after dropping off the USB bus; however due to ambiguity, no recovery will be performed, and all memories of previously connected gamepads will be forgotten.", Integer.valueOf(inputDevice.getId()));
         Toast.makeText(this.context, "Gamepad not recovered due to ambiguity", Toast.LENGTH_SHORT).show();
      } else if (this.userToGamepadIdMap.get(removedGamepadMemory.user) == null) {
         if (removedGamepadMemory.type == Gamepad.Type.XBOX_360) {
            gamepad = new MicrosoftGamepadXbox360();
         } else if (removedGamepadMemory.type == Gamepad.Type.LOGITECH_F310) {
            gamepad = new LogitechGamepadF310();
         } else if (removedGamepadMemory.type == Gamepad.Type.SONY_PS4) {
            gamepad = new SonyGamepadPS4();
         } else {
            throw new IllegalStateException();
         }
         RobotLog.vv(TAG, "Input device %d has been automagically recovered based on USB VID and PID after dropping off the USB bus; treating as %s because that's what we were treating it as when it dropped.", Integer.valueOf(inputDevice.getId()), gamepad.getClass().getSimpleName());
         gamepad.setVidPid(inputDevice.getVendorId(), inputDevice.getProductId());
         this.gamepadIdToGamepadMap.put(Integer.valueOf(inputDevice.getId()), gamepad);
         assignGamepad(inputDevice.getId(), removedGamepadMemory.user);
         Toast.makeText(this.context, String.format("Gamepad %d auto-recovered", new Object[]{Byte.valueOf(removedGamepadMemory.user.id)}), Toast.LENGTH_SHORT).show();
      }
   }
}