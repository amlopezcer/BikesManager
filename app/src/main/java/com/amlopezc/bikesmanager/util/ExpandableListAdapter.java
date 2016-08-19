package com.amlopezc.bikesmanager.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amlopezc.bikesmanager.R;
import com.amlopezc.bikesmanager.entity.BikeStation;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Support code to show and manage an expandable list
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    //Intents and bundles constants
    public static final String EXTRA_RESULT = "COORDINATES";
    public static final int OK_RESULT_CODE = 1;
    public static final String BUNDLE_LAT = "LAT";
    public static final String BUNDLE_LONG = "LONG";


    private Activity mContext;
    private List<String> mListDataHeader; //Groups (Bike stations)
    private HashMap<String, BikeStation> mChildData; //Bike stations details


    public ExpandableListAdapter(Activity mContext, List<String> mListDataHeader, HashMap<String,
            BikeStation> mChildData) {
        this.mContext = mContext;
        this.mListDataHeader = mListDataHeader;
        this.mChildData = mChildData;
    }

    @Override
    public int getGroupCount() {
        return mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildData.get(mListDataHeader.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null)
            convertView = mContext.getLayoutInflater().inflate(R.layout.listview_group, parent,
                    false);

        TextView tv_ListHeader = (TextView) convertView.findViewById(R.id.textView_listHeader);
        tv_ListHeader.setText(getGroup(groupPosition).toString());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mContext.getLayoutInflater().inflate(R.layout.listview_item, parent,
                    false);

        final BikeStation actual = (BikeStation) getChild(groupPosition, childPosition);
        setChildData(convertView, actual);
        Button btn = (Button) convertView.findViewById(R.id.button_showMap);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Return to the maps activity with the chosen coordinates
                Bundle bundle = new Bundle();
                bundle.putDouble(BUNDLE_LAT, actual.getmLatitude());
                bundle.putDouble(BUNDLE_LONG, actual.getmLongitude());

                Intent returnIntent = new Intent();
                returnIntent.putExtra(EXTRA_RESULT, bundle);
                mContext.setResult(OK_RESULT_CODE, returnIntent);

                mContext.finish();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void setChildData(View convertView, BikeStation bikeStation) {
        TextView tv_listChild;

        tv_listChild = (TextView) convertView.findViewById(R.id.textView_totalNumber);
        tv_listChild.setText(String.format(Locale.getDefault(), "%d", bikeStation.getmTotalMoorings()));

        tv_listChild = (TextView) convertView.findViewById(R.id.textView_availableNumber);
        tv_listChild.setText(String.format(Locale.getDefault(), "%d", bikeStation.getmAvailableBikes()));
        //Color for available bikes number
        setAvailabilityColor(bikeStation, tv_listChild);

        tv_listChild = (TextView) convertView.findViewById(R.id.textView_reservedNumber);
        tv_listChild.setText(String.format(Locale.getDefault(), "%d", bikeStation.getmReservedBikes()));

        tv_listChild = (TextView) convertView.findViewById(R.id.textView_reservedMooringsNumber);
        tv_listChild.setText(String.format(Locale.getDefault(), "%d", bikeStation.getmReservedMoorings()));

        tv_listChild = (TextView) convertView.findViewById(R.id.textView_fareNumber);
        tv_listChild.setText(String.format(Locale.getDefault(), "%.2f€", bikeStation.getCurrentFare()));
        setAvailabilityColor(bikeStation, tv_listChild);

        tv_listChild = (TextView) convertView.findViewById(R.id.textView_lastModData);
        tv_listChild.setText(String.format("%s", setTimeStampFormat(bikeStation.getmChangeTimestamp())));
    }

    private void setAvailabilityColor(BikeStation bikeStation, TextView textView) {
        int availability = bikeStation.getStationAvailability();

        if(availability == 0)
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        else if (availability < 50)
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.orange));
        else
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.green));
    }

    private String setTimeStampFormat(String string) {
        String year = string.substring(0, 4);
        String month = string.substring(5, 7);
        String day = string.substring(8, 10);

        String hour = string.substring(11,13);
        String minutes = string.substring(14,16);
        String seconds = string.substring(17,19);

        return String.format("%s:%s:%s | %s-%s-%s",
                hour, minutes, seconds,
                day, month, year);
    }
}
