package com.romi.simulation.websockets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.RobotLog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class SimulationWebSocketClient {

    private static final String TAG = "SimulationWebSocketClient";

    private static final String PATH = "wpilibws";

    private static SimulationWebSocketClient instance;

    private URI serverUri;

    private WebSocketClient client;

    //region Providers
    Map<String, Provider> providers = new HashMap<>();
    //endregion

    public static SimulationWebSocketClient getInstance() {
        if(instance == null) {
            instance = new SimulationWebSocketClient();
        }
        return instance;
    }

    private SimulationWebSocketClient() {
        setServer("192.168.49.2", 3300);

        addProvider("DriverStation", "", new DriverStationProvider("DriverStation", ""));
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

                for(Provider provider : providers.values()) {
                    provider.registerCallbacks();
                }

                tempTest();
            }

            @Override
            public void onMessage(String s) {
                JsonObject object = SimulationWebSocketHandler.receive(s);

                String type = object.get("type").getAsString();
                String device = object.get("device").getAsString();
                JsonObject payload = object.get("payload").getAsJsonObject();

                Provider provider = providers.get(getProviderKey(type, device));
                if(provider != null) {
                    for (Map.Entry<String, JsonElement> entry : payload.entrySet()) {
                        provider.onNetValueChanged(entry.getKey(), entry.getValue());
                    }
                }

                RobotLog.vv(TAG, s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                RobotLog.ii(TAG, "WebSocket closed");

                for(Provider provider : providers.values()) {
                    provider.unregisterCallbacks();
                }
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
        if(client != null) {
            RobotLog.ii(TAG, "Close WebSocket");
            client.close();
        }
        client = null;
    }

    public boolean isOpen() {
        return client != null && client.isOpen();
    }

    public void updateRobotState(@NonNull RobotState state) {
        switch(state) {
            case INIT:
                connect();
                break;
            case RUNNING:
                // @TODO(Romi) Initialize devices and driver station (or do this on robot init instead)?
                break;
            case STOPPED:
            case EMERGENCY_STOP:
                close();
                break;
            case UNKNOWN:
            case NOT_STARTED:
                break;
        }
    }

    public void send(String s) {
        if(isOpen()) {
            RobotLog.vv(TAG, s);
            client.send(s);
        }
    }

    public <T extends Provider> void addProvider(String type, String device, T provider) {
        String key = getProviderKey(type, device);
        providers.put(key, provider);
    }

    public static String getProviderKey(String type, String device) {
        return type + (device.equals("") ? "" : ":" + device);
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
        //send("{\"type\": \"DriverStation\",\"device\": \"\",\"data\": {\">enabled\": true}}");
        send("{\"type\": \"PWM\",\"device\": \"0\",\"data\": {\"<speed\": 1.0}}");
    }
}
