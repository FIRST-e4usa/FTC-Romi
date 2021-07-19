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

package com.dekaresearch.simulation.romi;

import android.content.Context;

import com.dekaresearch.simulation.hardware.SimAccelerationSensor;
import com.dekaresearch.simulation.hardware.SimDcMotor;
import com.dekaresearch.simulation.hardware.SimDcMotorEncoded;
import com.dekaresearch.simulation.hardware.SimDigitalChannel;
import com.dekaresearch.simulation.hardware.SimGyroSensor;
import com.dekaresearch.simulation.hardware.SimVoltageSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RomiHardwareFactory {
    public static HardwareMap createHardwareMap(Context context) {
        HardwareMap map = new HardwareMap(context);
        map.dcMotor.put("left_drive", new SimDcMotorEncoded(0, 4, 5));
        map.dcMotor.put("right_drive", new SimDcMotorEncoded(1, 6, 7));
        map.voltageSensor.put("battery", new SimVoltageSensor());

        for(int i = 0; i <= 3; i++) {
            map.digitalChannel.put("dio_" + i, new SimDigitalChannel(i));
        }

        map.gyroSensor.put("gyro", new SimGyroSensor());
        map.accelerationSensor.put("accelerometer", new SimAccelerationSensor());

        return map;
    }


}
