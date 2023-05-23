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
