package com.amlopezc.bikesmanager;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.util.ExpandableListAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.cocosw.bottomsheet.BottomSheet;


public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener {

    //Constants for intents
    public final static String EXTRA_STATIONS = "STATIONS";
    public final static String EXTRA_DATA = "DATA";

    private final int SETTINGS_REQUEST_CODE = 0;
    private final int LIST_REQUEST_CODE = 1;


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private HashMap<String, BikeStation> mStations = new HashMap<>();

    // Create a LatLngBounds that includes Madrid.
    public final LatLngBounds MADRID = new LatLngBounds(new LatLng(40.38, -3.72),
            new LatLng(40.48, -3.67));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initData(); //temp
        setUpMapIfNeeded();
        mMap.setOnMarkerClickListener(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    private void initData() { //TODO: Eliminar al coger del servidor
        String key;

        key = "Av. Alberto Alcocer, 162";
        mStations.put(key, new BikeStation(40.45, -3.68, 1, key, 10, 6, 0, 3));
        key = "Av. General Perón, 38";
        mStations.put(key, new BikeStation(40.45, -3.69, 2, key, 10, 10, 0, 0));
        key = "Calle de Alcalá, 75";
        mStations.put(key, new BikeStation(40.42, -3.68, 3, key, 10, 1, 0, 5));
        key = "Puerta del Sol";
        mStations.put(key, new BikeStation(40.41, -3.70, 4, key, 10, 5, 0, 0));
        key = "Plaza de la Cebada, 10";
        mStations.put(key, new BikeStation(40.41, -3.71, 5, key, 10, 4, 0, 0));
        key = "Calle Bailén, 9";
        mStations.put(key, new BikeStation(40.42, -3.71, 6, key, 10, 7, 2, 0));
        key = "Calle Gran Vía, 46";
        mStations.put(key, new BikeStation(40.42, -3.70, 7, key, 10, 6, 1, 0));
        key = "Estación de Atocha";
        mStations.put(key, new BikeStation(40.40, -3.69, 8, key, 10, 0, 0, 0));
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent;

        switch(item.getItemId()){
            case R.id.action_settings:
                intent = new Intent();
                intent.setClass(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                return true;
            case R.id.action_list:
                intent = new Intent(this, ListActivity.class);
                intent.putParcelableArrayListExtra(EXTRA_STATIONS, prepareListIntentData());
                startActivityForResult(intent, LIST_REQUEST_CODE);
                return true;
            case R.id.action_chart:
                intent = new Intent(this, ChartActivity.class);
                intent.putIntegerArrayListExtra(EXTRA_DATA, prepareChartIntentData());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<BikeStation> prepareListIntentData() {
        ArrayList<BikeStation> stations = new ArrayList<>();
        for (Map.Entry<String, BikeStation> entry : mStations.entrySet())
            stations.add(entry.getValue());
        return stations;
    }

    private ArrayList<Integer> prepareChartIntentData() {
        int total = 0;
        int available = 0;
        int broken = 0;
        int reserved = 0;

        for (Map.Entry<String, BikeStation> entry : mStations.entrySet()) {
            total += entry.getValue().getmTotalBikes();
            available += entry.getValue().getmAvailableBikes();
            broken += entry.getValue().getmBrokenBikes();
            reserved += entry.getValue().getmReservedBikes();
        }

        int occupied = total - available - broken - reserved;

        ArrayList<Integer> data = new ArrayList<>();
        data.add(total);
        data.add(available);
        data.add(broken);
        data.add(reserved);
        data.add(occupied);

        return data;
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Add stations
        addMarkers(); // TODO: Sacar los puestos del servidor
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Set the camera to the greatest possible zoom level that includes the bounds
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MADRID.getCenter(), 12));
    }

    private void addMarkers() { //TODO: Hay un error al volver, no guarda los cambios, cogiendolo del servidor se debería arreglar.
        for (Map.Entry<String, BikeStation> entry : mStations.entrySet())
            mMap.addMarker(new MarkerOptions().
                    position(new LatLng(entry.getValue().getmLatitude(),
                            entry.getValue().getmLongitude())).
                    title(entry.getKey()).
                    snippet(entry.getValue().getAvailabilityMessage()).
                    icon(getAvailabilityColor(entry.getValue())));
    }

    private BitmapDescriptor getAvailabilityColor(BikeStation bikeStation) {
        if(bikeStation.getmAvailableBikes() == 0)
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        else if (bikeStation.getmAvailableBikes() < 5)
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
        final BikeStation bikeStation = mStations.get(marker.getTitle());

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
                                takeBike(bikeStation, marker);
                                break;
                            case R.id.menu_leaveBike:
                                leaveBike(bikeStation, marker);
                                break;
                            case R.id.menu_reportBike:
                                Toast.makeText(getApplicationContext(), "report", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).show();

        return false;
    }

    private void takeBike(BikeStation bikeStation, Marker marker) {
        if(bikeStation.getmAvailableBikes() > 0) {
            bikeStation.setmAvailableBikes(bikeStation.getmAvailableBikes() - 1);

            marker.setSnippet(bikeStation.getAvailabilityMessage());
            marker.setIcon(getAvailabilityColor(bikeStation));

            Toast.makeText(getApplicationContext(),
                    String.format("Bike taken from '%s' station", bikeStation.getmAddress()),
                    Toast.LENGTH_LONG).
                    show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "No available bikes",
                    Toast.LENGTH_SHORT).
                    show();
        }
    }

    private void leaveBike(BikeStation bikeStation, Marker marker) {
        if(bikeStation.getmAvailableBikes() != bikeStation.getmTotalBikes()) { //TODO: De momento sólo cuento disponibles, a ver qué pasa con las rotas y las reservas
            bikeStation.setmAvailableBikes(bikeStation.getmAvailableBikes() + 1);

            marker.setSnippet(bikeStation.getAvailabilityMessage());
            marker.setIcon(getAvailabilityColor(bikeStation));

            Toast.makeText(getApplicationContext(),
                    String.format("Bike leaved in '%s' station", bikeStation.getmAddress()),
                    Toast.LENGTH_LONG).
                    show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Station full",
                    Toast.LENGTH_SHORT).
                    show();
        }
    }

}