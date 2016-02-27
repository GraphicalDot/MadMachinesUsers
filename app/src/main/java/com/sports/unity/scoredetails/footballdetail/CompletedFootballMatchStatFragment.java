package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scoredetails.cricketdetail.CricketPlayerbioHandler;
import com.sports.unity.scoredetails.cricketdetail.PlayerCricketBioDataActivity;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballMatchStatAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballMatchStatDTO;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

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
public class CompletedFootballMatchStatFragment extends Fragment implements CompletedFootballMatchStatHandler.CompletedFootballMatchContentListener{


    private ProgressBar progressBar;
    private String matchId;
    String matchName="";
    private RecyclerView rvFootballMatchStat;
    private TextView nocomments;
    private CompleteFootballMatchStatAdapter completeFootballMatchStatAdapter;
    private List<CompleteFootballMatchStatDTO> list = new ArrayList<>();


    public CompletedFootballMatchStatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
        matchId = i.getStringExtra(INTENT_KEY_ID);
        CompletedFootballMatchStatHandler cricketUpcomingMatchSummaryHandler = CompletedFootballMatchStatHandler.getInstance(context);
        cricketUpcomingMatchSummaryHandler.addListener(this);
        cricketUpcomingMatchSummaryHandler.requestCompledFootabllMatchStat(matchId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            View   view  = inflater.inflate(R.layout.fragment_completed_football_match_stats, container, false);
            initView(view);



        return view;
    }
    private void initView(View view) {
        try{
            rvFootballMatchStat = (RecyclerView) view.findViewById(R.id.rv_football_match_stat);
            rvFootballMatchStat.setHasFixedSize(true);
            completeFootballMatchStatAdapter = new CompleteFootballMatchStatAdapter(list,getContext());
            rvFootballMatchStat.setAdapter(completeFootballMatchStatAdapter);
            rvFootballMatchStat.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
            initErrorLayout(view);
            nocomments=(TextView)view.findViewById(R.id.no_comments);
        }catch (Exception e){e.printStackTrace();}


    }
    private void  showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void  hideProgressBar(){
        progressBar.setVisibility(View.GONE);
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
        final JSONArray dataArray= jsonObject.getJSONArray("data");

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                     for (int i = 0;i< dataArray.length();i++){

                     }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

}
