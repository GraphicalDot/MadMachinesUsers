package com.sports.unity.common.controller;

import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends CustomAppCompatActivity {

    private String userOrGroupName;
    private byte[] userOrGroupImage = null;
    private String groupServerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initView();
        setToolbar();
        setInitData();
    }

    private void getIntentExtras() {
        userOrGroupName = getIntent().getStringExtra("name");
        userOrGroupImage = getIntent().getByteArrayExtra("profilePicture");
        groupServerId = getIntent().getStringExtra("groupServerId");

    }

    private void setToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        ImageView back = (ImageView) toolbar.findViewById(R.id.backarrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        CircleImageView profilePicture = (CircleImageView) findViewById(R.id.user_picture);
        TextView name = (TextView) findViewById(R.id.name);

        getIntentExtras();

        name.setText(userOrGroupName);

        if (userOrGroupImage == null) {
            if (groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
                profilePicture.setImageResource(R.drawable.ic_user);
            } else {
                profilePicture.setImageResource(R.drawable.ic_group);
            }
        } else {
            profilePicture.setImageBitmap(BitmapFactory.decodeByteArray(userOrGroupImage, 0, userOrGroupImage.length));
        }
    }

    private void setInitData() {


        ListView teamListView = (ListView) findViewById(R.id.team_list);
        ListView leagueListView = (ListView) findViewById(R.id.league_list);
        ListView playerListView = (ListView) findViewById(R.id.player_list);

        ArrayList<String> savedList = UserUtil.getFavouriteFilters();

        List<String> teams = new ArrayList<>();
        List<String> leagues = new ArrayList<String>();
        List<String> players = new ArrayList<>();

        for (String name : savedList) {
            if (name.contains(Constants.NAV_COMP)) {
                name = name.replace(Constants.NAV_COMP, "");
                leagues.add(name);
            } else if (name.contains(Constants.NAV_TEAM)) {
                name = name.replace(Constants.NAV_TEAM, "");
                teams.add(name);
            } else if (name.contains(Constants.NAV_PLAYER)) {
                name = name.replace(Constants.NAV_PLAYER, "");
                players.add(name);
            }
        }

        Collections.sort(teams);
        Collections.sort(leagues);
        Collections.sort(players);

        ArrayAdapter<String> teamsListAdapter = new ArrayAdapter<String>(this, R.layout.textview_user_profile_activity, teams);
        teamListView.setAdapter(teamsListAdapter);

        ArrayAdapter<String> leaguesListAdapter = new ArrayAdapter<String>(this, R.layout.textview_user_profile_activity, leagues);
        leagueListView.setAdapter(leaguesListAdapter);

        ArrayAdapter<String> playerListAdapter = new ArrayAdapter<String>(this, R.layout.textview_user_profile_activity, players);
        playerListView.setAdapter(playerListAdapter);

        setListViewHeightBasedOnChildren(teamListView);
        setListViewHeightBasedOnChildren(leagueListView);
        setListViewHeightBasedOnChildren(playerListView);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

//            // Get total height of all item dividers.
//            int totalDividersHeight = 2 * (numberOfItems + 7);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight ;  //+ totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

        } else {
            // nothing
        }
    }
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
