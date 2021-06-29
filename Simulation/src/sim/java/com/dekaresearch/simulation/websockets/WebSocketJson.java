package com.dekaresearch.simulation.websockets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WebSocketJson
{
    protected static class InstanceHolder
    {
        public static final Gson theInstance = new GsonBuilder().disableHtmlEscaping().create();
    }
    public static Gson getInstance()
    {
        return InstanceHolder.theInstance;
    }
}
