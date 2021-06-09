package com.romi.simulation.hardware;

import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.RobotLog;
import com.romi.simulation.data.PWMData;

public class SimDcMotor implements DcMotor {

    private int port;
    private PWMData data;

    private int sign = 1;

    public SimDcMotor(int port) {
        this.port = port;
        this.data = PWMData.getInstances()[port];
    }

    @Override
    public MotorConfigurationType getMotorType() {
        return null;
    }

    @Override
    public void setMotorType(MotorConfigurationType motorType) {

    }

    @Override
    public DcMotorController getController() {
        return null;
    }

    @Override
    public int getPortNumber() {
        return port;
    }

    @Override
    public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
        RobotLog.addGlobalWarningMessage("Cannot set zero power behavior on a simulated motor");
    }

    @Override
    public ZeroPowerBehavior getZeroPowerBehavior() {
        return ZeroPowerBehavior.UNKNOWN;
    }

    @Override
    public void setPowerFloat() {
        RobotLog.addGlobalWarningMessage("Cannot set power float on a simulated motor");
        setPower(0);
    }

    @Override
    public boolean getPowerFloat() {
        return false;
    }

    @Override
    public void setTargetPosition(int position) {

    }

    @Override
    public int getTargetPosition() {
        return 0;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void setMode(RunMode mode) {

    }

    @Override
    public RunMode getMode() {
        return null;
    }

    @Override
    public void setDirection(Direction direction) {
        this.sign = direction == Direction.REVERSE ? -1 : 1;
    }

    @Override
    public Direction getDirection() {
        return sign == -1 ? Direction.REVERSE : Direction.FORWARD;
    }

    @Override
    public void setPower(double power) {
        data.speed.set(adjustSign(power));
    }

    @Override
    public double getPower() {
        return adjustSign(data.speed.get());
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

    public double adjustSign(double x) {
        return x * sign;
    }
}
