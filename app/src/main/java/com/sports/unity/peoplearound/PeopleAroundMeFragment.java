package com.sports.unity.peoplearound;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.sports.unity.R;
import com.sports.unity.messages.controller.model.User;
import com.sports.unity.messages.controller.model.User2;
import com.sports.unity.peoplearound.adapters.PeopleAroundMeAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleAroundMeFragment extends Fragment {

    DataNotifier dataNotifier = new DataNotifier() {
        @Override
        public void newData(ArrayList<User> content) {
            Log.i("lists recv", "true");
            updateContent(content);
        }
    };

    private void updateContent(ArrayList<User> content) {
        this.data = content;
        ((PeopleAroundMeAdapter) list.getAdapter()).updateData(data);
    }

    private ArrayList<User> data = new ArrayList<>();
    private ListView list;
    private ProgressBar progressBar;

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
        return v;
    }

    private void initView(View v) {
        list = (ListView) v.findViewById(R.id.people_around_list);
        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.data_exist);
        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        PeopleAroundMeAdapter peopleAroundMeAdapter = new PeopleAroundMeAdapter(getActivity(), R.layout.fragment_people_aroundme_card, data);
        list.setAdapter(peopleAroundMeAdapter);
        list.setEmptyView(relativeLayout);
        updateContent(data);
    }
}
