package com.sports.unity.playerprofile.football;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class PlayerProfileView extends CustomVolleyCallerActivity {


    private CircleImageView playerProfileImage;
    private ImageView playerTagImage;
    private TextView playerName;
    private TextView teamName;
    private TextView playerAge;
    private TextView positionValue;
    private TextView squadNumber;
    private TextView nationality;
    private TextView idSeason;
    private TextView noOfMatchesPlayed;
    private TextView goalsScoredNumber;
    private TextView tvNumberOfAssist;
    private TextView tvNumberOfYellowCard;
    private TextView tvNumberOfRedCard;
    private ImageView leagueImage;
    private TextView tvNextGameName;
    private TextView tvNextGameDetails;
    private TextView tvNextGameVenue;
    private TextView tvNextGameDate;
    private ImageView notificationImage;
    private RecyclerView recyclerView;
    private String playerNameKey;
    private PlayerScorecardAdapter mplayerScorecardAdapter;
    private List<PlayerScoreCardDTO> playerScoreCardDTOs = new ArrayList<>();
    private ProgressBar progressBar;
    private ImageView backImage;
    private View rootScrollBar;
    private LinearLayout errorLayout;

    private static final String REQUEST_LISTENER_KEY = "PLAYER_PROFILE_SCREEN_LISTENER";

    private static final String PLAYER_PROFILE_REQUEST_TAG = "playerProfileTag";

    private ScoresContentHandler.ContentListener contentListener = new ScoresContentHandler.ContentListener() {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            hideProgress();
            if (responseCode == 200) {
                try {
                    JSONObject response = new JSONObject(content);
                    if (response.getBoolean("success")) {
                        populateData(response);
                    } else {
                        showErrorLayout();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorLayout();
                }
            } else {
                showErrorLayout();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        ScoresContentHandler.getInstance().addResponseListener(contentListener, REQUEST_LISTENER_KEY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScoresContentHandler.getInstance().removeResponseListener(REQUEST_LISTENER_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile_view);
        getIntentExtras();
        initView();
        setInitData();
    }

    private void initView() {
        try {
            backImage = (ImageView) findViewById(R.id.back_img);
            playerProfileImage = (CircleImageView) findViewById(R.id.player_profile_image);
          /*  playerTagImage = (ImageView) findViewById(R.id.player_tag_image);*/
            playerName = (TextView) findViewById(R.id.player_name);
            teamName = (TextView) findViewById(R.id.team_name);
            playerAge = (TextView) findViewById(R.id.player_age_value);
            positionValue = (TextView) findViewById(R.id.position_value);
            squadNumber = (TextView) findViewById(R.id.squad_number);
            nationality = (TextView) findViewById(R.id.nationality_value);
            idSeason = (TextView) findViewById(R.id.id_current_season);
            leagueImage = (ImageView) findViewById(R.id.ic_league_image);
            tvNextGameName = (TextView) findViewById(R.id.tv_next_game_name);
            tvNextGameDetails = (TextView) findViewById(R.id.tv_next_game_name);
            tvNextGameVenue = (TextView) findViewById(R.id.next_game_venue);
            tvNextGameDate = (TextView) findViewById(R.id.tv_next_game_date);
            notificationImage = (ImageView) findViewById(R.id.notificationicon_image);
            recyclerView = (RecyclerView) findViewById(R.id.rc_player_details);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
            recyclerView.setNestedScrollingEnabled(false);
            mplayerScorecardAdapter = new PlayerScorecardAdapter(playerScoreCardDTOs);
            recyclerView.setAdapter(mplayerScorecardAdapter);
            progressBar = (ProgressBar) findViewById(R.id.progress);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
            progressBar.setIndeterminate(true);
            backImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();

                }
            });
            errorLayout = (LinearLayout) findViewById(R.id.error);
            rootScrollBar = findViewById(R.id.root_scroll_bar);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PlayerProfileView.this, "player has missing data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setInitData() {
        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put(Constants.PLAYER_NAME, playerNameKey);
            parameters.put(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_FOOTBALL);
            showProgress();
            ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_PLAYER_PROFILE, parameters, REQUEST_LISTENER_KEY, PLAYER_PROFILE_REQUEST_TAG);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void getIntentExtras() {
        playerNameKey = getIntent().getStringExtra(Constants.INTENT_KEY_ID);
    }

    private void populateData(JSONObject jsonObject) {
        try {

            JSONArray datArray = (JSONArray) jsonObject.get("data");

            JSONObject dataObject = datArray.getJSONObject(0);
            JSONArray otherComptetionArray = dataObject.getJSONArray("other_competitions");

            playerName.setText(dataObject.getString("name"));
            nationality.setText(dataObject.getString("Nationality"));
            positionValue.setText(dataObject.getString("Position"));
            squadNumber.setText(dataObject.getString("Jersey"));
            playerAge.setText(dataObject.getString("Age"));

            Glide.with(PlayerProfileView.this).load(dataObject.getString("player_image")).placeholder(R.drawable.ic_user).dontAnimate().into(playerProfileImage);

            teamName.setText(dataObject.getString("team"));

            PlayerScoreCardDTO dto;
            for (int i = 0; i < otherComptetionArray.length(); i++) {
                JSONObject comtObject = otherComptetionArray.getJSONObject(i);
                dto = new PlayerScoreCardDTO();
                dto.setTeamName(comtObject.getString("team"));
                dto.setLeagueName(comtObject.getString("league"));
                dto.setNoOfAssists(comtObject.getString("assists"));
                dto.setNoOfGames(comtObject.getString("games"));
                dto.setNoOfgoals(comtObject.getString("goals"));
                dto.setNoOfYellowCard(comtObject.getString("yellow_card"));
                dto.setNoOfRedCard(comtObject.getString("red_card"));
                playerScoreCardDTOs.add(dto);
            }
            mplayerScorecardAdapter.notifyDataSetChanged();

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "player has missing data", Toast.LENGTH_SHORT).show();
        }
    }


    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        rootScrollBar.setVisibility(View.GONE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        rootScrollBar.setVisibility(View.VISIBLE);
    }


    private void showErrorLayout() {
        errorLayout.setVisibility(View.VISIBLE);
        rootScrollBar.setVisibility(View.GONE);

    }

}
