package com.dekaresearch.simulation.websockets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SimulationWebSocketHandler {

    private static final String TAG = "SimulationWebSocketHandler";

    public static void send(String type, String device, JsonObject payload) {
        SimulationWebSocketClient client = SimulationWebSocketClient.getInstance();

        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.addProperty("device", device);
        object.add("data", payload);

        String data = WebSocketJson.getInstance().toJson(object);

        SimulationWebSocketClient.getInstance().send(data);
    }

    public static void send(String type, String device, String field, JsonElement value) {
        JsonObject payload = new JsonObject();
        payload.add(field, value);

        send(type, device, payload);
    }

    public static <T> void send(String type, String device, String field, T value) {
        if(value instanceof Integer) {
            send(type, device, field, ((Integer)value).intValue());
        } else if(value instanceof Double) {
            send(type, device, field, ((Double)value).doubleValue());
        } else if(value instanceof Boolean) {
            send(type, device, field, ((Boolean)value).booleanValue());
        } else if(value instanceof String) {
            send(type, device, field, (String) value);
        }
    }

    public static void send(String type, String device, String field, int value) {
        send(type, device, field, new JsonPrimitive(value));
    }

    public static void send(String type, String device, String field, double value) {
        send(type, device, field, new JsonPrimitive(value));
    }

    public static void send(String type, String device, String field, boolean value) {
        send(type, device, field, new JsonPrimitive(value));
    }

    public static void send(String type, String device, String field, String value) {
        send(type, device, field, new JsonPrimitive(value));
    }

    public static void send(String type, String device, String field, double[] value) {
        JsonArray array = new JsonArray();
        for(double d : value) {
            array.add(d);
        }
        send(type, device, field, array);
    }

    public static JsonObject receive(String message) {
        return WebSocketJson.getInstance().fromJson(message, JsonObject.class);
    }
}