package com.amlopezc.bikesmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.amlopezc.bikesmanager.util.ExpandableListAdapter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends AppCompatActivity implements AsyncTaskListener<String> {

    private List<String> mListDataHeader;
    private HashMap<String, BikeStation> mListDataChild;
    private ExpandableListView mExpandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expListView_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        HttpDispatcher dispatcher = new HttpDispatcher(this);
        dispatcher.doGet(this);
    }

    @Override
    public void processResult(String result) {
        try {
            ObjectMapper mapper = setObjectMapper();
            List<BikeStation> bikeStationList = mapper.readValue(result, new TypeReference<List<BikeStation>>() {});
            readData(bikeStationList);
            updateLocalLayout();
        } catch (Exception e){
            Log.e("JSON", e.getLocalizedMessage(), e);
            Toast.makeText(this, "Error al sincronizar con el servidor", Toast.LENGTH_SHORT).show();
        }
    }

    private ObjectMapper setObjectMapper() {
        return  new ObjectMapper()
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

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
    }

    private void updateLocalLayout() {
        updateExpandableList();
    }

    private void updateExpandableList() {
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);
        mExpandableListView.setAdapter(expandableListAdapter);
    }



}
