package com.globant.controllers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by javier on 06/04/17.
 */

public class PreferencesController {
    private static final String SHARED_PREFS_ID = "com.globant.iot.shared_preferences";
    private static final String AP_FILTER = "ap_filter";
    private static final String IP_ADDRESS = "ip_address";

    private Context mContext;

    public PreferencesController(Context context) {
        mContext = context;
    }

    private SharedPreferences getPreferences() {
        return mContext.getSharedPreferences(SHARED_PREFS_ID, Context.MODE_PRIVATE);
    }

    public String getFilter() {
        return getPreferences().getString(AP_FILTER,"KudosButton");
    }

    public void saveFilter(String filter) {
        getPreferences().edit().putString(AP_FILTER, filter).apply();
    }

    public String getIpAddress() {
        return getPreferences().getString(IP_ADDRESS,"192.168.4.1");
    }

    public void saveIpAddress(String ipAddress) {
        getPreferences().edit().putString(IP_ADDRESS, ipAddress).apply();
    }
}
