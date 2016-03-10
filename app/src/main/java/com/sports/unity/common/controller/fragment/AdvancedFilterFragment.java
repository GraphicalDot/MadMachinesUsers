package com.sports.unity.common.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sports.unity.R;
import com.sports.unity.common.controller.AdvancedFilterActivity;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.common.view.SlidingTabStrip;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by Mad on 12/28/2015.
 */
public class AdvancedFilterFragment extends Fragment {

    private Bundle bundle;
    private String SPORTS_TYPE;
    private final String SHOWCASE_ID = "filter_showcase1";
    private final String[] heading = {"Select your favourite leagues", "Select your favourite teams", "Select your favourite players"};
    private final String[] message = {"Add a star to your favourite leagues for easy use.", "Add a star to your favourite team for easy use.", "Add a star to your favourite players to get an update."};

    public AdvancedFilterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        SPORTS_TYPE = bundle.getString(Constants.SPORTS_TYPE);
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

        FilterPagerAdapter filterPagerAdapter = new FilterPagerAdapter(getActivity().getSupportFragmentManager(), SPORTS_TYPE);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(filterPagerAdapter);
        ((AdvancedFilterActivity)getActivity()).setUpViewPager(pager);

        SlidingTabLayout tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.ColorPrimary);
            }
        });
        tabs.setViewPager(pager);
        startShowcase(tabs.getTabStrip());

    }

    private void startShowcase(SlidingTabStrip strip) {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view
        final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);
        sequence.addSequenceItem(((AdvancedFilterActivity) getActivity()).search, getActivity().getResources().getString(R.string.showcase_heading), getActivity().getResources().getString(R.string.showcase_message), getActivity().getResources().getString(R.string.got_it));
        for (int i = 0; i < strip.getChildCount(); i++) {
            if (strip.getChildCount() < 3) {
                sequence.addSequenceItemWithRectShape(strip.getChildAt(i), heading[i + 1], message[i + 1], getActivity().getResources().getString(R.string.got_it));
            } else {
                sequence.addSequenceItemWithRectShape(strip.getChildAt(i), heading[i], message[i], getActivity().getResources().getString(R.string.got_it));
            }
        }
        sequence.start();

    }

}
