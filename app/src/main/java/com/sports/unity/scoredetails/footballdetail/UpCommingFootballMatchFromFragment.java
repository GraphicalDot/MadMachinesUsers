package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

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

    public UpCommingFootballMatchFromFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        matchId = i.getStringExtra(INTENT_KEY_ID);
        leagueId = i.getStringExtra(INTENT_KEY_LEAGUE_ID);
        date = i.getStringExtra(INTENT_KEY_DATE);
        team1 = i.getStringExtra(INTENT_KEY_TEAM1_NAME);
        team2 = i.getStringExtra(INTENT_KEY_TEAM2_NAME);
        UpCommingFootballMatchFromHandler upCommingFootballMatchFromHandler = UpCommingFootballMatchFromHandler.getInstance(context);
        upCommingFootballMatchFromHandler.addListener(this);
        upCommingFootballMatchFromHandler.requestUpcommingMatchFrom(leagueId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_football_upcoming_match_form_v2, container, false);
        initView(view);
        showProgressBar();
        return view;
    }
    private void initView(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        initErrorLayout(view);
        tvnamefirstteam=(TextView)view.findViewById(R.id.tv_name_first_team);
        tvlastfivematchteamfirst=(TextView)view.findViewById(R.id.tv_last_five_match_team_first);
        ivfirstmatchteamfirst=(ImageView)view.findViewById(R.id.iv_fifth_match_team_first);
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
        secondview=(View)view.findViewById(R.id.second_view);
        tvsecondpremieradivision=(TextView)view.findViewById(R.id.tv_second_premiera_division);
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
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        showErrorLayout(getView());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

}
