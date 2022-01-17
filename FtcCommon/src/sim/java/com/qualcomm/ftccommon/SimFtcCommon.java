package com.qualcomm.ftccommon;

import android.view.KeyEvent;
import android.view.MotionEvent;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.Range;

import com.dekaresearch.simulation.websockets.SimulationWebSocketClient;
import org.firstinspires.ftc.robotcore.internal.network.NetworkStatus;

public class SimFtcCommon {
    public static void setSimulationCallback(final UpdateUI.Callback callback) {
        SimulationWebSocketClient.getInstance().setListener(new SimulationWebSocketClient.Listener() {
            @Override
            public void onOpen() {
                RobotLog.ii("Simulation", "callback open");
                callback.setWebSocketStatus("connected");
                callback.updateNetworkConnectionStatus(NetworkStatus.ENABLED);
            }

            @Override
            public void onClose() {
                RobotLog.ii("Simulation", "callback close");
                callback.setWebSocketStatus("disconnected");
                callback.updateNetworkConnectionStatus(NetworkStatus.ACTIVE);
            }

            @Override
            public void onOpening() {
                callback.setWebSocketStatus("connecting...");
            }
        });
    }

    protected static float dpadThreshold = 0.2f;

    public static void updateGamepad(Gamepad gamepad, android.view.MotionEvent event) {

        gamepad.setGamepadId(event.getDeviceId());
        gamepad.setTimestamp(event.getEventTime());

        gamepad.left_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_X));
        gamepad.left_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Y));
        gamepad.right_stick_x = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_Z));
        gamepad.right_stick_y = cleanMotionValues(event.getAxisValue(MotionEvent.AXIS_RZ));
        gamepad.left_trigger = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);
        gamepad.right_trigger = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
        gamepad.dpad_down = event.getAxisValue(MotionEvent.AXIS_HAT_Y) > dpadThreshold;
        gamepad.dpad_up = event.getAxisValue(MotionEvent.AXIS_HAT_Y) < -dpadThreshold;
        gamepad.dpad_right = event.getAxisValue(MotionEvent.AXIS_HAT_X) > dpadThreshold;
        gamepad.dpad_left = event.getAxisValue(MotionEvent.AXIS_HAT_X) < -dpadThreshold;
    }

    /**
     * Update the gamepad based on a KeyEvent
     * @param event key event
     */
    public static void updateGamepad(Gamepad gamepad, android.view.KeyEvent event) {

        gamepad.setGamepadId(event.getDeviceId());
        gamepad.setTimestamp(event.getEventTime());

        int key = event.getKeyCode();
        if      (key == KeyEvent.KEYCODE_DPAD_UP) gamepad.dpad_up = pressed(event);
        else if (key == KeyEvent.KEYCODE_DPAD_DOWN) gamepad.dpad_down = pressed(event);
        else if (key == KeyEvent.KEYCODE_DPAD_RIGHT) gamepad.dpad_right = pressed(event);
        else if (key == KeyEvent.KEYCODE_DPAD_LEFT) gamepad.dpad_left = pressed(event);
        else if (key == KeyEvent.KEYCODE_BUTTON_A) gamepad.a = pressed(event);
        else if (key == KeyEvent.KEYCODE_BUTTON_B) gamepad.b = pressed(event);
        else if (key == KeyEvent.KEYCODE_BUTTON_X) gamepad.x = pressed(event);
        else if (key == KeyEvent.KEYCODE_BUTTON_Y) gamepad.y = pressed(event);
        else if (key == KeyEvent.KEYCODE_BUTTON_MODE) gamepad.guide = pressed(event);
        else if (key == KeyEvent.KEYCODE_BUTTON_START) gamepad.start = pressed(event);

            // Handle "select" and "back" key codes as a "back" button event.
        else if (key == KeyEvent.KEYCODE_BUTTON_SELECT || key == KeyEvent.KEYCODE_BACK) gamepad.back = pressed(event);

        else if (key == KeyEvent.KEYCODE_BUTTON_R1) gamepad.right_bumper = pressed(event);
        else if (key == KeyEvent.KEYCODE_BUTTON_L1) gamepad.left_bumper = pressed(event);
        else if (key == KeyEvent.KEYCODE_BUTTON_THUMBL) gamepad.left_stick_button = pressed(event);
        else if (key == KeyEvent.KEYCODE_BUTTON_THUMBR) gamepad.right_stick_button = pressed(event);
    }

    private static final float MAX_MOTION_RANGE = 1.0f;
    protected static float joystickDeadzone = 0.2f;

    // clean values
    // remove values larger than max
    // apply deadzone logic
    protected static float cleanMotionValues(float number) {

        // apply deadzone
        if (number < joystickDeadzone && number > -joystickDeadzone) return 0.0f;

        // apply trim
        if (number >  MAX_MOTION_RANGE) return  MAX_MOTION_RANGE;
        if (number < -MAX_MOTION_RANGE) return -MAX_MOTION_RANGE;

        // scale values "between deadzone and trim" to be "between 0 and Max range"
        if (number > 0)
            number = (float)Range.scale(number, joystickDeadzone, MAX_MOTION_RANGE, 0, MAX_MOTION_RANGE);
        else
            number = (float)Range.scale(number, -joystickDeadzone, -MAX_MOTION_RANGE, 0, -MAX_MOTION_RANGE);

        return number;
    }

    protected static boolean pressed(android.view.KeyEvent event) {
        return event.getAction() == KeyEvent.ACTION_DOWN;
    }
}
