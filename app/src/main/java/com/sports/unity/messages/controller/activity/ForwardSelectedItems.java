package com.sports.unity.messages.controller.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.messages.controller.fragment.ContactsFragment;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

public class ForwardSelectedItems extends CustomAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward_selected_items);
        initToolbar();
        Intent intent = getIntent();

        ArrayList<Integer> selectedIdsITemList = intent.getIntegerArrayListExtra(Constants.INTENT_FORWARD_SELECTED_IDS);
        addContactFragment(selectedIdsITemList);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setContentInsetsAbsolute(0, 0);
        ImageView backArrow = (ImageView) findViewById(R.id.backarrow);
        backArrow.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE,false));
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addContactFragment(ArrayList<Integer> selectedIdsITemList) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.INTENT_KEY_CONTACT_FRAGMENT_USAGE, ContactsFragment.USAGE_FOR_FORWARD);
        bundle.putIntegerArrayList(Constants.INTENT_FORWARD_SELECTED_IDS, selectedIdsITemList);

        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment).commit();

    }

}
