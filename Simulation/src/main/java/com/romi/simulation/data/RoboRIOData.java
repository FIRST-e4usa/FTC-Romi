package com.romi.simulation.data;

public class RoboRIOData {
    private static final RoboRIOData instance = new RoboRIOData();

    public static RoboRIOData getInstance() {
        return instance;
    }

    public SimDataValue<Double> vinVoltage = new SimDataValue<>(0.0);
}
