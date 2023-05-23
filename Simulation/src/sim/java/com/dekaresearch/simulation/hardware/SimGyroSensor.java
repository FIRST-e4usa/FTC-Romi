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

package com.dekaresearch.simulation.hardware;

import com.dekaresearch.simulation.data.GyroData;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.IntegratingGyroscope;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Axis;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.internal.android.dx.ssa.SetFactory;

import java.util.HashSet;
import java.util.Set;

public class SimGyroSensor implements GyroSensor, IntegratingGyroscope {

    private final GyroData data;

    public SimGyroSensor() {
        data = GyroData.getInstance();

        data.init.set(false);
        data.init.set(true);
    }

    @Override
    public void calibrate() {

    }

    @Override
    public boolean isCalibrating() {
        return false;
    }

    @Override
    public int getHeading() {
        return 0;
    }

    @Override
    public double getRotationFraction() {
        return 0;
    }

    @Override
    public int rawX() {
        return 0;
    }

    @Override
    public int rawY() {
        return 0;
    }

    @Override
    public int rawZ() {
        return 0;
    }

    @Override
    public void resetZAxisIntegrator() {

    }

    @Override
    public String status() {
        return null;
    }

    @Override
    public Set<Axis> getAngularVelocityAxes() {
        Set<Axis> result = new HashSet<>();
        result.add(Axis.X);
        result.add(Axis.Y);
        result.add(Axis.Z);
        return result;
    }

    @Override
    public AngularVelocity getAngularVelocity(AngleUnit unit) {
        AngularVelocity angularVelocity =
                new AngularVelocity(AngleUnit.DEGREES,
                        data.rateX.get().floatValue(),
                        data.rateY.get().floatValue(),
                        data.rateZ.get().floatValue(),
                        0);

        return angularVelocity.toAngleUnit(unit);
    }

    @Override
    public Set<Axis> getAngularOrientationAxes() {
        Set<Axis> result = new HashSet<>();
        result.add(Axis.X);
        result.add(Axis.Y);
        result.add(Axis.Z);
        return result;
    }

    @Override
    public Orientation getAngularOrientation(AxesReference reference, AxesOrder order, AngleUnit angleUnit) {
        Orientation orientation =
                new Orientation(
                        reference,
                        order,
                        AngleUnit.DEGREES,
                        data.angleX.get().floatValue(),
                        data.angleY.get().floatValue(),
                        data.angleZ.get().floatValue(),
                        0
                );

        return orientation.toAngleUnit(angleUnit);
    }

    @Override
    public Manufacturer getManufacturer() {
        return null;
    }

    @Override
    public String getDeviceName() {
        return null;
    }

    @Override
    public String getConnectionInfo() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {

    }

    @Override
    public void close() {

    }
}
