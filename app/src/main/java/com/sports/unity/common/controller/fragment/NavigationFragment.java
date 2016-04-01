package com.sports.unity.common.controller.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.controller.About;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.controller.NavListAdapter;
import com.sports.unity.common.controller.SelectSportsActivity;
import com.sports.unity.common.controller.SettingsActivity;
import com.sports.unity.common.controller.TeamLeagueDetails;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by Mad on 1/4/2016.
 */
public class NavigationFragment extends Fragment implements ExpandableListView.OnChildClickListener, View.OnClickListener {

    public boolean isMannual;
    private ArrayList<String> teamGroupItems = new ArrayList<String>();
    private ArrayList<FavouriteItem> teamChildItems = new ArrayList<FavouriteItem>();

    private ArrayList<String> competeGroupItems = new ArrayList<String>();
    private ArrayList<FavouriteItem> competeChildItems = new ArrayList<FavouriteItem>();
    private ArrayList<String> sportsGroupItem = new ArrayList<String>();
    private ArrayList<FavouriteItem> sportsChildItem = new ArrayList<FavouriteItem>();
    private LinearLayout staffPickView;
    private TextView iplTv;
    private ExpandableListView teamList, competeList, sportsList;

    private NavListAdapter teamAdapter, compAdapter, sportsAdapter;

    TextView editTeam, editComp, editSports;

    ImageView teamIndi, compIndi, sportsIndi;
    boolean isTeam, isComp;
    FavouriteItemWrapper favouriteItemWrapper;
    private boolean isResult = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favouriteItemWrapper = FavouriteItemWrapper.getInstance(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        initItemList();
    }

    @Override
    public void onPause() {
        super.onPause();
        teamGroupItems = new ArrayList<>();
        competeGroupItems = new ArrayList<>();
        sportsGroupItem = new ArrayList<>();
        teamChildItems = new ArrayList<>();
        competeChildItems = new ArrayList<>();
        sportsChildItem = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_nav, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpExpandableLists(view);
        initTextViews(view);
    }

    TextView.OnClickListener textViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.settings) {
                ((MainActivity) getActivity()).closeDrawer();
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            } else if (v.getId() == R.id.feedback) {
                ((MainActivity) getActivity()).closeDrawer();
                getEmailIntent();
            } else if (v.getId() == R.id.rate) {
                ((MainActivity) getActivity()).closeDrawer();
                openPlayStoreListing();
            } else if (v.getId() == R.id.about) {
                ((MainActivity) getActivity()).closeDrawer();
                openAboutPage();
            }
        }
    };

    private void openAboutPage() {
        Intent about = new Intent(getActivity(), About.class);
        startActivity(about);
    }

    private void openPlayStoreListing() {
        final String appPackageName = getActivity().getPackageName();
        Log.i("packagename", appPackageName);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException activityNotFoundException) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void getEmailIntent() {
        Resources resources = getActivity().getApplicationContext().getResources();
        String body = CommonUtil.getDeviceDetails();
        Intent gmailIntent = new Intent();
        gmailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        gmailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, resources.getStringArray(R.array.receipients));
        gmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, String.format(getString(R.string.email_subject)));
        gmailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        try {

            getActivity().startActivity(gmailIntent);
        } catch (ActivityNotFoundException ex) {
            // handle error
        }
    }

    private void initTextViews(View view) {
        TextView settings = (TextView) view.findViewById(R.id.settings);
        TextView shareFeedback = (TextView) view.findViewById(R.id.feedback);
        TextView rateUs = (TextView) view.findViewById(R.id.rate);
        TextView about = (TextView) view.findViewById(R.id.about);

        settings.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        shareFeedback.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        rateUs.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        about.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));

        settings.setTypeface(FontTypeface.getInstance(getActivity().getApplicationContext()).getRobotoRegular());
        shareFeedback.setTypeface(FontTypeface.getInstance(getActivity().getApplicationContext()).getRobotoRegular());
        rateUs.setTypeface(FontTypeface.getInstance(getActivity().getApplicationContext()).getRobotoRegular());
        about.setTypeface(FontTypeface.getInstance(getActivity().getApplicationContext()).getRobotoRegular());

        settings.setOnClickListener(textViewClickListener);
        shareFeedback.setOnClickListener(textViewClickListener);
        rateUs.setOnClickListener(textViewClickListener);
        about.setOnClickListener(textViewClickListener);

        iplTv = (TextView) view.findViewById(R.id.ipl);
        staffPickView = (LinearLayout) view.findViewById(R.id.staff_pick_ll);
        staffPickView.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        staffPickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavouriteItem favouriteItem = new FavouriteItem();
                favouriteItem.setId("3");
                favouriteItem.setSportsType(Constants.SPORTS_TYPE_CRICKET);
                favouriteItem.setFilterType(Constants.FILTER_TYPE_TEAM);
                favouriteItem.setName("Indian Premier League");
                onClickListnerForTeamAndLeague(favouriteItem, true);
            }
        });
    }

    private void setUpExpandableLists(View view) {
        teamIndi = (ImageView) view.findViewById(R.id.favindi);
        compIndi = (ImageView) view.findViewById(R.id.compindi);
        sportsIndi = (ImageView) view.findViewById(R.id.sportsindi);
        teamIndi.setOnClickListener(this);
        compIndi.setOnClickListener(this);
        sportsIndi.setOnClickListener(this);
        // editTeam = (TextView) view.findViewById(R.id.edit_team);
        //editComp = (TextView) view.findViewById(R.id.edit_comp);
        //editTeam.setOnClickListener(this);
        //editComp.setOnClickListener(this);
        editSports = (TextView) view.findViewById(R.id.edit_sports);
        editSports.setOnClickListener(this);
        teamList = (ExpandableListView) view.findViewById(R.id.fav_team);
        competeList = (ExpandableListView) view.findViewById(R.id.complist);
        sportsList = (ExpandableListView) view.findViewById(R.id.fav_sports);

        teamList.setSelector(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        competeList.setSelector(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        sportsList.setSelector(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));

        teamAdapter = new NavListAdapter(getActivity(), teamGroupItems, teamChildItems, teamIndi, false, null);
        compAdapter = new NavListAdapter(getActivity(), competeGroupItems, competeChildItems, compIndi, false, null);
        sportsAdapter = new NavListAdapter(getActivity(), sportsGroupItem, sportsChildItem, sportsIndi, true, editSports);

        teamList.setAdapter(teamAdapter);
        teamList.setGroupIndicator(null);
        teamList.setClickable(true);
        teamList.setDividerHeight(2);
        teamList.setOnChildClickListener(this);

        competeList.setAdapter(compAdapter);
        competeList.setClickable(true);
        competeList.setDividerHeight(2);
        competeList.setGroupIndicator(null);
        competeList.setOnChildClickListener(this);

        sportsList.setAdapter(sportsAdapter);
        sportsList.setClickable(true);
        sportsList.setDividerHeight(2);
        sportsList.setGroupIndicator(null);
        sportsList.setOnChildClickListener(this);
        setUpClickListeners();
    }

    private void setUpClickListeners() {
        teamList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                competeList.collapseGroup(0);
                sportsList.collapseGroup(0);
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });
        competeList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                teamList.collapseGroup(0);
                sportsList.collapseGroup(0);
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });
        sportsList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                teamList.collapseGroup(0);
                competeList.collapseGroup(0);
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });
        teamList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                teamList.setLayoutParams(params);
                teamList.requestLayout();
            }
        });
        competeList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                competeList.setLayoutParams(params);
                competeList.requestLayout();
            }
        });
        sportsList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                sportsList.setLayoutParams(params);
                sportsList.requestLayout();
            }
        });
    }


    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        switch (parent.getId()) {
            case R.id.fav_team:
                if (teamChildItems.size() > 0) {
                    FavouriteItem favouriteItem = teamChildItems.get(childPosition);
                    onClickListnerForTeamAndLeague(favouriteItem, false);
                }
                break;
            case R.id.complist:
                if (competeChildItems.size() > 0) {
                    FavouriteItem favouriteItem = competeChildItems.get(childPosition);
                    onClickListnerForTeamAndLeague(favouriteItem, false);
                }
                break;
        }


        return false;
    }

    private void onClickListnerForTeamAndLeague(FavouriteItem f, boolean isStaffPicked) {
        Intent intent = new Intent(getContext(), TeamLeagueDetails.class);
        intent.putExtra(Constants.INTENT_TEAM_LEAGUE_DETAIL_EXTRA, f.getJsonObject().toString());
        intent.putExtra(Constants.SPORTS_TYPE_STAFF, isStaffPicked);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void initItemList() {
        teamGroupItems.add("Favourite Teams");
        competeGroupItems.add("Favourite Leagues");
        sportsGroupItem.add("Favourite Sports");
        teamChildItems.addAll(favouriteItemWrapper.getAllTeams());
        competeChildItems.addAll(favouriteItemWrapper.getAllLeagues());
        sportsChildItem.addAll(favouriteItemWrapper.getAllSports(getActivity()));
        if (teamAdapter != null) {
            teamAdapter.updateItem(teamChildItems);
        }
        if (compAdapter != null) {
            compAdapter.updateItem(competeChildItems);
        }
        if (sportsAdapter != null) {
            sportsAdapter.updateItem(sportsChildItem);
        }
        if (isResult) {
            isResult = false;
            sportsList.collapseGroup(0);
            setListViewHeight(sportsList, 0);
            sportsList.expandGroup(0);
            ((MainActivity) getActivity()).closeDrawer();

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sportsindi:
                isMannual = true;
                competeList.collapseGroup(0);
                teamList.collapseGroup(0);
                if (sportsList.isGroupExpanded(0)) {
                    sportsList.collapseGroup(0);
                } else {
                    setListViewHeight(sportsList, 0);
                    sportsList.expandGroup(0);
                }
                break;
            case R.id.compindi:
                isMannual = true;
                teamList.collapseGroup(0);
                sportsList.collapseGroup(0);
                if (competeList.isGroupExpanded(0)) {
                    competeList.collapseGroup(0);
                } else {
                    setListViewHeight(competeList, 0);
                    competeList.expandGroup(0);
                }
                break;
            case R.id.favindi:
                isMannual = true;
                competeList.collapseGroup(0);
                sportsList.collapseGroup(0);
                if (teamList.isGroupExpanded(0)) {
                    teamList.collapseGroup(0);
                } else {
                    setListViewHeight(teamList, 0);
                    teamList.expandGroup(0);
                }
                break;
            case R.id.edit_sports:

                Intent selectSports = new Intent(getActivity(), SelectSportsActivity.class);
                selectSports.putExtra(Constants.RESULT_REQUIRED, true);
                startActivityForResult(selectSports, Constants.REQUEST_CODE_EDIT_SPORT);
                isResult = true;
                break;
//            case R.id.edit_team:
//                isTeam = true;
//                ((MainActivity) getActivity()).isPaused = true;
//                Intent advancedFilterTeam = new Intent(getActivity(), AdvancedFilterActivity.class);
//                advancedFilterTeam.putExtra(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_TEAM);
//                advancedFilterTeam.putExtra(Constants.IS_FROM_NAV, isTeam);
//                getActivity().startActivityForResult(advancedFilterTeam, Constants.REQUEST_CODE_NAV);
//                break;
//            case R.id.edit_comp:
//                isComp = true;
//                ((MainActivity) getActivity()).isPaused = true;
//                Intent advancedFilterLeague = new Intent(getActivity(), AdvancedFilterActivity.class);
//                advancedFilterLeague.putExtra(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_LEAGUE);
//                advancedFilterLeague.putExtra(Constants.IS_FROM_NAV, isComp);
//                getActivity().startActivityForResult(advancedFilterLeague, Constants.REQUEST_CODE_NAV);
        }
    }
}
