package com.sports.unity.messages.controller.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.Contacts;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mad on 2/22/2016.
 */
public class GroupInfoActivity extends CustomAppCompatActivity {

    private ListView participantsList;

    private long chatID;
    private String groupJID;
    private String name;
    private byte[] byteArray;

    private SportsUnityDBHelper.GroupParticipants groupParticipants = null;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info_activity);

        getIntentExtras();

        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);
        groupParticipants = sportsUnityDBHelper.getGroupParticipants(chatID);
        String currentUserJid = TinyDB.getInstance(this).getString(TinyDB.KEY_USER_JID);
        isAdmin = groupParticipants.adminJids.contains(currentUserJid);

        initToolbar();
        initViews();
    }

    private void getIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        byteArray = bundle.getByteArray("profilePicture");
        groupJID = bundle.getString("groupServerId");
        chatID = bundle.getLong("chatID");
    }

    private void initViews() {
        CircleImageView groupImage = (CircleImageView) findViewById(R.id.group_image);
        TextView groupName = (TextView) findViewById(R.id.group_name);
        TextView groupInfo = (TextView) findViewById(R.id.group_info);
        TextView groupCount = (TextView) findViewById(R.id.part_count);
        TextView delete = (TextView) findViewById(R.id.delete_group);

        groupName.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());
        groupInfo.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        delete.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        groupName.setText(name);
        if (byteArray != null) {
            groupImage.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        } else {
            groupImage.setImageResource(R.drawable.ic_group);
        }

        String partCount = getResources().getString(R.string.participant_count);
        partCount = String.format(partCount, groupParticipants.usersInGroup.size());
        groupCount.setText(partCount);

        participantsList = (ListView) findViewById(R.id.participants_list);
        participantsList.setAdapter(new GroupParticipantsAdapter(this, groupParticipants.usersInGroup, isAdmin));
        setListViewHeightBasedOnItems(participantsList);
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_group_info);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbarEdit = (TextView) toolbar.findViewById(R.id.toolbar_edit);
        toolbarEdit.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        toolbarEdit.setVisibility( isAdmin ? View.VISIBLE : View.GONE );
    }

    private void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {

            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
