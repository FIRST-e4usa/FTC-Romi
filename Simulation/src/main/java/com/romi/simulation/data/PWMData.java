package com.romi.simulation.data;

public class PWMData {
    private static final PWMData[] instances = new PWMData[2];

    static {
        instances[0] = new PWMData();
        instances[1] = new PWMData();
    }

    public static PWMData[] getInstances() {
        return instances;
    }

    public SimDataValue<Boolean> init = new SimDataValue<>(false);
    public SimDataValue<Double> speed = new SimDataValue<>(0.0);
    public SimDataValue<Double> position = new SimDataValue<>(0.0);
}
