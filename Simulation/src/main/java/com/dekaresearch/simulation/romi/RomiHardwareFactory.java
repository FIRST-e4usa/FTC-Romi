package com.dekaresearch.simulation.romi;

import android.content.Context;

import com.dekaresearch.simulation.hardware.SimDcMotor;
import com.dekaresearch.simulation.hardware.SimVoltageSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RomiHardwareFactory {
    public static HardwareMap createHardwareMap(Context context) {
        HardwareMap map = new HardwareMap(context);
        map.dcMotor.put("left_drive", new SimDcMotor(0));
        map.dcMotor.put("right_drive", new SimDcMotor(1));
        map.voltageSensor.put("battery", new SimVoltageSensor());

        return map;
    }


}
