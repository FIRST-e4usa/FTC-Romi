package com.qualcomm.robotcore.wifi;

import android.content.Context;

import com.qualcomm.robotcore.util.Network;

import org.firstinspires.ftc.robotcore.internal.network.ApChannel;
import org.firstinspires.ftc.robotcore.internal.network.InvalidNetworkSettingException;

import java.net.InetAddress;

import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class EmulationLoopbackAssistant extends NetworkConnection {

    private static EmulationLoopbackAssistant emulationLoopbackAssistant = null;

    private EmulationLoopbackAssistant(Context context) {
        super(context);
    }

    public synchronized static EmulationLoopbackAssistant getEmulationLoopbackAssistant(Context context) {
        if (emulationLoopbackAssistant == null) {
            emulationLoopbackAssistant = new EmulationLoopbackAssistant(context);
        }

        return emulationLoopbackAssistant;
    }

    @Override
    public NetworkType getNetworkType() {
        return NetworkType.EMULATION_LOOPBACK;
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
        return Network.getLoopbackAddress();
    }

    @Override
    public String getConnectionOwnerName() {
        return "";
    }

    @Override
    public String getConnectionOwnerMacAddress() {
        return "";
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public String getDeviceName() {
        return getConnectionOwnerName();
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public String getFailureReason() {
        return "";
    }

    @Override
    public String getPassphrase() {
        return "";
    }

    @Override
    public ConnectStatus getConnectStatus() {
        return ConnectStatus.CONNECTED;
    }

    @Override
    public void onWaitForConnection() {

    }

    @Override
    public void setNetworkSettings(@Nullable String deviceName, @Nullable String password, @Nullable ApChannel channel) throws InvalidNetworkSettingException {

    }
}
