package com.sports.unity.scoredetails.footballdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommingFootballMatchTableAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommngFootbalMatchTableDTO;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_LEAGUE_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM1_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM2_NAME;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchTableFargment extends BasicVolleyRequestResponseViewHelper {

    private static final String REQUEST_LISTENER_KEY = "LEAGUE_TABLE";
    private static final String LEAGUE_TABLE_REQUEST_TAG = "leagueTableTag";

    private Context context;

    private String title;
    private HashMap<String, String> requestParameters;
    private JSONObject response;

    private String team1 = "abc";
    private String team2 = "xyz";

    private View contentLayout = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    private UpCommingFootballMatchTableAdapter adapter;
    private RecyclerView recyclerView;

    private HashMap<String, ArrayList<UpCommngFootbalMatchTableDTO>> groupStandingsMap = new HashMap<>();
    private ArrayList<String> groupsListInOrder = new ArrayList<>();

    public UpCommingFootballMatchTableFargment(String title) {
        this.title = title;
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_upcoming_football_match_table;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return REQUEST_LISTENER_KEY;
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        UpcomingFootballMatchTableComponentListener upcomingFootballMatchTableComponentListener = new UpcomingFootballMatchTableComponentListener(getRequestTag(), progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        return upcomingFootballMatchTableComponentListener;
    }

    @Override
    public String getRequestTag() {
        return LEAGUE_TABLE_REQUEST_TAG;
    }

    @Override
    public String getRequestCallName() {
        return ScoresContentHandler.CALL_NAME_LEAGUE_TABLE;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public void initialiseViews(View view) {
        initView(view);
    }

    public void setRequestParameters(HashMap<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    private void initView(View view) {
        context = view.getContext();

        contentLayout = view.findViewById(R.id.content_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_football_match_table);
        recyclerView.setLayoutManager(new android.support.v7.widget.LinearLayoutManager(context));

        adapter = new UpCommingFootballMatchTableAdapter(context, team1, team2, groupsListInOrder, groupStandingsMap);
        recyclerView.setAdapter(adapter);

        {
            ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.header_row);
            viewGroup.setBackgroundColor(context.getResources().getColor(R.color.gray3));
            viewGroup.setPadding(0, 0, 0, 0);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                requestContent();
            }

        });
    }

    private boolean renderDisplay() {
        boolean success = getData(response);
        return success;
    }

    private boolean getData(JSONObject jsonObject) {
        boolean success = false;
        try {
            groupStandingsMap.clear();
            groupsListInOrder.clear();

            JSONArray dataArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject object = dataArray.getJSONObject(i);

                String group = object.getString("stand_group");
                if (group == null) {
                    group = "A";
                }

                ArrayList<UpCommngFootbalMatchTableDTO> tempArrayList = groupStandingsMap.get(group);
                if (tempArrayList == null) {
                    tempArrayList = new ArrayList<>();
                    groupStandingsMap.put(group, tempArrayList);
                    groupsListInOrder.add(group);

                    UpCommngFootbalMatchTableDTO dto = getDtoObject(object);
                    if (dto != null) {
                        tempArrayList.add(getHeaderRowObject(group));
                        tempArrayList.add(dto);
                    }
                } else {
                    tempArrayList.add(getDtoObject(object));
                }
            }

            if (groupStandingsMap.size() == 1) {
                String key = groupsListInOrder.iterator().next();
                ArrayList<UpCommngFootbalMatchTableDTO> list = groupStandingsMap.get(key);
                if (list != null && list.size() > 0) {
                    list.remove(0);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (groupStandingsMap.size() > 0) {
            adapter.initContent();
        } else {
            //nothing
        }

        success = true;
        return success;

    }

    private UpCommngFootbalMatchTableDTO getDtoObject(JSONObject teamObject) {
        UpCommngFootbalMatchTableDTO upCommngFootbalMatchTableDTO = new UpCommngFootbalMatchTableDTO();
        try {
            if (!teamObject.isNull("stand_season")) {
//                tvMatchDate.setText(teamObject.getString("stand_season"));
            }
            if (!teamObject.isNull("flag_image"))
                upCommngFootbalMatchTableDTO.setIvTeamProfileImage(teamObject.getString("flag_image"));
            if (!teamObject.isNull("team_id"))
                upCommngFootbalMatchTableDTO.setTeamId(teamObject.getString("team_id"));
            if (!teamObject.isNull("team_name"))
                upCommngFootbalMatchTableDTO.setTvTeamName(teamObject.getString("team_name"));
            if (!teamObject.isNull("games_drawn"))
                upCommngFootbalMatchTableDTO.setTvD(teamObject.getString("games_drawn"));
            if (!teamObject.isNull("games_lost"))
                upCommngFootbalMatchTableDTO.setTvL(teamObject.getString("games_lost"));
            if (!teamObject.isNull("games_played"))
                upCommngFootbalMatchTableDTO.setTvP(teamObject.getString("games_played"));
            if (!teamObject.isNull("games_won"))
                upCommngFootbalMatchTableDTO.setTvW(teamObject.getString("games_won"));
            if (!teamObject.isNull("team_points"))
                upCommngFootbalMatchTableDTO.setTvPts(teamObject.getString("team_points"));
            if (!teamObject.isNull("position"))
                upCommngFootbalMatchTableDTO.setRank(teamObject.getString("position"));
            if (!teamObject.isNull("goal_diff"))
                upCommngFootbalMatchTableDTO.setTvDG(teamObject.getString("goal_diff"));
            if (!teamObject.isNull("description"))
                upCommngFootbalMatchTableDTO.setDescription(teamObject.getString("description"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return upCommngFootbalMatchTableDTO;
    }

    private UpCommngFootbalMatchTableDTO getHeaderRowObject(String groupName) {
        UpCommngFootbalMatchTableDTO upCommngFootbalMatchTableDTO = new UpCommngFootbalMatchTableDTO();
        upCommngFootbalMatchTableDTO.setViewType(1);
        upCommngFootbalMatchTableDTO.setRank(groupName);
        upCommngFootbalMatchTableDTO.setIvTeamProfileImage("");
        upCommngFootbalMatchTableDTO.setTeamId("");
        upCommngFootbalMatchTableDTO.setTvP("");
        upCommngFootbalMatchTableDTO.setTvW("");
        upCommngFootbalMatchTableDTO.setTvD("");
        upCommngFootbalMatchTableDTO.setTvL("");
        upCommngFootbalMatchTableDTO.setTvDG("");
        upCommngFootbalMatchTableDTO.setTvPts("");
        upCommngFootbalMatchTableDTO.setDescription("");
        return upCommngFootbalMatchTableDTO;
    }

    private boolean handleContent(String content) {
        boolean success = false;
        try {
            JSONObject jsonObject = new JSONObject(content);
            success = jsonObject.getBoolean("success");
            if (success) {
                response = jsonObject;
            } else {
                //nothing
            }

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    public class UpcomingFootballMatchTableComponentListener extends CustomComponentListener {

        public UpcomingFootballMatchTableComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout, View contentLayout, SwipeRefreshLayout swipeRefreshLayout) {
            super(requestTag, progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        }

        @Override
        protected boolean isContentLayoutAvailable() {
            return groupStandingsMap.size() > 0;
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = UpCommingFootballMatchTableFargment.this.handleContent(content);
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderDisplay();
            if (success) {
                //nothing
            } else {
                showErrorLayout();
            }
        }

    }

}
