package com.romi.simulation.websockets;

import com.qualcomm.robotcore.util.RobotLog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class SimulationWebSocketClient extends WebSocketClient {

    private static final String TAG = "SimulationWebSocketClient";

    public SimulationWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        RobotLog.ii(TAG, "WebSocket connected");
    }

    @Override
    public void onMessage(String s) {

    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
}
