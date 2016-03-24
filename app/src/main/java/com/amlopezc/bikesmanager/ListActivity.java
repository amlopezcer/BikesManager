package com.amlopezc.bikesmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.amlopezc.bikesmanager.util.ExpandableListAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Shows bike station data as a expandable list
 */

public class ListActivity extends AppCompatActivity implements AsyncTaskListener<String> {

    private List<String> mListDataHeader;
    private HashMap<String, BikeStation> mListDataChild;
    private ExpandableListView mExpandableListView;
    private HttpDispatcher mHttpDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expListView_list);
        mHttpDispatcher = new HttpDispatcher(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Getting update data form the server
        fetchUpdatedServerData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Action bar item click handler
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            fetchUpdatedServerData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchUpdatedServerData() {
        mHttpDispatcher.doGet(this);
    }

    @Override
    public void processResult(String result, int operation) {
        //Process the server response
        switch (operation) {
            //Update data and layout
            case HttpDispatcher.OPERATION_GET:
                try {
                    ObjectMapper mapper = mHttpDispatcher.getMapper();
                    List<BikeStation> bikeStationList = mapper.readValue(result, new TypeReference<List<BikeStation>>() {});
                    readData(bikeStationList);
                    updateLocalLayout();
                } catch (Exception e) {
                    Log.e("[GET Result]" + getClass().getCanonicalName(), e.getLocalizedMessage(), e);
                    Toast.makeText(this,
                            i18n(R.string.toast_sync_error),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //Read server data to update current state
    private void readData(List<BikeStation> bikeStationList) {
        String headerTemplate = "%d - %s";
        mListDataHeader = new ArrayList<>();
        mListDataChild = new HashMap<>();
        int i = 0;

        for(BikeStation bikeStation : bikeStationList) {
            mListDataHeader.add(String.format(headerTemplate, bikeStation.getmId(), bikeStation.getmAddress()));
            mListDataChild.put(mListDataHeader.get(i), bikeStation);
            i++;
        }

        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_stations);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListDataHeader);
        textView.setAdapter(adapter);
        textView.clearFocus();
    }

    //Update layout (expandable list)
    private void updateLocalLayout() {
        updateExpandableList();
    }

    private void updateExpandableList() {
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);
        mExpandableListView.setAdapter(expandableListAdapter);

        mExpandableListView.requestFocus();


        //mExpandableListView.expandGroup(mListDataHeader.indexOf("73 - Plaza de los Astros"));

    }

    // Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

}
