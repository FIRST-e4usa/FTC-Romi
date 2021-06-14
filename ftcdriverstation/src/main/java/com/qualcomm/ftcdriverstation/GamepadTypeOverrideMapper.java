package com.qualcomm.ftcdriverstation;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.ArraySet;
import com.qualcomm.hardware.logitech.LogitechGamepadF310;
import com.qualcomm.hardware.microsoft.MicrosoftGamepadXbox360;
import com.qualcomm.hardware.sony.SonyGamepadPS4;
import com.qualcomm.robotcore.hardware.Gamepad;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class GamepadTypeOverrideMapper {
    static final String KEY_GAMEPAD_MAPPING = "GAMEPAD_MAPPING";
    Context context;
    Set<String> serializedEntries;
    SharedPreferences sharedPreferences;

    static class GamepadTypeOverrideEntry {
        Gamepad.Type mappedType;
        int pid;
        int vid;

        GamepadTypeOverrideEntry(int i, int i2, Gamepad.Type type) {
            this.vid = i;
            this.pid = i2;
            this.mappedType = type;
        }

        public boolean equals(GamepadTypeOverrideEntry gamepadTypeOverrideEntry) {
            boolean z = false;
            boolean z2 = (this.vid == gamepadTypeOverrideEntry.vid) & true;
            if (this.pid == gamepadTypeOverrideEntry.pid) {
                z = true;
            }
            return this.mappedType.equals(gamepadTypeOverrideEntry.mappedType) & z2 & z;
        }

        public boolean usbIdsMatch(GamepadTypeOverrideEntry gamepadTypeOverrideEntry) {
            boolean z = false;
            boolean z2 = (this.vid == gamepadTypeOverrideEntry.vid) & true;
            if (this.pid == gamepadTypeOverrideEntry.pid) {
                z = true;
            }
            return z2 & z;
        }

        public boolean usbIdsMatch(int i, int i2) {
            boolean z = false;
            boolean z2 = (this.vid == i) & true;
            if (this.pid == i2) {
                z = true;
            }
            return z2 & z;
        }

        /* access modifiers changed from: package-private */
        public Gamepad createGamepad() {
            int i = this.mappedType.ordinal();
            if (i == 1) {
                return new LogitechGamepadF310();
            }
            if (i == 2) {
                return new MicrosoftGamepadXbox360();
            }
            if (i == 3) {
                return new SonyGamepadPS4();
            }
            throw new IllegalStateException();
        }

        public String toString() {
            return String.format("%d:%d:%s", new Object[]{Integer.valueOf(this.vid), Integer.valueOf(this.pid), this.mappedType.toString()});
        }

        static GamepadTypeOverrideEntry fromString(String str) {
            String[] split = str.split(":");
            return new GamepadTypeOverrideEntry(Integer.valueOf(split[0]).intValue(), Integer.valueOf(split[1]).intValue(), Gamepad.Type.valueOf(split[2]));
        }
    }



    GamepadTypeOverrideMapper(Context context2) {
        this.context = context2;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context2);
        this.sharedPreferences = defaultSharedPreferences;
        this.serializedEntries = defaultSharedPreferences.getStringSet(KEY_GAMEPAD_MAPPING, (Set) null);
    }

    static String checkForClash(Set<String> set, GamepadTypeOverrideEntry gamepadTypeOverrideEntry) {
        for (String next : set) {
            if (GamepadTypeOverrideEntry.fromString(next).usbIdsMatch(gamepadTypeOverrideEntry)) {
                return next;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public synchronized void setEntries(ArrayList<GamepadTypeOverrideEntry> arrayList) {
        if (this.serializedEntries != null) {
            this.serializedEntries.clear();
        } else {
            this.serializedEntries = new ArraySet();
        }
        if (arrayList.isEmpty()) {
            this.sharedPreferences.edit().remove(KEY_GAMEPAD_MAPPING).commit();
        } else {
            Iterator<GamepadTypeOverrideEntry> it = arrayList.iterator();
            while (it.hasNext()) {
                this.serializedEntries.add(it.next().toString());
            }
            this.sharedPreferences.edit().remove(KEY_GAMEPAD_MAPPING).commit();
            this.sharedPreferences.edit().putStringSet(KEY_GAMEPAD_MAPPING, this.serializedEntries).commit();
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void addOrUpdate(GamepadTypeOverrideEntry gamepadTypeOverrideEntry) {
        Set<String> stringSet = this.sharedPreferences.getStringSet(KEY_GAMEPAD_MAPPING, (Set) null);
        this.serializedEntries = stringSet;
        if (stringSet != null) {
            String checkForClash = checkForClash(stringSet, gamepadTypeOverrideEntry);
            if (checkForClash != null) {
                this.serializedEntries.remove(checkForClash);
            }
            this.serializedEntries.add(gamepadTypeOverrideEntry.toString());
        } else {
            ArraySet arraySet = new ArraySet();
            this.serializedEntries = arraySet;
            arraySet.add(gamepadTypeOverrideEntry.toString());
        }
        this.sharedPreferences.edit().putStringSet(KEY_GAMEPAD_MAPPING, this.serializedEntries).commit();
    }

    /* access modifiers changed from: package-private */
    public synchronized GamepadTypeOverrideEntry getEntryFor(int i, int i2) {
        Iterator<GamepadTypeOverrideEntry> it = getEntries().iterator();
        while (it.hasNext()) {
            GamepadTypeOverrideEntry next = it.next();
            if (next.usbIdsMatch(i, i2)) {
                return next;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public synchronized ArrayList<GamepadTypeOverrideEntry> getEntries() {
        Set<String> stringSet = this.sharedPreferences.getStringSet(KEY_GAMEPAD_MAPPING, (Set) null);
        this.serializedEntries = stringSet;
        if (stringSet == null) {
            return new ArrayList<>();
        }
        ArrayList<GamepadTypeOverrideEntry> arrayList = new ArrayList<>();
        for (String fromString : this.serializedEntries) {
            arrayList.add(GamepadTypeOverrideEntry.fromString(fromString));
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public synchronized void delete(GamepadTypeOverrideEntry gamepadTypeOverrideEntry) {
        Set<String> stringSet = this.sharedPreferences.getStringSet(KEY_GAMEPAD_MAPPING, (Set) null);
        this.serializedEntries = stringSet;
        if (stringSet != null) {
            boolean z = false;
            for (String equals : stringSet) {
                if (equals.equals(gamepadTypeOverrideEntry.toString())) {
                    z = true;
                }
            }
            if (z) {
                this.serializedEntries.remove(gamepadTypeOverrideEntry.toString());
                this.sharedPreferences.edit().putStringSet(KEY_GAMEPAD_MAPPING, this.serializedEntries).commit();
                return;
            }
            throw new IllegalArgumentException();
        }
    }
}