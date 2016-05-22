package com.amlopezc.bikesmanager;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeUser;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskListener<String> {

    private BikeUser mBikeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.text_user_name), "");
        setTitle(username);

        Button buttonDeposit = (Button)findViewById(R.id.button_deposit_money);
        buttonDeposit.setOnClickListener(this);
        Button buttonEdit = (Button)findViewById(R.id.button_edit_profile);
        buttonEdit.setOnClickListener(this);
        Button buttonDeleteAccount = (Button)findViewById(R.id.button_delete_account);
        buttonDeleteAccount.setOnClickListener(this);

        initData();
    }

    private void initData() {
        mBikeUser = BikeUser.getInstance();

        TextView textView;

        textView = (TextView) findViewById(R.id.textView_address_bike);
        if(mBikeUser.ismBookTaken()) {
            textView.setText(mBikeUser.getmBookAddress());
            textView = (TextView) findViewById(R.id.textView_date_bike);
            textView.setText(mBikeUser.getmBookDate());
        } else
            textView.setText("No tienes ninguna bicicleta reservada");

        textView = (TextView) findViewById(R.id.textView_address_moorings);
        if(mBikeUser.ismMooringsTaken()) {
            textView.setText(mBikeUser.getmMooringsAddress());
            textView = (TextView) findViewById(R.id.textView_date_moorings);
            textView.setText(mBikeUser.getmMooringsDate());
        } else
            textView.setText("No tienes ningún anclaje reservado");

        textView = (TextView) findViewById(R.id.textView_profile_balance);
        textView.setText(i18n(R.string.text_format_money, mBikeUser.getmBalance()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_deposit_money:
                showDepositMoneyDialog();
                break;
            case R.id.button_edit_profile:
                Toast.makeText(this,
                        "edit",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_delete_account:
                confirmDeleteAccount();
                break;
        }
    }

    private void showDepositMoneyDialog() {
        DialogFragment dialog = new DepositMoneyDialogFragment();
        dialog.show(getFragmentManager(), DepositMoneyDialogFragment.CLASS_ID);
    }

    public void doPositiveClick(String deposit) {
        float newBalance = mBikeUser.getmBalance() + Float.parseFloat(deposit);
        mBikeUser.setmBalance(newBalance);

        //Update user
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
        httpDispatcher.doPut(this, mBikeUser, null);
    }

    //Dialog to confirm operation
    private void confirmDeleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(i18n(R.string.dialog_delete_account)).
                setPositiveButton(
                        i18n(R.string.dialog_delete),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteServerAccount();
                                dialog.cancel();
                            }
                        }).
                setNegativeButton(
                        i18n(R.string.text_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Delete account from the server
    private void deleteServerAccount() {
        BikeUser bikeUser = BikeUser.getInstance();
        String id = Integer.toString(bikeUser.getmId());

        HttpDispatcher dispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
        dispatcher.doDelete(this, id);
    }

    @Override
    public void processServerResult(String result, int operation) {
        //Process the server response
        switch (operation) {
            case HttpConstants.OPERATION_PUT:
                Toast.makeText(this,
                        i18n(R.string.confirm_deposit),
                        Toast.LENGTH_SHORT).show();
                initData();
                break;
            case HttpConstants.OPERATION_DELETE:
                deleteLocalUser(); //When the server operation is done, reset local configurations
                break;
        }
    }

    //Reset local configurations and return to the login Activity
    private void deleteLocalUser() {
        BikeUser bikeUser = BikeUser.getInstance();
        bikeUser.resetInstance();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(getString(R.string.text_user_name), "")
                .putString(getString(R.string.text_password), "")
                .apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

}
