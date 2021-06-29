package com.dekaresearch.simulation.hardware;

import com.dekaresearch.simulation.data.EncoderData;
import com.dekaresearch.simulation.data.PWMData;
import com.dekaresearch.simulation.util.MiniPID;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerNotifier;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;

public class SimDcMotorEncoded implements DcMotor, DcMotorEx, OpModeManagerNotifier.Notifications {
    private int port;
    private int channelA;
    private int channelB;

    private PWMData pwmData;
    private EncoderData encoderData;

    private final Thread pidThread = new Thread(new PIDRunnable());
    private final MiniPID pid = new MiniPID(0.0006, 0, 0);
    private int tolerance = 50;

    private double lastPower = 0;
    private RunMode mode = RunMode.RUN_WITHOUT_ENCODER;
    private int sign = 1;
    private int targetPosition;
    private boolean isBusy;

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
        this.targetPosition = position;
    }

    @Override
    public int getTargetPosition() {
        return this.targetPosition;
    }

    @Override
    public boolean isBusy() {
        return this.isBusy;
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

        if(mode != RunMode.RUN_TO_POSITION) {
            // Ensure that the motor is set to the correct power after PID
            //pwmData.speed.set(lastPower);
        }

        this.mode = mode;
    }

    @Override
    public RunMode getMode() {
        return mode;
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
        this.lastPower = power;
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

    private double adjustSign(double x) {
        return x * sign;
    }

    private double ramp(double target, double current, double rate) {
        if((target - current) > rate) {
            return current + rate;
        } else if((current - target) > rate) {
            return current - rate;
        } else {
            return current;
        }
    }

    @Override
    public void onOpModePreInit(OpMode opMode) {

    }

    @Override
    public void onOpModePreStart(OpMode opMode) {
        if(!(opMode instanceof OpModeManagerImpl.DefaultOpMode)) {
            pwmData.speed.set(0.0);
            pidThread.start();
        }
    }

    @Override
    public void onOpModePostStop(OpMode opMode) {
        if(!(opMode instanceof OpModeManagerImpl.DefaultOpMode)) {
            pidThread.interrupt();
        }

        isBusy = false;
        mode = RunMode.RUN_WITHOUT_ENCODER;
        targetPosition = 0;
    }

    private class PIDRunnable implements Runnable {

        @Override
        public void run() {
            while(!Thread.interrupted()) {
                if(mode == RunMode.RUN_TO_POSITION) {
                    isBusy = Math.abs(getCurrentPosition() - targetPosition) > tolerance;

                    if(isBusy) {
                        pid.setOutputLimits(Math.abs(lastPower));
                        double output = pid.getOutput(getCurrentPosition(), targetPosition);
                        output = adjustSign(output);

                        pwmData.speed.set(output);
                    } else {
                        pwmData.speed.set(0.0);
                    }
                } else {
                    isBusy = false;
                }
            }
        }
    }

    // region DC Motor Ex
    @Override
    public void setMotorEnable() {

    }

    @Override
    public void setMotorDisable() {

    }

    @Override
    public boolean isMotorEnabled() {
        return true;
    }

    @Override
    public void setVelocity(double angularRate) {

    }

    @Override
    public void setVelocity(double angularRate, AngleUnit unit) {

    }

    @Override
    public double getVelocity() {
        return 0;
    }

    @Override
    public double getVelocity(AngleUnit unit) {
        return 0;
    }

    @Override
    public void setPIDCoefficients(RunMode mode, PIDCoefficients pidCoefficients) {

    }

    @Override
    public void setPIDFCoefficients(RunMode mode, PIDFCoefficients pidfCoefficients) throws UnsupportedOperationException {

    }

    @Override
    public void setVelocityPIDFCoefficients(double p, double i, double d, double f) {

    }

    @Override
    public void setPositionPIDFCoefficients(double p) {

    }

    @Override
    public PIDCoefficients getPIDCoefficients(RunMode mode) {
        return null;
    }

    @Override
    public PIDFCoefficients getPIDFCoefficients(RunMode mode) {
        return null;
    }

    @Override
    public void setTargetPositionTolerance(int tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public int getTargetPositionTolerance() {
        return this.tolerance;
    }

    @Override
    public double getCurrent(CurrentUnit unit) {
        return 0;
    }

    @Override
    public double getCurrentAlert(CurrentUnit unit) {
        return 0;
    }

    @Override
    public void setCurrentAlert(double current, CurrentUnit unit) {

    }

    @Override
    public boolean isOverCurrent() {
        return false;
    }
    //endregion
}
