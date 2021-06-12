package com.dekaresearch.simulation.websockets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.dekaresearch.simulation.data.EncoderData;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Consumer;

public class EncoderProvider extends Provider {

    private final EncoderData data;

    private int countOffset = 0;

    public EncoderProvider(String device) {
        super("Encoder", device);

        int id = Integer.parseInt(device);
        if(id >= EncoderData.MAX_DEVICES) {
            id = 0;
        }
        data = EncoderData.getInstances()[id];
    }

    @Override
    public void registerCallbacks() {
        data.init.registerCallback(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) {
                JsonObject payload = new JsonObject();
                payload.addProperty("<init", value);
                if(value) {
                    payload.addProperty("<channel_a", data.getChannelA());
                    payload.addProperty("<channel_b", data.getChannelB());
                } else {
                    countOffset = 0;
                }
                SimulationWebSocketHandler.send(getType(), getDevice(), payload);
            }
        });
        //data.count.registerCallback(new BasicCallback<Integer>(">count"), false);
        data.reset.registerCallback(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean value) {
                if(value) {
                    RobotLog.ii("ENCODER", data.count.get() + " " + countOffset);
                    countOffset += data.count.get();
                    RobotLog.ii("ENCODER", countOffset + "");
                }
            }
        });
    }

    @Override
    public void unregisterCallbacks() {
        data.init.unregisterAllCallbacks();
        data.count.unregisterAllCallbacks();
        data.reset.unregisterAllCallbacks();
    }

    @Override
    public void onNetValueChanged(String key, JsonElement value) {
        if(key.equals(">count")) {
            data.count.set(value.getAsInt() - countOffset);
        }
    }
}
