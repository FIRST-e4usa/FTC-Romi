package com.dekaresearch.simulation.data;

public class AccelerometerData {
    public static final int MAX_DEVICES = 1;

    private static final AccelerometerData instance = new AccelerometerData();

    public static AccelerometerData getInstance() {
        return instance;
    }

    public SimDataValue<Boolean> init = new SimDataValue<>(false);

    public SimDataValue<Double> range = new SimDataValue<>(0.0);
    public SimDataValue<Double> x = new SimDataValue<>(0.0);
    public SimDataValue<Double> y = new SimDataValue<>(0.0);
    public SimDataValue<Double> z = new SimDataValue<>(0.0);
}
