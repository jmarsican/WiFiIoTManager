package com.globant.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.globant.controllers.PreferencesController;
import com.globant.controllers.UserLoginTask;
import com.globant.controllers.WiFiController;
import com.globant.iotwifimanager.APInfoAdapter;
import com.globant.iotwifimanager.LoginActivity;
import com.globant.iotwifimanager.R;
import com.globant.model.APInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javier marsicano on 10/04/17.
 */

public class KuddosButtonSearch extends Fragment implements APInfoAdapter.AdapterCallback {
    private EditText mSSIDView;
    private EditText mPasswordView;
    private RecyclerView mListView;
    private Button btnConnect;

    private WiFiController mController;
    private PreferencesController mPreferences;
    private final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0x123456;

    private Activity mActivity;

    public KuddosButtonSearch() {
    }

    private View.OnClickListener connectAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String selected = ((APInfoAdapter)mListView.getAdapter()).getSelectedItem().getSSID();
            mController.connectToAP(selected, mPasswordView.getText().toString());

            mListView.setAdapter(new APInfoAdapter(mController.list(""), KuddosButtonSearch.this));
            mListView.invalidate();
            btnConnect.setOnClickListener(disconnectAction);
            btnConnect.setText("DISCONNECT");
        }
    };

    private View.OnClickListener disconnectAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mController.disconnect();

            mListView.setAdapter(new APInfoAdapter(mController.list(""), KuddosButtonSearch.this));
            mListView.invalidate();
            btnConnect.setOnClickListener(connectAction);
            btnConnect.setText("CONNECT");
        }
    };

    @Override
    public void onAttach(Context context) {
        mActivity = (Activity)context;
        mController = new WiFiController(context);
        mPreferences = new PreferencesController(context);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.kudos_button_search_fragment,container,false);

        mSSIDView = (EditText) view.findViewById(R.id.tvSelectedSSID);

        mPasswordView = (EditText) view.findViewById(R.id.password);

        mListView = (RecyclerView) view.findViewById(R.id.rvAccessPoints);

        btnConnect = (Button) view.findViewById(R.id.sign_in_button);
        btnConnect.setOnClickListener(connectAction);

        Button btnRefresh = (Button) view.findViewById(R.id.refresh_button);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setAdapter(new APInfoAdapter(mController.list(mPreferences.getFilter()), KuddosButtonSearch.this));
                mListView.invalidate();
            }
        });

        Button send = (Button) view.findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });

        initList();

        return view;
    }

    private void initList() {
        List<APInfo> list;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                mActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            list = new ArrayList<>();
        }else {
            list = mController.list(mPreferences.getFilter());
        }
        mListView.setLayoutManager(new LinearLayoutManager(mActivity));
        mListView.setAdapter(new APInfoAdapter(list, this));
        mListView.setItemAnimator(new DefaultItemAnimator());
    }


    private void sendData() {
        // Reset errors.
        mSSIDView.setError(null);

        // Store values at the time of the login attempt.
        String ssid = mSSIDView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(ssid)) {
            Toast.makeText(mActivity,"Please select an access point",Toast.LENGTH_SHORT).show();
        } else if (!mController.isConnected()) {
            Toast.makeText(mActivity,"Couldn't connect to AP, please try again",Toast.LENGTH_SHORT).show();
            btnConnect.setText("CONNECT");
            btnConnect.setOnClickListener(connectAction);
        } else {
            UserLoginTask mAuthTask = new UserLoginTask(mPreferences.getIpAddress(), ssid, password);
            mAuthTask.execute((Void) null);

//            showProgress(true);
        }
    }


    @Override
    public void onClick() {
        String ssid = ((APInfoAdapter)mListView.getAdapter()).getSelectedItem().getSSID();
        mSSIDView.setText(ssid);
        mPasswordView.setText("");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}