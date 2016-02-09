package com.sports.unity.common.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.controller.AdvancedFilterActivity;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.news.controller.activity.NewsSearchActivity;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mad on 12/29/2015.
 */
public class FilterRecycleAdapter extends RecyclerView.Adapter<FilterRecycleAdapter.FilterItemView> {
    private ArrayList<FavouriteItem> itemDataSet;
    private List<String> favDataSet;
    public Activity activity;
    private boolean isEditMode;
    public FilterRecycleAdapter(Activity activity, ArrayList<FavouriteItem> itemDataSet,boolean isEdit) {
        isEditMode=isEdit;
        this.itemDataSet = itemDataSet;
        this.activity = activity;
    }
    public FilterRecycleAdapter(Activity activity, List<String> favDataSet) {
        this.favDataSet = favDataSet;
        this.activity = activity;
    }

    @Override
    public FilterItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, parent, false);
        FilterItemView vh = new FilterItemView(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FilterItemView holder,int position) {
        initView(holder,position);
    }


    @Override
    public int getItemCount() {
        if(!UserUtil.isFilterCompleted()||isEditMode) {
            return itemDataSet.size();
        }else{
            return favDataSet.size();
        }
    }


    private void initView(final FilterItemView holder, final int position) {

        if(!UserUtil.isFilterCompleted()||isEditMode) {
            final FavouriteItem favouriteItem=itemDataSet.get(position);
            String s=favouriteItem.getName();
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
                    favouriteItem.setChecked(!favouriteItem.isChecked());
                    itemDataSet.add(position, favouriteItem);
                    notifyDataSetChanged();
                }
            });
        }else{
           final String s=favDataSet.get(position);
            holder.cb.setVisibility(View.INVISIBLE);
            holder.tv.setText(s);
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchNews(s);
                }
            });
        }
    }

    private void searchNews(String s) {

        Intent newsSearch=new Intent(activity,NewsSearchActivity.class);
        newsSearch.putExtra(Constants.FILTER_SEARCH_EXTRA,s);
        activity.startActivity(newsSearch);
    }

    public class FilterItemView extends RecyclerView.ViewHolder {
        public TextView tv;
        public CheckBox cb;
        public View v;
        public FilterItemView(View itemView) {
            super(itemView);
            v=itemView;
            v.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
            tv = (TextView) itemView.findViewById(R.id.tv);
            tv.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
            cb = (CheckBox) itemView.findViewById(R.id.cb);
        }
    }
    public ArrayList<FavouriteItem> getItemDataSet(){
        return itemDataSet;
    }
    public void setItemDataSet(ArrayList<FavouriteItem> itemDataSet,boolean isEditMode){
        this.itemDataSet=itemDataSet;
        this.isEditMode=isEditMode;
        this.notifyDataSetChanged();
    }
    public void setFavDataSet(List<String> itemDataSet,boolean isEditMode){
        this.favDataSet=itemDataSet;
        this.isEditMode=isEditMode;
        this.notifyDataSetChanged();

    }

}
