package com.sports.unity.scoredetails.footballdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.view.CustomRobotoCondenseBoldTextView;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommingFootballMatchSquadAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommingFootballMatchSquadDTO;
import com.sports.unity.scores.controller.fragment.MatchListFragment;
import com.sports.unity.scores.model.ScoresContentHandler;
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
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM1_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM1_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM2_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM2_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchSqadFragment extends Fragment {

    String toss = "";
    String matchName = "";
    String date = "";
    private String matchId;
    private ProgressBar progressBar;
    private String teamFirstName;
    private String teamSecondName;
    private String teamFirstId = "";
    private String teamSecondId;
    private RecyclerView rcRecyclerViewTeamFirst;
    private RecyclerView rcRecyclerViewTeamSecond;
    private UpCommingFootballMatchSquadAdapter upCommingFootballMatchSquadAdapterFirst;
    private List<UpCommingFootballMatchSquadDTO> listTeamFirst = new ArrayList<UpCommingFootballMatchSquadDTO>();
    private UpCommingFootballMatchSquadAdapter upCommingFootballMatchSquadAdapterSecond;
    private List<UpCommingFootballMatchSquadDTO> listTeamSecond = new ArrayList<UpCommingFootballMatchSquadDTO>();
    private TextView tvTeamFirst;
    private TextView tvTeamSecond;
    private LinearLayout squadParnetLinearLayout;
    private LinearLayout errorLayout;
    private Context context;
    private Bundle bundle;
    private JSONArray teamFirstSquadArray;
    private JSONArray teamSecondSquadArray;
    private FavouriteItem favouriteItem;

    private SquadContentListener squadContentListener;

    public static final String SQUAD_BASE_URL = "http://52.74.75.79:8080/get_football_squads?team_1=";
    public static final String SQUAD_LISTENER_KEY = "squad_listener_key";
    public static final String SQUAD_REQUEST_TAG = "squad_request_tag";
    public static final String SQUAD_FOOTBALL_URL = "http://52.74.75.79:8080/get_team_players?team_id=";
    public static final String SQUAD_CRICKET_URL = "http://52.74.75.79:8080/v1/get_team_squad?team_id=";

    public UpCommingFootballMatchSqadFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        squadContentListener = new SquadContentListener();
        bundle = getArguments();
        if (bundle != null) {
            teamFirstId = bundle.getString(Constants.INTENT_KEY_TEAM1_ID);
            favouriteItem = new FavouriteItem(teamFirstId);
            teamFirstId = favouriteItem.getId();
        }
        teamFirstSquadArray = new JSONArray();
        teamSecondSquadArray = new JSONArray();
        this.context = getActivity();
        Intent i = getActivity().getIntent();
        if (TextUtils.isEmpty(teamFirstId)) {
            matchId = i.getStringExtra(INTENT_KEY_ID);
            matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
            toss = i.getStringExtra(INTENT_KEY_TOSS);
            date = i.getStringExtra(INTENT_KEY_DATE);
            teamFirstId = i.getStringExtra(INTENT_KEY_TEAM1_ID);
            teamSecondId = i.getStringExtra(INTENT_KEY_TEAM2_ID);
            teamFirstName = i.getStringExtra(INTENT_KEY_TEAM1_NAME);
            teamSecondName = i.getStringExtra(INTENT_KEY_TEAM2_NAME);
        } else {
            //nothing
            teamFirstName = favouriteItem.getName();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_football_match_squard_wrapper, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tvTeamFirst = (TextView) view.findViewById(R.id.tv_team_first_name);
        tvTeamSecond = (TextView) view.findViewById(R.id.tv_team_second_name);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        rcRecyclerViewTeamFirst = (RecyclerView) view.findViewById(R.id.rc_child1_rv);
        rcRecyclerViewTeamFirst.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        rcRecyclerViewTeamFirst.setNestedScrollingEnabled(false);
        rcRecyclerViewTeamSecond = (RecyclerView) view.findViewById(R.id.rc_child2_rv);
        rcRecyclerViewTeamSecond.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        rcRecyclerViewTeamSecond.setNestedScrollingEnabled(false);
        upCommingFootballMatchSquadAdapterFirst = new UpCommingFootballMatchSquadAdapter(listTeamFirst, getContext());
        rcRecyclerViewTeamFirst.setAdapter(upCommingFootballMatchSquadAdapterFirst);
        upCommingFootballMatchSquadAdapterSecond = new UpCommingFootballMatchSquadAdapter(listTeamSecond, getContext());
        rcRecyclerViewTeamSecond.setAdapter(upCommingFootballMatchSquadAdapterSecond);
        squadParnetLinearLayout = (LinearLayout) view.findViewById(R.id.squad_parent_linearlayout);
        squadParnetLinearLayout.setVisibility(View.GONE);
        initErrorLayout(view);

    }

    private void initErrorLayout(View view) {
        try {
            errorLayout = (LinearLayout) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);

    }

    private void showErrorLayout() {
        squadParnetLinearLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);

    }

    private void hideErrorLayout() {
        errorLayout.setVisibility(View.GONE);
        squadParnetLinearLayout.setVisibility(View.VISIBLE);
    }

    private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        listTeamFirst.clear();
        listTeamSecond.clear();
        Activity activity = getActivity();
        hideProgressBar();
        if (!jsonObject.isNull("data")) {
            JSONObject dataObject;
            if (bundle == null) {
                dataObject = jsonObject.getJSONObject("data");
                teamFirstSquadArray = dataObject.getJSONArray("team_1_squad");
                teamSecondSquadArray = dataObject.getJSONArray("team_2_squad");
            } else {
                teamFirstSquadArray = jsonObject.getJSONArray("data");
                tvTeamFirst.setText(favouriteItem.getName());
                if (favouriteItem.getSportsType().equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                    getView().findViewById(R.id.team2_layout).setVisibility(View.GONE);
                } else {
                    getView().findViewById(R.id.team1_layout).setVisibility(View.GONE);
                    getView().findViewById(R.id.team2_layout).setVisibility(View.GONE);
                    CustomRobotoCondenseBoldTextView cricketTeamName = (CustomRobotoCondenseBoldTextView) getView().findViewById(R.id.cricket_team_name);
                    cricketTeamName.setVisibility(View.VISIBLE);
                    cricketTeamName.setText("NAME");
                }
            }
            squadParnetLinearLayout.setVisibility(View.VISIBLE);
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tvTeamFirst.setText(teamFirstName);
                            tvTeamSecond.setText(teamSecondName);
                            UpCommingFootballMatchSquadDTO dto;
                            if (teamFirstSquadArray.length() > 0) {
                                for (int i = 0; i < teamFirstSquadArray.length(); i++) {
                                    JSONObject playerObject = teamFirstSquadArray.getJSONObject(i);
                                    dto = new UpCommingFootballMatchSquadDTO();

                                    if (bundle == null || favouriteItem.getSportsType().equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                                        getSquadDetails(dto, playerObject);
                                    } else {
                                        getCricketSquadDetails(dto, playerObject);
                                        upCommingFootballMatchSquadAdapterFirst.setCricketSquad(true);
                                    }
                                    listTeamFirst.add(dto);
                                }
                            }
                            if (teamSecondSquadArray.length() > 0) {
                                for (int i = 0; i < teamSecondSquadArray.length(); i++) {
                                    JSONObject playerObject = teamSecondSquadArray.getJSONObject(i);
                                    dto = new UpCommingFootballMatchSquadDTO();
                                    getSquadDetails(dto, playerObject);
                                    listTeamSecond.add(dto);
                                }
                            }
                            if (bundle == null) {
                                upCommingFootballMatchSquadAdapterFirst.notifyDataSetChanged();
                                upCommingFootballMatchSquadAdapterSecond.notifyDataSetChanged();
                            } else {
                                upCommingFootballMatchSquadAdapterFirst.notifyDataSetChanged();
                                rcRecyclerViewTeamFirst.setFocusable(false);
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showErrorLayout();
                        }
                    }
                });
            }
        } else {
            showErrorLayout();
        }
    }

    private void getSquadDetails(UpCommingFootballMatchSquadDTO dto, JSONObject playerObject) throws JSONException {
        if (!playerObject.isNull("short_name_id")) {
            dto.setId(playerObject.getString("short_name_id"));
        }
        if (!playerObject.isNull("name")) {
            dto.setTvPlayerName(playerObject.getString("name"));
        }
        if (!playerObject.isNull("age")) {
            dto.setTvPlayerAge("(" + playerObject.getString("age") + ")");
        }
        if (!playerObject.isNull("position")) {
            dto.setTvP(playerObject.getString("position").substring(0, 1));
        }
        if (!playerObject.isNull("games_played")) {
            dto.setTvpl(playerObject.getString("games_played"));
        }
        if (!playerObject.isNull("goals")) {
            dto.setTvgol(playerObject.getString("goals"));
        }
        if (!playerObject.isNull("red_cards")) {
            dto.setTvredcard(playerObject.getString("red_cards"));
        }
        if (!playerObject.isNull("yellow_cards")) {
            dto.setTvyellowcard(playerObject.getString("yellow_cards"));
        }
    }

    private void getCricketSquadDetails(UpCommingFootballMatchSquadDTO dto, JSONObject playerObject) throws JSONException {
        if (!playerObject.isNull("player_id")) {
            dto.setId(playerObject.getString("player_id"));
        }
        if (!playerObject.isNull("name")) {
            dto.setTvPlayerName(playerObject.getString("name"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ScoresContentHandler.getInstance().removeResponseListener(SQUAD_LISTENER_KEY);
    }

    @Override
    public void onResume() {
        super.onResume();
        ScoresContentHandler.getInstance().addResponseListener(squadContentListener, SQUAD_LISTENER_KEY);
        if (teamFirstSquadArray.length() == 0) {
            showProgressBar();
            requestContent();
        }
    }

    public void requestContent() {
        if (bundle != null) {
            if (favouriteItem.getSportsType().equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                ScoresContentHandler.getInstance().requestSquadContent(SQUAD_FOOTBALL_URL + favouriteItem.getId(), SQUAD_LISTENER_KEY, SQUAD_REQUEST_TAG);
            } else {
                //TODO request for cricket squad; dependency API

                ScoresContentHandler.getInstance().requestSquadContent(SQUAD_CRICKET_URL + favouriteItem.getId(), SQUAD_LISTENER_KEY, SQUAD_REQUEST_TAG);
                //ScoresContentHandler.getInstance().requestSquadContent(SQUAD_CRICKET_URL + 3, SQUAD_LISTENER_KEY, SQUAD_REQUEST_TAG);
            }
        } else {

            ScoresContentHandler.getInstance().requestSquadContent(SQUAD_BASE_URL + teamFirstId + "&team_2=" + teamSecondId, SQUAD_LISTENER_KEY, SQUAD_REQUEST_TAG);
        }
    }


    public class SquadContentListener implements ScoresContentHandler.ContentListener {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            if (tag.equals(SQUAD_REQUEST_TAG)) {
                boolean success = false;
                if (responseCode == 200) {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(content);
                        success = jsonObject.getBoolean("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (success) {
                        hideErrorLayout();
                        try {
                            renderDisplay(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i("List of Matches", "Error In Handling Content");
                        showErrorLayout();
                    }
                } else {
                    Log.i("List of Matches", "Error In Response");
                    showErrorLayout();
                }

                hideProgressBar();
            }
        }
    }

}
