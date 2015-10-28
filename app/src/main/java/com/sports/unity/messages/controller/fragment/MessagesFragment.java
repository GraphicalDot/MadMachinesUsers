package com.sports.unity.messages.controller.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.messages.controller.activity.CreateGroup;

/**
 * Created by Agupta on 8/13/2015.
 */
public class MessagesFragment extends Fragment implements View.OnClickListener {
    FrameLayout frame;
    Button contacts;
    Button chats;
    Button others;
    LinearLayout buttonContainerLayout;
    TextView createGrp;
    TextView joinGrp;
    TextView pplAroundMe;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(com.sports.unity.R.layout.messages, container, false);
        frame = (FrameLayout) v.findViewById(com.sports.unity.R.id.childFragmentContainer);
        contacts = (Button) v.findViewById(R.id.btn_contacts);
        contacts.setOnClickListener(this);
        contacts.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Regular.ttf"));
        chats = (Button) v.findViewById(R.id.btn_chat);
        chats.setOnClickListener(this);
        chats.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Regular.ttf"));
        others = (Button) v.findViewById(R.id.btn_others);
        others.setOnClickListener(this);
        others.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Regular.ttf"));
        buttonContainerLayout = (LinearLayout) v.findViewById(com.sports.unity.R.id.fragmentChangeButtonLayout);

        createGrp = (TextView) v.findViewById(R.id.create_group_txt);
        createGrp.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Regular.ttf"));
        createGrp.setOnClickListener(this);

        joinGrp = (TextView) v.findViewById(R.id.join_group_txt);
        joinGrp.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Regular.ttf"));

        pplAroundMe = (TextView) v.findViewById(R.id.ppl_around_me_txt);
        pplAroundMe.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Regular.ttf"));

        getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, new ChatFragment()).commit();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.i("Adding Child Fragment", "Now");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chat:
                getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, new ChatFragment()).commit();
                buttonContainerLayout.setBackgroundResource(R.drawable.btn_chat_focused);
                chats.setTextColor(Color.parseColor("#FFFFFF"));
                contacts.setTextColor(Color.parseColor("#2C84CC"));
                others.setTextColor(Color.parseColor("#2C84CC"));
                break;
            case R.id.btn_contacts:
                getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, new ContactsFragment()).commit();
                buttonContainerLayout.setBackgroundResource(R.drawable.btn_contacts_focused);
                contacts.setTextColor(Color.parseColor("#FFFFFF"));
                chats.setTextColor(Color.parseColor("#2C84CC"));
                others.setTextColor(Color.parseColor("#2C84CC"));
                break;
            case R.id.btn_others:
                getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, new OthersFragment()).commit();
                buttonContainerLayout.setBackgroundResource(R.drawable.btn_others_focused);
                others.setTextColor(Color.parseColor("#FFFFFF"));
                contacts.setTextColor(Color.parseColor("#2C84CC"));
                chats.setTextColor(Color.parseColor("#2C84CC"));
                break;
            case R.id.create_group_txt:
                Intent intent = new Intent(getActivity(), CreateGroup.class);
                startActivity(intent);
                Log.i("createGroupActivity : " , "shown");
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_messages_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
