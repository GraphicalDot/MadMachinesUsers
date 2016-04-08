package com.sports.unity.playerprofile.cricket;

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
import com.sports.unity.scoredetails.cricketdetail.CompletedMatchScoreCardHandler;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madmachines on 15/2/16.
 */
public class CricketPlayerBioFragment extends Fragment {

    private TextView tvPlayerbirthOfPlace;
    private TextView tvPlayerDateOfBirth;
    private TextView tvPlayerbattingStyle;
    private TextView tvPlayerBowingStyle;
    private TextView tvPlayerMajorTeam;
    private ProgressBar progressBar;
    private LinearLayout linearLayoutBio;
    private CricketPlayerbioHandler cricketPlayerbioHandler;
    private String playerId;
    private Context context;

    public CricketPlayerBioFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
//        playerId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
//        cricketPlayerbioHandler = CricketPlayerbioHandler.getInstance(context);
//        cricketPlayerbioHandler.addListener(this);
//        cricketPlayerbioHandler.requestData(playerId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player_cricket_bio, container, false);
        initView(view);
        populateData(view);
        return view;
    }

    private void populateData(View view) {
        if (getArguments().getString("content") != null) {
            try {
                JSONObject jsonObject = new JSONObject(getArguments().getString("content"));
                renderDisplay(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                showErrorLayout(view);
            }
        } else {
            showErrorLayout(view);
        }
    }

    private void initView(View view) {
        linearLayoutBio = (LinearLayout) view.findViewById(R.id.ll_bio_layout);
        tvPlayerDateOfBirth = (TextView) view.findViewById(R.id.tv_player_date_of_birth);
        tvPlayerbattingStyle = (TextView) view.findViewById(R.id.tv_player_batting_style);
        tvPlayerBowingStyle = (TextView) view.findViewById(R.id.tv_player_bowing_style);
        tvPlayerMajorTeam = (TextView) view.findViewById(R.id.tv_player_major_team);
        tvPlayerbirthOfPlace = (TextView) view.findViewById(R.id.tv_player_birth_of_place);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setIndeterminate(true);
//        initProgress(view);
        initErrorLayout(view);

    }

    private void initErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);

    }

    private void showErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);
        linearLayoutBio.setVisibility(View.GONE);
    }

    public void renderDisplay(JSONObject jsonObject) {
        linearLayoutBio.setVisibility(View.VISIBLE);
        try {
            JSONArray dataArray = jsonObject.getJSONArray("data");
            JSONObject dataObject = dataArray.getJSONObject(0);
            JSONObject playerInfo = dataObject.getJSONObject("info");

            tvPlayerDateOfBirth.setText(DateUtil.getFormattedDateDDMMYYYY(playerInfo.getString("born")));
            tvPlayerbattingStyle.setText(playerInfo.getString("batting_style"));
            tvPlayerBowingStyle.setText(playerInfo.getString("bowling_style"));
            tvPlayerbirthOfPlace.setText(playerInfo.getString("birth_place"));
            tvPlayerMajorTeam.setText(dataObject.getString("team"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
