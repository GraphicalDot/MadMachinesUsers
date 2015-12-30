package com.sports.unity.common.controller;

import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.view.SlidingTabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends CustomAppCompatActivity {

    private String userOrGroupName;
    private byte[] userOrGroupImage =null;
    private String groupServerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initView();
        setToolbar();
        setTab();

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

    private  void initView() {
        CircleImageView profilePicture = (CircleImageView) findViewById(R.id.user_picture);
        TextView name = (TextView) findViewById(R.id.name);
        TextView member = (TextView) findViewById(R.id.member);
        TextView admin = (TextView) findViewById(R.id.admin);

        getIntentExtras();

        name.setText(userOrGroupName);

        if (userOrGroupImage == null) {
            if(groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
                profilePicture.setImageResource(R.drawable.ic_user);
            } else {
                profilePicture.setImageResource(R.drawable.ic_group);
            }
        } else {
            profilePicture.setImageBitmap(BitmapFactory.decodeByteArray(userOrGroupImage, 0, userOrGroupImage.length));
        }
    }

    private  void setTab() {

        String titles[] = {"Groups", "Interests"};

        int numberOfTabs = titles.length;

        // Creating The ViewPagerAdapterInMainActivity and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        ViewPagerAdapterForProfile adapter = new ViewPagerAdapterForProfile(getSupportFragmentManager(), titles, numberOfTabs);

        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) findViewById(com.sports.unity.R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(com.sports.unity.R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setTabTextColor( R.color.profile_tab_selector);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.app_theme_blue);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
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
