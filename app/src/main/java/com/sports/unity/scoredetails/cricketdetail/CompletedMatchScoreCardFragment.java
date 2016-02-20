package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scoredetails.model.CricketScoreCard;
import com.sports.unity.scores.ScoreDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class CompletedMatchScoreCardFragment extends Fragment implements CompletedMatchScoreCardHandler.CompletedMatchContentListener{


    public CompletedMatchScoreCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String matchId =  getActivity().getIntent().getStringExtra("matchId");
        matchId = "rsaeng_2015_t20_01";
        CompletedMatchScoreCardHandler completedMatchScoreCardHandler = CompletedMatchScoreCardHandler.getInstance(context);
        completedMatchScoreCardHandler.addListener(this);
        completedMatchScoreCardHandler.requestCompletdMatchScoreCard(matchId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_completed_match_score_card, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {

        initErrorLayout(view);

    }

    @Override
    public void handleContent(JSONObject object) {
        {
            try {
               boolean success = object.getBoolean("success");
                boolean error = object.getBoolean("error");

                if( success ) {

                    renderDisplay(object);

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

    private void renderDisplay(final JSONObject scoreCard) throws JSONException {

        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i("run: ",scoreCard.toString());



                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

}
