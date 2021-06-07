package com.romi.simulation.data;

import org.firstinspires.ftc.robotcore.external.Supplier;

public class DriverStationData {
    private static final DriverStationData instance = new DriverStationData();

    public static DriverStationData getInstance() {
        return instance;
    }

    public SimDataValue<Boolean> enabled = new SimDataValue<>(false);
    public SimDataValue<Boolean> autonomous = new SimDataValue<>(false);
}
