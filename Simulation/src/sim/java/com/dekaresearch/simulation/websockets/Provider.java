/*
 * Copyright (c) 2021 Nolan Kuza
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of Nolan Kuza nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.dekaresearch.simulation.websockets;

import com.google.gson.JsonElement;

import org.firstinspires.ftc.robotcore.external.Consumer;

/**
 * An <code>abstract</code> class that defines the behavior of a class that provides the
 * interactions between a simulated data device
 * and the <b>WebSocket client</b>
 */
public abstract class Provider {
    private final String type;
    private final String device;

    /**
     * Creates a Provider with the <code>type</code> and <code>device</code> of the WebSocket
     * message. More information
     * <a href="https://github.com/wpilibsuite/allwpilib/blob/main/simulation/halsim_ws_core/doc/hardware_ws_api.md#text-data-frames">here</a>
     *
     * @param type The type of the device/message
     * @param device The device index or name (<code>""</code> for none)
     */
    public Provider(String type, String device) {
        this.type = type;
        this.device = device;
    }

    /**
     *
     * @return The <code>type</code> used in the WebSocket messages
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @return The <code>device</code> used in the WebSocket messages
     */
    public String getDevice() {
        return device;
    }

    /**
     * Should register all {@link Consumer Consumer} callbacks
     * to the {@link com.dekaresearch.simulation.data.SimDataValue SimDataValue}
     * objects of the simulated data device this provider is attached to.
     *
     * For example:
     * <pre>
     * {@code
     * data.speed.registerCallback(new BasicCallback<Double>("<speed"));
     * }</pre>
     */
    public abstract void registerCallbacks();

    /**
     * Should unregister all callbacks from the {@link com.dekaresearch.simulation.data.SimDataValue SimDataValue}
     * objects of the simulated data device this provider is attached to.
     *
     * For example:
     * <pre>
     * {@code
     * data.speed.unregisterAllCallbacks();
     * }</pre>
     */
    public abstract void unregisterCallbacks();

    /**
     * Should update the appropriate {@link com.dekaresearch.simulation.data.SimDataValue SimDataValue} object
     * of the simulated data device this provider is attached to.
     *
     * For example:
     * <pre>
     * {@code
     * if(key.equals(">vin_voltage")) {
     *     data.vinVoltage.set(value.getAsDouble());
     * }
     * }</pre>
     *
     * @param key The property name of the new JSON value from the WebSocket message.
     *            Typically these will start with `">"` because they are destined for the robot code.
     *            For example `">count"`.
     * @param value The new JSON value from the WebSocket message.
     */
    public abstract void onNetValueChanged(String key, JsonElement value);

    /**
     * A basic type of callback that sends a simulated data value over WebSockets
     * using the {@link SimulationWebSocketHandler#send} utility method.
     * Most if not all callbacks registered in {@link #registerCallbacks()}
     * can use {@link BasicCallback} for convenience.
     *
     * @param <T> The type of the {@link com.dekaresearch.simulation.data.SimDataValue SimDataValue}
     */
    public class BasicCallback<T> implements Consumer<T> {
        private final String field;

        public BasicCallback(String field) {
            this.field = field;
        }

        @Override
        public void accept(T value) {
            SimulationWebSocketHandler.send(getType(), getDevice(), field, value);
        }
    }
}
