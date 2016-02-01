package com.sports.unity.common.controller.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.controller.AdvancedFilterActivity;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.controller.NavListAdapter;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.util.Constants;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mad on 1/4/2016.
 */
public class NavigationFragment extends Fragment implements ExpandableListView.OnChildClickListener, View.OnClickListener {

    public boolean isMannual;
    ArrayList<String> teamGroupItems = new ArrayList<String>();
    private ArrayList<Object> teamChildItems = new ArrayList<Object>();

    ArrayList<String> competeGroupItems = new ArrayList<String>();
    private ArrayList<Object> competeChildItems = new ArrayList<Object>();

    ExpandableListView teamList, competeList;

    NavListAdapter teamAdapter, compAdapter;

    TextView editTeam, editComp;

    ImageView teamIndi, compIndi;
    boolean isTeam, isComp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initItemList();
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
    }

    private void setUpExpandableLists(View view) {
        teamIndi = (ImageView) view.findViewById(R.id.favindi);
        compIndi = (ImageView) view.findViewById(R.id.compindi);
        teamIndi.setOnClickListener(this);
        compIndi.setOnClickListener(this);
       // editTeam = (TextView) view.findViewById(R.id.edit_team);
        //editComp = (TextView) view.findViewById(R.id.edit_comp);
        //editTeam.setOnClickListener(this);
        //editComp.setOnClickListener(this);
        teamList = (ExpandableListView) view.findViewById(R.id.fav_team);
        competeList = (ExpandableListView) view.findViewById(R.id.complist);

        teamAdapter = new NavListAdapter(getActivity(), teamGroupItems, teamChildItems, teamIndi);
        compAdapter = new NavListAdapter(getActivity(), competeGroupItems, competeChildItems, compIndi);

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
        setUpClickListeners();
    }

    private void setUpClickListeners() {
        teamList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                competeList.collapseGroup(0);
             setListViewHeight(parent,groupPosition);
                return false;
            }
        });
        competeList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                teamList.collapseGroup(0);
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
                Toast.makeText(getActivity(), ((ArrayList<String>) teamChildItems.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                break;
            case R.id.complist:
                Toast.makeText(getActivity(), ((ArrayList<String>) competeChildItems.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                break;
        }


        return false;
    }

    private void initItemList() {
        ArrayList<String> savedList = UserUtil.getFavouriteFilters();
        teamGroupItems.add("Favourite Team");
        competeGroupItems.add("Competitions");
        List<String> teamChild = new ArrayList<>();
        List<String> compChild = new ArrayList<String>();
        for (String name : savedList) {
            if (name.contains(Constants.NAV_COMP)) {
                name = name.replace(Constants.NAV_COMP, "");
                compChild.add(name);
            } else if (name.contains(Constants.NAV_TEAM)) {
                name = name.replace(Constants.NAV_TEAM, "");
                teamChild.add(name);
            }
        }
        Collections.sort(teamChild);
        Collections.sort(compChild);
        teamChildItems.add(teamChild);
        competeChildItems.add(compChild);
    }

    private void updateTeamChild() {
        ArrayList<String> savedList = UserUtil.getFavouriteFilters();
        List<String> teamChild = new ArrayList<>();
        for (String name : savedList) {
            if (name.contains(Constants.NAV_TEAM)) {
                name = name.replace(Constants.NAV_TEAM, "");
                teamChild.add(name);
            }
        }
        Collections.sort(teamChild);
        teamChildItems = new ArrayList<Object>();
        teamChildItems.add(teamChild);
    }

    private void updateCompChild() {
        ArrayList<String> savedList = UserUtil.getFavouriteFilters();
        List<String> teamChild = new ArrayList<>();
        List<String> compChild = new ArrayList<String>();
        for (String name : savedList) {
            if (name.contains(Constants.NAV_COMP)) {
                name = name.replace(Constants.NAV_COMP, "");
                compChild.add(name);
            }
        }
        Collections.sort(compChild);
        competeChildItems = new ArrayList<Object>();
        competeChildItems.add(compChild);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.favindi:
                isMannual=true;
                competeList.collapseGroup(0);
                if (teamList.isGroupExpanded(0)) {
                    teamList.collapseGroup(0);
                } else {
                    setListViewHeight(teamList,0);
                    teamList.expandGroup(0);
                }
                break;
            case R.id.compindi:
                isMannual=true;
                teamList.collapseGroup(0);
                if (competeList.isGroupExpanded(0)) {
                    competeList.collapseGroup(0);
                } else {
                    setListViewHeight(competeList,0);
                    competeList.expandGroup(0);
                }
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

   // @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("max","REQU-"+requestCode);
//        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_NAV) {
//            if (isComp) {
//                updateCompChild();
//                isComp = false;
//                compAdapter.updateChildList(competeChildItems);
//                competeList.collapseGroup(0);
//                setListViewHeight(competeList, 0);
//                competeList.expandGroup(0);
//            } else if (isTeam) {
//                updateTeamChild();
//                isTeam = false;
//                teamAdapter.updateChildList(teamChildItems);
//                teamList.collapseGroup(0);
//                setListViewHeight(teamList,0);
//                teamList.expandGroup(0);
//            }
//        }
//    }
}
