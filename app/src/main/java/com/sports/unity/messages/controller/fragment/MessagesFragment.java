package com.sports.unity.messages.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.activity.PeopleAroundMeMap;
import com.sports.unity.messages.controller.viewhelper.OnSearchViewQueryListener;
import com.sports.unity.util.Constants;

/**
 * Created by Agupta on 8/13/2015.
 */
public class MessagesFragment extends Fragment implements View.OnClickListener {

    private OnSearchViewQueryListener mListener = null;

    private static final String CURRENT_FRAGMENT = "current_fragment";

    FrameLayout frame;
    Button contacts;
    Button chats;
    Button others;
    LinearLayout buttonContainerLayout;
    Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(com.sports.unity.R.layout.messages, container, false);
        frame = (FrameLayout) v.findViewById(com.sports.unity.R.id.childFragmentContainer);
        contacts = (Button) v.findViewById(R.id.btn_contacts);
        contacts.setOnClickListener(this);
        contacts.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoCondensedRegular());
        chats = (Button) v.findViewById(R.id.btn_chat);
        chats.setOnClickListener(this);
        chats.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoCondensedRegular());
        others = (Button) v.findViewById(R.id.btn_others);
        others.setOnClickListener(this);
        others.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoCondensedRegular());
        buttonContainerLayout = (LinearLayout) v.findViewById(com.sports.unity.R.id.fragmentChangeButtonLayout);


        FloatingActionButton peopleAroundMeFab = (FloatingActionButton) v.findViewById(R.id.floatingbutton);
        peopleAroundMeFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.app_theme_blue)));
        peopleAroundMeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PeopleAroundMeMap.class);
                startActivity(intent);
            }
        });

//        ImageButton createGroup = (ImageButton) v.findViewById(R.id.create_group);
//        createGroup.setOnClickListener(this);
//        ImageButton joinGroup = (ImageButton) v.findViewById(R.id.join_group);
//        joinGroup.setOnClickListener(this);
//        ImageButton peopleAroundMe = (ImageButton) v.findViewById(R.id.people_around_me);
//        peopleAroundMe.setOnClickListener(this);
//
//        TextView createGrp = (TextView) v.findViewById(R.id.create_group_txt);
//        createGrp.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoCondensedRegular());
//        createGrp.setOnClickListener(this);
//
//        TextView joinGrp = (TextView) v.findViewById(R.id.join_group_txt);
//        joinGrp.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoCondensedRegular());
//
//        TextView pplAroundMe = (TextView) v.findViewById(R.id.ppl_around_me_txt);
//        pplAroundMe.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoCondensedRegular());

        ChatFragment fragment = new ChatFragment();
        mListener = fragment;
        getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, fragment).commit();
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
            case R.id.btn_chat: {
                ChatFragment fragment = new ChatFragment();
                mListener = fragment;
                getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, fragment).commit();
                buttonContainerLayout.setBackgroundResource(R.drawable.btn_chat_focused);
                chats.setTextColor(getResources().getColor(R.color.ColorPrimary));
                contacts.setTextColor(getResources().getColor(R.color.app_theme_blue));
                others.setTextColor(getResources().getColor(R.color.app_theme_blue));
                break;
            }
            case R.id.btn_contacts: {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.INTENT_KEY_CONTACT_FRAGMENT_USAGE, ContactsFragment.USAGE_FOR_CONTACTS);

                ContactsFragment fragment = new ContactsFragment();
                mListener = fragment;
                fragment.setArguments(bundle);

                getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, fragment).commit();
                buttonContainerLayout.setBackgroundResource(R.drawable.btn_contacts_focused);
                contacts.setTextColor(getResources().getColor(R.color.ColorPrimary));
                chats.setTextColor(getResources().getColor(R.color.app_theme_blue));
                others.setTextColor(getResources().getColor(R.color.app_theme_blue));
                break;
            }
            case R.id.btn_others: {
                OthersFragment fragment = new OthersFragment();
                mListener = fragment;

                getChildFragmentManager().beginTransaction().replace(com.sports.unity.R.id.childFragmentContainer, fragment).commit();
                buttonContainerLayout.setBackgroundResource(R.drawable.btn_others_focused);
                others.setTextColor(getResources().getColor(R.color.ColorPrimary));
                contacts.setTextColor(getResources().getColor(R.color.app_theme_blue));
                chats.setTextColor(getResources().getColor(R.color.app_theme_blue));
                break;
            }
//            case R.id.create_group:
//                Intent intent = new Intent(getActivity(), CreateGroup.class);
//                startActivity(intent);
//                Log.i("createGroupActivity : ", "shown");
//                break;
//            case R.id.join_group:
//                //TODO
//                Toast.makeText(getActivity(), "Not implemented yet", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.people_around_me:
//                //TODO
//                Toast.makeText(getActivity(), "Not implemented yet", Toast.LENGTH_SHORT).show();
//                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_messages_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_menu_search);
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mListener != null) {
                    mListener.onSearchQuery(newText);
                }
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        mListener = null;
    }
}
