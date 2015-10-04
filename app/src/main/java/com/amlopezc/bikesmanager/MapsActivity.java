package com.amlopezc.bikesmanager;


import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapsActivity extends AppCompatActivity {

    //Constants for the list and chart intents
    public final static String EXTRA_STATIONS = "STATIONS";
    public final static String EXTRA_DATA = "DATA";
    public final static int CODE_LIST = 1;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ArrayList<BikeStation> mStations = new ArrayList<>(); //temp, adding stations.
    // Create a LatLngBounds that includes Madrid.
    public final LatLngBounds MADRID = new LatLngBounds(new LatLng(40.38, -3.72),
            new LatLng(40.48, -3.67));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initData(); //temp, just filling an ArrayList with BikeStations
        setUpMapIfNeeded();
    }

    private void initData() {
        mStations.add(new BikeStation(40.45, -3.68, 1, "Av. Alberto Alcocer, 162", 10, 6, 0, 3));
        mStations.add(new BikeStation(40.45, -3.69, 2, "Av. General Perón, 38", 10, 10, 0, 0));
        mStations.add(new BikeStation(40.42, -3.68, 3, "Calle de Alcalá, 75", 10, 1, 0, 5));
        mStations.add(new BikeStation(40.41, -3.70, 4, "Puerta del Sol", 10, 5, 0, 0));
        mStations.add(new BikeStation(40.41, -3.71, 5, "Plaza de la Cebada, 10", 10, 4, 0, 0));
        mStations.add(new BikeStation(40.42, -3.71, 6, "Calle Bailén, 9", 10, 7, 2, 0));
        mStations.add(new BikeStation(40.42, -3.70, 7, "Calle Gran Vía, 46", 10, 6, 1, 0));
        mStations.add(new BikeStation(40.40, -3.69, 8, "Estación de Atocha", 10, 0, 0, 0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_list:
                intent = new Intent(this, ListActivity.class);
                intent.putParcelableArrayListExtra(EXTRA_STATIONS, mStations);
                startActivityForResult(intent, CODE_LIST);
                return true;
            case R.id.action_chart:

                int total = 0;
                int available = 0;
                int broken = 0;
                int reserved = 0;
                for(BikeStation bikeStation : mStations){
                    total += bikeStation.getTotalBikes();
                    available += bikeStation.getAvailableBikes();
                    broken += bikeStation.getBrokenBikes();
                    reserved += bikeStation.getReservedBikes();
                 }
                int occupied = total - available - broken - reserved;

                ArrayList<Integer> data = new ArrayList<>();
                data.add(total);
                data.add(available);
                data.add(broken);
                data.add(reserved);
                data.add(occupied);

                intent = new Intent(this, ChartActivity.class);
                intent.putIntegerArrayListExtra(EXTRA_DATA, data);
                startActivity(intent);
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

    private void addMarkers() {

        BitmapDescriptor color;

        for(BikeStation bikeStation : mStations) {

            if(bikeStation.getAvailableBikes() == 0)
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            else if (bikeStation.getAvailableBikes() < 5)
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
            else
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

            mMap.addMarker(new MarkerOptions().
                    position(new LatLng(bikeStation.getLatitude(), bikeStation.getLongitude())).
                    title(bikeStation.getAddress()).
                    snippet(bikeStation.getAvailabilityMessage()).
                    icon(color));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Receiving coordinates from the list to position the camera
        if(requestCode == CODE_LIST && resultCode == ExpandableListAdapter.CODE_OK) {
            Bundle bundle = data.getBundleExtra(ExpandableListAdapter.EXTRA_RESULT);

            Double latCoord = bundle.getDouble(ExpandableListAdapter.BUNDLE_LAT);
            Double longCoord = bundle.getDouble(ExpandableListAdapter.BUNDLE_LONG);

            LatLng marker= new LatLng(latCoord, longCoord );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
        }
    }
}





