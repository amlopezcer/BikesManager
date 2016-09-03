package com.amlopezc.bikesmanager;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Dialog to allow the user to introduce the amount of money to deposit
 */
public class DepositMoneyDialogFragment extends DialogFragment {

    //For the caller activity
    public static final String CLASS_ID = "DepositMoneyDialogFragment";

    private EditText mEditText_money;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater and the view from it
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_deposit_money, null);

        mEditText_money = (EditText) view.findViewById(R.id.editText_depositMoney);

        builder.setMessage(i18n(R.string.builder_deposit)).
                setView(view).
                setPositiveButton(i18n(R.string.builder_deposit_positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String deposit = mEditText_money.getText().toString().trim();
                                ((AccountActivity)getActivity()).doPositiveClickDepositMoneyDialog(deposit);
                                dialog.cancel();
                            }
                        }).
                setNegativeButton(i18n(R.string.text_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false); //Disabled by default.

        //Code to enable positive button only when all data have been provided
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(!mEditText_money.getText().toString().trim().isEmpty() &&
                                !mEditText_money.getText().toString().trim().isEmpty());
            }
        };

        //Text fields must be completed to enable positive button
        mEditText_money.addTextChangedListener(watcher);

        return dialog;
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }


}
