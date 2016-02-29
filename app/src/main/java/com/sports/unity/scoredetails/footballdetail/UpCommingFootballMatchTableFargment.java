package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommingFootballMatchTableAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommngFootbalMatchTableDTO;
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
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM1_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM2_NAME;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchTableFargment extends Fragment implements UpCommingFootballMatchTableHandler.UpCommingFootballMatchTableContentListener{




    private String date = "";
    private String matchId ="";
    private String leagueId = "";
    private String team1;
    private String team2;
    private UpCommingFootballMatchTableAdapter adapter;
    private List<UpCommngFootbalMatchTableDTO> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvMatchDate;
    public UpCommingFootballMatchTableFargment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        matchId = i.getStringExtra(INTENT_KEY_ID);
        leagueId = i.getStringExtra(Constants.INTENT_KEY_LEAGUE_ID);
        date = i.getStringExtra(INTENT_KEY_DATE);
        team1 = i.getStringExtra(INTENT_KEY_TEAM1_NAME);
        team2 = i.getStringExtra(INTENT_KEY_TEAM2_NAME);
        UpCommingFootballMatchTableHandler upCommingFootballMatchTableHandler = UpCommingFootballMatchTableHandler.getInstance(context);
        upCommingFootballMatchTableHandler.addListener(this);
        upCommingFootballMatchTableHandler.requestUpcommingMatchTableContent(leagueId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_football_match_table, container, false);
        initView(view);

        return view;
    }
    private void initView(View view) {
        tvMatchDate = (TextView) view.findViewById(R.id.tv_match_date);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_football_match_table);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext() ,VERTICAL, false));
        adapter = new UpCommingFootballMatchTableAdapter(list, getContext(),team1,team2);
        recyclerView.setAdapter(adapter);
        initErrorLayout(view);

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
        final JSONArray dataArray = jsonObject.getJSONArray("data");
        hideProgressBar();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        UpCommngFootbalMatchTableDTO upCommngFootbalMatchTableDTO = null;
                        for (int i = 0;i< dataArray.length();i++){
                            upCommngFootbalMatchTableDTO = new UpCommngFootbalMatchTableDTO();
                            JSONObject teamObject = dataArray.getJSONObject(i);
                            upCommngFootbalMatchTableDTO.setTvSerialNumber(String.valueOf(i + 1));
                            if(!teamObject.isNull("stand_season"))
                            tvMatchDate.setText(teamObject.getString("stand_season").replace("V","-"));
                            if(!teamObject.isNull("flag_image"))
                            upCommngFootbalMatchTableDTO.setIvTeamProfileImage(teamObject.getString("flag_image"));
                            if(!teamObject.isNull("team_name"))
                            {
                                upCommngFootbalMatchTableDTO.setTvTeamName(teamObject.getString("team_name"));

     }

                            if(!teamObject.isNull("games_drawn"))
                            upCommngFootbalMatchTableDTO.setTvD(teamObject.getString("games_drawn"));
                            if(!teamObject.isNull("games_lost"))
                            upCommngFootbalMatchTableDTO.setTvL(teamObject.getString("games_lost"));
                            if(!teamObject.isNull("team_points"))
                            upCommngFootbalMatchTableDTO.setTvP(teamObject.getString("team_points"));
                            if(!teamObject.isNull("games_won"))
                                upCommngFootbalMatchTableDTO.setTvW(teamObject.getString("games_won"));
                            if(!teamObject.isNull("team_points"))
                            upCommngFootbalMatchTableDTO.setTvPts(teamObject.getString("team_points"));




                            list.add(upCommngFootbalMatchTableDTO);
                        }
                        adapter.notifyDataSetChanged();
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
