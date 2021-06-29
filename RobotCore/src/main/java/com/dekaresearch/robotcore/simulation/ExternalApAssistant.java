package com.dekaresearch.robotcore.simulation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.qualcomm.robotcore.util.Network;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.wifi.NetworkConnection;
import com.qualcomm.robotcore.wifi.NetworkType;

import org.firstinspires.ftc.robotcore.internal.network.ApChannel;
import org.firstinspires.ftc.robotcore.internal.network.InvalidNetworkSettingException;
import org.firstinspires.ftc.robotcore.internal.network.WifiUtil;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.net.InetAddress;
import java.util.ArrayList;

import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class ExternalApAssistant extends NetworkConnection {

    private static ExternalApAssistant externalApAssistant = null;

    @SuppressLint("WifiManagerLeak")
    protected final WifiManager wifiManager = (WifiManager) AppUtil.getDefContext().getSystemService(Context.WIFI_SERVICE);

    private ExternalApAssistant(Context context) {
        super(context);
    }

    public synchronized static ExternalApAssistant getExternalApAssistant(Context context) {
        if (externalApAssistant == null) {
            externalApAssistant = new ExternalApAssistant(context);
        }

        return externalApAssistant;
    }

    @Override
    public NetworkType getNetworkType() {
        return NetworkType.EXTERNALAP;
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
        ArrayList<InetAddress> localIpAddresses = Network.getLocalIpAddresses();
        localIpAddresses = Network.removeLoopbackAddresses(localIpAddresses);
        localIpAddresses = Network.removeIPv6Addresses(localIpAddresses);

        // Safety
        if(localIpAddresses.size() == 0) {
            return Network.getLoopbackAddress();
        }

        return localIpAddresses.get(0);
    }

    @Override
    public String getConnectionOwnerName() {
        return WifiUtil.getConnectedSsid();
    }

    @Override
    public String getConnectionOwnerMacAddress() {
        return "";
    }

    @Override
    public boolean isConnected() {
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            boolean acquiredIp = !getConnectionOwnerAddress().equals(Network.getLoopbackAddress());
            return wifiInfo.getNetworkId() != -1 && acquiredIp;
        }
        return false;
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
