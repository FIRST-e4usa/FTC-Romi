package com.dekaresearch.simulation.websockets;

import com.dekaresearch.simulation.data.AIData;
import com.google.gson.JsonElement;

public class AIProvider extends Provider {

    private final AIData data;

    public AIProvider(String device) {
        super("AI", device);

        int port = Integer.parseInt(device);
        if(port >= AIData.MAX_DEVICES) {
            port = 0;
        }
        data = AIData.getInstances()[port];
    }

    @Override
    public void registerCallbacks() {
        data.init.registerCallback(new BasicCallback<Boolean>("<init"));
        data.voltage.registerCallback(new BasicCallback<Double>(">voltage"));
    }

    @Override
    public void unregisterCallbacks() {
        data.init.unregisterAllCallbacks();
        data.voltage.unregisterAllCallbacks();
    }

    @Override
    public void onNetValueChanged(String key, JsonElement value) {
        if(key.equals(">voltage")) {
            data.voltage.set(value.getAsDouble());
        }
    }
}
