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

package com.dekaresearch.simulation.data;

import org.firstinspires.ftc.robotcore.external.Consumer;

import java.util.ArrayList;
import java.util.List;

public class SimDataValue<T> {
    private T value;

    private final List<Consumer<T>> callbacks = new ArrayList<>();

    public SimDataValue(T initialValue) {
        value = initialValue;
    }

    public void set(T value) {
        if(!this.value.equals(value)) {
            synchronized (callbacks) {
                for (Consumer<T> callback : callbacks) {
                    callback.accept(value);
                }
            }
        }
        this.value = value;
    }

    public T get() {
        return value;
    }

    /**
     * Register a callback. By default this will send the initial value to the callback upon registration.
     * Use {@link #registerCallback(Consumer callback, boolean sendInitial)} with <code>sendInitial = false</code> if this is undesired.
     * @param callback
     */
    public void registerCallback(Consumer<T> callback) {
        registerCallback(callback, true);
    }

    public void registerCallback(Consumer<T> callback, boolean sendInitial) {
        synchronized (callbacks) {
            callbacks.add(callback);
            if (sendInitial) callback.accept(this.value);
        }
    }

    public void unregisterAllCallbacks() {
        synchronized (callbacks) {
            callbacks.clear();
        }
    }
}
