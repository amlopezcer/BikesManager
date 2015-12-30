package com.amlopezc.bikesmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;



public class ConnectionDataDialogFragment extends DialogFragment {

    /**
     * Interface to be implemented in the host activity to receive event callbacks
     */
    public interface connectionDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    // Instance of the interface to deliver action events
    connectionDialogListener mListener;

    // Instantiate the connectionDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the connectionDialogListener so we can send events to the host
            mListener = (connectionDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement connectionDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater and the view from it
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_connection_data, null);

        fillData(view);

        builder.setMessage("Set connection data")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Getting and setting data
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        EditText et;

                        et = (EditText) view.findViewById(R.id.editText_userName);
                        String data = et.getText().toString();
                        sharedPreferences.edit().putString(SettingsActivityFragment.KEY_PREF_SYNC_USER, data).apply();

                        et = (EditText) view.findViewById(R.id.editText_serverAddress);
                        data = et.getText().toString();
                        sharedPreferences.edit().putString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, data).apply();

                        et = (EditText) view.findViewById(R.id.editText_serverPort);
                        data = et.getText().toString();
                        sharedPreferences.edit().putString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, data).apply();
                    }
                });

       /* ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);*/

        return builder.create();
    }

    private void fillData(View view) {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        EditText editText;

        editText = (EditText) view.findViewById(R.id.editText_userName);
        String userName = shp.getString(SettingsActivityFragment.KEY_PREF_SYNC_USER, "");
        setFields(editText, userName);
        editText = (EditText) view.findViewById(R.id.editText_serverAddress);
        String serverAddress = shp.getString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, "");
        setFields(editText, serverAddress);
        editText = (EditText) view.findViewById(R.id.editText_serverPort);
        String serverPort = shp.getString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, "");
        setFields(editText, serverPort);
    }

    private void setFields(EditText editText, String data) {
        if(!data.isEmpty())
            editText.setText(data);
    }








}
