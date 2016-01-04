package com.sports.unity.common.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sports.unity.R;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by Mad on 12/28/2015.
 */
public class AdvancedFilterFragment extends Fragment {

    private Bundle bundle;
    private ArrayList<String> sportsSelected;
    private String SPORTS_FILTER_TYPE;
    public AdvancedFilterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        SPORTS_FILTER_TYPE =bundle.getString(Constants.SPORTS_FILTER_TYPE);
        sportsSelected= UserUtil.getSportsSelected();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.advanced_filter_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTab(view);
    }

    private void setTab(View view) {
        FilterPagerAdapter filterPagerAdapter=new FilterPagerAdapter(getActivity().getSupportFragmentManager(),sportsSelected,SPORTS_FILTER_TYPE);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(filterPagerAdapter);
        SlidingTabLayout tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setTabTextColor(R.color.profile_tab_selector);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.app_theme_blue);
            }
        });
        tabs.setViewPager(pager);
    }

}
