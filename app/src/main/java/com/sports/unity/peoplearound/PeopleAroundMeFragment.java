package com.sports.unity.peoplearound;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sports.unity.R;
import com.sports.unity.messages.controller.model.User;
import com.sports.unity.peoplearound.adapters.PeopleAroundMeAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleAroundMeFragment extends Fragment {

    DataNotifier dataNotifier = new DataNotifier() {
        @Override
        public void newData(ArrayList<User> content, int responseCode) {
            if (content == null) {
                list.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                LinearLayout dataError = (LinearLayout) errorLayout.findViewById(R.id.data_error);
                LinearLayout connectionError = (LinearLayout) errorLayout.findViewById(R.id.connection_error);
                if (responseCode == 0) {
                    dataError.setVisibility(View.GONE);
                    connectionError.setVisibility(View.VISIBLE);
                } else {
                    dataError.setVisibility(View.VISIBLE);
                    connectionError.setVisibility(View.GONE);
                }
            } else {
                list.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                updateContent(content);
            }
        }
    };

    private void updateContent(ArrayList<User> content) {
        this.data = content;
        ((PeopleAroundMeAdapter) list.getAdapter()).updateData(data);
    }

    private ArrayList<User> data = new ArrayList<>();
    private ListView list;
    private FrameLayout errorLayout;
    private RelativeLayout emptyLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_people_around_me, container, false);
        ((PeopleAroundActivity) getActivity()).addListener(dataNotifier, getArguments().getString(PeopleAroundActivity.BUNDLE_TAG));
        initView(v);
        initErrorLayout(v);
        return v;
    }

    private void initErrorLayout(View v) {
        errorLayout = (FrameLayout) v.findViewById(R.id.error);
    }

    private void initView(View v) {
        String TAG = getArguments().getString(PeopleAroundActivity.BUNDLE_TAG);
        list = (ListView) v.findViewById(R.id.people_around_list);
        emptyLayout = (RelativeLayout) v.findViewById(R.id.data_exist);
        PeopleAroundMeAdapter peopleAroundMeAdapter = new PeopleAroundMeAdapter(getActivity(), R.layout.fragment_people_aroundme_card, data, TAG);
        list.setAdapter(peopleAroundMeAdapter);
        list.setEmptyView(emptyLayout);
        updateContent(data);
    }
}
