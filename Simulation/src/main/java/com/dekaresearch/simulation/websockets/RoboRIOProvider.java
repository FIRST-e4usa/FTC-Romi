package com.dekaresearch.simulation.websockets;

import com.google.gson.JsonElement;
import com.dekaresearch.simulation.data.RoboRIOData;

public class RoboRIOProvider extends Provider {

    private final RoboRIOData data = RoboRIOData.getInstance();

    public RoboRIOProvider() {
        super("RoboRIO", "");
    }

    @Override
    public void registerCallbacks() {
        data.vinVoltage.registerCallback(new BasicCallback<Double>(">vin_voltage"), false);
    }

    @Override
    public void unregisterCallbacks() {
        data.vinVoltage.unregisterAllCallbacks();
    }

    @Override
    public void onNetValueChanged(String key, JsonElement value) {
        if(key.equals(">vin_voltage")) {
            data.vinVoltage.set(value.getAsDouble());
        }
    }
}
