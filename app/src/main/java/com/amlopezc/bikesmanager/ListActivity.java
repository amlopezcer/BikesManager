package com.amlopezc.bikesmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.util.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ExpandableListAdapter mExpandableListAdapter;
    private ExpandableListView mExpandableListView;
    private List<String> mListDataHeader;
    private HashMap<String, List<BikeStation>> mListDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expListView_list);
        prepareListData();
        mExpandableListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);
        mExpandableListView.setAdapter(mExpandableListAdapter);
    }

    private void prepareListData() {
        mListDataHeader = new ArrayList<>();
        mListDataChild = new HashMap<>();

        Intent intent = getIntent();
        ArrayList<BikeStation> list = intent.getParcelableArrayListExtra(MapsActivity.
                EXTRA_STATIONS);

        //Adding header data
        mListDataHeader.add("Available bikes");
        mListDataHeader.add("All stations");

        //Adding child data
        ArrayList<BikeStation> availableStations = new ArrayList<>();
        ArrayList<BikeStation> allStations = new ArrayList<>();

        for(BikeStation bikeStation : list) {
            if(bikeStation.getmAvailableBikes() > 0)
                availableStations.add(bikeStation);
            allStations.add(bikeStation);
        }

        mListDataChild.put(mListDataHeader.get(0), availableStations);
        mListDataChild.put(mListDataHeader.get(1), allStations);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
}
