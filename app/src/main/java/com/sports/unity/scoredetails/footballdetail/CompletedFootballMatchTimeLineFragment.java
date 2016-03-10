package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.view.CustomLinearLayoutManager;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballTimeLineAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballTimeLineDTO;
import com.sports.unity.scores.ScoreDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class CompletedFootballMatchTimeLineFragment extends Fragment implements CompletedFootballMatchTimeLineHandler.CompletedMatchContentListener{


    private RecyclerView recyclerView;
    String toss = "";
    String matchName="";
    String date = "";
    private String matchId;
    private SwipeRefreshLayout swTimeLineRefresh;
    private TextView nocomments;
    private ProgressBar progressBar;
    private CompleteFootballTimeLineAdapter completeFootballTimeLineAdapter;
    private List<CompleteFootballTimeLineDTO> list = new ArrayList<>();
    private CompletedFootballMatchTimeLineHandler completedFootballMatchTimeLineHandler;

    public CompletedFootballMatchTimeLineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        matchId =  i.getStringExtra(INTENT_KEY_ID);
        matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
        toss = i.getStringExtra(INTENT_KEY_TOSS);
        date = i.getStringExtra(INTENT_KEY_DATE);
        completedFootballMatchTimeLineHandler = CompletedFootballMatchTimeLineHandler.getInstance(context);
        completedFootballMatchTimeLineHandler.addListener(this);
        completedFootballMatchTimeLineHandler.requestCompletedMatchTimeLine(matchId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_football_match_timeline, container, false);
        initView(view);

        return view;
    }
    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        completeFootballTimeLineAdapter = new CompleteFootballTimeLineAdapter(list,getContext());
        recyclerView.setAdapter(completeFootballTimeLineAdapter);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        swTimeLineRefresh = (SwipeRefreshLayout) view.findViewById(R.id.sw_timeline_refresh);
        initErrorLayout(view);
        nocomments=(TextView)view.findViewById(R.id.no_comments);
        swTimeLineRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (completedFootballMatchTimeLineHandler != null) {
                    completedFootballMatchTimeLineHandler.requestCompletedMatchTimeLine(matchId);
                    swTimeLineRefresh.setRefreshing(true);
                }
            }
        });

    }

    @Override
    public void handleContent(String object) {
        {

            try {
                showProgressBar();
                JSONObject jsonObject = new JSONObject(object);
                boolean success = jsonObject.getBoolean("success");
                boolean error = jsonObject.getBoolean("error");

                if( success ) {

                    renderDisplay(jsonObject);

                } else {
                    showErrorLayout(getView());
                }
            }catch (Exception ex){
                ex.printStackTrace();
                showErrorLayout(getView());
            }
        }
    }
    private void initErrorLayout(View view) {
        try {
            LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.GONE);
        }catch (Exception e){e.printStackTrace();}
    }

    private void showErrorLayout(View view) {

        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);
   }
   private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        list.clear();
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        hideProgressBar();
        swTimeLineRefresh.setRefreshing(false);
        final JSONArray dataArray = jsonObject.getJSONArray("data");
        list.clear();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CompleteFootballTimeLineDTO completeFootballTimeLineDTO = null;

                        for (int i = 0; i < dataArray.length(); i++) {
                            completeFootballTimeLineDTO = new CompleteFootballTimeLineDTO();
                            JSONObject dataObject = dataArray.getJSONObject(i);
                            if (!dataObject.isNull("team")) {
                                completeFootballTimeLineDTO.setTeamName(dataObject.getString("team"));
                                if (dataObject.getString("team").equalsIgnoreCase(getContext().getString(R.string.home_team_name))) {

                                    setTeamFirstTimeDTO(completeFootballTimeLineDTO, dataObject);

                                } else {

                                    setTeamSecondTimeDTO(completeFootballTimeLineDTO, dataObject);
                                }
                            }
                            list.add(completeFootballTimeLineDTO);
                        }
                        Collections.sort(list);
                        completeFootballTimeLineAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

    private void setTeamFirstTimeDTO(CompleteFootballTimeLineDTO completeFootballTimeLineDTO, JSONObject dataObject) throws JSONException {

        if(!dataObject.isNull("event_time")){
            completeFootballTimeLineDTO.setTvTeamFirstTime(dataObject.getString("event_time")+"'");
        }else if(!dataObject.isNull("minute")){
            completeFootballTimeLineDTO.setTvTeamFirstTime(dataObject.getString("minute")+"'");
        }

        if(!dataObject.isNull("player_on")){
            completeFootballTimeLineDTO.setTvTeamFirstOnPlayer("ON:" + dataObject.getString("player_on"));
        }
        if(!dataObject.isNull("player_off")){
            completeFootballTimeLineDTO.setTvTeamFirstOffPlayer("OFF:" + dataObject.getString("player_off"));
        }

        if(!dataObject.isNull("event")){
            completeFootballTimeLineDTO.setDrwDrawable(getDrwableResource(dataObject.getString("event")));
            completeFootballTimeLineDTO.setTvTeamFirstOnPlayer(dataObject.getString("player_name"));
        }else {
            completeFootballTimeLineDTO.setDrwDrawable(getDrwableResource(""));
        }


    }
    private void setTeamSecondTimeDTO(CompleteFootballTimeLineDTO completeFootballTimeLineDTO, JSONObject dataObject) throws JSONException {

        if(!dataObject.isNull("event_time")){
            completeFootballTimeLineDTO.setTvTeamSecondTime(dataObject.getString("event_time")+"'");
        }else if(!dataObject.isNull("minute")){
            completeFootballTimeLineDTO.setTvTeamSecondTime(dataObject.getString("minute")+"'");
        }

        if(!dataObject.isNull("player_on")){
            completeFootballTimeLineDTO.setTvTeamSecondOnPlayer("ON:" + dataObject.getString("player_on"));
        }
        if(!dataObject.isNull("player_off")){
            completeFootballTimeLineDTO.setTvTeamSecondOffPlayer("OFF:" + dataObject.getString("player_off"));
        }

        if(!dataObject.isNull("event")){
            completeFootballTimeLineDTO.setDrwDrawable(getDrwableResource(dataObject.getString("event")));
            completeFootballTimeLineDTO.setTvTeamSecondOnPlayer(dataObject.getString("player_name"));
        }else {
            completeFootballTimeLineDTO.setDrwDrawable(getDrwableResource(""));
        }


    }

    private Drawable getDrwableResource(String event) {
        Resources.Theme theme = getActivity().getTheme();
        int drwableId = R.drawable.ic_subsitute_circle;
        if("yellowcards".equalsIgnoreCase(event)){
           drwableId = R.drawable.ic_yellow_card_circle;
        }else if("goals".equalsIgnoreCase(event)){
            drwableId = R.drawable.ic_goal_circle;
        }
        else if("redcards".equalsIgnoreCase(event)){
            drwableId = R.drawable.ic_red_card_circle;
        }
        Drawable drawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = getResources().getDrawable(drwableId,theme);
        } else {
            drawable = getResources().getDrawable(drwableId);
        }
        return drawable;
    }

    private void  showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void  hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }
}
