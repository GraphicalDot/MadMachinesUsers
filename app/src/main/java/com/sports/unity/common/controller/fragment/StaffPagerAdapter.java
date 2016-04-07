package com.sports.unity.common.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.controller.TeamLeagueDetails;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Mad on 06-Apr-16.
 */
public class StaffPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<FavouriteItem> data;
    private LayoutInflater inflater;
    private RadioGroup radioGroup;
    private FrameLayout staffView;
    private boolean isDeleted = false;

    public StaffPagerAdapter(Context context, ArrayList<FavouriteItem> data, LayoutInflater inflater, RadioGroup radioGroup, FrameLayout staffView) {
        this.context = context;
        this.data = data;
        this.inflater = inflater;
        this.radioGroup = radioGroup;
        this.staffView = staffView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final FrameLayout scoreView = (FrameLayout) inflater.inflate(R.layout.score_staff_item, null);
        ImageView flag = (ImageView) scoreView.findViewById(R.id.flag);
        ImageView closeBtn = (ImageView) scoreView.findViewById(R.id.close);
        closeBtn.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, false));
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinyDB.getInstance(context).putBoolean(data.get(position).getId(), true);
                if (data.size() > 1) {
                    radioGroup.removeView(radioGroup.findViewById(data.size() - 1));
                    data.remove(position);
                    isDeleted = true;
                    // destroyItem(container, position, scoreView);
                    StaffPagerAdapter.this.notifyDataSetChanged();
                    notifyDataSetChanged();
                    staffView.invalidate();
                    if (position == 0) {
                        radioGroup.check(0);
                    } else if (position == data.size()) {
                        radioGroup.check(position - 1);
                    } else {
                        radioGroup.check(position);
                    }

                    radioGroup.requestLayout();
                    radioGroup.invalidate();
                } else {
                    staffView.setVisibility(View.GONE);
                }
            }
        });
        Glide.with(context).load(Uri.parse(data.get(position).getFlagImageUrl())).placeholder(R.drawable.ic_no_img).into(flag);
        scoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TeamLeagueDetails.class);
                intent.putExtra(Constants.INTENT_TEAM_LEAGUE_DETAIL_EXTRA, data.get(position).getJsonObject().toString());
                intent.putExtra(Constants.SPORTS_TYPE_STAFF, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
        container.addView(scoreView);
        return scoreView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

}
