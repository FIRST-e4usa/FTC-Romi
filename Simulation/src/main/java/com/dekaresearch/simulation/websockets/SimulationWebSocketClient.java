package com.dekaresearch.simulation.websockets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.RobotLog;

import com.dekaresearch.simulation.data.*;

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

                createProviders();
            }

            @Override
            public void onMessage(String s) {
                JsonObject object = SimulationWebSocketHandler.receive(s);

                String type = object.get("type").getAsString();
                String device = object.get("device").getAsString();
                JsonObject payload = object.get("data").getAsJsonObject();

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

                destroyProviders();
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

    public <T extends Provider> void addProvider(T provider) {
        String key = getProviderKey(provider.getType(), provider.getDevice());
        providers.put(key, provider);
    }

    public void createProviders() {
        addProvider(new DriverStationProvider());
        addProvider(new RoboRIOProvider());

        // TODO move these to init?
        addProvider(new GyroProvider("RomiGyro"));
        for(int i = 0; i < PWMData.MAX_DEVICES; i++) {
            addProvider(new PWMProvider(Integer.toString(i)));
        }
        for(int i = 0; i < DIOData.MAX_DEVICES; i++) {
            addProvider(new DIOProvider(Integer.toString(i)));
        }
        for(int i = 0; i < EncoderData.MAX_DEVICES; i++) {
            addProvider(new EncoderProvider(Integer.toString(i)));
        }
        addProvider(new AccelerometerProvider("BuiltInAccel"));

        for(Provider provider : providers.values()) {
            provider.registerCallbacks();
        }
    }

    public void destroyProviders() {
        for(Map.Entry<String, Provider> entry : providers.entrySet()) {
            entry.getValue().unregisterCallbacks();
            providers.remove(entry.getKey());
        }
    }

    public static String getProviderKey(String type, String device) {
        return type + (device.equals("") ? "" : ":" + device);
    }
}
