package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
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
    CompletedFootballMatchTimeLineHandler cricketUpcomingMatchSummaryHandler;

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
        cricketUpcomingMatchSummaryHandler = CompletedFootballMatchTimeLineHandler.getInstance(context);
        cricketUpcomingMatchSummaryHandler.addListener(this);
        cricketUpcomingMatchSummaryHandler.requestCompletedMatchTimeLine(matchId);

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
        completeFootballTimeLineAdapter = new CompleteFootballTimeLineAdapter(list,getContext());
        recyclerView.setAdapter(completeFootballTimeLineAdapter);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        swTimeLineRefresh = (SwipeRefreshLayout) view.findViewById(R.id.sw_timeline_refresh);
        initErrorLayout(view);
        nocomments=(TextView)view.findViewById(R.id.no_comments);
        swTimeLineRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (cricketUpcomingMatchSummaryHandler  != null) {
                    cricketUpcomingMatchSummaryHandler.requestCompletedMatchTimeLine(matchId);
                    swTimeLineRefresh.setRefreshing(false);
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
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        hideProgressBar();
        if(swTimeLineRefresh.isRefreshing()){
            swTimeLineRefresh.setRefreshing(false);
        }
        final JSONArray dataArray = jsonObject.getJSONArray("data");
        final String localTeam = "localteam";
        final String visitorTeam = "visitorteam";
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CompleteFootballTimeLineDTO completeFootballTimeLineDTO = null;

                        for (int i = 0 ;i<dataArray.length();i++){
                            completeFootballTimeLineDTO = new CompleteFootballTimeLineDTO();
                            JSONObject dataObject = dataArray.getJSONObject(i);
                             if(!dataObject.isNull("team")){

                                 if(dataObject.getString("team").equals(localTeam)){
                                     StringBuilder teamBuilder = new StringBuilder();
                                     if(!dataObject.isNull("event_time")){
                                         teamBuilder.append(dataObject.getString("event_time"));
                                     }else if(!dataObject.isNull("minute")){
                                         teamBuilder.append(dataObject.getString("minute")+"'");
                                     }

                                     if(!dataObject.isNull("player_on")){
                                         teamBuilder.append("ON: "+dataObject.getString("player_on")+"\n");
                                         teamBuilder.append("OFF: "+dataObject.getString("player_off"));
                                     }
                                     completeFootballTimeLineDTO.setTvTeamFirst(teamBuilder.toString());
                                     if(!dataObject.isNull("event")){
                                         completeFootballTimeLineDTO.setKey(dataObject.getString("event"));
                                         completeFootballTimeLineDTO.setTvTeamFirst(dataObject.getString("player_name"));
                                     }

                                 }else{
                                     StringBuilder teamBuilder = new StringBuilder();

                                     if(!dataObject.isNull("player_on")){
                                         teamBuilder.append("ON: "+dataObject.getString("player_on")+"\n");
                                         teamBuilder.append("OFF: "+dataObject.getString("player_off"));
                                     }
                                     if(!dataObject.isNull("event_time")){
                                         teamBuilder.append(dataObject.getString("event_time"));
                                     }else if(!dataObject.isNull("minute")){
                                         teamBuilder.append(dataObject.getString("minute")+"'");
                                     }
                                    completeFootballTimeLineDTO.setTvTeamSecond(teamBuilder.toString());
                                     if(!dataObject.isNull("event")){
                                         completeFootballTimeLineDTO.setKey(dataObject.getString("event"));
                                         completeFootballTimeLineDTO.setTvTeamSecond(dataObject.getString("player_name"));
                                     }
                                 }
                             }
                            list.add(completeFootballTimeLineDTO);
                        }
                        completeFootballTimeLineAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }
    private void  showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void  hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }
}
