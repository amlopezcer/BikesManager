package com.amlopezc.bikesmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.entity.BikeUser;
import com.amlopezc.bikesmanager.entity.Booking;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.amlopezc.bikesmanager.util.ExpandableListAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cocosw.bottomsheet.BottomSheet;

/**
 * Main layout, show a google map with the stations as a markers. Main features:
 * - Take, leave, book bikes
 * - Connect to google maps app to enable all of its features
 * - Navigate to other related layout (such as the chart or expandable list view)
 * - Via Action Bar: change settings, read and update account, log out...
 */

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,
        AsyncTaskListener<String>, View.OnClickListener {

    private final int LIST_REQUEST_CODE = 1; //Intent code to connect to the ListActivity
    private final int MY_LOCATION_REQUEST_CODE = 1; //To request location permission

    //LatLngBounds that includes Madrid to initialize map camera
    private final LatLngBounds MADRID  = new LatLngBounds(new LatLng(40.38, -3.72), new LatLng(40.48, -3.67));

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private HashMap<String, BikeStation> mStations;
    private BikeUser mBikeUser;
    private Booking mCurrentBooking;
    private String mCurrentBikeStationAddress;

    private TextView mTextView_balance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mStations = new HashMap<>();

        setUpMapIfNeeded();
        mMap.setOnMarkerClickListener(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //Request location permission, turning on location services if granted
        requestLocationPermission();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.map)).
                    getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    //Set some map features: zoom + camera
    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MADRID.getCenter(), 12));
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else
                showBasicErrorDialog(i18n(R.string.text_location_not_granted), i18n(R.string.text_ok));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();

        //Init widgets
        mTextView_balance = (TextView) findViewById(R.id.textView_balance);
        assert mTextView_balance != null;
        mTextView_balance.setOnClickListener(this);

        ImageButton imageButton_account = (ImageButton) findViewById(R.id.imageButton_goToAccount);
        assert imageButton_account != null;
        imageButton_account.setOnClickListener(this);

        //Ensuring connection data is set, showing ConnectionDataDialog otherwise
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serverAddress = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, "");
        String serverPort = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, "");

        if(serverAddress.trim().isEmpty() || serverPort.trim().isEmpty())
            showConnectionDataDialog();
        else { //Getting update data form the server
            initUserIfNeeded();
            getUpdatedStationData();
        }

        mCurrentBooking = null;

        updateBikeStateBar();
    }

    //Update the layout depending on user state
    private void updateBikeStateBar() {
        RelativeLayout bikeStateBar = (RelativeLayout) findViewById(R.id.relativeLayout_bikeState);
        assert bikeStateBar != null;
        TextView bikeStateText = (TextView) findViewById(R.id.textView_bikeStateText);
        assert bikeStateText != null;

        if(mBikeUser.ismBikeTaken()) {
            bikeStateBar.setBackgroundColor(ContextCompat.getColor(this, R.color.lightPrimaryColor));
            bikeStateText.setText(i18n(R.string.text_state_bike_taken));
        } else {
            bikeStateBar.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGrey));
            bikeStateText.setText(i18n(R.string.text_state_bike_not_taken));
        }

        mTextView_balance.setText(i18n(R.string.text_format_money, mBikeUser.getmBalance()));
    }

    //Set the user with updated info from the server
    private void initUserIfNeeded() {
        if (mBikeUser == null)
            mBikeUser = BikeUser.getInstance();

        //conditions which indicate the user is not updated
        if(mBikeUser.getmUserName() == null || mBikeUser.getmUserName().isEmpty() ||
                mBikeUser.getmId() == -1 || mBikeUser.isBookingTimedOut())
            getUpdatedUserData();
    }

    private void getUpdatedUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.text_user_name), "");

        //Get the user selected
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
        httpDispatcher.doGet(this, String.format(HttpConstants.GET_FIND_USER_USERNAME, username)); //path: .../user/{username}
    }

    //Get updated server data related to bike stations
    private void getUpdatedStationData() {
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_STATION);
        httpDispatcher.doGet(this, HttpConstants.GET_FIND_ALL);
    }

    private void showConnectionDataDialog() {
        DialogFragment dialog = new ConnectionDataDialogFragment();
        dialog.show(getFragmentManager(), ConnectionDataDialogFragment.CLASS_ID);
    }

    public void doPositiveClickConnectionDataDialog() {
        initUserIfNeeded();
        getUpdatedStationData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_balance:
            case R.id.imageButton_goToAccount:
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Action bar item click handler
        Intent intent;

        switch(item.getItemId()){
            case R.id.action_list: //Navigates to the ListActivity
                intent = new Intent(this, ListActivity.class);
                startActivityForResult(intent, LIST_REQUEST_CODE);
                return true;
            case R.id.action_chart: //Navigates to the ChartActivity
                intent = new Intent(this, ChartActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_account: //Navigates to the AccountActivity
                intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh: //Updates data
                initUserIfNeeded();
                getUpdatedStationData();
                updateBikeStateBar();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MADRID.getCenter(), 12)); //Move the camera to the init position for user help
                return true;
            case R.id.action_settings: //Navigates to the SettingsActivity
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout: //Log out
                confirmLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Log the user out over an explicit confirmation
    private void confirmLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(i18n(R.string.dialog_logout)).
                setPositiveButton(
                        i18n(R.string.item_text_logout),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                logOut();
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

    //Log the user out and go to he login activity
    private void logOut() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit().
                putString(getString(R.string.text_user_name), "").
                putString(getString(R.string.text_password), "").
                apply();

        mBikeUser.resetInstance();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //Update markers with current server data
    private void updateMarkers() {
        mMap.clear(); //Clear the map and redraw all markers
        for (HashMap.Entry<String, BikeStation> entry : mStations.entrySet())
            mMap.addMarker(new MarkerOptions().
                    position(new LatLng(
                            entry.getValue().getmLatitude(),
                            entry.getValue().getmLongitude())).
                    title(entry.getKey()). //key = "id - address
                    snippet(setMarkerSnippet(entry.getValue())).
                    icon(getAvailabilityColor(entry.getValue())));
    }

    //Set marker snippets with some info from the station
    private String setMarkerSnippet(BikeStation bikeStation) {
        return i18n(R.string.text_snippet_marker,
                bikeStation.getmAvailableBikes(),
                bikeStation.getmTotalMoorings(),
                bikeStation.getAvailableMoorings(),
                bikeStation.getmTotalMoorings(),
                bikeStation.getCurrentFare());
    }

    //Set marker colors depending on the availability (green to red) or booking status (blue)
    private BitmapDescriptor getAvailabilityColor(BikeStation bikeStation) {
        if((mBikeUser.ismBookTaken() && mBikeUser.getmBookAddress().equals(bikeStation.getmAddress())) ||
                (mBikeUser.ismMooringsTaken() && mBikeUser.getmMooringsAddress().equals(bikeStation.getmAddress())))
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);

        int availability = bikeStation.getStationAvailability();

        if(availability == 0)
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        else if (availability < 50) //50%
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        else
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
    }

    //Process intents responses
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Ensure there is something to read
        if (data == null)
            return;
        switch (requestCode) {
            case LIST_REQUEST_CODE:
                /**
                 * Coordinates from the ListActivity to position the camera, no need to check resultCode
                 * because it can only be one (OK_RESULT_CODE).Include the 'if' statement to check
                 * it if needed.
                 */
                Bundle bundle = data.getBundleExtra(ExpandableListAdapter.EXTRA_RESULT);
                Double latCoord = bundle.getDouble(ExpandableListAdapter.BUNDLE_LAT);
                Double longCoord = bundle.getDouble(ExpandableListAdapter.BUNDLE_LONG);
                LatLng marker = new LatLng(latCoord, longCoord);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
                break;
        }
    }

    //Manage the click on the marker to do whatever with the station selected with a BottomSheet
    @Override
    public boolean onMarkerClick(final Marker marker) {
        new BottomSheet.Builder(this, R.style.BottomSheet_StyleDialog).
                title(marker.getTitle()).
                grid().
                sheet(R.menu.menu_bottomsheet).
                listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurrentBikeStationAddress = marker.getTitle();
                        initUserIfNeeded(); //Get updated server data first
                        getUpdatedStationData();
                        switch (which) {
                            case R.id.menu_takeBike:
                                performBikeStationOperation(HttpConstants.PUT_TAKE_BIKE);
                                break;
                            case R.id.menu_leaveBike:
                                performBikeStationOperation(HttpConstants.PUT_LEAVE_BIKE);
                                break;
                            case R.id.menu_book:
                                showBookDialog();
                                break;
                        }
                    }
                }).show();
        return false;
    }

    //Manages the operation selected with the server
    private void performBikeStationOperation(String operation) {
        if(!isUserAbleToModifyBikeStation(operation, mStations.get(mCurrentBikeStationAddress)))
            return;

        BikeStation bikeStation = mStations.get(mCurrentBikeStationAddress).updateBikeStation(operation, mBikeUser);

        //If the station availability allows the operation , update the server
        if(bikeStation != null) {
            HttpDispatcher httpDispatcher;

            //Update user locally and update the booking
            switch(operation) {
                case HttpConstants.PUT_TAKE_BIKE:
                    if(mBikeUser.ismBookTaken()) {
                        httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_BOOKING);
                        httpDispatcher.doDelete(this, null, String.format(Locale.getDefault(), HttpConstants.DELETE_BOOKING_BY_USERNAME, mBikeUser.getmUserName(), Booking.BOOKING_TYPE_BIKE));
                    }
                    mBikeUser.takeBike();
                    break;
                case HttpConstants.PUT_LEAVE_BIKE:
                    if(mBikeUser.ismMooringsTaken()) {
                        httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_BOOKING);
                        httpDispatcher.doDelete(this, null, String.format(Locale.getDefault(), HttpConstants.DELETE_BOOKING_BY_USERNAME, mBikeUser.getmUserName(), Booking.BOOKING_TYPE_MOORINGS));
                    }
                    mBikeUser.leaveBike();
                    break;
                case HttpConstants.PUT_BOOK_BIKE:
                    mBikeUser.bookBike(bikeStation.getmAddress());
                    mCurrentBooking = new Booking(mBikeUser.getmUserName(), bikeStation.getmAddress(), mBikeUser.getmBookDate(), Booking.BOOKING_TYPE_BIKE);
                    break;
                case HttpConstants.PUT_BOOK_MOORINGS:
                    mBikeUser.bookMoorings(bikeStation.getmAddress());
                    mCurrentBooking = new Booking(mBikeUser.getmUserName(), bikeStation.getmAddress(), mBikeUser.getmMooringsDate(), Booking.BOOKING_TYPE_MOORINGS);
                    break;
            }

            //Here, only update the BikeStation; later, if done, update the user and/or the booking to ensure consistency
            httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_STATION);
            httpDispatcher.doPut(this, bikeStation, operation);

        } else
            showBasicErrorDialog(i18n(R.string.text_bikeop_impossible), i18n(R.string.text_ok));
    }

    //Check if the current user status allows to take or leave bikes
    private boolean isUserAbleToModifyBikeStation(String operation, BikeStation bikeStation) {
        if(operation.equals(HttpConstants.PUT_TAKE_BIKE) && (mBikeUser.ismBikeTaken() ||
                (mBikeUser.ismBookTaken() && !mBikeUser.getmBookAddress().equals(bikeStation.getmAddress())))) {
            showBasicErrorDialog(i18n(R.string.text_bike_taken), i18n(R.string.text_ok));
            return false;
        }

        if(operation.equals(HttpConstants.PUT_LEAVE_BIKE) && (!mBikeUser.ismBikeTaken() ||
                (mBikeUser.ismMooringsTaken() && !mBikeUser.getmMooringsAddress().equals(bikeStation.getmAddress())))) {
            showBasicErrorDialog(i18n(R.string.text_bike_not_taken), i18n(R.string.text_ok));
            return false;
        }

        if(operation.equals(HttpConstants.PUT_TAKE_BIKE) && bikeStation.getCurrentFare() > mBikeUser.getmBalance()) {
            showBasicErrorDialog(i18n(R.string.text_balance_insufficient), i18n(R.string.text_ok));
            return false;
        }

        if(operation.equals(HttpConstants.PUT_TAKE_BIKE)) //Here, the bike can be taken
            mBikeUser.setmBalance(mBikeUser.getmBalance() - bikeStation.getCurrentFare());

        return true;
    }

    private void showBookDialog() {
        DialogFragment dialog = new BookDialogFragment();
        dialog.show(getFragmentManager(), BookDialogFragment.CLASS_ID);
    }

    //Book dialog response indicating the booking type
    public void doPositiveClickBookDialog(boolean isBikeBooked, boolean isMooringsBooked) {
        if(isBikeBooked)
            performBikeStationOperation(HttpConstants.PUT_BOOK_BIKE);

        if(isMooringsBooked)
            performBikeStationOperation(HttpConstants.PUT_BOOK_MOORINGS);
    }

    @Override
    public void processServerResult(String result, int operation) {
        switch (operation) {
            //Update data and layout
            case HttpConstants.OPERATION_GET:
                try {
                    if (result.contains(BikeStation.ENTITY_ID)) { //GET related to bike station instance
                        manageStationData(result);
                        initUserIfNeeded();
                    } else { //GET related to user instance
                        manageUserData(result);
                        updateBikeStateBar();
                    }
                } catch (Exception e) {
                    Log.e("[GET Result]" + getClass().getCanonicalName(), e.getLocalizedMessage(), e);
                    showBasicErrorDialog(i18n(R.string.toast_sync_error), i18n(R.string.text_ok));
                }
                break;
            case HttpConstants.OPERATION_PUT:
                if (result.contains(HttpConstants.SERVER_RESPONSE_OK))
                    if (result.contains(BikeStation.ENTITY_ID)) {
                        //Result related to bike station instance, now PUT the user and the booking if appropriate
                        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
                        httpDispatcher.doPut(this, mBikeUser, HttpConstants.PUT_BASIC_BY_ID);

                        if(mCurrentBooking != null) {
                            httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_BOOKING);
                            httpDispatcher.doPost(this, mCurrentBooking);
                            mCurrentBooking = null;
                        }

                    } else //Result related to the 2nd update, everything goes fine
                        Toast.makeText(this,
                                i18n(R.string.text_operation_completed),
                                Toast.LENGTH_SHORT).show();
                else if (result.contains(HttpConstants.SERVER_RESPONSE_KO)) {
                        //Here, only the bike station operation can goes wrong, get user data from the server to discard local changes
                        getUpdatedUserData();
                        showBasicErrorDialog(i18n(R.string.text_bikeop_impossible), i18n(R.string.text_ok));
                    }
                else
                    showBasicErrorDialog(i18n(R.string.toast_sync_error), i18n(R.string.text_ok));


                initUserIfNeeded();
                getUpdatedStationData();
                updateBikeStateBar();
                break;
            //Only the Booking instance performs a POST or DELETE here, but there is no answer from the server to process
            case HttpConstants.OPERATION_POST: break;
            case HttpConstants.OPERATION_DELETE: break;
        }
    }

    //Manage Station data received form the server by updating local data and layout
    private void manageStationData(String result) throws Exception {
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_STATION);
        ObjectMapper mapper = httpDispatcher.getMapper();
        List<BikeStation> bikeStationList = mapper.readValue(result,
                new TypeReference<List<BikeStation>>() {});
        readData(bikeStationList);
        updateLocalLayout();
    }

    //Read server data to update current state
    private void readData(List<BikeStation> bikeStationList) {
        mStations = new HashMap<>();
        for(BikeStation bikeStation : bikeStationList)
            mStations.put(bikeStation.getStationHeader(), bikeStation);
    }

    //Update local layout (update map = update markers)
    private void updateLocalLayout() {
        updateMarkers();
    }

    //Manage user data received form the server by updating local singleton instance
    private void manageUserData(String result) throws Exception {
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
        ObjectMapper mapper = httpDispatcher.getMapper();
        BikeUser bikeUser = mapper.readValue(result, BikeUser.class);

        //Update the singleton local instance
        mBikeUser = BikeUser.updateInstance(bikeUser);
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