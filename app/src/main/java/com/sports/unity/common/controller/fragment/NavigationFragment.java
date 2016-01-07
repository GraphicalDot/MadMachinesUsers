package com.sports.unity.common.controller.fragment;

import android.app.Activity;
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


    private static XMPPTCPConnection con;
    private SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(getActivity());

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

        con = XMPPClient.getConnection();

        return inflater.inflate(R.layout.fragment_nav, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setNavigationProfile(view);
        setUpExpandableLists(view);
    }

    public void setNavigationProfile(View view) {

        LinearLayout navHeader = (LinearLayout) view.findViewById(R.id.nav_header);

        CircleImageView profile_photo = (CircleImageView) navHeader.findViewById(R.id.circleView);
        TextView name = (TextView) navHeader.findViewById(R.id.name);

        String user_name = TinyDB.getInstance(getActivity()).getString(TinyDB.KEY_PROFILE_NAME);
        String user_details = TinyDB.getInstance(getActivity()).getString(TinyDB.KEY_USERNAME);

        Contacts contact = sportsUnityDBHelper.getContact(user_details);


        if (contact.image != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(contact.image, 0, contact.image.length);
            profile_photo.setImageBitmap(bmp);
        } else {
            profile_photo.setImageResource(R.drawable.ic_user);
        }


        name.setText(user_name);

    }

    private void setUpExpandableLists(View view) {
        teamIndi = (ImageView) view.findViewById(R.id.favindi);
        compIndi = (ImageView) view.findViewById(R.id.compindi);
        teamIndi.setOnClickListener(this);
        compIndi.setOnClickListener(this);
        editTeam = (TextView) view.findViewById(R.id.edit_team);
        editComp = (TextView) view.findViewById(R.id.edit_comp);
        editTeam.setOnClickListener(this);
        editComp.setOnClickListener(this);
        teamList = (ExpandableListView) view.findViewById(R.id.fav_team);
        competeList = (ExpandableListView) view.findViewById(R.id.complist);

        teamAdapter = new NavListAdapter(getActivity(), teamGroupItems, teamChildItems, editTeam, teamIndi);
        compAdapter = new NavListAdapter(getActivity(), competeGroupItems, competeChildItems, editComp, compIndi);

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
                return false;
            }
        });

        competeList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                teamList.collapseGroup(0);
                return false;
            }
        });
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
                competeList.collapseGroup(0);
                if (teamList.isGroupExpanded(0)) {
                    teamList.collapseGroup(0);
                } else {
                    teamList.expandGroup(0);
                }
                break;
            case R.id.compindi:
                teamList.collapseGroup(0);
                if (competeList.isGroupExpanded(0)) {
                    competeList.collapseGroup(0);
                } else {
                    competeList.expandGroup(0);
                }
                break;
            case R.id.edit_team:
                isTeam = true;
                ((MainActivity) getActivity()).isPaused = true;
                Intent advancedFilterTeam = new Intent(getActivity(), AdvancedFilterActivity.class);
                advancedFilterTeam.putExtra(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_TEAM);
                advancedFilterTeam.putExtra(Constants.IS_FROM_NAV, isTeam);
                getActivity().startActivityForResult(advancedFilterTeam, Constants.REQUEST_CODE_NAV);
                break;
            case R.id.edit_comp:
                isComp = true;
                ((MainActivity) getActivity()).isPaused = true;
                Intent advancedFilterLeague = new Intent(getActivity(), AdvancedFilterActivity.class);
                advancedFilterLeague.putExtra(Constants.SPORTS_FILTER_TYPE, Constants.FILTER_TYPE_LEAGUE);
                advancedFilterLeague.putExtra(Constants.IS_FROM_NAV, isComp);
                getActivity().startActivityForResult(advancedFilterLeague, Constants.REQUEST_CODE_NAV);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("max","REQU-"+requestCode);
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_NAV) {
            if (isComp) {
                updateCompChild();
                isComp = false;
                compAdapter.updateChildList(competeChildItems);
            } else if (isTeam) {
                updateTeamChild();
                isTeam = false;
                teamAdapter.updateChildList(teamChildItems);
            }
        }
    }
}
