package com.romi.simulation.websockets;

import java.net.URI;
import java.net.URISyntaxException;

public class SimulationWebSocketClientFactory {

    private static final String PATH = "wpilibws";

    public static SimulationWebSocketClient create()  {
        return create("192.168.49.2", 3300);
    }

    public static SimulationWebSocketClient create(String host, int port) {
        try {
            URI uri = new URI("ws://" + host + ":" + port + "/" + PATH);
            return new SimulationWebSocketClient(uri);
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
