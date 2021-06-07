package com.romi.simulation.data;

import org.firstinspires.ftc.robotcore.external.Consumer;

import java.util.ArrayList;
import java.util.List;

public class SimDataValue<T> {
    private T value;

    private List<Consumer<T>> callbacks = new ArrayList<>();

    public SimDataValue(T initialValue) {
        value = initialValue;
    }

    public void set(T value) {
        this.value = value;
        for(Consumer<T> callback : callbacks) {
            callback.accept(value);
        }
    }

    public T get() {
        return value;
    }

    public void registerCallback(Consumer<T> callback) {
        callbacks.add(callback);
        callback.accept(this.value);
    }

    public void unregisterAllCallbacks() {
        callbacks.clear();
    }
}
