package com.sports.unity.common.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Mad on 12/22/2015.
 */
public class NavListAdapter extends BaseExpandableListAdapter {
    ArrayList<String> groupItems = new ArrayList<String>();
    private ArrayList<Object> childItems = new ArrayList<Object>();
    LayoutInflater inflater;
    View editTeam;
    ImageView indiIm;
    Activity act;

    public NavListAdapter(Activity context, ArrayList<String> groupList, ArrayList<Object> childItems, TextView tv, ImageView indiIm) {
        this.groupItems = groupList;
        this.childItems = childItems;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.editTeam = tv;
        act = context;
        this.indiIm = indiIm;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ArrayList<String> child = (ArrayList<String>) childItems.get(groupPosition);

        TextView textView = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.nav_list_item, null);
        }

        textView = (TextView) convertView.findViewById(R.id.itemtext);
        textView.setText(child.get(childPosition));

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        TextView textView = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.nav_list_header, null);
        }

        textView = (TextView) convertView.findViewById(R.id.itemheader);
        if(isExpanded){
            textView.setTextColor(act.getResources().getColor(R.color.gray1));
        }else{
            textView.setTextColor(act.getResources().getColor(R.color.app_theme_blue));
        }
        textView.setText(groupItems.get(groupPosition));
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((ArrayList<String>) childItems.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ArrayList<String>) childItems.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupItems.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groupItems.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        indiIm.setBackgroundResource(R.drawable.arrow_down);
        editTeam.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        indiIm.setBackgroundResource(R.drawable.ic_arrow_up);
        editTeam.setVisibility(View.VISIBLE);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void updateChildList(ArrayList<Object> childItem){
        this.childItems=childItem;
        this.notifyDataSetChanged();
    }
}
