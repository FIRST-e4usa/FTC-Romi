package com.romi.simulation.data;

public class DIOData {
    public static final int MAX_DEVICES = 8;

    private static final DIOData[] instances = new DIOData[MAX_DEVICES];

    static {
        for(int i = 0; i < MAX_DEVICES; i++) {
            instances[i] = new DIOData();
        }
    }

    public static DIOData[] getInstances() {
        return instances;
    }

    public SimDataValue<Boolean> init = new SimDataValue<>(false);
    public SimDataValue<Boolean> input = new SimDataValue<>(false);
    public SimDataValue<Boolean> value = new SimDataValue<>(false);
}
