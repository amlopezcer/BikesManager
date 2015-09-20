package com.amlopezc.bikesmanager.util;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amlopezc.bikesmanager.R;
import com.amlopezc.bikesmanager.entity.BikeStation;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity mContext;
    private List<String> mListDataHeader; //Groups (Bike stations)
    private HashMap<String, List<BikeStation>> mListDataChild; //Bike stations details

    public ExpandableListAdapter(Activity mContext, List<String> mListDataHeader, HashMap<String,
            List<BikeStation>> mListDataChild) {
        this.mContext = mContext;
        this.mListDataHeader = mListDataHeader;
        this.mListDataChild = mListDataChild;
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).get(childPosition);
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
        return false;
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

        final BikeStation actual = (BikeStation) this.getChild(groupPosition, childPosition);
        final TextView tv_ListChild = (TextView) convertView.findViewById(R.id.textView_itemList);
        tv_ListChild.setText(actual.getmDescription());

        Button btn = (Button) convertView.findViewById(R.id.button_listItem);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, tv_ListChild.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
