package com.dekaresearch.simulation.data;

public class AIData {
    public static final int MAX_DEVICES = 4;

    private static final AIData[] instances = new AIData[MAX_DEVICES];

    static {
        for(int i = 0; i < MAX_DEVICES; i++) {
            instances[i] = new AIData();
        }
    }

    public static AIData[] getInstances() {
        return instances;
    }

    public SimDataValue<Boolean> init = new SimDataValue<>(false);
    public SimDataValue<Double> voltage = new SimDataValue<>(0.0);
}
