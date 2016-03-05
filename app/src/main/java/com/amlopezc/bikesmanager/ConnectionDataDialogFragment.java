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
import android.widget.Toast;


public class ConnectionDataDialogFragment extends DialogFragment {

    private EditText mEditText_user;
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

        mEditText_user = (EditText) view.findViewById(R.id.editText_userName);
        mEditText_server = (EditText) view.findViewById(R.id.editText_serverAddress);
        mEditText_port = (EditText) view.findViewById(R.id.editText_serverPort);

        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        fillData();

        builder.setMessage(i18n(R.string.builder_msg))
                .setView(view)
                .setPositiveButton(i18n(R.string.text_set), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Getting and setting data
                        String data = mEditText_user.getText().toString();
                        mDefaultSharedPreferences.edit()
                                .putString(SettingsActivityFragment.KEY_PREF_SYNC_USER, data)
                                .apply();

                        data = mEditText_server.getText().toString();
                        mDefaultSharedPreferences.edit()
                                .putString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, data)
                                .apply();

                        data = mEditText_port.getText().toString();
                        mDefaultSharedPreferences.edit()
                                .putString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, data)
                                .apply();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(!mEditText_user.getText().toString().trim().isEmpty() &&
                                !mEditText_server.getText().toString().trim().isEmpty() &&
                                !mEditText_port.getText().toString().trim().isEmpty());
            }
        };

        mEditText_user.addTextChangedListener(watcher);
        mEditText_server.addTextChangedListener(watcher);
        mEditText_port.addTextChangedListener(watcher);

        return dialog;
    }

    private void fillData() {
        String userName = mDefaultSharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_USER, "");
        setFields(mEditText_user, userName);
        String serverAddress = mDefaultSharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, "");
        setFields(mEditText_server, serverAddress);
        String serverPort = mDefaultSharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, "");
        setFields(mEditText_port, serverPort);
    }

    private void setFields(EditText editText, String data) {
        if(!data.trim().isEmpty())
            editText.setText(data);
    }

    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

}
