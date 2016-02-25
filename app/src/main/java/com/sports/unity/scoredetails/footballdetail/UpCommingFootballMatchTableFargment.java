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
import android.widget.Toast;

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

import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_LEAGUE_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchTableFargment extends Fragment implements UpCommingFootballMatchTableHandler.UpCommingFootballMatchTableContentListener{


    String toss = "";
    String matchName="";
    String date = "";
    String matchId ="";
    String leagueId = "";
    private UpCommingFootballMatchTableAdapter adapter;
    private List<UpCommngFootbalMatchTableDTO> list = new ArrayList<>();
    private RecyclerView recyclerView;
    public UpCommingFootballMatchTableFargment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        leagueId = i.getStringExtra(INTENT_KEY_LEAGUE_ID);
        date = i.getStringExtra(INTENT_KEY_DATE);
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
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_football_match_table);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UpCommingFootballMatchTableAdapter(list, getContext());
        recyclerView.setAdapter(adapter);
        initErrorLayout(view);

    }
    @Override
    public void handleContent(String object) {
        {


            try {
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
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        final JSONArray dataArray = jsonObject.getJSONArray("data");



        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        UpCommngFootbalMatchTableDTO upCommngFootbalMatchTableDTO;
                        for (int i = 0;i< dataArray.length();i++){
                            upCommngFootbalMatchTableDTO = new UpCommngFootbalMatchTableDTO();
                            JSONObject teamObject = dataArray.getJSONObject(i);
                            upCommngFootbalMatchTableDTO.setTvSerialNumber(i+1);
                            upCommngFootbalMatchTableDTO.setIvTeamProfileImage(teamObject.getString("flag_image"));
                            upCommngFootbalMatchTableDTO.setTvTeamName(teamObject.getString("team_name"));
                            upCommngFootbalMatchTableDTO.setTvD(teamObject.getString("team_name"));
                            upCommngFootbalMatchTableDTO.setTvL(teamObject.getString("team_name"));
                            upCommngFootbalMatchTableDTO.setTvP(teamObject.getString("team_name"));
                            upCommngFootbalMatchTableDTO.setTvW(teamObject.getString("team_name"));
                            upCommngFootbalMatchTableDTO.setTvPts(teamObject.getString("team_name"));
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

}
