package com.globant.controllers;

import android.os.AsyncTask;
import android.widget.Toast;

import com.globant.iotwifimanager.LoginActivity;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jmarsiscano on 10/04/17.
 */

public class UserLoginTask extends AsyncTask<Void,Void,Boolean> {
    private final String[] ENDPOINT = new String[]{
            "http://%s/wifisave?s=%s&p=%s"
    };

    private final String mSSID;
    private final String mPassword;
    private final String mIpAddress;

    public UserLoginTask(String ipAddress, String ssid, String password) {
        mIpAddress = ipAddress;
        mSSID = ssid;
        mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            URL url = new URL(String.format(ENDPOINT[0], mIpAddress, mSSID, mPassword));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            urlConnection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
//            showProgress(false);

        if (!success) {
//            Toast.makeText(LoginActivity.this,"Network error",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
//            showProgress(false);
    }
}
