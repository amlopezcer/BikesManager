package com.amlopezc.bikesmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Shows the dialog to set connection data (IP + port) if any of them is not
 */

public class ConnectionDataDialogFragment extends DialogFragment {

    //For MapsActivity when calling and showing this dialog
    public static final String CLASS_ID = "ConnectionDataDialogFragment";

    private EditText mEditText_server;
    private EditText mEditText_port;

    private SharedPreferences mDefaultSharedPreferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater and the view from it
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_connection_data, null);

        mEditText_server = (EditText) view.findViewById(R.id.editText_serverAddress);
        mEditText_port = (EditText) view.findViewById(R.id.editText_serverPort);

        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        fillData(); //Get default data

        builder.setMessage(i18n(R.string.builder_connection_data_msg))
                .setView(view)
                .setPositiveButton(i18n(R.string.text_set), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Getting and setting data when "set" button is clicked
                        String data = mEditText_server.getText().toString().trim();
                        mDefaultSharedPreferences.edit()
                                .putString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, data)
                                .apply();

                        data = mEditText_port.getText().toString().trim();
                        mDefaultSharedPreferences.edit()
                                .putString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, data)
                                .apply();

                        //If the caller is the MapsActivity, update its layout
                        if (getActivity().getComponentName().getClassName().contains("Maps"))
                            ((MapsActivity) getActivity()).doPositiveClick();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(!mEditText_server.getText().toString().trim().isEmpty() &&
                        !mEditText_port.getText().toString().trim().isEmpty());

        //Code to enable "Set" button only when all data have been provided
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(!mEditText_server.getText().toString().trim().isEmpty() &&
                                !mEditText_port.getText().toString().trim().isEmpty());
            }
        };

        //All text fields must be completed to enable "Set", so the TextWatcher is listening to all of them
        mEditText_server.addTextChangedListener(watcher);
        mEditText_port.addTextChangedListener(watcher);

        return dialog;
    }

    //Get default data from the SharedPreferences, if any
    private void fillData() {
        String serverAddress = mDefaultSharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, "");
        setFields(mEditText_server, serverAddress);
        String serverPort = mDefaultSharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, "");
        setFields(mEditText_port, serverPort);
    }

    //Set EditText content from default data from the SharedPreferences, if any
    private void setFields(EditText editText, String data) {
        if(!data.trim().isEmpty())
            editText.setText(data);
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

}
