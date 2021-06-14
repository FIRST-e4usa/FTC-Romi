package com.qualcomm.robotcore.wifi;

import android.content.Context;

import org.firstinspires.ftc.robotcore.internal.network.ApChannel;
import org.firstinspires.ftc.robotcore.internal.network.InvalidNetworkSettingException;

import java.net.InetAddress;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class LoopbackAssistant extends NetworkConnection {

    private static LoopbackAssistant loopbackAssistant = null;

    public LoopbackAssistant(Context context) {
        super(context);
    }

    public synchronized static LoopbackAssistant getLoopbackAssistant(Context context) {
        if (loopbackAssistant == null) loopbackAssistant = new LoopbackAssistant(context);
        return loopbackAssistant;
    }

    @Override
    public NetworkType getNetworkType() {
        return NetworkType.LOOPBACK;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void discoverPotentialConnections() {

    }

    @Override
    public void cancelPotentialConnections() {

    }

    @Override
    public void createConnection() {

    }

    @Override
    public void connect(String deviceAddress) {

    }

    @Override
    public void connect(String connectionName, String connectionPassword) {

    }

    @Override
    public void detectWifiReset() {

    }

    @Override
    public InetAddress getConnectionOwnerAddress() {
        return InetAddress.getLoopbackAddress();
    }

    @Override
    public String getConnectionOwnerName() {
        return null;
    }

    @Override
    public String getConnectionOwnerMacAddress() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public String getDeviceName() {
        return null;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public String getFailureReason() {
        return null;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public ConnectStatus getConnectStatus() {
        return null;
    }

    @Override
    public void onWaitForConnection() {

    }

    @Override
    public void setNetworkSettings(@Nullable String deviceName, @Nullable String password, @Nullable ApChannel channel) throws InvalidNetworkSettingException {

    }
}
