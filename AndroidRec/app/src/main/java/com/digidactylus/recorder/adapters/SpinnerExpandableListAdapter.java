package com.digidactylus.recorder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.digidactylus.recorder.R;

import java.util.HashMap;
import java.util.List;

public class SpinnerExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> groupTitles;
    private HashMap<String, List<String>> itemMap;

    public SpinnerExpandableListAdapter(Context context, List<String> groupTitles, HashMap<String, List<String>> itemMap) {
        this.context = context;
        this.groupTitles = groupTitles;
        this.itemMap = itemMap;
    }

    @Override
    public int getGroupCount() {
        return groupTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemMap.get(groupTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemMap.get(groupTitles.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }
        TextView title = convertView.findViewById(R.id.list_group_title);
        title.setText(groupTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String itemTitle = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        TextView title = convertView.findViewById(R.id.list_item_title);
        title.setText(itemTitle);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
