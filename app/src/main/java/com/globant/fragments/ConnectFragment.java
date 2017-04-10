package com.globant.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.globant.iotwifimanager.APInfoAdapter;
import com.globant.iotwifimanager.LoginActivity;
import com.globant.iotwifimanager.R;

/**
 * Created by javier marsicano on 10/04/17.
 */

public class ConnectFragment extends Fragment {

    private EditText mSSIDView;
    private EditText mPasswordView;

    private Button btnConnect;

    private LoginActivity mActivity;

    private View.OnClickListener connectAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivity.connect(mPasswordView.getText().toString());

//            mController.list("");
            //TODO other fragment flow
            btnConnect.setOnClickListener(disconnectAction);
            btnConnect.setText("DISCONNECT");
        }
    };

    private View.OnClickListener disconnectAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            mController.disconnect();

//            mListView.setAdapter(new APInfoAdapter(mController.list(""), KuddosButtonSearch.this));
//            mListView.invalidate(); TODO other fragment
            btnConnect.setOnClickListener(connectAction);
            btnConnect.setText("CONNECT");
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (LoginActivity) context;
    }

    public ConnectFragment() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connect_fragment,container,false);

        mSSIDView = (EditText) view.findViewById(R.id.tvSelectedSSID);

        mPasswordView = (EditText) view.findViewById(R.id.password);

        btnConnect = (Button) view.findViewById(R.id.sign_in_button);
        btnConnect.setOnClickListener(connectAction);

        return view;
    }
}
