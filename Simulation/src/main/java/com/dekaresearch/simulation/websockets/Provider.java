package com.dekaresearch.simulation.websockets;

import com.google.gson.JsonElement;

import org.firstinspires.ftc.robotcore.external.Consumer;

public abstract class Provider {
    private final String type;
    private final String device;

    public Provider(String type, String device) {
        this.type = type;
        this.device = device;
    }

    public String getType() {
        return type;
    }

    public String getDevice() {
        return device;
    }

    public abstract void registerCallbacks();
    public abstract void unregisterCallbacks();
    public abstract void onNetValueChanged(String key, JsonElement value);

    public class BasicCallback<T> implements Consumer<T> {
        private final String field;

        public BasicCallback(String field) {
            this.field = field;
        }

        @Override
        public void accept(T value) {
            SimulationWebSocketHandler.send(getType(), getDevice(), field, value);
        }
    }
}
