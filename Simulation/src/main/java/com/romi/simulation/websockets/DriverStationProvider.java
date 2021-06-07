package com.romi.simulation.websockets;

import com.google.gson.JsonObject;
import com.romi.simulation.data.DriverStationData;

import org.firstinspires.ftc.robotcore.external.Consumer;

public class DriverStationProvider extends Provider {
    private final DriverStationData data = DriverStationData.getInstance();

    public DriverStationProvider(String type, String device) {
        super(type, device);
    }

    @Override
    public void registerCallbacks() {
        data.enabled.registerCallback(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) {
                SimulationWebSocketHandler.send(type, "", ">enabled", value);
            }
        });
    }

    @Override
    public void unregisterCallbacks() {
        data.enabled.unregisterAllCallbacks();
    }

    @Override
    public void onNetValueChanged(JsonObject jsonPayload) {

    }
}
