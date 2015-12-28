package com.amlopezc.bikesmanager.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.amlopezc.bikesmanager.R;
import com.amlopezc.bikesmanager.entity.BikeStation;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    //intent and bundle
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
                // Returning to the maps activity with chosen coordinates
                Bundle bundle = new Bundle();
                bundle.putDouble(BUNDLE_LAT, actual.getLatitude());
                bundle.putDouble(BUNDLE_LONG, actual.getLongitude());

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

    private void setChildData(View convertView, BikeStation actual) { //TODO: meter el dateStamp de la última reserva
        TextView tv_ListChild;

        tv_ListChild = (TextView) convertView.findViewById(R.id.textView_totalNumber);
        tv_ListChild.setText(Integer.toString(actual.getTotalBikes()));
        tv_ListChild = (TextView) convertView.findViewById(R.id.textView_availableNumber);
        tv_ListChild.setText(Integer.toString(actual.getAvailableBikes()));
        //Color for available bikes number
        if(actual.getAvailableBikes() == 0)
            tv_ListChild.setTextColor(Color.RED);
        else if (actual.getTotalBikes() - actual.getAvailableBikes() > actual.getAvailableBikes())
            tv_ListChild.setTextColor(Color.rgb(255, 128, 0)); //Orange
        else
            tv_ListChild.setTextColor(Color.rgb(0, 102, 0)); // Dark green
        tv_ListChild = (TextView) convertView.findViewById(R.id.textView_reservedNumber);
        tv_ListChild.setText(Integer.toString(actual.getReservedBikes()));
        tv_ListChild = (TextView) convertView.findViewById(R.id.textView_brokenNumber);
        tv_ListChild.setText(Integer.toString(actual.getBrokenBikes()));
        tv_ListChild = (TextView) convertView.findViewById(R.id.textView_coordinates);
        tv_ListChild.setText(String.format("%f, %f", actual.getLatitude(), actual.getLongitude()));
    }
}
