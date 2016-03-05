package com.amlopezc.bikesmanager;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeStation;
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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.cocosw.bottomsheet.BottomSheet;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener,
        AsyncTaskListener<String> {

    private final int SETTINGS_REQUEST_CODE = 0;
    private final int LIST_REQUEST_CODE = 1;
    private final String OP_TAKE = "take";
    private final String OP_LEAVE = "leave";


    //LatLngBounds that includes Madrid.
    private final LatLngBounds MADRID  = new LatLngBounds(new LatLng(40.38, -3.72), new LatLng(40.48, -3.67));

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private HashMap<String, BikeStation> mStations;
    private HttpDispatcher dispatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mStations = new HashMap<>();
        dispatcher = new HttpDispatcher(this);

        setUpMapIfNeeded();
        mMap.setOnMarkerClickListener(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();

        //Ensuring connection data is set
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_USER, "");
        String serverAddress = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, "");
        String serverPort = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, "");
        if(userName.trim().isEmpty() || serverAddress.trim().isEmpty() || serverPort.trim().isEmpty())
            showConnectionDataDialog();

        //Fetch updated server data related to the bike stations
        fetchUpdatedServerData();
    }

    private void fetchUpdatedServerData() {
        dispatcher.doGet(this);
    }

    private void showConnectionDataDialog() {
        DialogFragment dialog = new ConnectionDataDialogFragment();
        dialog.show(getFragmentManager(), "ConnectionDialogFragment");
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
            case R.id.action_settings:
                intent = new Intent();
                intent.setClass(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                return true;
            case R.id.action_list:
                intent = new Intent(this, ListActivity.class);
                startActivityForResult(intent, LIST_REQUEST_CODE);
                return true;
            case R.id.action_chart:
                intent = new Intent(this, ChartActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                fetchUpdatedServerData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MADRID.getCenter(), 12));
    }

    private void updateMarkers() {
        mMap.clear();
        for (Map.Entry<String, BikeStation> entry : mStations.entrySet())
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(
                            entry.getValue().getmLatitude(),
                            entry.getValue().getmLongitude()))
                    .title(entry.getKey())
                    .snippet(entry.getValue().getAvailabilityMessage())
                    .icon(getAvailabilityColor(entry.getValue())));
    }

    private BitmapDescriptor getAvailabilityColor(BikeStation bikeStation) {
        if(bikeStation.getmAvailableBikes() == 0)
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        else if (bikeStation.getmTotalBikes() - bikeStation.getmAvailableBikes() > bikeStation.getmAvailableBikes())
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        else
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LIST_REQUEST_CODE:
                /**
                 * Coordinates from the list to position the camera, no need to check resultCode
                 * because it can only be one (OK_RESULT_CODE). Include the 'if' statement to check
                 * it if needed.
                 */
                Bundle bundle = data.getBundleExtra(ExpandableListAdapter.EXTRA_RESULT);
                Double latCoord = bundle.getDouble(ExpandableListAdapter.BUNDLE_LAT);
                Double longCoord = bundle.getDouble(ExpandableListAdapter.BUNDLE_LONG);
                LatLng marker = new LatLng(latCoord, longCoord);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
                break;
            case SETTINGS_REQUEST_CODE: //Configuration data :TODO: son pruebas, eliminar más adelante
                SharedPreferences sharedPreferences = PreferenceManager.
                        getDefaultSharedPreferences(this);
                String username = sharedPreferences.getString("username", null);
                String msg = String.format("Datos del usuario '%s' guardados", username);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {


        new BottomSheet.Builder(this).
                title(marker.getTitle()).
                grid().
                sheet(R.menu.menu_bottomsheet).
                darkTheme().
                listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.menu_takeBike:
                                modifyBike(marker, OP_TAKE); //TODO: hacer un GET indivudual?
                                break;
                            case R.id.menu_leaveBike:
                                modifyBike(marker, OP_LEAVE); //TODO: hacer un GET indivudual?
                                break;
                            case R.id.menu_reportBike:
                                Toast.makeText(getApplicationContext(), "Report", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).show();
        return false;
    }

    private void modifyBike(Marker marker, String operation) {
        fetchUpdatedServerData();
        BikeStation bikeStation = mStations.get(marker.getTitle());

        boolean isOperationPossible = false;

        switch (operation) {
            case OP_TAKE:
                isOperationPossible = bikeStation.getmAvailableBikes() > 0;
                if(isOperationPossible)
                    bikeStation.setmAvailableBikes(bikeStation.getmAvailableBikes() - 1);
                break;
            case OP_LEAVE:
                isOperationPossible = (bikeStation.getmAvailableBikes() + bikeStation.getmBrokenBikes() + bikeStation.getmReservedBikes()) < bikeStation.getmTotalBikes();
                if(isOperationPossible)
                    bikeStation.setmAvailableBikes(bikeStation.getmAvailableBikes() + 1);
                break;
        }

        if(isOperationPossible) {
            bikeStation.setmTimeStamp(getCurrentDateFormatted());
            bikeStation.setServerId(bikeStation.getmId());
            dispatcher.doPut(this, bikeStation, operation);
        } else
            Toast.makeText(this, "El estado de la estación no permite completar la operación", Toast.LENGTH_SHORT).show();

    }

    private String getCurrentDateFormatted() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        Calendar cal = Calendar.getInstance();
        StringBuilder builder = new StringBuilder(dateFormat.format(cal.getTime()));
        return builder.append("T").append(timeFormat.format(cal.getTime())).append("+01:00").toString();
    }

    @Override
    public void processResult(String result, int operation) {
        final String SERVER_RESPONSE_OK = "SERVER_OK";
        final String SERVER_RESPONSE_KO = "SERVER_KO";

        switch (operation) {
            case HttpDispatcher.OPERATION_GET:
                try {
                    ObjectMapper mapper = dispatcher.getMapper();
                    List<BikeStation> bikeStationList = mapper.readValue(result,
                            new TypeReference<List<BikeStation>>() {});
                    readData(bikeStationList);
                    updateLocalLayout();
                } catch (Exception e) {
                    Log.e("JSON (GET result)", e.getLocalizedMessage(), e);
                    Toast.makeText(this, "Error al sincronizar con el servidor", Toast.LENGTH_SHORT).show();
                }
                break;

            case HttpDispatcher.OPERATION_PUT:
                if (result.equals(SERVER_RESPONSE_OK))
                    Toast.makeText(this, "Operación realizada con éxito", Toast.LENGTH_SHORT).show();
                else if (result.equals(SERVER_RESPONSE_KO))
                    Toast.makeText(this, "El estado de la estación no permite completar la operación", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Error al sincronizar con el servidor", Toast.LENGTH_SHORT).show();
                fetchUpdatedServerData();
                break;
        }
    }

    private void updateLocalLayout() {
        updateMarkers();
    }

    private void readData(List<BikeStation> bikeStationList) {
        mStations = new HashMap<>();
        String headerTemplate = "%d - %s";
        for(BikeStation bikeStation : bikeStationList)
            mStations.put(String.format(headerTemplate, bikeStation.getmId(), bikeStation.getmAddress()), bikeStation);
    }
}