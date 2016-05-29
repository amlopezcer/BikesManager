package com.amlopezc.bikesmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.amlopezc.bikesmanager.util.DeviceUtilities;
import com.amlopezc.bikesmanager.util.ExpandableListAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Shows bike station data as a expandable list
 */

public class ListActivity extends AppCompatActivity implements AsyncTaskListener<String>, View.OnClickListener {

    private List<String> mListDataHeader;
    private HashMap<String, BikeStation> mListDataChild;
    private ExpandableListView mExpandableListView;
    private AutoCompleteTextView mTextViewSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expListView_list);
        ImageButton imageButtonSearch = (ImageButton) findViewById(R.id.imgButton_search);
        imageButtonSearch.setOnClickListener(this);
        mTextViewSearch = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_stations);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Getting update data form the server
        getStationsUpdatedServerData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        DeviceUtilities.hideSoftKeyboard(this); //Hides the keyboard

        // Action bar item click handler
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getStationsUpdatedServerData();
                return true;
            case R.id.action_collapse: //Collapse ExpandableList
                collapseAll();
                return true;
            case R.id.action_expand: //Expand ExpandableList
                expandAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getStationsUpdatedServerData() {
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_STATION);
        httpDispatcher.doGet(this, HttpConstants.GET_FIND_ALL);
    }

    @Override
    public void processServerResult(String result, int operation) {
        //Process the server response
        switch (operation) {
            //Update data and layout
            case HttpConstants.OPERATION_GET:
                try {
                    HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_STATION);
                    ObjectMapper mapper = httpDispatcher.getMapper();
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

        //Set the autoCompleteTextView to allow searches over the list
        setSearchAutoCompleteTextView();
    }

    private void setSearchAutoCompleteTextView() {
        // Create the adapter from my listDataHeader and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListDataHeader);
        mTextViewSearch.setAdapter(adapter);
    }

    //Update layout (expandable list)
    private void updateLocalLayout() {
        updateExpandableList();
    }

    private void updateExpandableList() {
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);
        mExpandableListView.setAdapter(expandableListAdapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.imgButton_search:
                searchStation();
                break;
        }
    }

    private void searchStation() {
        String id = mTextViewSearch.getText().toString().trim();
        if(id.isEmpty())
            return; //Nothing to search

        DeviceUtilities.hideSoftKeyboard(this); //Hide the keyboard

        mTextViewSearch.getText().clear();

        int groupPosition = mListDataHeader.indexOf(id);
        if (groupPosition > 0) { //Something has been found
            collapseAll();
            mExpandableListView.setSelectedGroup(groupPosition);
            mExpandableListView.expandGroup(groupPosition); //Expand the group, now the list has 1 more element
            mExpandableListView.smoothScrollToPosition(groupPosition + 1); //Navigate to the new element
        } else {
            Toast.makeText(this,
                    i18n(R.string.toast_id_not_found,id),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void collapseAll() {
        for(int i = 0; i < mExpandableListView.getExpandableListAdapter().getGroupCount(); i++) {
            mExpandableListView.collapseGroup(i);
        }
    }

    private void expandAll() {
        for(int i = 0; i < mExpandableListView.getExpandableListAdapter().getGroupCount(); i++) {
            mExpandableListView.expandGroup(i);
        }
    }

    // Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

}
