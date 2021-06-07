package com.romi.simulation.websockets;

import com.google.gson.JsonObject;

public abstract class Provider {
    private final String type;
    private final String device;

    public Provider(String type, String device) {
        this.type = type;
        this.device = device;
    }

    public abstract void registerCallbacks();
    public abstract void unregisterCallbacks();
    public abstract void onNetValueChanged(final JsonObject jsonPayload);
}
