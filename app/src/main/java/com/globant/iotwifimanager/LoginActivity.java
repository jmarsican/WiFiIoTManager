package com.globant.iotwifimanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.globant.wifi.WiFiController;

import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements APInfoAdapter.AdapterCallback {


    private static final String[] ENDPOINT = new String[]{
            "http://192.168.4.1", "/wifisave?s=%s&p=%s"
    };
    private static final String SSID_FILTER = "KudosButton";

    // UI references.
    private EditText mSSIDView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private RecyclerView mListView;
    private WiFiController mController;

    private Button btnConnect;

    private OnClickListener connectAction = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String selected = ((APInfoAdapter)mListView.getAdapter()).getSelectedItem().getSSID();
            mController.connectToAP(selected, mPasswordView.getText().toString());

            mListView.setAdapter(new APInfoAdapter(mController.list(""), LoginActivity.this));
            mListView.invalidate();
            btnConnect.setOnClickListener(disconnectAction);
            btnConnect.setText("DISCONNECT");
        }
    };

    private OnClickListener disconnectAction = new OnClickListener() {
        @Override
        public void onClick(View view) {
            mController.disconnect();

            mListView.setAdapter(new APInfoAdapter(mController.list(""), LoginActivity.this));
            mListView.invalidate();
            btnConnect.setOnClickListener(connectAction);
            btnConnect.setText("CONNECT");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSSIDView = (EditText) findViewById(R.id.tvSelectedSSID);

        mPasswordView = (EditText) findViewById(R.id.password);

        btnConnect = (Button) findViewById(R.id.sign_in_button);
        btnConnect.setOnClickListener(connectAction);

        Button btnRefresh = (Button) findViewById(R.id.refresh_button);
        final String filter = mSSIDView.getText().toString();
        btnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setAdapter(new APInfoAdapter(mController.list(filter), LoginActivity.this));
                mListView.invalidate();
            }
        });

        Button send = (Button) findViewById(R.id.send_button);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                UserLoginTask mAuthTask = new UserLoginTask("", "");
                mAuthTask.execute((Void) null);

            }
        });

        mLoginFormView = findViewById(R.id.wifi_form);
        mProgressView = findViewById(R.id.login_progress);

        initList();
    }

    private void initList() {
        mController = new WiFiController(getApplicationContext());

        mListView = (RecyclerView) findViewById(R.id.rvAccessPoints);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(new APInfoAdapter(mController.list(SSID_FILTER), this));
        mListView.setItemAnimator(new DefaultItemAnimator());
    }


    private void attemptLogin() {

        // Reset errors.
        mSSIDView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mSSIDView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mSSIDView.setError(getString(R.string.error_field_required));
            focusView = mSSIDView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mSSIDView.setError(getString(R.string.error_invalid_email));
            focusView = mSSIDView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onClick() {
        String ssid = ((APInfoAdapter)mListView.getAdapter()).getSelectedItem().getSSID();
        mSSIDView.setText(ssid);
        mPasswordView.setText("");
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUrl;
        private final String mPassword;

        UserLoginTask(String url, String password) {
            mUrl = url;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            URL url = null;
            try {
                url = new URL(ENDPOINT[0]+String.format(ENDPOINT[1],"SSID","PWD"));
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
            showProgress(false);

            if (success) {
//                finish();
            } else {
                mPasswordView.setError("Network error");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

}

