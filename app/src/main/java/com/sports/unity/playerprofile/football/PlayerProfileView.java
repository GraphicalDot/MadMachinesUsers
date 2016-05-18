package com.sports.unity.playerprofile.football;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.common.viewhelper.VolleyCallComponentHelper;
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

    private static final String REQUEST_LISTENER_KEY = "PLAYER_PROFILE_SCREEN_LISTENER";
    private static final String PLAYER_PROFILE_REQUEST_TAG = "PLAYER_PROFILE_TAG";

    private String playerNameKey;
    private List<PlayerScoreCardDTO> playerScoreCardDTOs = new ArrayList<>();

    private JSONObject playerJSONObject = null;

    private CircleImageView playerProfileImage;
    private TextView playerName;
    private TextView teamName;
    private TextView playerAge;
    private TextView positionValue;
    private TextView squadNumber;
    private TextView nationality;

    private RecyclerView recyclerView;
    private PlayerScorecardAdapter mplayerScorecardAdapter;
    private ImageView backImage;
    private View rootScrollBar;

    private ProgressBar progressBar;
    private ViewGroup errorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player_profile_view);
        getIntentExtras();
        initView();

        {
            onComponentCreate();
            requestPlayerProfile();
        }
    }

    @Override
    public VolleyCallComponentHelper getVolleyCallComponentHelper() {
        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper( REQUEST_LISTENER_KEY, new PlayerProfileComponentListener(progressBar, errorLayout));
        return volleyCallComponentHelper;
    }

    private void getIntentExtras() {
        playerNameKey = getIntent().getStringExtra(Constants.INTENT_KEY_ID);
    }

    private void initView() {
        try {
            backImage = (ImageView) findViewById(R.id.back_img);
            playerProfileImage = (CircleImageView) findViewById(R.id.player_profile_image);
            playerName = (TextView) findViewById(R.id.player_name);
            teamName = (TextView) findViewById(R.id.team_name);
            playerAge = (TextView) findViewById(R.id.player_age_value);
            positionValue = (TextView) findViewById(R.id.position_value);
            squadNumber = (TextView) findViewById(R.id.squad_number);
            nationality = (TextView) findViewById(R.id.nationality_value);

            recyclerView = (RecyclerView) findViewById(R.id.rc_player_details);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
            recyclerView.setNestedScrollingEnabled(false);

            mplayerScorecardAdapter = new PlayerScorecardAdapter(playerScoreCardDTOs);
            recyclerView.setAdapter(mplayerScorecardAdapter);

            backImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();

                }
            });

            errorLayout = (ViewGroup) findViewById(R.id.error);
            progressBar = (ProgressBar) findViewById(R.id.progress);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
            progressBar.setIndeterminate(true);

            rootScrollBar = findViewById(R.id.root_scroll_bar);
            rootScrollBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(PlayerProfileView.this, "player has missing data", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPlayerProfile() {
        Log.i("News Detail", "Request news Details");

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PLAYER_NAME, playerNameKey);
        parameters.put(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_FOOTBALL);
        requestContent(ScoresContentHandler.CALL_NAME_PLAYER_PROFILE, parameters, PLAYER_PROFILE_REQUEST_TAG);
    }

    private boolean handleResponse(String response){
        boolean success = false;
        try{
            playerJSONObject = new JSONObject(response);
            success = true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return success;
    }

    private boolean renderContent() {
        boolean success = false;
        try {
            JSONArray datArray = (JSONArray) playerJSONObject.get("data");

            JSONObject dataObject = datArray.getJSONObject(0);
            JSONArray otherComptetionArray = dataObject.getJSONArray("other_competitions");
            playerName.setText(dataObject.getString("name"));
            nationality.setText(dataObject.getString("Nationality"));
            positionValue.setText(dataObject.getString("Position"));
            //squadNumber.setText(dataObject.getString("Jersey"));
            playerAge.setText(dataObject.getString("Age"));

            Glide.with(PlayerProfileView.this).load(dataObject.getString("player_image")).placeholder(R.drawable.ic_user).dontAnimate().into(playerProfileImage);

            teamName.setText(dataObject.getString("team"));

            PlayerScoreCardDTO dto;
            for (int i = 0; i < otherComptetionArray.length(); i++) {
                JSONObject comtObject = otherComptetionArray.getJSONObject(i);
                dto = new PlayerScoreCardDTO();
                dto.setTeamName(comtObject.getString("team"));
                dto.setLeagueName(comtObject.getString("league"));
                dto.setNoOfAssists("25");
                dto.setNoOfGames(comtObject.getString("games"));
                dto.setNoOfgoals(comtObject.getString("goals"));
                dto.setNoOfYellowCard(comtObject.getString("yellow_card"));
                dto.setNoOfRedCard(comtObject.getString("red_card"));
                playerScoreCardDTOs.add(dto);
            }
            mplayerScorecardAdapter.notifyDataSetChanged();

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "player has missing data", Toast.LENGTH_SHORT).show();
        }

        if( success ){
            rootScrollBar.setVisibility(View.VISIBLE);
        }
        return success;
    }

    private class PlayerProfileComponentListener extends CustomComponentListener {

        public PlayerProfileComponentListener(ProgressBar progressBar, ViewGroup errorLayout) {
            super(PLAYER_PROFILE_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public boolean handleContent(String tag, String content) {
            return PlayerProfileView.this.handleResponse(content);
        }

        @Override
        public void changeUI(String tag) {
            boolean success = PlayerProfileView.this.renderContent();
            if( ! success ){
                showErrorLayout();
            } else {
                //nothing
            }
        }

    }

}
