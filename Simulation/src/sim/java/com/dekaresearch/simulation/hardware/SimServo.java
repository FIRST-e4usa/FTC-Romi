package com.dekaresearch.simulation.hardware;

import com.dekaresearch.simulation.data.PWMData;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

public class SimServo implements Servo {

    private int port;
    private PWMData data;

    private double position = Double.NaN;
    private boolean reversed = false;
    private double min = 0.0;
    private double max = 1.0;

    public SimServo(int port) {
        this.port = port;
        this.data = PWMData.getInstances()[port];

        data.init.set(false);
        data.init.set(true);
    }

    @Override
    public ServoController getController() {
        return null;
    }

    @Override
    public int getPortNumber() {
        return port;
    }

    @Override
    public void setDirection(Direction direction) {
        reversed = direction == Direction.REVERSE;
    }

    @Override
    public Direction getDirection() {
        return reversed ? Direction.REVERSE : Direction.FORWARD;
    }

    @Override
    public void setPosition(double position) {
        if(checkBounds(position)) {
            data.position.set(scale(adjust(position)));
            this.position = position;
        }
    }

    @Override
    public double getPosition() {
        return position;
    }

    @Override
    public void scaleRange(double min, double max) {
        if(max > min && checkBounds(min) && checkBounds(max)) {
            this.min = min;
            this.max = max;
        }
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
        position = Double.NaN;
        setDirection(Direction.FORWARD);
        scaleRange(0.0, 1.0);
    }

    @Override
    public void close() {

    }

    private double scale(double position) {
        return (position * (max - min)) + min;
    }

    private double adjust(double position) {
        return reversed ? 1.0 - position : position;
    }

    private static boolean checkBounds(double x) {
        return x >= 0.0 && x <= 1.0;
    }
}
