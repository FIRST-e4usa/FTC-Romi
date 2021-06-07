package com.romi.simulation.websockets;

import com.qualcomm.robotcore.util.RobotLog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class SimulationWebSocketClient {

    private static final String TAG = "SimulationWebSocketClient";

    private static final String PATH = "wpilibws";

    private static SimulationWebSocketClient instance;

    private URI serverUri;

    private WebSocketClient client;

    public static SimulationWebSocketClient getInstance() {
        if(instance == null) {
            instance = new SimulationWebSocketClient();
        }
        return instance;
    }

    private SimulationWebSocketClient() {
        setServer("192.168.49.2", 3300);
    }

    public void setServer(URI serverUri) {
        this.serverUri = serverUri;
    }

    public void setServer(String host, int port) {
        try {
            URI uri = new URI("ws://" + host + ":" + port + "/" + PATH);
            this.serverUri = uri;
        } catch (URISyntaxException e) {
            RobotLog.ee(TAG, e.getLocalizedMessage());
        }
    }

    private void createClient() {
        client = new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                RobotLog.ii(TAG, "WebSocket connected");
                tempTest();
            }

            @Override
            public void onMessage(String s) {
                RobotLog.vv(TAG, s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                RobotLog.ii(TAG, "WebSocket closed");
            }

            @Override
            public void onError(Exception e) {
                if(e.getLocalizedMessage() != null) {
                    RobotLog.ee(TAG, e.getLocalizedMessage());
                }
            }
        };
    }

    public void connect() {
        RobotLog.ii(TAG, "Try connect");
        if(client == null || client.isClosed()) {
            createClient();
        }
        client.connect();
    }

    public void close() {
        RobotLog.ii(TAG, "Close WebSocket");
        client.close();
        client = null;
    }

    public boolean isOpen() {
        return client != null && client.isOpen();
    }

    public void send(String s) {
        if(isOpen()) {
            RobotLog.vv(TAG, s);
            client.send(s);
        }
    }

    //TODO(Romi) Removed
    public void tempTest() {

        //send("{\"type\": \"DriverStation\",\"device\": \"\",\"data\": {\">ds\": true}}");

        send("{\"type\": \"PWM\",\"device\": \"0\",\"data\": {\"<init\": true}}");
        send("{\"data\": {\"<init\": true},\"device\": \"4\",\"type\": \"DIO\"}");
        send("{\"data\": {\"<init\": true},\"device\": \"5\",\"type\": \"DIO\"}");
        send("{\"data\": {\"<channel_a\": 4,\"<channel_b\": 5,\"<init\": true},\"device\": \"0\",\"type\": \"Encoder\"}");

        //send("{\"type\":\"DriverStation\",\"device\":\"\",\"data\":{\">new_data\":true}}");
    }

    public void temp2() {
        send("{\"type\": \"DriverStation\",\"device\": \"\",\"data\": {\">enabled\": true}}");
        send("{\"type\": \"PWM\",\"device\": \"0\",\"data\": {\"<speed\": 1.0}}");
    }
}
