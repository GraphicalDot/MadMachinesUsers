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
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchTableHandler;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by madmachines on 23/2/16.
 */
public class StaffPickedFragment extends Fragment implements UpCommingFootballMatchTableHandler.UpCommingFootballMatchTableContentListener {
    private UpCommingFootballMatchTableHandler upCommingFootballMatchTableHandler;
    private ProgressBar progressBar;
    private ArrayList<String> seriesArray;
    private JSONArray dataArray;
    private LinearLayout errorLayout;
    private LinearLayout tableView;
    private LayoutInflater inflater;
    private String groupName = "";

    private SwipeRefreshLayout swipeRefreshLayout;
    private FavouriteItem favouriteItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        String s = bundle.getString(Constants.INTENT_KEY_ID);
        favouriteItem = new FavouriteItem(s);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.staff_pick_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        tableView = (LinearLayout) view.findViewById(R.id.staff_view);
        initErrorLayout(view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sv_swipe_football_match_table);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (upCommingFootballMatchTableHandler != null) {
                    upCommingFootballMatchTableHandler.requestStaffPickedLeague(favouriteItem.getId());
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });
    }

    @Override
    public void handleContent(String object) {
        {
            try {
                JSONObject jsonObject = new JSONObject(object);
                boolean success = jsonObject.getBoolean("success");
                boolean error = jsonObject.getBoolean("error");

                if (success) {

                    renderDisplay(jsonObject);

                } else {
                    hideProgressBar();
                    showErrorLayout(getView());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                hideProgressBar();
                showErrorLayout(getView());
            }

        }
    }

    private void initErrorLayout(View view) {
        try {
            errorLayout = (LinearLayout) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showErrorLayout(View view) {
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        errorLayout.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        Activity activity = getActivity();
        JSONArray array = jsonObject.getJSONArray("data");
        JSONObject obj1 = array.getJSONObject(0);
        final JSONObject object = obj1.getJSONObject("season_table");
        groupName = obj1.getString("series_name");
        seriesArray = new ArrayList<String>();
        for (int i = 0; i < object.names().length(); i++) {
            seriesArray.add(object.names().getString(i));
        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        try {
            StaffPickTableDTO staffPickTableDTO = null;
            tableView.removeAllViews();
            for (int j = 0; j < seriesArray.size(); j++) {
                dataArray = new JSONArray();
                dataArray = object.getJSONArray(seriesArray.get(j));
                List<StaffPickTableDTO> list = new ArrayList<StaffPickTableDTO>();
                final LinearLayout tableData = (LinearLayout) inflater.inflate(R.layout.staff_pick_table, null);
                final TextView textView = (TextView) tableData.findViewById(R.id.group);
                RecyclerView recyclerView = (RecyclerView) tableData.findViewById(R.id.rv_staff_pick);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setHasFixedSize(false);
                recyclerView.setFocusable(false);
                final StaffPickTableAdapter adapter = new StaffPickTableAdapter(getActivity(), list);
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

                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (seriesArray.size() > 0) {
                                hideProgressBar();
                                hideErrorLayout();
                                textView.setText(groupName);
                                tableView.addView(tableData);
                                adapter.notifyDataSetChanged();
                            } else {
                                showErrorLayout(getView());
                            }

                        }
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorLayout(getView());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (upCommingFootballMatchTableHandler != null) {
            upCommingFootballMatchTableHandler.addListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (upCommingFootballMatchTableHandler == null) {
            upCommingFootballMatchTableHandler = UpCommingFootballMatchTableHandler.getInstance(getContext());
            upCommingFootballMatchTableHandler.requestStaffPickedLeague(favouriteItem.getId());
            showProgressBar();
        }
        upCommingFootballMatchTableHandler.addListener(this);
    }

}

