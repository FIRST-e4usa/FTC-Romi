package com.dekaresearch.simulation.websockets;

import com.dekaresearch.simulation.data.AccelerometerData;
import com.google.gson.JsonElement;

public class AccelerometerProvider extends Provider {
    private final AccelerometerData data;

    public AccelerometerProvider(String device) {
        super("Accel", device);

        data = AccelerometerData.getInstance();
    }

    @Override
    public void registerCallbacks() {
        data.init.registerCallback(new BasicCallback<Boolean>(">init"));
        data.range.registerCallback(new BasicCallback<Double>(">range"));
    }

    @Override
    public void unregisterCallbacks() {
        data.init.unregisterAllCallbacks();
        data.range.unregisterAllCallbacks();
    }

    @Override
    public void onNetValueChanged(String key, JsonElement value) {
        if(key.equals(">x")) data.x.set(value.getAsDouble());
        if(key.equals(">y")) data.y.set(value.getAsDouble());
        if(key.equals(">z")) data.z.set(value.getAsDouble());
    }
}
