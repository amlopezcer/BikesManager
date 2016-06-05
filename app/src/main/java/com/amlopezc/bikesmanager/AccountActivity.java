package com.amlopezc.bikesmanager;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeUser;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;

/**
 * Class to read the profile / account. It manages additional operations such as
 * booking cancels, money depositions, account elimination or access to account modification.
 */

public class AccountActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskListener<String> {

    private BikeUser mBikeUser; //Current logged user (singleton instance)
    private Button mButtonCancelBikeBook, mButtonCancelMooringsBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //Set activity title with the username
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.text_user_name), "");
        setTitle(username);

        mBikeUser = BikeUser.getInstance();

        Button buttonDeposit = (Button)findViewById(R.id.button_deposit_money);
        buttonDeposit.setOnClickListener(this);
        Button buttonEdit = (Button)findViewById(R.id.button_edit_profile);
        buttonEdit.setOnClickListener(this);
        Button buttonDeleteAccount = (Button)findViewById(R.id.button_delete_account);
        buttonDeleteAccount.setOnClickListener(this);

        mButtonCancelBikeBook = (Button)findViewById(R.id.button_cancel_book_bike);
        mButtonCancelBikeBook.setOnClickListener(this);
        mButtonCancelMooringsBook = (Button)findViewById(R.id.button_cancel_book_moorings);
        mButtonCancelMooringsBook.setOnClickListener(this);

        disableCancelButtonsIfNeeded();

        initBookingData();
    }

    //Disabled buttons format
    private void disableCancelButtonsIfNeeded() {
        if(!mBikeUser.ismBookTaken()) {
            mButtonCancelBikeBook.setEnabled(false);
            mButtonCancelBikeBook.setTextColor(ContextCompat.getColor(this, R.color.lightGrey));
        }
        if(!mBikeUser.ismMooringsTaken()) {
            mButtonCancelMooringsBook.setEnabled(mBikeUser.ismMooringsTaken());
            mButtonCancelMooringsBook.setTextColor(ContextCompat.getColor(this, R.color.lightGrey));
        }
    }

    //Initialization of some dynamic data realted to bookings
    private void initBookingData() { //TODO: Modificar para poner una cuenta atrás, también tengo que repensarlo un pooc y poner botones de cancelación, esto es temporal
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

        updateCurrentBalance();
    }

    private void updateCurrentBalance() {
        TextView textView = (TextView) findViewById(R.id.textView_profile_balance);
        textView.setText(i18n(R.string.text_format_money, mBikeUser.getmBalance()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_deposit_money:
                showDepositMoneyDialog();
                break;
            case R.id.button_edit_profile:
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.button_delete_account:
                confirmDeleteAccount();
                break;
            case R.id.button_cancel_book_bike: //TODO: pendiente actualización en servidor (cuando haga la del mapa con los PUT diferenciados)
                mBikeUser.cancelBookBike();
                initBookingData();
                disableCancelButtonsIfNeeded();
                break;
            case R.id.button_cancel_book_moorings:
                mBikeUser.cancelBookMoorings();
                initBookingData();
                disableCancelButtonsIfNeeded();
                break;
        }
    }

    //Show the DepositMoneyDialog to allow the user to select the amount of money to deposit
    private void showDepositMoneyDialog() {
        DialogFragment dialog = new DepositMoneyDialogFragment();
        dialog.show(getFragmentManager(), DepositMoneyDialogFragment.CLASS_ID);
    }

    //Update current balance and user instance in case the deposit has been confirmed
    public void doPositiveClickDepositMoneyDialog(String deposit) {
        float newBalance = mBikeUser.getmBalance() + Float.parseFloat(deposit);
        mBikeUser.setmBalance(newBalance);

        //Update the user in the server
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
        httpDispatcher.doPut(this, mBikeUser, null);
    }

    //Dialog to confirm delete account operation
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
            case HttpConstants.OPERATION_PUT: //User update
                Toast.makeText(this,
                        i18n(R.string.confirm_deposit),
                        Toast.LENGTH_SHORT).show();
                updateCurrentBalance();
                break;
            case HttpConstants.OPERATION_DELETE: //User deletion
                deleteLocalUser(); //When the server operation is done, reset local configurations
                break;
        }
    }

    //Reset local configurations and return to the login Activity
    private void deleteLocalUser() {
        BikeUser bikeUser = BikeUser.getInstance();
        bikeUser.resetInstance();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit().
                putString(getString(R.string.text_user_name), "").
                putString(getString(R.string.text_password), "").
                apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

}
