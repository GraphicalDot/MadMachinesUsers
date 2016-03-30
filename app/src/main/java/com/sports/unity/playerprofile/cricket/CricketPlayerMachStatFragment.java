package com.sports.unity.playerprofile.cricket;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by madmachines on 15/2/16.
 */
public class CricketPlayerMachStatFragment extends Fragment implements CricketPlayerMatchStatHandler.CricketPlayerMatchStatContentListener {
    Map<String, String> battingTestsmatchMap = new HashMap<String, String>();
    Map<String, String> battingOdisMap = new HashMap<String, String>();
    Map<String, String> battingT20sMap = new HashMap<String, String>();
    Map<String, String> battingIPLMap = new HashMap<String, String>();
    Map<String, String> bowlingTestsmatchMap = new HashMap<String, String>();
    Map<String, String> bowlingOdisMap = new HashMap<String, String>();
    Map<String, String> bowlingT20sMap = new HashMap<String, String>();
    Map<String, String> bowlingIPLMap = new HashMap<String, String>();
    private RecyclerView rcBattingPerformanceSummery;
    private RecyclerView rcBowlingPerformanceSummary;
    private ImageView battingImageView;
    private ImageView bowlingImageView;
    private CricketPlayerMatchBattingStatAdapter cricketPlayerMatchBattingStatAdapter;
    private CricketPlayerMatchBowlingStatAdapter cricketPlayerMatchBowlingStatAdapter;
    private List<CricketPlayerMatchStatDTO> playerMatchBattingStatDTOList = new ArrayList<>();
    private List<CricketPlayerMatchStatDTO> playerMatchBowlingStatDTOList = new ArrayList<>();
    private CricketPlayerMatchStatHandler cricketPlayerMatchStatHandler;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayoutBatting;
    private RelativeLayout relativeLayoutBolling;
    private LinearLayout linearLayoutBatting;
    private LinearLayout linearLayoutBolling;
    private String playerId;
    private Context context;

    public CricketPlayerMachStatFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         playerId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
       /* playerId = "4429";*/
         this.context = context;
        cricketPlayerMatchStatHandler = CricketPlayerMatchStatHandler.getInstance(context);
        cricketPlayerMatchStatHandler.addListener(this);
        cricketPlayerMatchStatHandler.requestData(playerId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_players_cricket_stat_batting, container, false);
        initView(view);
        initProgress(view);
        return view;
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
    private void initView(View view) {
        rcBattingPerformanceSummery = (RecyclerView) view.findViewById(R.id.rc_batting_performance_summary);
        rcBattingPerformanceSummery.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, true));
        rcBattingPerformanceSummery.setNestedScrollingEnabled(false);
        rcBattingPerformanceSummery.setHasFixedSize(false);
        rcBowlingPerformanceSummary = (RecyclerView) view.findViewById(R.id.rc_bowling_performance_summary);
        rcBowlingPerformanceSummary.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, true));
        rcBowlingPerformanceSummary.setNestedScrollingEnabled(false);
        rcBowlingPerformanceSummary.setHasFixedSize(false);
        battingImageView = (ImageView) view.findViewById(R.id.iv_down);
        bowlingImageView = (ImageView) view.findViewById(R.id.iv_down_second);
        relativeLayoutBatting = (RelativeLayout) view.findViewById(R.id.ll_batting);
        relativeLayoutBolling = (RelativeLayout) view.findViewById(R.id.ll_bolling);
        linearLayoutBatting = (LinearLayout) view.findViewById(R.id.ll_batting_layout);
        linearLayoutBolling = (LinearLayout) view.findViewById(R.id.ll_bolling_layout);
        final View battingRow = view.findViewById(R.id.prl_batting);
        final View bowlingRow = view.findViewById(R.id.prl_bowling);

        relativeLayoutBatting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (rcBattingPerformanceSummery.getVisibility() == GONE) {
                    rcBattingPerformanceSummery.setVisibility(VISIBLE);
                    battingRow.setVisibility(VISIBLE);
                    battingImageView.setImageResource(R.drawable.ic_down_arrow_gray);
                } else {
                    rcBattingPerformanceSummery.setVisibility(GONE);
                    battingRow.setVisibility(GONE);
                    battingImageView.setImageResource(R.drawable.ic_up_arrow_gray);
                }
            }
        });
        relativeLayoutBolling.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (rcBowlingPerformanceSummary.getVisibility() == GONE) {
                    rcBowlingPerformanceSummary.setVisibility(VISIBLE);
                    bowlingRow.setVisibility(VISIBLE);
                    bowlingImageView.setImageResource(R.drawable.ic_down_arrow_gray);
                } else {
                    rcBowlingPerformanceSummary.setVisibility(GONE);
                    bowlingRow.setVisibility(GONE);
                    bowlingImageView.setImageResource(R.drawable.ic_up_arrow_gray);
                }
            }
        });

        cricketPlayerMatchBattingStatAdapter = new CricketPlayerMatchBattingStatAdapter(playerMatchBattingStatDTOList);
        rcBattingPerformanceSummery.setAdapter(cricketPlayerMatchBattingStatAdapter);
        cricketPlayerMatchBowlingStatAdapter = new CricketPlayerMatchBowlingStatAdapter(playerMatchBowlingStatDTOList);
        rcBowlingPerformanceSummary.setAdapter(cricketPlayerMatchBowlingStatAdapter);
        initErrorLayout(view);

    }

    @Override
    public void handleContent(String content) {
        try {
            showProgress();
            JSONObject jsonObject = new JSONObject(content);

            boolean success = jsonObject.getBoolean("success");
            boolean error = jsonObject.getBoolean("error");

            if (success) {
                linearLayoutBatting.setVisibility(View.VISIBLE);
                linearLayoutBolling.setVisibility(View.VISIBLE);
                renderDisplay(jsonObject);

            } else {
                linearLayoutBatting.setVisibility(View.GONE);
                linearLayoutBolling.setVisibility(View.GONE);
                showErrorLayout(getView());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            linearLayoutBatting.setVisibility(View.GONE);
            linearLayoutBolling.setVisibility(View.GONE);
            showErrorLayout(getView());
        }
    }

    private void initErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(GONE);

    }

    private void showErrorLayout(View view) {

        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(VISIBLE);

    }

    private void renderDisplay(JSONObject jsonObject) throws JSONException {
        playerMatchBattingStatDTOList.clear();
        playerMatchBowlingStatDTOList.clear();
        battingTestsmatchMap.clear();
        battingOdisMap.clear();
        battingT20sMap .clear();
        battingIPLMap .clear();
        bowlingTestsmatchMap .clear();
        bowlingOdisMap .clear();
        bowlingT20sMap .clear();
        bowlingIPLMap .clear();
        rcBattingPerformanceSummery.setVisibility(VISIBLE);
        rcBowlingPerformanceSummary.setVisibility(VISIBLE);
        final JSONArray dataArray = jsonObject.getJSONArray("data");
        Log.i("Msg", "Array length" + dataArray.length());
        final JSONObject dataObject = (JSONObject) dataArray.get(0);
        final JSONObject playerStatistics = dataObject.getJSONObject("statistics");
        final Iterator<String> keys = playerStatistics.keys();

        PlayerCricketBioDataActivity activity = (PlayerCricketBioDataActivity) getActivity();
        hideProgress();
        if (activity != null) {
            activity.setProfileInfo(dataObject);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (keys.hasNext()){
                            String key = keys.next();
                            JSONObject battingJsonObject = playerStatistics.getJSONObject(key);
                            JSONObject battingArray = battingJsonObject.getJSONObject("batting");
                            JSONObject bowlingArray = battingJsonObject.getJSONObject("bowling");
                            if (battingArray != null) battingStatProcess(key, battingArray);
                            if (bowlingArray != null) bowlingStatProcess(key, bowlingArray);
                        }

                        CricketPlayerMatchStatDTO cricketPlayerMatchStatDTO = null;
                        Set<String> keySet = battingTestsmatchMap.keySet();
                        for (String mapKey : keySet) {
                            String title = null;
                            title = mapKey.toUpperCase();
                            title = title.replaceAll("_", " ");
                            cricketPlayerMatchStatDTO = new CricketPlayerMatchStatDTO();
                            cricketPlayerMatchStatDTO.setTitles(title);
                            cricketPlayerMatchStatDTO.setTestsMatch(battingTestsmatchMap.get(mapKey));
                            cricketPlayerMatchStatDTO.setOdis(battingOdisMap.get(mapKey));
                            cricketPlayerMatchStatDTO.setT20s(battingT20sMap.get(mapKey));
                            cricketPlayerMatchStatDTO.setIpl(battingIPLMap.get(mapKey));
                            playerMatchBattingStatDTOList.add(cricketPlayerMatchStatDTO);


                        }
                         keySet = bowlingTestsmatchMap.keySet();
                        for (String bowllingKey : keySet) {
                            String title = null;
                            title = bowllingKey.toUpperCase();
                            title = title.replaceAll("_", " ");
                            cricketPlayerMatchStatDTO = new CricketPlayerMatchStatDTO();
                            cricketPlayerMatchStatDTO.setTitles(title);
                            cricketPlayerMatchStatDTO.setTestsMatch(bowlingTestsmatchMap.get(bowllingKey));
                            cricketPlayerMatchStatDTO.setOdis(bowlingOdisMap.get(bowllingKey));
                            cricketPlayerMatchStatDTO.setT20s(bowlingT20sMap.get(bowllingKey));
                            cricketPlayerMatchStatDTO.setIpl(battingIPLMap.get(bowllingKey));
                            playerMatchBowlingStatDTOList.add(cricketPlayerMatchStatDTO);


                        }
                        cricketPlayerMatchBowlingStatAdapter.notifyDataSetChanged();
                        cricketPlayerMatchBattingStatAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

    private void battingStatProcess(String key, JSONObject batting) throws JSONException {

            if (batting != null) {
                if ("TEST".equalsIgnoreCase(key)) {
                    battingTestsmatchMap.put("innings", batting.getString("innings"));
                    battingTestsmatchMap.put("runs", batting.getString("runs"));
                    battingTestsmatchMap.put("matches", batting.getString("matches"));
                    //battingTestsmatchMap.put("average", batting.getString("average"));
                    //battingTestsmatchMap.put("strike_rate", batting.getString("strike_rate"));
                    //battingTestsmatchMap.put("highest", batting.getString("highest"));
                    battingTestsmatchMap.put("100s", batting.getString("100s"));
                    battingTestsmatchMap.put("50s", batting.getString("50s"));
                    //battingTestsmatchMap.put("not_out", batting.getString("not_out"));
                } else if ("ODI".equalsIgnoreCase(key)) {

                    battingOdisMap.put("innings", batting.getString("innings"));
                    battingOdisMap.put("runs", batting.getString("runs"));
                    battingOdisMap.put("matches", batting.getString("matches"));
                   // battingOdisMap.put("average", batting.getString("average"));
                    //battingOdisMap.put("strike_rate", batting.getString("strike_rate"));
                    //battingOdisMap.put("highest", batting.getString("highest"));
                    battingOdisMap.put("100s", batting.getString("100s"));
                    battingOdisMap.put("50s", batting.getString("50s"));
                  //  battingOdisMap.put("not_out", batting.getString("not_out"));
                } else if ("T20 Int".equalsIgnoreCase(key)) {

                    battingT20sMap.put("innings", batting.getString("innings"));
                    battingT20sMap.put("runs", batting.getString("runs"));
                    battingT20sMap.put("matches", batting.getString("matches"));
                    //battingT20sMap.put("average", batting.getString("average"));
                  //  battingT20sMap.put("strike_rate", batting.getString("strike_rate"));
                   // battingT20sMap.put("highest", batting.getString("highest"));
                    battingT20sMap.put("100s", batting.getString("100s"));
                    battingT20sMap.put("50s", batting.getString("50s"));
                   // battingT20sMap.put("not_out", batting.getString("not_out"));
                } else if ("IPL".equalsIgnoreCase(key)) {

                    battingIPLMap.put("innings", batting.getString("innings"));
                    battingIPLMap.put("runs", batting.getString("runs"));
                    battingIPLMap.put("matches", batting.getString("matches"));
                    //battingIPLMap.put("average", batting.getString("average"));
                    //battingIPLMap.put("strike_rate", batting.getString("strike_rate"));
                    //battingIPLMap.put("highest", batting.getString("highest"));
                    battingIPLMap.put("100s", batting.getString("100s"));
                    battingIPLMap.put("50s", batting.getString("50s"));
                    //battingIPLMap.put("not_out", batting.getString("not_out"));
                }



        }

    }

    private void bowlingStatProcess(String key, JSONObject bowling) throws JSONException {

            if (bowling != null) {
                if ("TEST".equalsIgnoreCase(key)) {

                   // bowlingTestsmatchMap.put("runs", bowling.getString("runs"));
                    bowlingTestsmatchMap.put("matches", bowling.getString("matches"));
                    bowlingTestsmatchMap.put("innings", bowling.getString("innings"));
                    //bowlingTestsmatchMap.put("average", bowling.getString("average"));
                   // bowlingTestsmatchMap.put("balls", bowling.getString("balls"));
                    //bowlingTestsmatchMap.put("best", bowling.getString("best"));
                    bowlingTestsmatchMap.put("wickets", bowling.getString("wickets"));
                    bowlingTestsmatchMap.put("economy", bowling.getString("economy"));
                    bowlingTestsmatchMap.put("overs", bowling.getString("overs"));
                } else if ("ODI".equalsIgnoreCase(key)) {

                   // bowlingOdisMap.put("runs", bowling.getString("runs"));
                    bowlingOdisMap.put("matches", bowling.getString("matches"));
                    bowlingOdisMap.put("innings", bowling.getString("innings"));
                    /*bowlingOdisMap.put("average", bowling.getString("average"));
                    bowlingOdisMap.put("balls", bowling.getString("balls"));
                    bowlingOdisMap.put("best", bowling.getString("best"));*/
                    bowlingOdisMap.put("wickets", bowling.getString("wickets"));
                    bowlingOdisMap.put("economy", bowling.getString("economy"));
                    bowlingOdisMap.put("overs", bowling.getString("overs"));

                } else if ("T20 Int".equalsIgnoreCase(key)) {

                   // bowlingT20sMap.put("runs", bowling.getString("runs"));
                    bowlingT20sMap.put("matches", bowling.getString("matches"));
                    bowlingT20sMap.put("innings", bowling.getString("innings"));
                    /*bowlingT20sMap.put("average", bowling.getString("average"));
                    bowlingT20sMap.put("balls", bowling.getString("balls"));
                    bowlingT20sMap.put("best", bowling.getString("best"));*/
                    bowlingT20sMap.put("wickets", bowling.getString("wickets"));
                    bowlingT20sMap.put("economy", bowling.getString("economy"));
                    bowlingT20sMap.put("overs", bowling.getString("overs"));
                } else if ("IPL".equalsIgnoreCase(key)) {

                    //bowlingIPLMap.put("runs", bowling.getString("runs"));
                    bowlingIPLMap.put("matches", bowling.getString("matches"));
                    bowlingIPLMap.put("innings", bowling.getString("innings"));
                   /* bowlingIPLMap.put("average", bowling.getString("average"));
                    bowlingIPLMap.put("balls", bowling.getString("balls"));
                    bowlingIPLMap.put("best", bowling.getString("best"));*/
                    bowlingIPLMap.put("wickets", bowling.getString("wickets"));
                    bowlingIPLMap.put("economy", bowling.getString("economy"));
                    bowlingIPLMap.put("overs", bowling.getString("overs"));

                }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(cricketPlayerMatchStatHandler != null){
            cricketPlayerMatchStatHandler.addListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress();
        if(cricketPlayerMatchStatHandler != null){
            cricketPlayerMatchStatHandler.addListener(this);

        }else {
            cricketPlayerMatchStatHandler= CricketPlayerMatchStatHandler.getInstance(context);
        }
        cricketPlayerMatchStatHandler.requestData(playerId);
    }
}
