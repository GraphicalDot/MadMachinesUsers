package com.sports.unity.common.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.R;
import com.sports.unity.common.controller.AdvancedFilterActivity;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.User;
import com.sports.unity.news.controller.activity.NewsSearchActivity;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mad on 12/29/2015.
 */
public class FilterRecycleAdapter extends RecyclerView.Adapter<FilterRecycleAdapter.FilterItemView> {
    private ArrayList<FavouriteItem> itemDataSet;
    private List<String> favDataSet;
    public Activity activity;

    public FilterRecycleAdapter(Activity activity, ArrayList<FavouriteItem> itemDataSet) {
        this.itemDataSet = itemDataSet;
        this.activity = activity;
    }

    @Override
    public FilterItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, parent, false);
        FilterItemView vh = new FilterItemView(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FilterItemView holder, int position) {
        initView(holder, position);
    }


    @Override
    public int getItemCount() {
        return itemDataSet.size();
    }


    private void initView(final FilterItemView holder, final int position) {
        final FavouriteItem favouriteItem = itemDataSet.get(position);
        String s = favouriteItem.getName();
        holder.tv.setText(s);
        if (favouriteItem.isChecked()) {
            if (!((AdvancedFilterActivity) activity).favList.contains(favouriteItem)) {
                ((AdvancedFilterActivity) activity).favList.add(favouriteItem);
            }
            holder.cb.setChecked(favouriteItem.isChecked());
            holder.tv.setTextColor(activity.getResources().getColor(R.color.app_theme_blue));
        } else {
            holder.cb.setChecked(favouriteItem.isChecked());
            holder.tv.setTextColor(activity.getResources().getColor(R.color.gray1));
            if (((AdvancedFilterActivity) activity).favList.contains(favouriteItem)) {
                ((AdvancedFilterActivity) activity).favList.remove(favouriteItem);
            }
        }
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemDataSet.remove(position);
                if (!favouriteItem.isChecked()) {
                    //FIREBASE INTEGRATION
                    {
                        FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(activity);
                        Bundle bundle = new Bundle();
                        String name = favouriteItem.getName();
                        bundle.putString(FirebaseUtil.Param.NAME, FirebaseUtil.trimValue(name));
                        bundle.putString(FirebaseUtil.Param.ID, favouriteItem.getId());
                        bundle.putString(FirebaseUtil.Param.SPORTS_TYPE, favouriteItem.getSportsType());
                        bundle.putString(FirebaseUtil.Param.FILTER_TYPE, favouriteItem.getFilterType());
                        if (!UserUtil.isFilterCompleted()) {
                            bundle.putBoolean(FirebaseUtil.Param.PROFILE_CREATION, true);
                        } else {
                            bundle.putBoolean(FirebaseUtil.Param.PROFILE_CREATION, false);
                        }
                        FirebaseUtil.logEvent(firebaseAnalytics, bundle, FirebaseUtil.Event.FAV_SELECTION);
                    }
                }
                favouriteItem.setChecked(!favouriteItem.isChecked());
                itemDataSet.add(position, favouriteItem);
                notifyDataSetChanged();
            }
        });
    }

    public class FilterItemView extends RecyclerView.ViewHolder {
        public TextView tv;
        public CheckBox cb;
        public View v;

        public FilterItemView(View itemView) {
            super(itemView);
            v = itemView;
            v.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
            tv = (TextView) itemView.findViewById(R.id.tv);
            tv.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
            cb = (CheckBox) itemView.findViewById(R.id.cb);
        }
    }

    public ArrayList<FavouriteItem> getItemDataSet() {
        return itemDataSet;
    }

    public void setItemDataSet(ArrayList<FavouriteItem> itemDataSet) {
        this.itemDataSet = itemDataSet;
        this.notifyDataSetChanged();
    }

}
