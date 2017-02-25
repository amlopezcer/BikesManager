package com.amlopezc.bikesmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.entity.BikeUser;
import com.amlopezc.bikesmanager.entity.Booking;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Class to read the profile / account. It manages additional operations such as
 * booking cancels, money depositions, account elimination or access to account modification.
 */
public class AccountActivityFragment extends Fragment implements View.OnClickListener,
        AsyncTaskListener<String> {

    //Constants to position the countdown timer control of bikes and slots in the 'mIsTimerRunning' array
    private final int BIKE_TIMER_POS = 0;
    private final int SLOTS_TIMER_POS = 1;
    //Constant to control the booking which want to be canceled
    private final int OP_CANCEL_BIKE = 0;
    private final int OP_CANCEL_SLOTS = 1;

    private BikeUser mBikeUser; //Current logged user (singleton instance)
    private Button mButtonCancelBikeBook, mButtonCancelSlotsBook;
    private TextView mtTextViewBookBikeAddress, mTextViewBookBikeClock, mTextViewBookSlotsAddress,
            mTextViewBookMooringClock, mTextViewBalance;
    private CountDownTimer mCountDownTimerBike, mCountDownTimerSlots;
    private ArrayList<Boolean> mIsTimerRunning; //To control countdown timers
    private boolean mCancelBike; //Cancel operation selected (bikes or slots)
    private boolean mCancelSlots; //Cancel operation selected (bikes or slots)
    private String mStationAddressBikes;
    private String mStationAddressSlots;
    private boolean mIsActivityRunning; //To control timers behavior when they finish


    public AccountActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        //Get the user and update it if there are any timed out booking
        mBikeUser = BikeUser.getInstance();
        checkUserTimedOutBookings();

        //Init timers
        mIsTimerRunning = new ArrayList<>();
        //In the beginning, no countdown timer is running
        mIsTimerRunning.add(BIKE_TIMER_POS, false);
        mIsTimerRunning.add(SLOTS_TIMER_POS, false);

        mCancelBike = false;
        mCancelSlots = false;
        mStationAddressBikes = "";
        mStationAddressSlots = "";

        initComponentsUI(view);
        disableCancelButtonsIfNeeded();
        initBookingData();

        return view;
    }

    //Set the user with updated info from the server
    private void checkUserTimedOutBookings() {
        boolean updatable = false;

        if(mBikeUser.isBikeBookingTimedOut()) {
            mBikeUser.cancelBookBike();
            updatable = true;
        }
        if(mBikeUser.isSlotsBookingTimedOut()) {
            mBikeUser.cancelBookSlots();
            updatable = true;
        }
        if(updatable)
            updateServerUser();
    }

    //Update the user in the server
    private void updateServerUser() {
        HttpDispatcher httpDispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_USER);
        httpDispatcher.doPut(this, mBikeUser, HttpConstants.PUT_BASIC_BY_ID);
    }

    //Init basic UI components
    private void initComponentsUI(View view) {
        Button buttonDeposit = (Button) view.findViewById(R.id.button_deposit_money);
        buttonDeposit.setOnClickListener(this);
        Button buttonEdit = (Button)view.findViewById(R.id.button_edit_profile);
        buttonEdit.setOnClickListener(this);
        Button buttonDeleteAccount = (Button)view.findViewById(R.id.button_delete_account);
        buttonDeleteAccount.setOnClickListener(this);

        mButtonCancelBikeBook = (Button)view.findViewById(R.id.button_cancel_book_bike);
        mButtonCancelBikeBook.setOnClickListener(this);
        mButtonCancelSlotsBook = (Button)view.findViewById(R.id.button_cancel_book_slots);
        mButtonCancelSlotsBook.setOnClickListener(this);

        mtTextViewBookBikeAddress = (TextView) view.findViewById(R.id.textView_book_bike_address);
        mTextViewBookBikeClock = (TextView)view.findViewById(R.id.textView_book_bike_clock);
        mTextViewBookSlotsAddress = (TextView) view.findViewById(R.id.textView_book_slots_address);
        mTextViewBookMooringClock = (TextView)view.findViewById(R.id.textView_book_slots_clock);
        mTextViewBalance = (TextView)view.findViewById(R.id.textView_profile_balance);
    }

    //Depending on the user booking status, format Cancel buttons
    private void disableCancelButtonsIfNeeded() {
        if(!mBikeUser.ismBookTaken()) {
            mButtonCancelBikeBook.setEnabled(false);
            mButtonCancelBikeBook.setTextColor(ContextCompat.getColor(getActivity(), R.color.lightGrey));
        }
        if(!mBikeUser.ismSlotsTaken()) {
            mButtonCancelSlotsBook.setEnabled(mBikeUser.ismSlotsTaken());
            mButtonCancelSlotsBook.setTextColor(ContextCompat.getColor(getActivity(), R.color.lightGrey));
        }
    }

    //Initialization of some dynamic data related to bookings (addresses and timers)
    private void initBookingData() {

        //Set timers
        if(mBikeUser.ismBookTaken()) {
            mtTextViewBookBikeAddress.setText(mBikeUser.getmBookAddress());
            if(!mIsTimerRunning.get(BIKE_TIMER_POS)) {
                //Get remaining booking time
                long remainingTime = mBikeUser.getRemainingBookingTime(mBikeUser.getmBookDate());
                mIsTimerRunning.add(BIKE_TIMER_POS, true);
                mCountDownTimerBike = new CountDownTimer(remainingTime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) millisUntilFinished / 1000;
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        if(mIsActivityRunning) //Check if the fragment where the timer is going to be updated is attached to the activity
                            mTextViewBookBikeClock.setText(i18n(R.string.textView_remaining_time, minutes, seconds));
                    }
                    public void onFinish() {
                        finishTimer(OP_CANCEL_BIKE);
                        if(mIsActivityRunning) { //Update user from here only if the activity is running
                            mBikeUser.cancelBookBike();
                            updateServerUser();
                            initBookingData();
                            disableCancelButtonsIfNeeded();
                        }
                    }
                }.start();
            }
        } else {
            mtTextViewBookBikeAddress.setText(i18n(R.string.textView_no_bikes));
            mTextViewBookBikeClock.setText("");
        }

        if(mBikeUser.ismSlotsTaken()) {
            mTextViewBookSlotsAddress.setText(mBikeUser.getmSlotsAddress());
            if(!mIsTimerRunning.get(SLOTS_TIMER_POS)) {
                //Get remaining booking time
                long remainingTime = mBikeUser.getRemainingBookingTime(mBikeUser.getmSlotsDate());
                mIsTimerRunning.add(SLOTS_TIMER_POS, true);
                mCountDownTimerSlots = new CountDownTimer(remainingTime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) millisUntilFinished / 1000;
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        if(mIsActivityRunning) //Check if the fragment where the timer is going to be updated is attached to the activity
                            mTextViewBookMooringClock.setText(i18n(R.string.textView_remaining_time, minutes, seconds));
                    }
                    public void onFinish() {
                        finishTimer(OP_CANCEL_SLOTS);
                        if(mIsActivityRunning) { //Update user from here only if the activity is running
                            mBikeUser.cancelBookSlots();
                            updateServerUser();
                            initBookingData();
                            disableCancelButtonsIfNeeded();
                        }
                    }
                }.start();
            }
        } else{
            mTextViewBookSlotsAddress.setText(i18n(R.string.textView_no_slots));
            mTextViewBookMooringClock.setText("");
        }

        updateCurrentBalance();
    }

    //Update current balance layout
    private void updateCurrentBalance() {
        mTextViewBalance.setText(i18n(R.string.text_format_money, mBikeUser.getmBalance()));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_deposit_money:
                showDepositMoneyDialog();
                break;
            case R.id.button_edit_profile:
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.button_delete_account:
                if(mBikeUser.ismBikeTaken())
                    showBasicErrorDialog(i18n(R.string.dialog_error_delete_account), i18n(R.string.text_ok));
                else
                    confirmDeleteAccount();
                break;
            case R.id.button_cancel_book_bike:
                confirmCancelBooking(OP_CANCEL_BIKE);
                break;
            case R.id.button_cancel_book_slots:
                confirmCancelBooking(OP_CANCEL_SLOTS);
                break;
        }
    }

    //Show the DepositMoneyDialog to allow the user to select the amount of money to deposit
    private void showDepositMoneyDialog() {
        DialogFragment dialog = new DepositMoneyDialogFragment();
        dialog.show(getActivity().getFragmentManager(), DepositMoneyDialogFragment.CLASS_ID);
    }

    //Update current balance and user instance in case the deposit has been confirmed
    public void doPositiveClickDepositMoneyDialog(String deposit) {
        float newBalance = mBikeUser.getmBalance() + Float.parseFloat(deposit);
        mBikeUser.setmBalance(newBalance);

        updateServerUser();
    }

    //Dialog to confirm delete account operation
    private void confirmDeleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(i18n(R.string.dialog_delete_account)).
                setPositiveButton(
                        i18n(R.string.dialog_delete),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //Delete bookings
                                if(mBikeUser.ismBookTaken())
                                    cancelBooking(OP_CANCEL_BIKE, true);
                                if(mBikeUser.ismSlotsTaken())
                                    cancelBooking(OP_CANCEL_SLOTS, true);

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
        HttpDispatcher dispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_USER);
        dispatcher.doDelete(this, mBikeUser, HttpConstants.DELETE_BASIC_BY_ID);
    }

    //Dialog to confirm the booking cancel
    private void confirmCancelBooking(final int operation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(i18n(R.string.dialog_cancel_booking)).
                setPositiveButton(
                        i18n(R.string.dialog_cancel_positive),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cancelBooking(operation, false);
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
    private void cancelBooking(int operation, boolean deleteUserOp) {
        String address;
        int bookingType;
        HttpDispatcher httpDispatcher;

        if(operation == OP_CANCEL_BIKE) {
            mCancelBike = true;
            mStationAddressBikes = mBikeUser.getmBookAddress();
            address = mBikeUser.getmBookAddress().replaceAll(" ", "_"); //To avoid issues with urls
            mBikeUser.cancelBookBike();
            bookingType = Booking.BOOKING_TYPE_BIKE;
        } else {
            mCancelSlots = true;
            mStationAddressSlots = mBikeUser.getmSlotsAddress();
            address = mBikeUser.getmSlotsAddress().replaceAll(" ", "_"); //To avoid issues with urls
            mBikeUser.cancelBookSlots();
            bookingType = Booking.BOOKING_TYPE_SLOTS;
        }

        //Get the station, its update occurs when the server responds
        httpDispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_STATION);
        httpDispatcher.doGet(this, String.format(HttpConstants.GET_FIND_BIKESTATION_ADDRESS, address)); //path: .../stationAddress/{address}

        //Delete the booking
        httpDispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_BOOKING);
        httpDispatcher.doDelete(this, null, String.format(Locale.getDefault(), HttpConstants.DELETE_BOOKING_BY_USERNAME, mBikeUser.getmUserName(), bookingType));

        //Update user and layout
        finishTimer(operation);

        if(!deleteUserOp) //If the booking removal is due to a user deletion, there is no need of update it
            updateServerUser();

        initBookingData();
        disableCancelButtonsIfNeeded();
    }

    //Finish timers correctly
    private void finishTimer(int operation){
        if(operation == OP_CANCEL_BIKE) {
            if(mCountDownTimerBike != null)
                mCountDownTimerBike.cancel();
            mIsTimerRunning.add(BIKE_TIMER_POS, false);
        } else {
            if(mCountDownTimerSlots != null)
                mCountDownTimerSlots.cancel();
            mIsTimerRunning.add(SLOTS_TIMER_POS, false);
        }
    }

    @Override
    public void processServerResult(String result, int operation) {
        //Process the server response
        switch (operation) {
            case HttpConstants.OPERATION_GET:
                try { //Here I only GET Stations
                    HttpDispatcher httpDispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_STATION);
                    ObjectMapper mapper = httpDispatcher.getMapper();
                    BikeStation bikeStation = mapper.readValue(result, BikeStation.class);

                    //Update bike station
                    if (mCancelBike && mStationAddressBikes.equals(bikeStation.getmAddress())) {
                        bikeStation.cancelBikeBooking();
                        mCancelBike = false;
                        mStationAddressBikes = "";
                        httpDispatcher.doPut(this, bikeStation, HttpConstants.PUT_BASIC_BY_ID);
                    }

                    if(mCancelSlots && mStationAddressSlots.equals(bikeStation.getmAddress())) {
                        bikeStation.cancelSlotsBooking();
                        mCancelSlots = false;
                        mStationAddressSlots = "";
                        httpDispatcher.doPut(this, bikeStation, HttpConstants.PUT_BASIC_BY_ID);
                    }
                } catch (Exception e) {
                    Log.e("[GET Result]" + getClass().getCanonicalName(), e.getLocalizedMessage(), e);
                    showBasicErrorDialog(i18n(R.string.text_sync_error), i18n(R.string.text_ok));
                }
                break;
            case HttpConstants.OPERATION_PUT: //No checks in the server, response is always OK
                if(result.contains(BikeUser.ENTITY_ID)) {
                    updateCurrentBalance(); //Same PUT for cancel bookings or update balance, so I just do this anyway
                    /*Show a Toast for user feedback (included in the IF statement to be shown
                        just once when cancel bookings (two PUTs: user and station))
                     */
                    Toast.makeText(getActivity(),
                            i18n(R.string.text_operation_completed),
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case HttpConstants.OPERATION_DELETE:
                //User deletion, the other option is booking deletion which do not require any checks
                if(result.contains(BikeUser.ENTITY_ID))
                    deleteLocalUser(); //When the server operation is done, reset local configurations
                break;
        }
    }

    //Reset local configurations and return to the login Activity
    private void deleteLocalUser() {
        mBikeUser.resetInstance();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit().
                putString(getString(R.string.text_user_name), "").
                putString(getString(R.string.text_password), "").
                apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    // Show a basic error dialog with a custom message
   private void showBasicErrorDialog(String message, String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
