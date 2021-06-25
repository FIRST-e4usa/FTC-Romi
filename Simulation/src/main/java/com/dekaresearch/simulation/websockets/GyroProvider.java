package com.dekaresearch.simulation.websockets;

import com.google.gson.JsonElement;
import com.dekaresearch.simulation.data.GyroData;

public class GyroProvider extends Provider {
    private final GyroData data;

    public GyroProvider(String device) {
        super("Gyro", device);

        data = GyroData.getInstance();
    }

    @Override
    public void registerCallbacks() {
        data.init.registerCallback(new BasicCallback<Boolean>("<init"));
        //data.angleX.registerCallback(new BasicCallback<Double>(">angle_x"), false);
        //data.angleY.registerCallback(new BasicCallback<Double>(">angle_y"), false);
        //data.angleZ.registerCallback(new BasicCallback<Double>(">angle_z"), false);
        //data.rateX.registerCallback(new BasicCallback<Double>(">rate_x"), false);
        //data.rateY.registerCallback(new BasicCallback<Double>(">rate_y"), false);
        //data.rateZ.registerCallback(new BasicCallback<Double>(">rate_z"), false);
    }

    @Override
    public void unregisterCallbacks() {
        data.init.unregisterAllCallbacks();
        data.angleX.unregisterAllCallbacks();
        data.angleY.unregisterAllCallbacks();
        data.angleZ.unregisterAllCallbacks();
        data.rateX.unregisterAllCallbacks();
        data.rateY.unregisterAllCallbacks();
        data.rateZ.unregisterAllCallbacks();
    }

    @Override
    public void onNetValueChanged(String key, JsonElement value) {
        if(key.equals(">angle_x")) data.angleX.set(value.getAsDouble());
        if(key.equals(">angle_y")) data.angleY.set(value.getAsDouble());
        if(key.equals(">angle_z")) data.angleZ.set(value.getAsDouble());
        if(key.equals(">rate_x")) data.rateX.set(value.getAsDouble());
        if(key.equals(">rate_y")) data.rateY.set(value.getAsDouble());
        if(key.equals(">rate_z")) data.rateZ.set(value.getAsDouble());
    }
}
