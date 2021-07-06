package com.dekaresearch.simulation.hardware;

import com.dekaresearch.simulation.data.AIData;
import com.qualcomm.robotcore.hardware.AnalogInputController;
import com.qualcomm.robotcore.util.SerialNumber;

public class SimAnalogInputController implements AnalogInputController {

    public SimAnalogInputController() {
        for(AIData data : AIData.getInstances()) {
            data.init.set(false);
            data.init.set(true);
        }
    }

    @Override
    public double getAnalogInputVoltage(int channel) {
        return AIData.getInstances()[channel].voltage.get();
    }

    @Override
    public double getMaxAnalogInputVoltage() {
        return 5;
    }

    @Override
    public SerialNumber getSerialNumber() {
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
