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
        if(key.equals(">period")) {
            data.period.set(value.getAsDouble());
        }
    }
}
