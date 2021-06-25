package com.dekaresearch.simulation.data;

public class PWMData {
    public static final int MAX_DEVICES = 2;

    private static final PWMData[] instances = new PWMData[MAX_DEVICES];

    static {
        for(int i = 0; i < MAX_DEVICES; i++) {
            instances[i] = new PWMData();
        }
    }

    public static PWMData[] getInstances() {
        return instances;
    }

    public SimDataValue<Boolean> init = new SimDataValue<>(false);
    public SimDataValue<Double> speed = new SimDataValue<>(0.0);
    public SimDataValue<Double> position = new SimDataValue<>(0.0);
}
