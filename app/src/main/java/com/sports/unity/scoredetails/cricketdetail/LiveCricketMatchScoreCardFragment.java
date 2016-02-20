package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scoredetails.model.CricketScoreCard;
import com.sports.unity.scoredetails.model.Scorecard;
import com.sports.unity.scores.ScoreDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LiveCricketMatchScoreCardFragment extends Fragment implements LivedMatchScoreCardHandler.LiveMatchContentListener{


    public LiveCricketMatchScoreCardFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String matchId =  getActivity().getIntent().getStringExtra("matchId");
        matchId = "nzaus_2016_test_02";
        LivedMatchScoreCardHandler cricketPlayerbioHandler = LivedMatchScoreCardHandler.getInstance(context);
        cricketPlayerbioHandler.addListener(this);
        cricketPlayerbioHandler.requestMatchScoreCard(matchId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cricket_live_match_summery, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {

        initErrorLayout(view);

    }

    @Override
    public void handleContent(CricketScoreCard scorecard) {
        {
            try {
                boolean success = scorecard.isSuccess();

                if( success ) {

                    renderDisplay(scorecard);

                } else {
                    Toast.makeText(getActivity(), R.string.match_not_exist, Toast.LENGTH_SHORT).show();
                    showErrorLayout(getView());
                }
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getActivity(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void initErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);

    }

    private void showErrorLayout(View view) {

        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);

    }

    private void renderDisplay(CricketScoreCard scoreCard) throws JSONException {

        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {




                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

}
