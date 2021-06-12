package com.dekaresearch.simulation.websockets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.dekaresearch.simulation.data.EncoderData;

import org.firstinspires.ftc.robotcore.external.Consumer;

public class EncoderProvider extends Provider {

    private final EncoderData data;

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
                }
                SimulationWebSocketHandler.send(getType(), getDevice(), payload);
            }
        });
        // TODO reset?
        data.count.registerCallback(new BasicCallback<Integer>(">count"), false);
    }

    @Override
    public void unregisterCallbacks() {
        data.init.unregisterAllCallbacks();
        //data.count.unregisterAllCallbacks();
    }

    @Override
    public void onNetValueChanged(String key, JsonElement value) {
        if(key.equals(">count")) {
            data.count.set(value.getAsInt());
        }
    }
}
