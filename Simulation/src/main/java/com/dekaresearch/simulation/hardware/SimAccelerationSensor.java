package com.dekaresearch.simulation.hardware;

import com.dekaresearch.simulation.data.AccelerometerData;
import com.qualcomm.robotcore.hardware.AccelerationSensor;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;

public class SimAccelerationSensor implements AccelerationSensor {

    private final AccelerometerData data;

    public SimAccelerationSensor() {
        data = AccelerometerData.getInstance();

        data.init.set(false);
        data.init.set(true);
    }

    @Override
    public Acceleration getAcceleration() {
        return Acceleration.fromGravity(
                data.x.get(),
                data.y.get(),
                data.z.get(),
                0
        );
    }

    @Override
    public String status() {
        return null;
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
