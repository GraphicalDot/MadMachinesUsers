package com.sports.unity.player.view;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.EnterOtpActivity;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
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

    private static final String REQUEST_LISTENER_KEY = "PLAYER_PROFILE_SCREEN_LISTENER";

    private static final String PLAYER_PROFILE_REQUEST_TAG = "playerProfileTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile_view);
        getIntentExtras();
        initView();
        setToolbar();
        {
            PlayerProfileComponentListener playerProfileComponentListener = new PlayerProfileComponentListener(progressBar);
            ArrayList<CustomComponentListener> listeners = new ArrayList<>();
            listeners.add(playerProfileComponentListener);
            onComponentCreate(listeners, REQUEST_LISTENER_KEY);
        }
        setInitData();
    }

    private void setToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        /*ImageView back = (ImageView) toolbar.findViewById(R.id.backarrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
    }

    private void initView() {
        try{
            backImage = (ImageView) findViewById(R.id.img);
            playerProfileImage = (CircleImageView) findViewById(R.id.player_profile_image);
            playerTagImage = (ImageView) findViewById(R.id.player_tag_image);
            playerName = (TextView) findViewById(R.id.player_name);
            teamName = (TextView) findViewById(R.id.team_name);
            playerAge = (TextView) findViewById(R.id.player_age_value);
            positionValue = (TextView) findViewById(R.id.position_value);
            squadNumber = (TextView) findViewById(R.id.squad_number);
            nationality = (TextView) findViewById(R.id.nationality_value);
            idSeason = (TextView) findViewById(R.id.id_current_season);
            leagueImage= (ImageView) findViewById(R.id.ic_league_image);
            tvNextGameName = (TextView) findViewById(R.id.tv_next_game_name);
            tvNextGameDetails = (TextView) findViewById(R.id.tv_next_game_name);
            tvNextGameVenue = (TextView) findViewById(R.id.next_game_venue);
            tvNextGameDate = (TextView) findViewById(R.id.tv_next_game_date);
            notificationImage = (ImageView) findViewById(R.id.notificationicon_image);
            recyclerView= (RecyclerView) findViewById(R.id.rc_player_details);
            recyclerView.setLayoutManager(new LinearLayoutManager(this , VERTICAL, false));
            mplayerScorecardAdapter = new PlayerScorecardAdapter(playerScoreCardDTOs);
            recyclerView.setAdapter(mplayerScorecardAdapter);
            progressBar = (ProgressBar) findViewById(R.id.progress);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
            backImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();

                }
            });
            LinearLayout errorLayout = (LinearLayout) findViewById(R.id.error);
            errorLayout.setVisibility(View.GONE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setInitData() {
        try {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put(Constants.PLAYER_NAME, playerNameKey);
            parameters.put(Constants.SPORTS_TYPE, Constants.SPORTS_TYPE_FOOTBALL);
            ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_PLAYER_PROFILE, parameters, REQUEST_LISTENER_KEY, PLAYER_PROFILE_REQUEST_TAG);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    private class PlayerProfileComponentListener extends CustomComponentListener {

        private boolean success;

        public PlayerProfileComponentListener(ProgressBar progressBar) {
            super(PLAYER_PROFILE_REQUEST_TAG, progressBar, null);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = false;
            try {
                showProgress();
                JSONObject response = new JSONObject(content);
                if (response.getBoolean("success")) {
                    this.success = true;
                    Log.i("player profile", content);
                    populateData(content);
               } else {
                    this.success = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            success = true;
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI() {

        }


    }


    private void getIntentExtras() {
        playerNameKey = getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        //playerNameKey="Cristiano Ronaldo";

    }

    private void populateData(String content){
        try {
            hideProgress();
            JSONObject jsonObject = new JSONObject(content);

            boolean success = jsonObject.getBoolean("success");
            boolean error = jsonObject.getBoolean("error");

            if( success ) {
                JSONArray datArray = (JSONArray) jsonObject.get("data");

                JSONObject dataObject = datArray.getJSONObject(0);
                JSONArray profileArray = dataObject.getJSONArray("profile");
                JSONObject profileData = profileArray.getJSONObject(0);
                JSONArray otherComptetionArray =  dataObject.getJSONArray("other_competitions");
                hideProgress();
                if(profileData != null){

                    if(!dataObject.isNull("name")){
                        playerName.setText(dataObject.getString("name"));
                    }
                    if(!profileData.isNull("Date of Birth")){
                        playerAge.setText(profileData.getString("Date of Birth"));
                    }
                    if(!profileData.isNull("nationality")){
                        nationality.setText(profileData.getString("nationality"));
                    }
                    if(!dataObject.isNull("player_image")){
                        Glide.with(this).load(dataObject.getString("player_image")).placeholder(R.drawable.ic_no_img).into(playerProfileImage);
                    }
                    playerTagImage.setImageResource(R.drawable.ic_no_img);
                   /* if(!dataObject.isNull("player_image")){
                        Glide.with(this).load(dataObject.getString("player_image")).placeholder(R.drawable.ic_no_img).into(playerTagImage);
                    }*/

                    if(!dataObject.isNull("team")){
                        teamName.setText(dataObject.getString("team"));
                    } else {
                        teamName.setText("NA");
                    }
                    PlayerScoreCardDTO dto;
                    for (int i = 0;i<otherComptetionArray.length();i++){
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
                       /* if(!object.isNull("yellow_cards")){
                            tvNumberOfYellowCard.setText(object.getString("yellow_cards"));
                        }
                        if(!object.isNull("red_cards")){
                            tvNumberOfRedCard.setText(object.getString("red_cards"));
                        }

                        if(!object.isNull("assists")){
                            tvNumberOfAssist.setText(object.getString("assists"));
                        }
                        if(!object.isNull("goals")){
                            goalsScoredNumber.setText(object.getString("goals"));
                        }

                        if(!object.isNull("position")){
                            positionValue.setText(object.getString("position"));
                        }*/




                }
            } else {
                showErrorLayout();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            showErrorLayout();
        }
    }


    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

    }
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);

    }



    private void showErrorLayout() {

        LinearLayout errorLayout = (LinearLayout)findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);

    }

}
