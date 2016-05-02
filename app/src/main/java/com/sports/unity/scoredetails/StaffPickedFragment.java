package com.sports.unity.scoredetails;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by madmachines on 23/2/16.
 */
public class StaffPickedFragment extends BasicVolleyRequestResponseViewHelper {

    private String title;
    private HashMap<String,String> requestParameters;
    private JSONObject response;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout tableView;

    public StaffPickedFragment(String title) {
        this.title = title;
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.staff_pick_fragment;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return "StaffPickRequestListener";
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress);

        StaffPickComponentListener componentListener = new StaffPickComponentListener( getRequestTag(), progressBar, errorLayout);
        return componentListener;
    }

    @Override
    public String getRequestTag() {
        return "StaffPickRequestTag";
    }

    @Override
    public String getRequestCallName() {
        return ScoresContentHandler.CALL_NAME_FOOTBALL_STAFF_PICK;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public void initialiseViews(View view) {
        initView(view);
    }


    public void setRequestParameters(HashMap<String,String> params ) {
        this.requestParameters = params;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Bundle bundle = getArguments();
//        String s = bundle.getString(Constants.INTENT_KEY_ID);
//        favouriteItem = new FavouriteItem(s);
//    }

    private void initView(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sv_swipe_football_match_table);
        swipeRefreshLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestContent();
            }
        });

        tableView = (LinearLayout) view.findViewById(R.id.staff_view);
    }

    private boolean handleContent(String object) {
        boolean success = false;
        try {
            JSONObject jsonObject = new JSONObject(object);
            success = jsonObject.getBoolean("success");
            if (success) {
                response = jsonObject;
            } else {
                //nothing
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private boolean renderDisplay() {
        boolean success = false;

        try {
            JSONArray array = response.getJSONArray("data");
            JSONObject obj1 = array.getJSONObject(0);
            JSONObject object = obj1.getJSONObject("season_table");

            String groupName = obj1.getString("series_name");
            ArrayList<String> seriesArray = new ArrayList<String>();
            for (int i = 0; i < object.names().length(); i++) {
                seriesArray.add(object.names().getString(i));
            }

            Context context = swipeRefreshLayout.getContext();

            StaffPickTableDTO staffPickTableDTO = null;
            tableView.removeAllViews();

            JSONArray dataArray = null;
            for (int j = 0; j < seriesArray.size(); j++) {
                dataArray = object.getJSONArray(seriesArray.get(j));

                List<StaffPickTableDTO> list = new ArrayList<StaffPickTableDTO>();
                LinearLayout tableData = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.staff_pick_table, null);
                TextView textView = (TextView) tableData.findViewById(R.id.group);
                RecyclerView recyclerView = (RecyclerView) tableData.findViewById(R.id.rv_staff_pick);
                recyclerView.setLayoutManager(new LinearLayoutManager(context, VERTICAL, false));
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setHasFixedSize(false);
                recyclerView.setFocusable(false);

                StaffPickTableAdapter adapter = new StaffPickTableAdapter(context, list);
                recyclerView.setAdapter(adapter);
                for (int i = 0; i < dataArray.length(); i++) {
                    staffPickTableDTO = new StaffPickTableDTO();
                    JSONObject teamObject = dataArray.getJSONObject(i);
                    if (!teamObject.isNull("team_id")) {
                        staffPickTableDTO.setTeamId(teamObject.getString("team_id"));
                    }
                    if (!teamObject.isNull("team_name")) {
                        staffPickTableDTO.setTvTeamName(teamObject.getString("team_name"));
                    }
                    if (!teamObject.isNull("tied"))
                        staffPickTableDTO.setTvD(teamObject.getString("tied"));
                    if (!teamObject.isNull("lost"))
                        staffPickTableDTO.setTvL(teamObject.getString("lost"));
                    if (!teamObject.isNull("played"))
                        staffPickTableDTO.setTvP(teamObject.getString("played"));
                    if (!teamObject.isNull("won"))
                        staffPickTableDTO.setTvW(teamObject.getString("won"));
                    if (!teamObject.isNull("points"))
                        staffPickTableDTO.setTvPts(teamObject.getString("points"));
                    if (!teamObject.isNull("net_run_rate"))
                        staffPickTableDTO.setTvNRR(teamObject.getString("net_run_rate"));
                    list.add(staffPickTableDTO);
                }

                {
                    if (seriesArray.size() > 0) {
                        textView.setText(groupName);
                        tableView.addView(tableData);
                        adapter.notifyDataSetChanged();
                    } else {
                        //nothing
                    }
                }
            }

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    public class StaffPickComponentListener extends CustomComponentListener {

        public StaffPickComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
            super(requestTag, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = StaffPickedFragment.this.handleContent(content);
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        protected void showErrorLayout() {
            if( swipeRefreshLayout.getVisibility() == View.GONE ) {
                super.showErrorLayout();
            } else {
                //nothing
            }
        }

        @Override
        protected void showProgress() {
            if( swipeRefreshLayout.getVisibility() == View.GONE ) {
                super.showProgress();
            } else {
                //nothing
            }
        }

        @Override
        protected void hideProgress() {
            super.hideProgress();

            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderDisplay();
            if( success ){
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                showErrorLayout();
            }
        }

    }

}

