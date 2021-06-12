package com.dekaresearch.simulation.hardware;

import com.dekaresearch.simulation.data.EncoderData;
import com.dekaresearch.simulation.data.PWMData;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.RobotLog;

public class SimDcMotorEncoded implements DcMotor {
    private int port;
    private int channelA;
    private int channelB;

    private PWMData pwmData;
    private EncoderData encoderData;

    private int sign = 1;

    public SimDcMotorEncoded(int port, int channelA, int channelB) {
        this.port = port;
        this.channelA = channelA;
        this.channelB = channelB;
        this.pwmData = PWMData.getInstances()[port];
        this.encoderData = EncoderData.getInstances()[port];

        pwmData.init.set(false);
        pwmData.init.set(true);

        encoderData.init.set(false);
        encoderData.initialize(channelA, channelB);
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
        return encoderData.count.get();
    }

    @Override
    public void setMode(RunMode mode) {
        if(mode == RunMode.STOP_AND_RESET_ENCODER) {
            encoderData.reset.set(true);
            encoderData.reset.set(false);
        }
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
        pwmData.speed.set(adjustSign(power));
    }

    @Override
    public double getPower() {
        return adjustSign(pwmData.speed.get());
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
        //pwmData.init.set(false);
        //encoderData.init.set(false);
    }

    public double adjustSign(double x) {
        return x * sign;
    }
}
