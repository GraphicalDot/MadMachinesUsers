package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madmachines on 23/2/16.
 */
public class CompletedFootballMatchStatFragment extends Fragment implements CompletedFootballMatchStatHandler.CompletedFootballMatchContentListener{

    private ProgressBar progressBar;
    private CompletedFootballMatchStatHandler completedFootballMatchStatHandler;
    private String matchId;
    public CompletedFootballMatchStatFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        matchId =  getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        completedFootballMatchStatHandler = CompletedFootballMatchStatHandler.getInstance(context);
        completedFootballMatchStatHandler.addListener(this);
        completedFootballMatchStatHandler.requestCompledFootabllMAtchStat(matchId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_completed_football_match_stats, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {

        initProgress(view);
        showProgress();
        initErrorLayout(view);

    }

    @Override
    public void handleContent(String content) {
        try {

            JSONObject jsonObject = new JSONObject(content);

            boolean success = jsonObject.getBoolean("success");
            boolean error = jsonObject.getBoolean("error");

            if( success ) {

                renderDisplay(jsonObject);

            } else {
                showErrorLayout(getView());
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getActivity(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
            showErrorLayout(getView());
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
    private void initProgress(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
    }
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

    }
    public void hideProgress() {


        progressBar.setVisibility(View.GONE);

    }
    private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        hideProgress();
        final JSONObject data = (JSONObject) jsonObject.get("data");

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
    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
