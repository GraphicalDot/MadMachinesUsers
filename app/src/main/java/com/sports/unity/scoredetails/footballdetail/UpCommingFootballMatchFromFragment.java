package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sports.unity.util.Constants.COLOR_BLUE;
import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_LEAGUE_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM1_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM2_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchFromFragment extends Fragment implements UpCommingFootballMatchFromHandler.UpCommingMatchFromContentListener{

    private ProgressBar progressBar;
    private String date = "";
    private String matchId ="";
    private String leagueId = "";
    private String team1;
    private String team2;
    private SwipeRefreshLayout commentaryrefresh;
    private TextView tvnamefirstteam;
    private TextView tvlastfivematchteamfirst;
    private ImageView ivfirstmatchteamfirst;
    private ImageView ivsecondmatchteamfirst;
    private ImageView ivthirdmatchteamfirst;
    private ImageView ivforthmatchteamfirst;
    private ImageView ivfifthmatchteamfirst;
    private View firstview;
    private TextView tvfirstpremieradivision;
    private TextView tvfirstpoint;
    private TextView tvfirstwins;
    private TextView tvfirstdraws;
    private TextView tvfirstloss;
    private TextView tvpointoffirstteam;
    private TextView tvwinmatchoffirstteam;
    private TextView tvdrawmatchoffirstteam;
    private TextView tvlossmatchoffirstteam;
    private TextView tvnamesecondteam;
    private TextView tvlastfivematchteamsecond;
    private ImageView tvfirstmatchteamsecond;
    private ImageView tvsecondmatchteamsecond;
    private ImageView tvthirdmatchteamsecond;
    private ImageView tvforthmatchteamsecond;
    private ImageView tvfifthmatchteamsecond;
    private TextView tvsecondpoint;
    private TextView tvsecondwins;
    private TextView tvseconddraws;
    private TextView tvsecondloss;
    private TextView tvpointofsecondteam;
    private TextView tvwinmatchofsecondteam;
    private TextView tvdrawmatchofsecondteam;
    private TextView tvlossmatchofsecondteam;
    private View secondview;
    private TextView tvsecondpremieradivision;
    private View parentView;
    private LinearLayout errorLayout;
    private UpCommingFootballMatchFromHandler upCommingFootballMatchFromHandler;
    private Context context;

    public UpCommingFootballMatchFromFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Intent i = getActivity().getIntent();
        matchId = i.getStringExtra(INTENT_KEY_ID);
        leagueId = i.getStringExtra(INTENT_KEY_LEAGUE_ID);
        date = i.getStringExtra(INTENT_KEY_DATE);
        team1 = i.getStringExtra(INTENT_KEY_TEAM1_NAME);
        team2 = i.getStringExtra(INTENT_KEY_TEAM2_NAME);
        upCommingFootballMatchFromHandler = UpCommingFootballMatchFromHandler.getInstance(context);
        upCommingFootballMatchFromHandler.addListener(this);
        upCommingFootballMatchFromHandler.requestUpcommingMatchFrom(leagueId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_football_upcoming_match_form_v2, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        initErrorLayout(view);
        tvnamefirstteam=(TextView)view.findViewById(R.id.tv_name_first_team);
        tvlastfivematchteamfirst=(TextView)view.findViewById(R.id.tv_last_five_match_team_first);
        ivfirstmatchteamfirst=(ImageView)view.findViewById(R.id.iv_first_match_team_first);
        ivsecondmatchteamfirst=(ImageView)view.findViewById(R.id.iv_second_match_team_first);
        ivthirdmatchteamfirst=(ImageView)view.findViewById(R.id.iv_third_match_team_first);
        ivforthmatchteamfirst=(ImageView)view.findViewById(R.id.iv_forth_match_team_first);
        ivfifthmatchteamfirst=(ImageView)view.findViewById(R.id.iv_fifth_match_team_first);
        firstview=(View)view.findViewById(R.id.first_view);
        tvfirstpremieradivision=(TextView)view.findViewById(R.id.tv_first_premiera_division);
        tvfirstpoint=(TextView)view.findViewById(R.id.tv_first_point);
        tvfirstwins=(TextView)view.findViewById(R.id.tv_first_wins);
        tvfirstdraws=(TextView)view.findViewById(R.id.tv_first_draws);
        tvfirstloss=(TextView)view.findViewById(R.id.tv_first_loss);
        tvpointoffirstteam=(TextView)view.findViewById(R.id.tv_point_of_first_team);
        tvwinmatchoffirstteam=(TextView)view.findViewById(R.id.tv_win_match_of_first_team);
        tvdrawmatchoffirstteam=(TextView)view.findViewById(R.id.tv_draw_match_of_first_team);
        tvlossmatchoffirstteam=(TextView)view.findViewById(R.id.tv_loss_match_of_first_team);
        tvnamesecondteam=(TextView)view.findViewById(R.id.tv_name_second_team);
        tvlastfivematchteamsecond=(TextView)view.findViewById(R.id.tv_last_five_match_team_second);
        tvfirstmatchteamsecond=(ImageView)view.findViewById(R.id.tv_first_match_team_second);
        tvsecondmatchteamsecond=(ImageView)view.findViewById(R.id.tv_second_match_team_second);
        tvthirdmatchteamsecond=(ImageView)view.findViewById(R.id.tv_third_match_team_second);
        tvforthmatchteamsecond=(ImageView)view.findViewById(R.id.tv_forth_match_team_second);
        tvfifthmatchteamsecond=(ImageView)view.findViewById(R.id.tv_fifth_match_team_second);
        tvsecondpoint=(TextView)view.findViewById(R.id.tv_second_point);
        tvsecondwins=(TextView)view.findViewById(R.id.tv_second_wins);
        tvseconddraws=(TextView)view.findViewById(R.id.tv_second_draws);
        tvsecondloss=(TextView)view.findViewById(R.id.tv_second_loss);
        tvpointofsecondteam=(TextView)view.findViewById(R.id.tv_point_of_second_team);
        tvwinmatchofsecondteam=(TextView)view.findViewById(R.id.tv_win_match_of_second_team);
        tvdrawmatchofsecondteam=(TextView)view.findViewById(R.id.tv_draw_match_of_second_team);
        tvlossmatchofsecondteam=(TextView)view.findViewById(R.id.tv_loss_match_of_second_team);
        parentView = view.findViewById(R.id.root_layout);
        commentaryrefresh=(SwipeRefreshLayout)view.findViewById(R.id.commentary_refresh);

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
                showErrorLayout(getView());
            }
        }
    }
    private void initErrorLayout(View view) {
        try {
            errorLayout = (LinearLayout) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.GONE);
        }catch (Exception e){e.printStackTrace();}
    }

    private void showErrorLayout(View view) {

        parentView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);


    }

    private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        hideProgressBar();
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        final JSONArray dataArray = jsonObject.getJSONArray("data");
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for(int i = 0; i< dataArray.length();i++){
                            JSONObject teamFromObject = dataArray.getJSONObject(i);
                            if(!teamFromObject.isNull("team_name")){
                                if(team1.equals(teamFromObject.getString("team_name"))){
                                    tvnamefirstteam.setText(teamFromObject.getString("team_name"));
                                    if(!teamFromObject.isNull("recent_form")){
                                        String recentForm = teamFromObject.getString("recent_form");
                                        if(recentForm !=null && recentForm.length()<1){
                                            initializeTeamForms(recentForm);
                                        }else{
                                            showErrorLayout(getView());
                                        }

                                       }

                                    if(!teamFromObject.isNull("team_points")){
                                        tvpointoffirstteam.setText(teamFromObject.getString("team_points"));}
                                    if(!teamFromObject.isNull("games_won")) {
                                        tvwinmatchoffirstteam.setText(teamFromObject.getString("games_won"));
                                    }if(!teamFromObject.isNull("games_drawn")){
                                        tvdrawmatchoffirstteam.setText(teamFromObject.getString("games_drawn"));}
                                    if(!teamFromObject.isNull("games_lost")){
                                        tvlossmatchoffirstteam.setText(teamFromObject.getString("games_lost"));}

                                }else if(team2.equals(teamFromObject.getString("team_name"))){
                                    tvnamesecondteam.setText(teamFromObject.getString("team_name"));
                                    if(!teamFromObject.isNull("recent_form")) {
                                        String recentForm = teamFromObject.getString("recent_form");
                                        if(recentForm !=null && recentForm.length()<1){
                                            initFromDataTeamSecond(recentForm);
                                        }else{
                                            showErrorLayout(getView());
                                        }


                                    } if(!teamFromObject.isNull("team_points")){
                                        tvpointofsecondteam.setText(teamFromObject.getString("team_points"));}
                                    if(!teamFromObject.isNull("games_won")){
                                        tvwinmatchofsecondteam.setText(teamFromObject.getString("games_won"));}
                                    if(!teamFromObject.isNull("games_drawn")) {
                                        tvdrawmatchofsecondteam.setText(teamFromObject.getString("games_drawn"));
                                    }
                                    if(!teamFromObject.isNull("games_lost")){
                                        tvlossmatchofsecondteam.setText(teamFromObject.getString("games_lost"));}

                                }else{
                                    showErrorLayout(getView());
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

    private void initFromDataTeamSecond(String recentForm) {
        tvfirstmatchteamsecond.setImageDrawable(getBallColor("" + recentForm.charAt(0), getBallColor(recentForm.charAt(0))));

        tvsecondmatchteamsecond.setImageDrawable(getBallColor("" + recentForm.charAt(1), getBallColor(recentForm.charAt(1))));

        tvthirdmatchteamsecond.setImageDrawable(getBallColor("" + recentForm.charAt(2), getBallColor(recentForm.charAt(2))));

        tvforthmatchteamsecond.setImageDrawable(getBallColor("" + recentForm.charAt(3), getBallColor(recentForm.charAt(3))));

        tvfifthmatchteamsecond.setImageDrawable(getBallColor("" + recentForm.charAt(4), getBallColor(recentForm.charAt(4))));
    }

    private void initializeTeamForms(String recentForm) {
        ivfirstmatchteamfirst.setImageDrawable(getBallColor("" + recentForm.charAt(0), getBallColor(recentForm.charAt(0))));

        ivsecondmatchteamfirst.setImageDrawable(getBallColor("" + recentForm.charAt(1), getBallColor(recentForm.charAt(1))));

        ivthirdmatchteamfirst.setImageDrawable(getBallColor("" + recentForm.charAt(2), getBallColor(recentForm.charAt(2))));

        ivforthmatchteamfirst.setImageDrawable(getBallColor("" + recentForm.charAt(3), getBallColor(recentForm.charAt(3))));

        ivfifthmatchteamfirst.setImageDrawable(getBallColor("" + recentForm.charAt(4), getBallColor(recentForm.charAt(4))));
    }


    private int getBallColor(char c){
        Log.i("getBallColor: ", " " + c);
        int color= 0;
        switch (c){
            case 'W':
            case 'w':
                color=   getResources().getColor(R.color.green);
                break;
            case 'L':
            case 'l':
                color=getResources().getColor(R.color.loose);
                break;
            case 'D':
            case 'd':
                color= getResources().getColor(R.color.draw);
                break;
            default:color = Color.WHITE;
        }

        return color ;
    }


    private Drawable getBallColor(String text,int color){
        int radius = getContext().getResources().getDimensionPixelSize(R.dimen.recent_ball_radius);
        int border = getContext().getResources().getDimensionPixelSize(R.dimen.user_image_border);
       TextDrawable drawable = TextDrawable.builder()
                .beginConfig().textColor(Color.WHITE)
                .withBorder(border)
                .width(radius)
                .height(radius)
                .bold()
                .endConfig()
                .buildRound(text, color);
        return  drawable;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(upCommingFootballMatchFromHandler != null){
            upCommingFootballMatchFromHandler.addListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressBar();
        if(upCommingFootballMatchFromHandler != null){
            upCommingFootballMatchFromHandler.addListener(this);

        }else {
            upCommingFootballMatchFromHandler = UpCommingFootballMatchFromHandler.getInstance(context);

        }
        upCommingFootballMatchFromHandler.requestUpcommingMatchFrom(leagueId);
    }


}
