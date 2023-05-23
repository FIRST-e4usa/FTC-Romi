/*
 * Copyright (c) 2021 Nolan Kuza
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of Nolan Kuza nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
