package com.globant.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.globant.model.APInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javier on 03/04/17.
 */

public class WiFiController {
    private static final String TAG = WiFiController.class.toString();

    private WifiManager wifiManager;
    private String connectedSsidName;


    public WiFiController(Context context) {
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }

    public List<APInfo> list(String filter) {
        List<APInfo> output = new ArrayList<>();
        for (ScanResult result: wifiManager.getScanResults()) {
            if(result.SSID.startsWith(filter)) {
                APInfo info = new APInfo();
                info.setSSID(result.SSID);
                info.setDescription(Integer.toString(result.level)+" dBm");
                output.add(info);
            }
        }
        return output;
    }

    private WifiConfiguration createAPConfiguration(String networkSSID, String networkPasskey, String securityMode) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        wifiConfiguration.SSID = "\"" + networkSSID + "\"";

        if (securityMode.equalsIgnoreCase("OPEN")) {

            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        } else if (securityMode.equalsIgnoreCase("WEP")) {

            wifiConfiguration.wepKeys[0] = "\"" + networkPasskey + "\"";
            wifiConfiguration.wepTxKeyIndex = 0;
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        } else if (securityMode.equalsIgnoreCase("PSK")) {

            wifiConfiguration.preSharedKey = "\"" + networkPasskey + "\"";
            wifiConfiguration.hiddenSSID = true;
            wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        } else {
            Log.i(TAG, "# Unsupported security mode: "+securityMode);

            return null;
        }

        return wifiConfiguration;

    }

    public void disconnect() {
        wifiManager.disconnect();
    }

    public int connectToAP(String networkSSID, String networkPasskey) {
        for (ScanResult result : wifiManager.getScanResults()) {

            if (result.SSID.equals(networkSSID)) {

                String securityMode = getScanResultSecurity(result);

                WifiConfiguration wifiConfiguration = createAPConfiguration(networkSSID, networkPasskey, securityMode);

                int res = wifiManager.addNetwork(wifiConfiguration);
                Log.d(TAG, "# addNetwork returned " + res);

                boolean b = wifiManager.enableNetwork(res, true);
                Log.d(TAG, "# enableNetwork returned " + b);

                wifiManager.setWifiEnabled(true);

                boolean changeHappen = wifiManager.saveConfiguration();

                if (res != -1 && changeHappen) {
                    Log.d(TAG, "# Change happen");
                    connectedSsidName = networkSSID;
                } else {
                    Log.d(TAG, "# Change NOT happen");
                }

                return res;
            }
        }

        return -1;
    }

    private String getScanResultSecurity(ScanResult scanResult) {

        final String cap = scanResult.capabilities;
        final String[] securityModes = { "WEP", "PSK", "EAP" };

        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i];
            }
        }

        return "OPEN";
    }

    public String getConnectedSsidName() {
        return connectedSsidName;
    }
}
