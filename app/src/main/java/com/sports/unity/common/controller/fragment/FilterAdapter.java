package com.sports.unity.common.controller.fragment;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.controller.FilterActivity;
import com.sports.unity.common.controller.PlayerProfileDetails;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Mad on 2/8/2016.
 */
public class FilterAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private ArrayList<FavouriteItem> mData = new ArrayList<FavouriteItem>();
    private Context context;
    private LayoutInflater mInflater;
    private PlayerProfileDetails playerProfileDetails;

    public FilterAdapter(Context context, ArrayList<FavouriteItem> item) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = item;
        this.context = context;
        if(context instanceof PlayerProfileDetails){
            playerProfileDetails = (PlayerProfileDetails) context;
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.filter_list_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.itemtext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String playerName = mData.get(position).getName();
        final String playerId = mData.get(position).getId();
        final String sportsType = mData.get(position).getSportsType();
        holder.textView.setText(playerName);
        holder.textView.setTypeface(FontTypeface.getInstance(context).getRobotoRegular());
        holder.textView.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerProfileDetails != null){
                    playerProfileDetails.playerProfile(playerName,playerId,sportsType);
                }
            }
        });
        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        String headerText = "";
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.filter_header, null);
            holder.textView = (TextView) convertView.findViewById(R.id.itemtext);
            holder.imageView = (ImageView) convertView.findViewById(R.id.flag);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        if (mData.get(position).getSportsType().equals(Constants.GAME_KEY_CRICKET)) {
            headerText = "Cricket";
            holder.imageView.setImageResource(R.drawable.ic_cricket);
        } else {
            headerText = "Football";
            holder.imageView.setImageResource(R.drawable.ic_football);
        }

        holder.textView.setText(headerText);
        holder.textView.setTypeface(FontTypeface.getInstance(context).getRobotoSlabBold());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        if (mData.get(position).getSportsType().equals(Constants.GAME_KEY_CRICKET)) {
            return (long) 0.0;
        } else {
            return (long) 1.0;
        }
    }

    public static class ViewHolder {
        public TextView textView;
    }

    public static class HeaderViewHolder {
        public TextView textView;
        public ImageView imageView;
    }

}
