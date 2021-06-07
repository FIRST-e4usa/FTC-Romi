package com.romi.simulation.websockets;

import com.romi.simulation.data.DriverStationData;

import org.firstinspires.ftc.robotcore.external.Consumer;

public class DriverStationSupplier {

    private static final String type = "DriverStation";

    private final DriverStationData data = DriverStationData.getInstance();

    public DriverStationSupplier() {

    }

    public void registerCallbacks() {
        data.enabled.registerCallback(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) {
                SimulationWebSocketHandler.send(type, "", ">enabled", value);
            }
        });
    }

    public void unregisterCallbacks() {
        data.enabled.unregisterAllCallbacks();
    }
}
