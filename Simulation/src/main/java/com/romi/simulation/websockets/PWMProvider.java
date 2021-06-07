package com.romi.simulation.websockets;

import com.google.gson.JsonElement;
import com.romi.simulation.data.PWMData;

public class PWMProvider extends Provider {

    private final PWMData data;

    public PWMProvider(String type, String device) {
        super(type, device);

        int port = Integer.parseInt(getDevice());
        if(port < PWMData.getInstances().length) {
            data = PWMData.getInstances()[port];
        } else {
            // TODO(Romi) change error handling
            data = PWMData.getInstances()[0];
        }
    }

    @Override
    public void registerCallbacks() {
        data.init.registerCallback(new BasicCallback<Boolean>("<init"));
        data.speed.registerCallback(new BasicCallback<Double>("<speed"));
        data.position.registerCallback(new BasicCallback<Double>("<position"));
    }

    @Override
    public void unregisterCallbacks() {
        data.init.unregisterAllCallbacks();
        data.speed.unregisterAllCallbacks();
        data.position.unregisterAllCallbacks();
    }

    @Override
    public void onNetValueChanged(String key, JsonElement value) {

    }
}
