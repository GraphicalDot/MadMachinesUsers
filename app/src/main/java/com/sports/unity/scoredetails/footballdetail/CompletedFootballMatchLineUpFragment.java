package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.sports.unity.R;
import com.sports.unity.scoredetails.cricketdetail.CompletedMatchScoreCardHandler;
import com.sports.unity.scoredetails.cricketdetail.CricketUpcomingMatchSummaryHandler;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballLineUpAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballLineUpDTO;
import com.sports.unity.scores.ScoreDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class CompletedFootballMatchLineUpFragment extends Fragment implements CompletedFootballMatchLineUpHandler.CompletedMatchContentListener{


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
    private RecyclerView recyclerView;
    private CompleteFootballLineUpAdapter completeFootballLineUpAdapter;
    private List<CompleteFootballLineUpDTO> list = new ArrayList<>();

    public CompletedFootballMatchLineUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        matchId =  i.getStringExtra(INTENT_KEY_ID);
        matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
        toss = i.getStringExtra(INTENT_KEY_TOSS);
        date = i.getStringExtra(INTENT_KEY_DATE);
        CompletedFootballMatchLineUpHandler cricketUpcomingMatchSummaryHandler = CompletedFootballMatchLineUpHandler.getInstance(context);
        cricketUpcomingMatchSummaryHandler.addListener(this);
        cricketUpcomingMatchSummaryHandler.requestCompletdMatchLineUps(matchId);

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
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_substitutes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        completeFootballLineUpAdapter = new CompleteFootballLineUpAdapter(list,getContext());
        recyclerView.setAdapter(completeFootballLineUpAdapter);





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
            showProgressBar();

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
        hideProgressBar();
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        final JSONObject dataObject = jsonObject.getJSONObject("data");
        final  JSONArray subsArray = dataObject.getJSONArray("subs");
        final JSONArray teamsObjectArray = dataObject.getJSONArray("teams");
        final JSONArray substitutionsArray = dataObject.getJSONArray("substitutions");
        final JSONArray matchEventsArray = dataObject.getJSONArray("match_events");

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TextDrawable drawable = null;
                        tvCaptainFirst.setText("NA");
                        tvCaptainSecond.setText("NA");
                        View view;


                        CompleteFootballLineUpDTO completeFootballLineUpDTO;
                        for(int i = 0; i<subsArray.length();i++){
                            JSONObject subsObject = subsArray.getJSONObject(i);
                            completeFootballLineUpDTO = new CompleteFootballLineUpDTO();
                            completeFootballLineUpDTO.setPlayerName(subsObject.getString("player_name"));
                            completeFootballLineUpDTO.setPlayerPostionNumber(subsObject.getString("position"));
                            completeFootballLineUpDTO.setCardType("yellow");
                            completeFootballLineUpDTO.setGoal("goal");
                            completeFootballLineUpDTO.getEnterExitImage();


                            list.add(completeFootballLineUpDTO);


                        } completeFootballLineUpAdapter.notifyDataSetChanged();

                        for(int i = 0; i<teamsObjectArray.length();i++){
                            /*JSONObject teamsObject = teamsObjectArray.getJSONObject(i);
                            linearLayout = new LinearLayout(getContext());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                            lp.weight = 0.5f;
                            linearLayout.setLayoutParams(lp);
                            tvPlayerName = new TextView(getContext());
                            ivPlayerPosition = new ImageView(getContext());
                            tvPlayerName.setText(teamsObject.getString("name"));
                            drawable=  getTextDrawable(teamsObject.getString("position"), Color.WHITE,R.color.app_theme_blue);
                            ivPlayerPosition.setImageDrawable(drawable);
                            linearLayout.addView(ivPlayerPosition);
                            linearLayout.addView(tvPlayerName);
                          rcLineup.addView(linearLayout);*/
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }



    private TextDrawable getTextDrawable(String value,int textColor,int color) {

        int radius = getContext().getResources().getDimensionPixelSize(R.dimen.recent_ball_radius);
        int border = getContext().getResources().getDimensionPixelSize(R.dimen.user_image_border);
        return TextDrawable.builder()
                .beginConfig()
                .textColor(textColor)
                .withBorder(border)
                .width(radius)
                .height(radius)
                .bold()
                .endConfig()
                .buildRound(value, color);
    }



}
