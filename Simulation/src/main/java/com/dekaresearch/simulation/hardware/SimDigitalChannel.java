package com.dekaresearch.simulation.hardware;

import com.dekaresearch.simulation.data.DIOData;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.util.RobotLog;

public class SimDigitalChannel implements DigitalChannel {

    private int port;
    private DIOData data;

    public SimDigitalChannel(int port) {
        this.port = port;
        this.data = DIOData.getInstances()[port];
    }

    @Override
    public Mode getMode() {
        return data.input.get() ? Mode.INPUT : Mode.OUTPUT;
    }

    @Override
    public void setMode(Mode mode) {
        data.input.set(mode == Mode.INPUT);
    }

    @Override
    public boolean getState() {
        return data.value.get();
    }

    @Override
    public void setState(boolean state) {
        if(!data.input.get()) {
            data.value.set(state);
        }
    }

    @Override
    @Deprecated public void setMode(DigitalChannelController.Mode mode) {
        data.input.set(mode == DigitalChannelController.Mode.INPUT);
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
        data.init.set(false);
        data.init.set(true);
    }

    @Override
    public void close() {
        data.init.set(false);
    }
}
