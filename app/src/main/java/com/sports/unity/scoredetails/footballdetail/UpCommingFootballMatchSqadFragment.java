package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.view.CustomRobotoCondenseBoldTextView;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommingFootballMatchSquadAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommingFootballMatchSquadDTO;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchSqadFragment extends BasicVolleyRequestResponseViewHelper {

    private static final String SQUAD_LISTENER_KEY = "squad_listener_key";
    private static final String SQUAD_REQUEST_TAG = "squad_request_tag";

//    private static final String SQUAD_BASE_URL = Constants.SCORE_BASE_URL+"get_football_squads?team_1=";
//    private static final String SQUAD_FOOTBALL_URL = Constants.SCORE_BASE_URL+"/get_team_players?team_id=";
//    private static final String SQUAD_CRICKET_URL = Constants.SCORE_BASE_URL+"/v1/get_team_squad?team_id=";

    private Context context;

    private String title;
    private HashMap<String,String> requestParameters;
    private JSONObject response;

    private Bundle bundle = null;
    private FavouriteItem favouriteItem = null;

    private String teamFirstName;
    private String teamSecondName;

    private UpCommingFootballMatchSquadAdapter upCommingFootballMatchSquadAdapterFirst;
    private List<UpCommingFootballMatchSquadDTO> listTeamFirst = new ArrayList<UpCommingFootballMatchSquadDTO>();
    private UpCommingFootballMatchSquadAdapter upCommingFootballMatchSquadAdapterSecond;
    private List<UpCommingFootballMatchSquadDTO> listTeamSecond = new ArrayList<UpCommingFootballMatchSquadDTO>();

    private RecyclerView rcRecyclerViewTeamFirst;
    private RecyclerView rcRecyclerViewTeamSecond;
    private TextView tvTeamFirst;
    private TextView tvTeamSecond;
    private LinearLayout squadParnetLinearLayout;
    private JSONArray teamFirstSquadArray;
    private JSONArray teamSecondSquadArray;
    private View layoutView;

    public UpCommingFootballMatchSqadFragment(String title, Intent intent, Bundle bundle) {
        this.title = title;

        this.bundle = bundle;
        {
            String teamFirstId = null;
            if (bundle != null) {
                teamFirstId = bundle.getString(Constants.INTENT_KEY_TEAM1_ID);
                favouriteItem = new FavouriteItem(teamFirstId);
                teamFirstId = favouriteItem.getId();
            }

            teamFirstSquadArray = new JSONArray();
            teamSecondSquadArray = new JSONArray();

            if (TextUtils.isEmpty(teamFirstId)) {
                teamFirstName = intent.getStringExtra(Constants.INTENT_KEY_TEAM1_NAME);
                teamSecondName = intent.getStringExtra(Constants.INTENT_KEY_TEAM2_NAME);
            } else {
                teamFirstName = favouriteItem.getName();
            }
        }
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_upcoming_football_match_squard_wrapper;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return SQUAD_LISTENER_KEY;
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress);

        UpcomingFootballMatchSquadComponentListener  componentListener = new UpcomingFootballMatchSquadComponentListener( getRequestTag(), progressBar, errorLayout);
        return componentListener;
    }

    @Override
    public String getRequestTag() {
        return SQUAD_REQUEST_TAG;
    }

    @Override
    public String getRequestCallName() {
        String callName = null;
        if( favouriteItem != null ){
            if( Constants.SPORTS_TYPE_FOOTBALL.equals(favouriteItem.getSportsType()) ) {
                callName = ScoresContentHandler.CALL_NAME_FOOTBALL_PLAYERS;
            } else {
                callName = ScoresContentHandler.CALL_NAME_CRICKET_PLAYER;
            }
        } else {
            callName = ScoresContentHandler.CALL_NAME_FOOTBALL_SQUAD;
        }
        return callName;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public void initialiseViews(View view) {
        initView(view);
    }

    public void setRequestParameters(HashMap<String,String> params ) {
        this.requestParameters = params;
    }

//    public void requestContent() {
//        if (bundle != null) {
//            if (favouriteItem.getSportsType().equals(Constants.SPORTS_TYPE_FOOTBALL)) {
//                ScoresContentHandler.getInstance().requestSquadContent(SQUAD_FOOTBALL_URL + favouriteItem.getId(), SQUAD_LISTENER_KEY, SQUAD_REQUEST_TAG);
//            } else {
//                //TODO request for cricket squad; dependency API
//                ScoresContentHandler.getInstance().requestSquadContent(SQUAD_CRICKET_URL + favouriteItem.getId(), SQUAD_LISTENER_KEY, SQUAD_REQUEST_TAG);
//                //ScoresContentHandler.getInstance().requestSquadContent(SQUAD_CRICKET_URL + 3, SQUAD_LISTENER_KEY, SQUAD_REQUEST_TAG);
//            }
//        } else {
//
//            ScoresContentHandler.getInstance().requestSquadContent(SQUAD_BASE_URL + teamFirstId + "&team_2=" + teamSecondId, SQUAD_LISTENER_KEY, SQUAD_REQUEST_TAG);
//        }
//    }

    private void initView(View view) {
        context = view.getContext();
        layoutView = view;

        tvTeamFirst = (TextView) view.findViewById(R.id.tv_team_first_name);
        tvTeamSecond = (TextView) view.findViewById(R.id.tv_team_second_name);

        rcRecyclerViewTeamFirst = (RecyclerView) view.findViewById(R.id.rc_child1_rv);
        rcRecyclerViewTeamFirst.setLayoutManager(new LinearLayoutManager(context, VERTICAL, false));
        rcRecyclerViewTeamFirst.setNestedScrollingEnabled(false);
        rcRecyclerViewTeamSecond = (RecyclerView) view.findViewById(R.id.rc_child2_rv);
        rcRecyclerViewTeamSecond.setLayoutManager(new LinearLayoutManager(context, VERTICAL, false));
        rcRecyclerViewTeamSecond.setNestedScrollingEnabled(false);
        upCommingFootballMatchSquadAdapterFirst = new UpCommingFootballMatchSquadAdapter(listTeamFirst, context);
        rcRecyclerViewTeamFirst.setAdapter(upCommingFootballMatchSquadAdapterFirst);
        upCommingFootballMatchSquadAdapterSecond = new UpCommingFootballMatchSquadAdapter(listTeamSecond, context);
        rcRecyclerViewTeamSecond.setAdapter(upCommingFootballMatchSquadAdapterSecond);
        squadParnetLinearLayout = (LinearLayout) view.findViewById(R.id.squad_parent_linearlayout);
        squadParnetLinearLayout.setVisibility(View.GONE);

    }

    private boolean renderDisplay()  {
        boolean success = false;
        try{
            listTeamFirst.clear();
            listTeamSecond.clear();

            if (!response.isNull("data")) {
                JSONObject dataObject;
                if (bundle == null) {
                    dataObject = response.getJSONObject("data");
                    teamFirstSquadArray = dataObject.getJSONArray("team_1_squad");
                    teamSecondSquadArray = dataObject.getJSONArray("team_2_squad");
                } else {
                    teamFirstSquadArray = response.getJSONArray("data");
                    tvTeamFirst.setText(favouriteItem.getName());
                    if (favouriteItem.getSportsType().equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                        layoutView.findViewById(R.id.team2_layout).setVisibility(View.GONE);
                    } else {
                        layoutView.findViewById(R.id.team1_layout).setVisibility(View.GONE);
                        layoutView.findViewById(R.id.team2_layout).setVisibility(View.GONE);
                        CustomRobotoCondenseBoldTextView cricketTeamName = (CustomRobotoCondenseBoldTextView) layoutView.findViewById(R.id.cricket_team_name);
                        cricketTeamName.setVisibility(View.VISIBLE);
                        cricketTeamName.setText("NAME");
                    }
                }
                squadParnetLinearLayout.setVisibility(View.VISIBLE);
                {
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

                }

                success = true;
            } else {
                //nothing
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  success;
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
        if(!playerObject.isNull("assists")){
            dto.setAssist(playerObject.getString("assists"));
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

    private  boolean handleContent(String content){
        boolean success = false;
        try {
            JSONObject jsonObject = new JSONObject(content);
            success = jsonObject.getBoolean("success");
            if (success) {
                response = jsonObject;
            } else {
                //nothing
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    public class UpcomingFootballMatchSquadComponentListener extends CustomComponentListener {

        public UpcomingFootballMatchSquadComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
            super(requestTag, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = UpCommingFootballMatchSqadFragment.this.handleContent(content);
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderDisplay();
            if( success ){
                //nothing
            } else {
                showErrorLayout();
            }
        }

    }


}
