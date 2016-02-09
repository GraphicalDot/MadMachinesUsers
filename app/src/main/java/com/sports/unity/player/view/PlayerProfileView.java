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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.EnterOtpActivity;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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

    private static final String REQUEST_LISTENER_KEY = "PLAYER_PROFILE_SCREEN_LISTENER";

    private static final String PLAYER_PROFILE_REQUEST_TAG = "playerProfileTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile_view);
        initView();
        setToolbar();
        {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            PlayerProfileComponentListener createUserComponentListener = new PlayerProfileComponentListener(progressBar);
            ArrayList<CustomComponentListener> listeners = new ArrayList<>();
            listeners.add(createUserComponentListener);
            onComponentCreate(listeners, REQUEST_LISTENER_KEY);
        }
    }

    private void setToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        ImageView back = (ImageView) toolbar.findViewById(R.id.backarrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        try{
            playerProfileImage = (CircleImageView) findViewById(R.id.player_profile_image);
            playerTagImage = (ImageView) findViewById(R.id.player_tag_image);
            playerName = (TextView) findViewById(R.id.player_name);
            teamName = (TextView) findViewById(R.id.team_name);
            playerAge = (TextView) findViewById(R.id.player_age);
            positionValue = (TextView) findViewById(R.id.position_value);
            squadNumber = (TextView) findViewById(R.id.squad_number);
            nationality = (TextView) findViewById(R.id.nationality_value);
            idSeason = (TextView) findViewById(R.id.id_current_season);
            noOfMatchesPlayed = (TextView) findViewById(R.id.tv_number_of_match);
            goalsScoredNumber = (TextView) findViewById(R.id.tv_goals_scored_number);
            tvNumberOfAssist = (TextView) findViewById(R.id.tv_number_of_assist);
            tvNumberOfYellowCard= (TextView) findViewById(R.id.tv_number_of_yellow_card);
            tvNumberOfRedCard= (TextView) findViewById(R.id.tv_number_of_red_card);
            leagueImage= (ImageView) findViewById(R.id.ic_league_image);
            tvNextGameName = (TextView) findViewById(R.id.tv_next_game_name);
            tvNextGameDetails = (TextView) findViewById(R.id.tv_next_game_name);
            tvNextGameVenue = (TextView) findViewById(R.id.next_game_venue);
            tvNextGameDate = (TextView) findViewById(R.id.tv_next_game_date);
            notificationImage = (ImageView) findViewById(R.id.notificationicon_image);
            recyclerView= (RecyclerView) findViewById(R.id.recycle_view);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setInitData() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PLAYER_NAME, playerNameKey);
        ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_PLAYER_PROFILE, parameters, REQUEST_LISTENER_KEY, PLAYER_PROFILE_REQUEST_TAG);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class PlayerProfileComponentListener extends CustomComponentListener {

        private boolean success;

        public PlayerProfileComponentListener(ProgressBar progressBar) {
            super(PLAYER_PROFILE_REQUEST_TAG, progressBar, null);
        }

        @Override
        protected void showErrorLayout() {
            Toast.makeText(getApplicationContext(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = false;
            try {
                JSONObject response = new JSONObject(content);
                if (response.getString("status").equals("200")) {
                    this.success = true;
                 Log.i("playerprofile",content);
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




}
