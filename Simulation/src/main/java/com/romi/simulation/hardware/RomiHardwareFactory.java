package com.romi.simulation.hardware;

import android.content.Context;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class RomiHardwareFactory {
    public static HardwareMap createHardwareMap(Context context) {
        HardwareMap map = new HardwareMap(context);
        map.dcMotor.put("motor_0", new SimDcMotor(0));
        map.dcMotor.put("motor_1", new SimDcMotor(1));
        map.voltageSensor.put("roborio", new SimVoltageSensor());

        return map;
    }
}
