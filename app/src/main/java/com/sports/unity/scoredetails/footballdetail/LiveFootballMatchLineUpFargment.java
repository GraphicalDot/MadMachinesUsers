package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scores.ScoreDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class LiveFootballMatchLineUpFargment extends Fragment implements LiveFootballMatchLineUpHandler.LiveMatchContentListener {

    private ProgressBar progressBar;
    String toss = "";
    String matchName="";
    String date = "";
    private String matchId;
    private TextView tvCaptainFirst;
    private TextView tvCaptainSecond;
    private TextView tvCarlesPayol;
    private TextView tvlineup;
    private GridLayout rcLineup;
    private TextView tvsubstitutes;
    private GridLayout gvSubstitutes;


    public LiveFootballMatchLineUpFargment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        String matchId =  i.getStringExtra(INTENT_KEY_ID);
        matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
        toss = i.getStringExtra(INTENT_KEY_TOSS);
        date = i.getStringExtra(INTENT_KEY_DATE);
        LiveFootballMatchLineUpHandler liveFootballMatchLineUpHandler = LiveFootballMatchLineUpHandler.getInstance(context);
        liveFootballMatchLineUpHandler.addListener(this);
        liveFootballMatchLineUpHandler.requestLiveMatchLineUp(matchId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_football_live_match_lineups, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        initErrorLayout(view);
        tvCaptainFirst=(TextView)view.findViewById(R.id.tv_team_first_captain);
        tvCaptainSecond=(TextView)view.findViewById(R.id.tv_team_second_captain);
        tvlineup=(TextView)view.findViewById(R.id.tv_line_up);
        tvsubstitutes=(TextView)view.findViewById(R.id.tv_substitutes);
        rcLineup = (GridLayout) view.findViewById(R.id.gv_lineup);
        gvSubstitutes = (GridLayout) view.findViewById(R.id.gv_substitutes);


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
            hideProgressBar();

            try {
                JSONObject jsonObject = new JSONObject(object);
                JSONObject data = jsonObject.getJSONObject("data");
                boolean success = data.getBoolean("success");
                boolean error = data.getBoolean("error");

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
        hideProgressBar();
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        final JSONObject dataObject = jsonObject.getJSONObject("data");
        final JSONArray subsArray = dataObject.getJSONArray("subs");
        final JSONArray teamsObjectArray = dataObject.getJSONArray("teams");
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tvCaptainFirst.setText("NA");
                        tvCaptainSecond.setText("NA");
                        LinearLayout linearLayout = null;
                        TextView tvPlayerName = null;
                        TextView tvPosition = null;
                        for(int i = 0; i<subsArray.length();i++){
                            JSONObject subsObject = subsArray.getJSONObject(i);
                            linearLayout = new LinearLayout(getContext());
                            tvPlayerName = new TextView(getContext());
                            tvPosition = new TextView(getContext());
                            tvPlayerName.setText(subsObject.getString("player_name"));
                            tvPosition.setText(subsObject.getString("position"));
                            linearLayout.addView(tvPosition);
                            linearLayout.addView(tvPlayerName);

                            gvSubstitutes.addView(linearLayout);
                        }

                        for(int i = 0; i<teamsObjectArray.length();i++){
                            JSONObject teamsObject = teamsObjectArray.getJSONObject(i);
                            linearLayout = new LinearLayout(getContext());
                            tvPlayerName = new TextView(getContext());
                            tvPosition = new TextView(getContext());
                            tvPlayerName.setText(teamsObject.getString("name"));
                            tvPosition.setText(teamsObject.getString("position"));
                            linearLayout.addView(tvPosition);
                            linearLayout.addView(tvPlayerName);
                            rcLineup.addView(linearLayout);
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
