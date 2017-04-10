package com.globant.iotwifimanager;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.globant.controllers.PreferencesController;
import com.globant.controllers.WiFiController;
import com.globant.fragments.ConnectFragment;
import com.globant.fragments.KuddosButtonSearch;
import com.globant.model.APInfo;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements APInfoAdapter.AdapterCallback {
    private WiFiController mController;
    private PreferencesController mPreferences;

    String mSelectedSsid;

    private static final String SSID_FILTER = "KudosButton";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mController = new WiFiController(getApplicationContext());
        mPreferences = new PreferencesController(getApplicationContext());

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new KuddosButtonSearch()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_config, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.config_item) {
            DialogFragment configDialog = new ConfigDialog();
            configDialog.show(getSupportFragmentManager(), "config_dialog");
            return true;
        } else if (item.getItemId() == R.id.about_item) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToNextScreen(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConnectFragment()).commit();
    }

    public List<APInfo> getScanResults() {
        return mController.list("");
    }

    public void connect(String password){
        mController.connectToAP(mSelectedSsid, password);
    }

    @Override
    public void onClick(APInfo selected) {
        mSelectedSsid = selected.getSSID();

        //TODO next fragment connection flow
    }
}

