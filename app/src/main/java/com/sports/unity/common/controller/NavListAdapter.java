package com.sports.unity.common.controller;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by Mad on 12/22/2015.
 */
public class NavListAdapter extends BaseExpandableListAdapter {
    ArrayList<String> groupItems = new ArrayList<String>();
    private ArrayList<FavouriteItem> childItems = new ArrayList<FavouriteItem>();
    LayoutInflater inflater;
    View editView;
    ImageView indiIm;
    Activity act;
    boolean isEditable;

    public NavListAdapter(Activity context, ArrayList<String> groupList, ArrayList<FavouriteItem> childItems, ImageView indiIm, boolean isEditable, TextView tv) {
        this.groupItems = groupList;
        this.childItems = childItems;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.editView = tv;
        act = context;
        this.indiIm = indiIm;
        this.isEditable = isEditable;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        TextView textView = null;
        ImageView iv = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.nav_list_item, null);
        }

        textView = (TextView) convertView.findViewById(R.id.itemtext);
        iv = (ImageView) convertView.findViewById(R.id.flag);
        if (childItems.size() > 0) {
            textView.setText(childItems.get(childPosition).getName());
            textView.setTextColor(act.getResources().getColor(R.color.text_color));
            String uri = null;
            try {
                uri = childItems.get(childPosition).getFlagImageUrl();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (uri != null) {
                Glide.with(act).load(Uri.parse(uri)).placeholder(R.drawable.ic_no_img).dontAnimate().into(iv);
            } else {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(R.drawable.ic_no_img);
            }
            if (childItems.get(childPosition).getName().toLowerCase().equals(Constants.GAME_KEY_CRICKET.toLowerCase())) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(R.drawable.ic_cricket_group);
            } else if (childItems.get(childPosition).getName().toLowerCase().equals(Constants.GAME_KEY_FOOTBALL.toLowerCase())) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(R.drawable.ic_football_group);
            }
        } else {
            textView.setText("No favourites added");
            textView.setTextColor(act.getResources().getColor(R.color.gray1));
            iv.setVisibility(View.GONE);
        }
        textView.setTypeface(FontTypeface.getInstance(act).getRobotoMedium());

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        TextView textView = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.nav_list_header, null);
        }

        textView = (TextView) convertView.findViewById(R.id.itemheader);
        if (isExpanded) {
            textView.setTextColor(act.getResources().getColor(R.color.app_theme_blue));
        } else {
            textView.setTextColor(act.getResources().getColor(R.color.gray1));
        }
        textView.setTypeface(FontTypeface.getInstance(act).getRobotoMedium());
//        textView.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        textView.setText(groupItems.get(groupPosition));
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (childItems.size() > 0) {
            return childItems.get(childPosition);
        } else {
            return new String("No favourites added");
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        if (childItems.size() > 0) {

            return childPosition;
        } else {
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (childItems.size() > 0) {
            return childItems.size();
        } else {

            return 1;
        }
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
        indiIm.setImageResource(R.drawable.ic_side_nav_expand);
        if (isEditable)
            editView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        indiIm.setImageResource(R.drawable.ic_side_nav_collapse);
        if (isEditable)
            editView.setVisibility(View.VISIBLE);
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

    public void updateItem(ArrayList<FavouriteItem> childItems) {
        this.childItems = childItems;
        this.notifyDataSetChanged();
    }

}
