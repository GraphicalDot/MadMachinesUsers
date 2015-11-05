package com.sports.unity.messages.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.activity.CreateGroup;
import com.sports.unity.messages.controller.model.GroupMessaging;
import com.sports.unity.util.CommonUtil;

import java.util.ArrayList;

/**
 * Created by amandeep on 30/10/15.
 */
public class GroupDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);

        setToolBar();
        initView(view);

        return view;
    }

    private void setToolBar(){
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ((ImageView)toolbar.findViewById(R.id.backImage)).setImageResource(R.drawable.ic_close_blk);
        toolbar.findViewById(R.id.backImage).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        TextView title = (TextView) toolbar.findViewById(R.id.title);
        title.setText( R.string.group_title_create);

        TextView actionView = (TextView) toolbar.findViewById(R.id.actionButton);
        actionView.setText( R.string.next);
        actionView.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoCondensedBold());
        actionView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                moveOn(view);
            }
        });

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initView(View view){

    }

    private void moveOn(View view){
        EditText groupNameEditText = (EditText)getView().findViewById(R.id.groupName);

        String groupName = groupNameEditText.getText().toString().trim();

        if( groupName.isEmpty() ) {
            Toast.makeText(getActivity(), R.string.group_message_provide_group_name, Toast.LENGTH_SHORT).show();
        } else {
            CreateGroup createGroupActivity = ((CreateGroup) getActivity());
            createGroupActivity.setGroupDetails(groupName, "");

            createGroupActivity.moveToMembersListFragment();
        }
    }

}
