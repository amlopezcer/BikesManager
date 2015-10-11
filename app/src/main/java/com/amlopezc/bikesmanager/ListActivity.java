package com.amlopezc.bikesmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.amlopezc.bikesmanager.entity.BikeStation;
import com.amlopezc.bikesmanager.util.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private List<String> mListDataHeader;
    private HashMap<String, BikeStation> mListDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ExpandableListAdapter mExpandableListAdapter;
        ExpandableListView mExpandableListView;

        mExpandableListView = (ExpandableListView) findViewById(R.id.expListView_list);
        prepareListData();
        mExpandableListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);
        mExpandableListView.setAdapter(mExpandableListAdapter);
    }

    private void prepareListData() {
        mListDataHeader = new ArrayList<>();
        mListDataChild = new HashMap<>();

        Intent intent = getIntent();
        ArrayList<BikeStation> bikeStationList = intent.getParcelableArrayListExtra(
                MapsActivity.EXTRA_STATIONS);

        BikeStation bikeStation;

        for(int i = 0; i < bikeStationList.size(); i++) {
            bikeStation = bikeStationList.get(i);
            mListDataHeader.add(bikeStation.getAddress());
            mListDataChild.put(mListDataHeader.get(i), bikeStation);
        }
    }


}
