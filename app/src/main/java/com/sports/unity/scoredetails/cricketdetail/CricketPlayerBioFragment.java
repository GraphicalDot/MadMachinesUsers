package com.sports.unity.scoredetails.cricketdetail;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by madmachines on 15/2/16.
 */
public class CricketPlayerBioFragment extends Fragment implements CricketPlayerbioHandler.CricketPlayerbioContentListener {

    private TextView tvPlayerbirthOfPlace;
    private TextView tvPlayerDateOfBirth;
    private TextView tvPlayerbattingStyle;
    private TextView tvPlayerBowingStyle;
    private TextView tvPlayerMajorTeam;
    public CricketPlayerBioFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String playerId =  getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
       /* playerId = "6f65e8cd45ae14c916cf2c1c69b6102c";*/
        CricketPlayerbioHandler cricketPlayerbioHandler = CricketPlayerbioHandler.getInstance(context);
        cricketPlayerbioHandler.addListener(this);
        cricketPlayerbioHandler.requestData(playerId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player_cricket_bio, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {
        tvPlayerDateOfBirth = (TextView) view.findViewById(R.id.tv_player_date_of_birth);
        tvPlayerbattingStyle = (TextView) view.findViewById(R.id.tv_player_batting_style);
        tvPlayerBowingStyle = (TextView) view.findViewById(R.id.tv_player_bowing_style);
        tvPlayerMajorTeam = (TextView) view.findViewById(R.id.tv_player_major_team);
        tvPlayerbirthOfPlace = (TextView) view.findViewById(R.id.tv_player_birth_of_place);
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
                Toast.makeText(getActivity(), R.string.player_details_not_exists, Toast.LENGTH_SHORT).show();
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

    private void renderDisplay(JSONObject jsonObject) throws JSONException {
        final JSONObject data = (JSONObject) jsonObject.get("data");
        final JSONObject playerInfo = (JSONObject) data.get("info");
        PlayerCricketBioDataActivity activity = (PlayerCricketBioDataActivity) getActivity();
        if (activity != null) {
            activity.setProfileInfo(data);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                      if (!playerInfo.isNull("Born")) {
                            tvPlayerDateOfBirth.setText(playerInfo.getString("Born"));
                        }
                        if (!playerInfo.isNull("Batting style")) {
                            tvPlayerbattingStyle.setText(playerInfo.getString("Batting style"));
                        }
                        if (!playerInfo.isNull("Bowling style")) {
                            tvPlayerBowingStyle.setText(playerInfo.getString("Bowling style"));
                        }
                        if (!playerInfo.isNull("Place of birth")) {
                            tvPlayerbirthOfPlace.setText(playerInfo.getString("Place of birth"));
                        }

                        if (!data.isNull("teams_played_for")) {
                            JSONArray array = data.getJSONArray("teams_played_for");
                            for (int i = 0; i < array.length(); i++) {

                                tvPlayerMajorTeam.setText(array.get(i).toString()+"\n");
                            }

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
