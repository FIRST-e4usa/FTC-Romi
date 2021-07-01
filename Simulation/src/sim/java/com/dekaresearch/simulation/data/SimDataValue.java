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
