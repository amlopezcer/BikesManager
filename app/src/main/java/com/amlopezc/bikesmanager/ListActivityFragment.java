package com.amlopezc.bikesmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

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
public class ListActivityFragment extends Fragment implements AsyncTaskListener<String>, View.OnClickListener {

    private List<String> mListDataHeader;
    private HashMap<String, BikeStation> mListDataChild;
    private ExpandableListView mExpandableListView;
    private AutoCompleteTextView mTextViewSearch;


    public ListActivityFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Listen to the Action Bar events
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        initComponentsUI(view);

        return view;
    }

    private void initComponentsUI(View view) {
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.expListView_list);
        ImageButton imageButtonSearch = (ImageButton) view.findViewById(R.id.imgButton_search);
        imageButtonSearch.setOnClickListener(this);
        mTextViewSearch = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView_stations);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Getting update data form the server
        getStationsUpdatedServerData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DeviceUtilities.hideSoftKeyboard(getActivity()); //Hides the keyboard

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
        HttpDispatcher httpDispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_STATION);
        httpDispatcher.doGet(this, HttpConstants.GET_FIND_ALL);
    }

    @Override
    public void processServerResult(String result, int operation) {
        //Process the server response
        switch (operation) {
            //Update data and layout
            case HttpConstants.OPERATION_GET:
                try {
                    HttpDispatcher httpDispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_STATION);
                    ObjectMapper mapper = httpDispatcher.getMapper();
                    List<BikeStation> bikeStationList = mapper.readValue(result, new TypeReference<List<BikeStation>>() {});
                    readData(bikeStationList);
                    updateLocalLayout();
                } catch (Exception e) {
                    Log.e("[GET Result]" + getClass().getCanonicalName(), e.getLocalizedMessage(), e);
                    showBasicErrorDialog(i18n(R.string.text_sync_error), i18n(R.string.text_ok));
                }
                break;
        }
    }

    //Read server data to update current state
    private void readData(List<BikeStation> bikeStationList) {
        mListDataHeader = new ArrayList<>();
        mListDataChild = new HashMap<>();
        int i = 0;

        for(BikeStation bikeStation : bikeStationList) {
            mListDataHeader.add(bikeStation.getStationHeader());
            mListDataChild.put(mListDataHeader.get(i), bikeStation);
            i++;
        }

        //Set the autoCompleteTextView to allow searches over the list
        setSearchAutoCompleteTextView();
    }

    private void setSearchAutoCompleteTextView() {
        // Create the adapter from my listDataHeader and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mListDataHeader);
        mTextViewSearch.setAdapter(adapter);
    }

    //Update layout (expandable list)
    private void updateLocalLayout() {
        updateExpandableList();
    }

    private void updateExpandableList() {
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);
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

        DeviceUtilities.hideSoftKeyboard(getActivity()); //Hide the keyboard

        mTextViewSearch.getText().clear();

        int groupPosition = mListDataHeader.indexOf(id);
        if (groupPosition > 0) { //Something has been found
            collapseAll();
            mExpandableListView.setSelectedGroup(groupPosition);
            mExpandableListView.expandGroup(groupPosition); //Expand the group, now the list has 1 more element
            mExpandableListView.smoothScrollToPosition(groupPosition + 1); //Navigate to the new element
        } else
            showBasicErrorDialog(i18n(R.string.toast_id_not_found,id), i18n(R.string.text_ok));
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

    // Show a basic error dialog with a custom message
    private void showBasicErrorDialog(String message, String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    // Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }
}
