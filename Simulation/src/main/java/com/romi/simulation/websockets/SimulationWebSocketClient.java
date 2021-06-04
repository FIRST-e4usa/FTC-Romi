package com.romi.simulation.websockets;

import com.qualcomm.robotcore.util.RobotLog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class SimulationWebSocketClient {

    private static final String TAG = "SimulationWebSocketClient";

    private static WebSocketClient client;

    public SimulationWebSocketClient(URI serverUri) {
        client = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                RobotLog.ii(TAG, "WebSocket connected");
                tempTest();
            }

            @Override
            public void onMessage(String s) {
                //RobotLog.ii(TAG, s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                RobotLog.ii(TAG, "WebSocket closed");
            }

            @Override
            public void onError(Exception e) {
                RobotLog.ee(TAG, e.getLocalizedMessage());
            }
        };
    }

    public void connect() {
        RobotLog.ii(TAG, "Try connect");
        client.connect();
    }

    public void close() {
        client.close();
    }

    public void send(String s) {
        RobotLog.ii(TAG, s);
        client.send(s);
    }

    //TODO(Romi) Remove
    public void tempTest() {
        send("{\"type\":\"PWM\",\"device\":0,\"data\":{\"<init\":true}}");

        send("{\"type\":\"DriverStation\",\"device\":\"\",\"data\":{\">enabled\":true}}");
        send("{\"type\":\"DriverStation\",\"device\":\"\",\"data\":{\">new_data\":true}}");

        send("{\"type\":\"PWM\",\"device\":0,\"data\":{\"<speed\":1.0}}");
    }
}
