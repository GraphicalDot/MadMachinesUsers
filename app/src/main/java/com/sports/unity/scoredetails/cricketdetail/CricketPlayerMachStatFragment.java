package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.player.view.PlayerScoreCardDTO;
import com.sports.unity.player.view.PlayerScorecardAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 15/2/16.
 */
public class CricketPlayerMachStatFragment extends Fragment  implements CricketPlayerMatchStatHandler.CricketPlayerMatchStatContentListener{
    private GridLayout glBattingPerformanceSummery;
    private GridLayout glBowlingPerformanceSummary;
    private ImageView battingImageView;
    private ImageView bowlingImageView;
    private CricketPlayerMatchBattingStatAdapter cricketPlayerMatchBattingStatAdapter;
    private List<CricketPlayerMatchStatDTO> playerMatchStatDTOList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    public CricketPlayerMachStatFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String playerId =  getActivity().getIntent().getStringExtra("playerId");
        playerId = "6f65e8cd45ae14c916cf2c1c69b6102c";
        CricketPlayerMatchStatHandler cricketPlayerMatchStatHandler = CricketPlayerMatchStatHandler.getInstance(context);
        cricketPlayerMatchStatHandler.addListener(this);
        cricketPlayerMatchStatHandler.requestData(playerId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_players_cricket_stat_batting, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {
        glBattingPerformanceSummery = (GridLayout) view.findViewById(R.id.gl_batting_performance_summary);
        glBowlingPerformanceSummary = (GridLayout) view.findViewById(R.id.gl_bowling_performance_summary);
        battingImageView = (ImageView) view.findViewById(R.id.iv_down);
        bowlingImageView = (ImageView) view.findViewById(R.id.iv_down_second);
        battingImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(glBattingPerformanceSummery.getVisibility()== View.GONE){
                    glBattingPerformanceSummery.setVisibility(View.VISIBLE);
                } else {
                    glBattingPerformanceSummery.setVisibility(View.GONE);
                }
            }
        });
        bowlingImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(glBowlingPerformanceSummary.getVisibility()== View.GONE){
                    glBowlingPerformanceSummary.setVisibility(View.VISIBLE);
                } else {
                    glBowlingPerformanceSummary.setVisibility(View.GONE);
                }
            }
        });

        cricketPlayerMatchBattingStatAdapter = new CricketPlayerMatchBattingStatAdapter(playerMatchStatDTOList);
        mRecyclerView.setAdapter(cricketPlayerMatchBattingStatAdapter);
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
        glBattingPerformanceSummery.setVisibility(View.VISIBLE);
        glBowlingPerformanceSummary.setVisibility(View.VISIBLE);
        final JSONObject data = (JSONObject) jsonObject.get("data");
       // final JSONObject playerInfo = (JSONObject) data.get("info");
        final JSONArray playerStatsArray = (JSONArray) data.get("stats");
        PlayerCricketBioDataActivity activity = (PlayerCricketBioDataActivity) getActivity();
        if (activity != null) {
            activity.setProfileInfo(data);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < playerStatsArray.length(); i++) {

                            JSONObject battingJsonObject= (JSONObject) playerStatsArray.get(i);
                            JSONArray battingArray = battingJsonObject.getJSONArray("batting") ;
                            if(battingArray != null){
                                CricketPlayerMatchStatDTO cricketPlayerMatchStatDTO = null;
                               for(int j = 0; j < battingArray.length(); j++){
                                   JSONObject batting = (JSONObject) battingArray.get(i);
                                   cricketPlayerMatchStatDTO = new CricketPlayerMatchStatDTO();
                                   if(batting != null) {
                                       cricketPlayerMatchStatDTO.setTitles(batting.getString("matches"));
                                       cricketPlayerMatchStatDTO.setTestsMatch(batting.getString("runs"));
                                       cricketPlayerMatchStatDTO.setOdis(batting.getString("matches"));
                                       cricketPlayerMatchStatDTO.setT20s(batting.getString("runs"));

                                   }

                                   playerMatchStatDTOList.add(cricketPlayerMatchStatDTO);
                               }
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
