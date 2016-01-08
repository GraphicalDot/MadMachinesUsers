package com.sports.unity.common.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sports.unity.R;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by madmachines on 3/9/15.
 */
public class SportsGridViewAdapter extends BaseAdapter {

    private Context mContext;

    /*For future use: to add all the sports
    in sports selection screen*/
    /*private Integer[] mThumbIds = {
            R.drawable.btn_basketball_disabled,
            R.drawable.btn_cricket_disabled,
            R.drawable.btn_football_disabled,
            R.drawable.btn_tennis_disabled,
            R.drawable.btn_f1_disabled,
    };*/
    private Integer[] mThumbIds = {
            R.drawable.btn_cricket_disabled,
            R.drawable.btn_football_disabled
    };

    private String[] mSports = {
            Constants.GAME_KEY_CRICKET,
            Constants.GAME_KEY_FOOTBALL
    };
    private Integer[] mThumbIdsSelected = {
            R.drawable.btn_cricket_selected,
            R.drawable.btn_football_selected
    };
    private ArrayList<String> sports = new ArrayList<String>();
    public SportsGridViewAdapter(Context c) {
        mContext = c;
       sports= UserUtil.getSportsSelected();
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
           // imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        if(sports.contains(mSports[position])){
            imageView.setImageResource(mThumbIdsSelected[position]);
        }else {
            imageView.setImageResource(mThumbIds[position]);
        }
        return imageView;
    }


}
