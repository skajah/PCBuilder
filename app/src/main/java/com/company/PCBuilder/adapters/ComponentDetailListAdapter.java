package com.company.PCBuilder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.company.PCBuilder.PCComponent;
import com.company.PCBuilder.R;

import java.util.HashMap;
import java.util.List;

public class ComponentDetailListAdapter extends BaseExpandableListAdapter {
    private List<String> headerItems;
    private HashMap<String, List<String>> childItems;
    private Context context;

    public ComponentDetailListAdapter(Context context, List<String> headerItems, HashMap<String, List<String>> childItems){
        this.context = context;
        this.headerItems = headerItems;
        this.childItems = childItems;
    }

    @Override
    public int getGroupCount() {
        return headerItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childItems.get(headerItems.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headerItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childItems.get(headerItems.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String header = (String) getGroup(groupPosition);
        View row;
        if (convertView == null)
            row = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.header_layout, null);
        else
            row = convertView;
        ((TextView) row.findViewById(R.id.header_text)).setText(header);
        return row;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String detail = (String) getChild(groupPosition, childPosition);
        View row;
        if (convertView == null)
            row = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.detail_layout, null);
        else
            row = convertView;
        ((TextView) row.findViewById(R.id.detail_text)).setText(detail);
        return row;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}



