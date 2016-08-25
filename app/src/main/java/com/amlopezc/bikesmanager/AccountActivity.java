package com.amlopezc.bikesmanager;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.entity.BikeUser;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

/**
 * Class to read the profile / account. It manages additional operations such as
 * booking cancels, money depositions, account elimination or access to account modification.
 */

public class AccountActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskListener<String> {

    //Constants to position the countdown timer control of bikes and moorings in the 'mIsTimerRunning' array
    private final int BIKE_TIMER_POS = 0;
    private final int MOORINGS_TIMER_POS = 1;
    //Constant to control the booking which want to be canceled
    private final int OP_CANCEL_BIKE = 0;
    private final int OP_CANCEL_MOORINGS = 1;

    private BikeUser mBikeUser; //Current logged user (singleton instance)
    private Button mButtonCancelBikeBook, mButtonCancelMooringsBook;
    private CountDownTimer mCountDownTimerBike, mCountDownTimerMoorings;
    private ArrayList<Boolean> mIsTimerRunning; //To control countdown timers
    private int mCancelOperation;
    private boolean mIsActivityRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //Set activity title with the username
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.text_user_name), "");
        setTitle(username);

        mBikeUser = BikeUser.getInstance();
        mIsTimerRunning = new ArrayList<>();
        //In the beginning, no countdown timer is running
        mIsTimerRunning.add(BIKE_TIMER_POS, false);
        mIsTimerRunning.add(MOORINGS_TIMER_POS, false);

        Button buttonDeposit = (Button)findViewById(R.id.button_deposit_money);
        assert buttonDeposit != null;
        buttonDeposit.setOnClickListener(this);
        Button buttonEdit = (Button)findViewById(R.id.button_edit_profile);
        assert buttonEdit != null;
        buttonEdit.setOnClickListener(this);
        Button buttonDeleteAccount = (Button)findViewById(R.id.button_delete_account);
        assert buttonDeleteAccount != null;
        buttonDeleteAccount.setOnClickListener(this);

        mButtonCancelBikeBook = (Button)findViewById(R.id.button_cancel_book_bike);
        assert mButtonCancelBikeBook != null;
        mButtonCancelBikeBook.setOnClickListener(this);
        mButtonCancelMooringsBook = (Button)findViewById(R.id.button_cancel_book_moorings);
        assert mButtonCancelMooringsBook != null;
        mButtonCancelMooringsBook.setOnClickListener(this);

        disableCancelButtonsIfNeeded();

        initBookingData();
    }

    @Override
    public void onStart() {
        super.onStart();
        mIsActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsActivityRunning = false;
    }

    //Depending on the user booking status, format Cancel buttons
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

    //Initialization of some dynamic data related to bookings (addresses and timers)
    private void initBookingData() {
        TextView textViewBookBikeAddress = (TextView) findViewById(R.id.textView_book_bike_address);
        final TextView textViewBookBikeClock = (TextView) findViewById(R.id.textView_book_bike_clock);

        if(mBikeUser.ismBookTaken()) {
            assert textViewBookBikeAddress != null;
            textViewBookBikeAddress.setText(mBikeUser.getmBookAddress());
            if(!mIsTimerRunning.get(BIKE_TIMER_POS)) {
                //Get remaining booking time
                long remainingTime = mBikeUser.getRemainingBookingTime(mBikeUser.getmBookDate());
                mIsTimerRunning.add(BIKE_TIMER_POS, true);
                mCountDownTimerBike = new CountDownTimer(remainingTime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) millisUntilFinished / 1000;
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        assert textViewBookBikeClock != null;
                        textViewBookBikeClock.setText(i18n(R.string.textView_remaining_time, minutes, seconds));
                    }
                    public void onFinish() {
                        if(mBikeUser.ismBookTaken() && mIsActivityRunning)
                            cancelBooking(OP_CANCEL_BIKE);
                        else
                            finishTimer(OP_CANCEL_BIKE);
                    }
                }.start();
            }
        } else {
            assert textViewBookBikeAddress != null;
            textViewBookBikeAddress.setText(i18n(R.string.textView_no_bikes));
            assert textViewBookBikeClock != null;
            textViewBookBikeClock.setText("");
        }

        TextView textViewBookMooringsAddress = (TextView) findViewById(R.id.textView_book_moorings_address);
        final TextView textViewBookMooringClock = (TextView) findViewById(R.id.textView_book_moorings_clock);

        if(mBikeUser.ismMooringsTaken()) {
            assert textViewBookMooringsAddress != null;
            textViewBookMooringsAddress.setText(mBikeUser.getmMooringsAddress());
            if(!mIsTimerRunning.get(MOORINGS_TIMER_POS)) {
                //Get remaining booking time
                long remainingTime = mBikeUser.getRemainingBookingTime(mBikeUser.getmMooringsDate());
                mIsTimerRunning.add(MOORINGS_TIMER_POS, true);
                mCountDownTimerMoorings = new CountDownTimer(remainingTime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) millisUntilFinished / 1000;
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        assert textViewBookMooringClock != null;
                        textViewBookMooringClock.setText(i18n(R.string.textView_remaining_time, minutes, seconds));
                    }
                    public void onFinish() {
                        if(mBikeUser.ismMooringsTaken() && mIsActivityRunning) //This explicit cancellation can only be performed if the activity is running
                            cancelBooking(OP_CANCEL_MOORINGS);
                        else
                            finishTimer(OP_CANCEL_MOORINGS);
                    }
                }.start();
            }
        } else{
            assert textViewBookMooringsAddress != null;
            textViewBookMooringsAddress.setText(i18n(R.string.textView_no_moorings));
            assert textViewBookMooringClock != null;
            textViewBookMooringClock.setText("");
        }

        updateCurrentBalance();
    }

    //Update current balance layout
    private void updateCurrentBalance() {
        TextView textView = (TextView) findViewById(R.id.textView_profile_balance);
        assert textView != null;
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
            case R.id.button_cancel_book_bike:
                confirmCancelBooking(OP_CANCEL_BIKE);
                break;
            case R.id.button_cancel_book_moorings:
                confirmCancelBooking(OP_CANCEL_MOORINGS);
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

        updateServerUser();
    }

    //Update the user in the server
    private void updateServerUser() {
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
        httpDispatcher.doPut(this, mBikeUser, HttpConstants.PUT_BASIC_BY_ID);
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

    //Delete account from the server from user id
    private void deleteServerAccount() {
        HttpDispatcher dispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
        dispatcher.doDelete(this, Integer.toString(mBikeUser.getmId()));
    }

    //Dialog to confirm the booking cancel
    private void confirmCancelBooking(final int operation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(i18n(R.string.dialog_cancel_booking)).
                setPositiveButton(
                        i18n(R.string.dialog_cancel_positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cancelBooking(operation);
                                dialog.cancel();
                            }
                        }).
                setNegativeButton(
                        i18n(R.string.text_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Logic to cancel bookings
    private void cancelBooking(int operation) {
        String address;
        mCancelOperation = operation;

        if(operation == OP_CANCEL_BIKE) {
            address = mBikeUser.getmBookAddress().replaceAll(" ", "_"); //To avoid issues with urls
            mBikeUser.cancelBookBike();

        } else {
            address = mBikeUser.getmMooringsAddress().replaceAll(" ", "_"); //To avoid issues with urls
            mBikeUser.cancelBookMoorings();
        }

        finishTimer(operation);

        //Get the station
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_STATION);
        httpDispatcher.doGet(this, String.format(HttpConstants.GET_FIND_BIKESTATION_ADDRESS, address)); //path: .../stationAddress/{address}

        //Update user
        updateServerUser();

        initBookingData();
        disableCancelButtonsIfNeeded();
    }

    private void finishTimer(int operation){
        if(operation == OP_CANCEL_BIKE) {
            if(mCountDownTimerBike != null)
                mCountDownTimerBike.cancel();
            mIsTimerRunning.add(BIKE_TIMER_POS, false);
        } else {
            if(mCountDownTimerMoorings != null)
                mCountDownTimerMoorings.cancel();
            mIsTimerRunning.add(MOORINGS_TIMER_POS, false);
        }
    }

    @Override
    public void processServerResult(String result, int operation) {
        //Process the server response
        switch (operation) {
            case HttpConstants.OPERATION_GET: //Bikestation always, I don't GET users here
                try {
                    HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_STATION);
                    ObjectMapper mapper = httpDispatcher.getMapper();
                    BikeStation bikeStation = mapper.readValue(result, BikeStation.class);

                    //Update bike station
                    if(mCancelOperation == OP_CANCEL_BIKE)
                        bikeStation.cancelBikeBooking();
                    else
                        bikeStation.cancelMooringsBooking();

                    httpDispatcher.doPut(this, bikeStation, HttpConstants.PUT_BASIC_BY_ID);

                } catch (Exception e) {
                    Log.e("[GET Result]" + getClass().getCanonicalName(), e.getLocalizedMessage(), e);
                    showBasicErrorDialog(i18n(R.string.toast_sync_error), i18n(R.string.text_ok));
                }
                break;
            case HttpConstants.OPERATION_PUT: //No checks in the server, so response is always going to be OK
                if(result.contains(BikeUser.ENTITY_ID))
                    updateCurrentBalance(); //Same PUT for cancel bookings or update balance, so I just do this always

                Toast.makeText(this,
                        i18n(R.string.text_operation_complete),
                        Toast.LENGTH_SHORT).show();

                break;
            case HttpConstants.OPERATION_DELETE: //User deletion
                deleteLocalUser(); //When the server operation is done, reset local configurations
                break;
        }
    }

    //Reset local configurations and return to the login Activity
    private void deleteLocalUser() {
        mBikeUser.resetInstance();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit().
                putString(getString(R.string.text_user_name), "").
                putString(getString(R.string.text_password), "").
                apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Show a basic error dialog with a custom message
   private void showBasicErrorDialog(String message, String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(i18n(R.string.text_error)).
                setIcon(R.drawable.ic_error_outline).
                setMessage(message).
                setPositiveButton(
                        positiveButtonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

}