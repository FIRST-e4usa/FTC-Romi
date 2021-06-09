package com.dekaresearch.simulation.hardware;

import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.dekaresearch.simulation.data.RoboRIOData;

public class SimVoltageSensor implements VoltageSensor {
    @Override
    public double getVoltage() {
        return RoboRIOData.getInstance().vinVoltage.get();
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
