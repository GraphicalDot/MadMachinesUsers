package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sports.unity.R;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballTimeLineAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballTimeLineDTO;
import com.sports.unity.scores.model.ScoresContentHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by madmachines on 23/2/16.
 */
public class CompletedFootballMatchTimeLineFragment extends BasicVolleyRequestResponseViewHelper {

    private String title;
    private HashMap<String, String> requestParameters;
    private JSONObject response;

    private View contentLayout = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    private CompleteFootballTimeLineAdapter completeFootballTimeLineAdapter;

    private List<CompleteFootballTimeLineDTO> list = new ArrayList<>();

    public CompletedFootballMatchTimeLineFragment(String title) {
        this.title = title;
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_football_match_timeline;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return "FootballTimelineRequestListener";
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        MatchTimelineComponentListener componentListener = new MatchTimelineComponentListener(getRequestTag(), progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        return componentListener;
    }

    @Override
    public String getRequestTag() {
        return "MatchTimelineRequestTag";
    }

    @Override
    public String getRequestCallName() {
        return ScoresContentHandler.CALL_NAME_FOOTBALL_TIMELINE;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public void initialiseViews(View view) {
        initView(view);
    }


    public void setRequestParameters(HashMap<String, String> params) {
        this.requestParameters = params;
    }

    private void initView(View view) {
        Context context = view.getContext();

        contentLayout = view.findViewById(R.id.content_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                requestContent();
            }

        });

        RecyclerView recyclerView = (RecyclerView) contentLayout;
        RecyclerView.LayoutManager manager = new android.support.v7.widget.LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        completeFootballTimeLineAdapter = new CompleteFootballTimeLineAdapter(list, context);
        recyclerView.setAdapter(completeFootballTimeLineAdapter);
    }

    private boolean handleContent(String object) {
        boolean success = false;
        try {
            JSONObject jsonObject = new JSONObject(object);
            success = jsonObject.getBoolean("success");
            if (success) {
                response = jsonObject;
            } else {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private boolean renderDisplay() {
        boolean success = false;
        if (!response.isNull("data")) {
            try {
                JSONArray dataArray = response.getJSONArray("data");
                list.clear();

                CompleteFootballTimeLineDTO completeFootballTimeLineDTO = null;
                for (int i = 0; i < dataArray.length(); i++) {
                    completeFootballTimeLineDTO = new CompleteFootballTimeLineDTO();
                    JSONObject dataObject = dataArray.getJSONObject(i);
                    if (!dataObject.isNull("team")) {
                        completeFootballTimeLineDTO.setTeamName(dataObject.getString("team"));
                        if (dataObject.getString("team").equalsIgnoreCase(swipeRefreshLayout.getContext().getString(R.string.home_team_name))) {
                            setTeamFirstTimeDTO(completeFootballTimeLineDTO, dataObject);
                        } else {
                            setTeamSecondTimeDTO(completeFootballTimeLineDTO, dataObject);
                        }
                    }
                    list.add(completeFootballTimeLineDTO);
                }
                Collections.sort(list);
                completeFootballTimeLineAdapter.notifyDataSetChanged();

                success = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            //nothing
        }
        return success;
    }

    private void setTeamFirstTimeDTO(CompleteFootballTimeLineDTO completeFootballTimeLineDTO, JSONObject dataObject) throws JSONException {
        if (!dataObject.isNull("event_time")) {
            completeFootballTimeLineDTO.setTvTeamFirstTime(dataObject.getString("event_time") + "'");
        } else if (!dataObject.isNull("minute")) {
            completeFootballTimeLineDTO.setTvTeamFirstTime(dataObject.getString("minute") + "'");
        }

        if (!dataObject.isNull("player_on")) {
            completeFootballTimeLineDTO.setTvTeamFirstOnPlayer("ON:" + dataObject.getString("player_on"));
        }

        if (!dataObject.isNull("player_off")) {
            completeFootballTimeLineDTO.setTvTeamFirstOffPlayer("OFF:" + dataObject.getString("player_off"));
        }

        if (!dataObject.isNull("event")) {
            completeFootballTimeLineDTO.setDrwDrawable(getDrwableResource(dataObject.getString("event")));
            completeFootballTimeLineDTO.setTvTeamFirstOnPlayer(dataObject.getString("player_name"));
        } else {
            completeFootballTimeLineDTO.setDrwDrawable(getDrwableResource(""));
        }
    }

    private void setTeamSecondTimeDTO(CompleteFootballTimeLineDTO completeFootballTimeLineDTO, JSONObject dataObject) throws JSONException {
        if (!dataObject.isNull("event_time")) {
            completeFootballTimeLineDTO.setTvTeamSecondTime(dataObject.getString("event_time") + "'");
        } else if (!dataObject.isNull("minute")) {
            completeFootballTimeLineDTO.setTvTeamSecondTime(dataObject.getString("minute") + "'");
        }

        if (!dataObject.isNull("player_on")) {
            completeFootballTimeLineDTO.setTvTeamSecondOnPlayer("ON:" + dataObject.getString("player_on"));
        }

        if (!dataObject.isNull("player_off")) {
            completeFootballTimeLineDTO.setTvTeamSecondOffPlayer("OFF:" + dataObject.getString("player_off"));
        }

        if (!dataObject.isNull("event")) {
            completeFootballTimeLineDTO.setDrwDrawable(getDrwableResource(dataObject.getString("event")));
            completeFootballTimeLineDTO.setTvTeamSecondOnPlayer(dataObject.getString("player_name"));
        } else {
            completeFootballTimeLineDTO.setDrwDrawable(getDrwableResource(""));
        }
    }

    private Drawable getDrwableResource(String event) {
        Resources.Theme theme = swipeRefreshLayout.getContext().getTheme();
        int drwableId = R.drawable.ic_subsitute_circle;
        if ("yellowcard".equalsIgnoreCase(event)) {
            drwableId = R.drawable.ic_yellow_card_circle;
        } else if ("goal".equalsIgnoreCase(event)) {
            drwableId = R.drawable.ic_goal_circle;
        } else if ("redcard".equalsIgnoreCase(event) || "yellowred".equalsIgnoreCase(event)) {
            drwableId = R.drawable.ic_red_card_circle;
        }
        Drawable drawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = swipeRefreshLayout.getResources().getDrawable(drwableId, theme);
        } else {
            drawable = swipeRefreshLayout.getResources().getDrawable(drwableId);
        }
        return drawable;
    }

    public class MatchTimelineComponentListener extends CustomComponentListener {

        public MatchTimelineComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout, View contentLayout, SwipeRefreshLayout swipeRefreshLayout) {
            super(requestTag, progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        }

        @Override
        protected boolean isContentLayoutAvailable() {
            return list.size() > 0;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = CompletedFootballMatchTimeLineFragment.this.handleContent(content);
            return success;
        }


        @Override
        public void changeUI(String tag) {
            boolean success = renderDisplay();
            if( success ){
                //nothing
            } else {
                showErrorLayout();
            }
        }

    }

}
