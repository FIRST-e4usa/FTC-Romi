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
