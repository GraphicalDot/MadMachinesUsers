package com.sports.unity.scoredetails;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sports.unity.R;
import com.sports.unity.scores.DataRequestService;
import com.sports.unity.scores.DataServiceContract;
import com.sports.unity.scores.ErrorContract;
import com.sports.unity.scores.controller.fragment.BroadcastListAdapter;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentaryFragment extends Fragment implements FragementInterface<CommentriesModel>, DataServiceContract, ErrorContract {

    private ArrayList<CommentriesModel> commentaries = new ArrayList<>();

    private String sportsType;
    private String matchId;
    Handler h = new Handler();
    private RecyclerView mRecyclerView;
    private BroadcastListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DataRequestService dataServiceContract;
    public CommentaryFragment() {
        // Required empty public constructor
    }
   @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DataServiceContract)
        {
            dataServiceContract = (DataRequestService)context;
            dataServiceContract.requestData(0);
        }
       dataChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_commentary, container, false);
        Bundle b = getArguments();
        sportsType = b.getString(Constants.INTENT_KEY_TYPE);
        matchId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        commentaries = b.getParcelableArrayList("commentries");
        initView(view);
        dataChanged();
        return view;
    }


    private void initView(View view) {

        // ((TextView)view.findViewById(R.id.venue)).setTypeface(FontTypeface.getInstance(getContext()).getRobotoCondensedBold());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getContext(), VERTICAL, false));
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new BroadcastListAdapter(sportsType, commentaries, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.commentary_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (dataServiceContract != null) {
                    dataServiceContract.requestData(0);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }
    @Override
    public List<CommentriesModel> getItems() {
        return commentaries;
    }

    @Override
    public void dataChanged() {
        try{
            if(mRecyclerView !=null) {
                       /* mRecyclerView.postInvalidate();*/
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
            }
        }catch (Exception e){e.printStackTrace();}


    }


    @Override
    public void errorHandle() {
      Log.i("Error", "errorHandle: ");
        //swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();
    }

}
