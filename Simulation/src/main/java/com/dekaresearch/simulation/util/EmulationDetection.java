package com.dekaresearch.simulation.util;

import android.os.Environment;

import java.io.File;

public class EmulationDetection {
    public static boolean isBlueStacks() {
        String path = Environment.getExternalStorageDirectory().toString();
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files == null) return false;
        for (File file : files) {
            if (file.getName().contains("windows")) {
                return true;
            }
        }
        return false;
    }
}
