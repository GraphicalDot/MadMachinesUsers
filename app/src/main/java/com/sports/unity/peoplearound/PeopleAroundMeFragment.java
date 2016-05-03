package com.sports.unity.peoplearound;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.clans.fab.FloatingActionButton;
import com.sports.unity.R;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.messages.controller.model.Person;
import com.sports.unity.peoplearound.adapters.PeopleAroundMeAdapter;
import com.sports.unity.util.Constants;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleAroundMeFragment extends Fragment implements DataNotifier {

    private ArrayList<Person> peoples ;
    private RecyclerView recyclerview;
    private PeopleAroundMeAdapter mAdapter;
    private Context context;
    private ProgressBar progressBar;
    private boolean customLocation;
    private FloatingActionButton myLocation;
    private View emptyView;

    public PeopleAroundMeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if(context instanceof  DataRequestService){
            DataRequestService dataRequestService = (DataRequestService) context;
            dataRequestService.dataRequest();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_people_around_me, container, false);
        initViews(v);
        initProgress(v);

        return v;
    }

    private void initViews(View v) {
        customLocation = false;
        emptyView = v.findViewById(R.id.data_exist);
        myLocation = (FloatingActionButton) v.findViewById(R.id.myLocation);
        peoples = getArguments().getParcelableArrayList(Constants.PARAM_PEOPLES);
        recyclerview=(RecyclerView) v.findViewById(R.id.recyclerview);
        mAdapter = new PeopleAroundMeAdapter(peoples,context);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, true));
        recyclerview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        hideProgress();
    }

    private void showErrorLayout(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);
        CustomComponentListener.renderAppropriateErrorLayout(errorLayout);
    }

    private void hideErrorLayout(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);
    }

    private void initProgress(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void showProgress(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if( progressBar != null ){
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(context instanceof  DataRequestService){
            DataRequestService dataRequestService = (DataRequestService) context;
            dataRequestService.dataRequest();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void notifyPeoples() {
        Log.i("notifyPeoples", "notifyPeoples: " + peoples);
        if(mAdapter!=null){
            renderUsers();
            recyclerview.postInvalidate();
            mAdapter.notifyDataSetChanged();
           final  PeopleAroundActivity activity  = (PeopleAroundActivity) getActivity();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    myLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                             customLocation = activity.checkIfGPSEnabled();
                            if(customLocation){
                                activity.getLocation();
                            }

                        }
                    });
                }});
        }
    }

    private void renderUsers() {
        hideProgress();
        if( peoples.size() == 0 ) {
            recyclerview.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            recyclerview.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public  interface DataRequestService{
        void dataRequest();
        void cancelRequest();
    }

}
