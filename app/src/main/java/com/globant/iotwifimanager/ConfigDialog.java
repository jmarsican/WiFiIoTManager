package com.globant.iotwifimanager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.globant.controllers.PreferencesController;

/**
 * Created by javier on 06/04/17.
 */

public class ConfigDialog extends AppCompatDialogFragment {
    private PreferencesController mPreferences;
    private EditText etFilter;
    private EditText etIpAddress;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPreferences = new PreferencesController(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_configuration, null);
        builder.setTitle("SETTINGS")
                .setView(view)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(),"Saving...",Toast.LENGTH_SHORT).show();
                        mPreferences.saveIpAddress(etIpAddress.getText().toString());
                        mPreferences.saveFilter(etFilter.getText().toString());
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ConfigDialog.this.getDialog().cancel();
                    }
                });

        etFilter = (EditText) view.findViewById(R.id.etFilter);
        etFilter.setText(mPreferences.getFilter());

        etIpAddress = (EditText) view.findViewById(R.id.etIp);
        etIpAddress.setText(mPreferences.getIpAddress());

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
