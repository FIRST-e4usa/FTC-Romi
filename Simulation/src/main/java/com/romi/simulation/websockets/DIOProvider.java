package com.romi.simulation.websockets;

import com.google.gson.JsonElement;
import com.romi.simulation.data.DIOData;

public class DIOProvider extends Provider {

    private final DIOData data;

    public DIOProvider(String device) {
        super("DIO", device);

        int port = Integer.parseInt(device);
        if(port >= DIOData.MAX_DEVICES) {
            port = 0;
        }
        data = DIOData.getInstances()[port];
    }

    @Override
    public void registerCallbacks() {
        data.init.registerCallback(new BasicCallback<Boolean>("<init"));
        data.input.registerCallback(new BasicCallback<Boolean>("<input"));
        data.value.registerCallback(new BasicCallback<Boolean>("<>value"));
    }

    @Override
    public void unregisterCallbacks() {
        data.init.unregisterAllCallbacks();
        data.input.unregisterAllCallbacks();
        data.value.unregisterAllCallbacks();
    }

    @Override
    public void onNetValueChanged(String key, JsonElement value) {
        if(key.equals("<>value")) {
            data.value.set(value.getAsBoolean());
        }
    }
}
