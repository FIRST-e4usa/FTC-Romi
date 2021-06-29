package com.dekaresearch.simulation.data;

public class GyroData {
    public static final int MAX_DEVICES = 1;

    private static final GyroData instance = new GyroData();

    public static GyroData getInstance() {
        return instance;
    }

    public SimDataValue<Boolean> init = new SimDataValue<>(false);

    public SimDataValue<Double> angleX = new SimDataValue<>(0.0);
    public SimDataValue<Double> angleY = new SimDataValue<>(0.0);
    public SimDataValue<Double> angleZ = new SimDataValue<>(0.0);

    public SimDataValue<Double> rateX = new SimDataValue<>(0.0);
    public SimDataValue<Double> rateY = new SimDataValue<>(0.0);
    public SimDataValue<Double> rateZ = new SimDataValue<>(0.0);
}
