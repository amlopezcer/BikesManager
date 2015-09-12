package com.amlopezc.bikesmanager;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

public class MapsActivity extends AppCompatActivity {

    // Create a LatLngBounds that includes Madrid.
    public static final LatLngBounds MADRID = new LatLngBounds(
            new LatLng(40.38, -3.72), new LatLng(40.48, -3.67));

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ArrayList<BikeStation> mStations; //temp, adding stations.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        BikeStation bikeStation;

        mStations = new ArrayList<>();
        mStations.add(new BikeStation(40.45, -3.68, 1, "Av. Alberto Alcocer, 162", 10, 6, 0, 0));
        mStations.add(new BikeStation(40.45, -3.69, 2, "Av. General Perón, 38", 10, 10, 0, 0));
        mStations.add(new BikeStation(40.42, -3.68, 3, "Calle de Alcalá, 75", 10, 1, 0, 0));
        mStations.add(new BikeStation(40.41, -3.70, 4, "Puerta del Sol", 10, 5, 0, 0));
        mStations.add(new BikeStation(40.41, -3.71, 5, "Plaza de la Cebada, 10", 10, 4, 0, 0));
        mStations.add(new BikeStation(40.42, -3.71, 6, "Calle Bailén, 9", 10, 7, 0, 0));
        mStations.add(new BikeStation(40.42, -3.70, 7, "Calle Gran Vía, 46", 10, 6, 0, 0));
        mStations.add(new BikeStation(40.40, -3.69, 8, "Estación de Atocha", 10, 0, 0, 0));

        Iterator iterator = mStations.iterator();
        BitmapDescriptor color;

        while(iterator.hasNext()) {
            bikeStation = (BikeStation)iterator.next();

            if(bikeStation.getmAvailableBikes() == 0)
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            else if (bikeStation.getmAvailableBikes() < 5)
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
            else
                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

            mMap.addMarker(new MarkerOptions().
                    position(new LatLng(bikeStation.getmLatitude(), bikeStation.getmLongitude())).
                    title(bikeStation.getmDescription()).
                    snippet(bikeStation.getAvailabilityMessage()).
                    icon(color));
        }

    }
}





