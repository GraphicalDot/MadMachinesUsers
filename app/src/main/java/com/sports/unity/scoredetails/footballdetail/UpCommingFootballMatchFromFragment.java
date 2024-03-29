package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.sports.unity.R;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchFromFragment extends BasicVolleyRequestResponseViewHelper {

    private static final String REQUEST_TAG = "UPCOMING_FOOTBALL_MATCH_FORM";

    private String title;
    private HashMap<String, String> requestParameters;
    private JSONObject response;

    private String team1;
    private String team2;

    private Context context;
    private View emptyView;
    private View contentLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView tvnamefirstteam;
    private ImageView[] ivfirstmatchteamfirst = new ImageView[5];
    private TextView tvpointoffirstteam;
    private TextView tvwinmatchoffirstteam;
    private TextView tvdrawmatchoffirstteam;
    private TextView tvlossmatchoffirstteam;
    private TextView tvnamesecondteam;
    private ImageView[] tvfirstmatchteamsecond = new ImageView[5];

    private TextView tvpointofsecondteam;
    private TextView tvwinmatchofsecondteam;
    private TextView tvdrawmatchofsecondteam;
    private TextView tvlossmatchofsecondteam;

    private RelativeLayout team1PointHistoryLayout;
    private RelativeLayout team2PointHistoryLayout;

    private TextView team1Name;
    private TextView team2Name;

    private View view1;
    private View view2;

    public UpCommingFootballMatchFromFragment(String title, Intent intent) {
        this.title = title;

        team1 = intent.getStringExtra(Constants.INTENT_KEY_TEAM1_NAME);
        team2 = intent.getStringExtra(Constants.INTENT_KEY_TEAM2_NAME);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_football_upcoming_match_form_v2;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return "FootballFormRequestListener";
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        UpcomingFootballMatchFormComponentListener componentListener = new UpcomingFootballMatchFormComponentListener(getRequestTag(), progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        return componentListener;
    }

    @Override
    public String getRequestTag() {
        return REQUEST_TAG;
    }

    @Override
    public String getRequestCallName() {
        return ScoresContentHandler.CALL_NAME_FOOTBALL_FORM;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public void initialiseViews(View view) {
        initView(view);
    }


    public void setRequestParameters(HashMap<String, String> params) {
        this.requestParameters = params;
    }

    private void initView(View view) {
        context = view.getContext();

        contentLayout = view.findViewById(R.id.content_layout);
        contentLayout.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                requestContent();
            }

        });

        emptyView = view.findViewById(R.id.tv_empty_view);

        tvnamefirstteam = (TextView) view.findViewById(R.id.tv_name_first_team);
        ivfirstmatchteamfirst[0] = (ImageView) view.findViewById(R.id.iv_first_match_team_first);
        ivfirstmatchteamfirst[1] = (ImageView) view.findViewById(R.id.iv_second_match_team_first);
        ivfirstmatchteamfirst[2] = (ImageView) view.findViewById(R.id.iv_third_match_team_first);
        ivfirstmatchteamfirst[3] = (ImageView) view.findViewById(R.id.iv_forth_match_team_first);
        ivfirstmatchteamfirst[4] = (ImageView) view.findViewById(R.id.iv_fifth_match_team_first);
        tvpointoffirstteam = (TextView) view.findViewById(R.id.tv_point_of_first_team);
        tvwinmatchoffirstteam = (TextView) view.findViewById(R.id.tv_win_match_of_first_team);
        tvdrawmatchoffirstteam = (TextView) view.findViewById(R.id.tv_draw_match_of_first_team);
        tvlossmatchoffirstteam = (TextView) view.findViewById(R.id.tv_loss_match_of_first_team);
        tvnamesecondteam = (TextView) view.findViewById(R.id.tv_name_second_team);
        tvfirstmatchteamsecond[0] = (ImageView) view.findViewById(R.id.tv_first_match_team_second);
        tvfirstmatchteamsecond[1] = (ImageView) view.findViewById(R.id.tv_second_match_team_second);
        tvfirstmatchteamsecond[2] = (ImageView) view.findViewById(R.id.tv_third_match_team_second);
        tvfirstmatchteamsecond[3] = (ImageView) view.findViewById(R.id.tv_forth_match_team_second);
        tvfirstmatchteamsecond[4] = (ImageView) view.findViewById(R.id.tv_fifth_match_team_second);
        tvpointofsecondteam = (TextView) view.findViewById(R.id.tv_point_of_second_team);
        tvwinmatchofsecondteam = (TextView) view.findViewById(R.id.tv_win_match_of_second_team);
        tvdrawmatchofsecondteam = (TextView) view.findViewById(R.id.tv_draw_match_of_second_team);
        tvlossmatchofsecondteam = (TextView) view.findViewById(R.id.tv_loss_match_of_second_team);

        team1PointHistoryLayout = (RelativeLayout) view.findViewById(R.id.layout_team_history_value);
        team2PointHistoryLayout = (RelativeLayout) view.findViewById(R.id.layout_team_history_value_second_team);


        view1 = view.findViewById(R.id.first_view);
        view2 = view.findViewById(R.id.second_view);

        team1Name = (TextView) view.findViewById(R.id.tv_first_premiera_division);
        team2Name = (TextView) view.findViewById(R.id.tv_second_premiera_division);
        team1Name.setVisibility(View.GONE);
        team2Name.setVisibility(View.GONE);
    }

    private boolean renderDisplay() {
        boolean success = false;
        boolean noData = false;
        try {
            JSONArray dataArray = response.getJSONArray("data");
            {
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject teamFromObject = dataArray.getJSONObject(i);
                    if (!teamFromObject.isNull("team_name")) {
                        if (team1.equals(teamFromObject.getString("team_name"))) {
                            tvnamefirstteam.setText(teamFromObject.getString("team_name"));
                            if (!teamFromObject.isNull("recent_form")) {
                                String recentForm = teamFromObject.getString("recent_form");
                                if (recentForm != null && recentForm.length() > 0) {
                                    initializeTeamForms(recentForm);
                                } else {
                                    noData = true;
                                }
                            }
                            int team1PointHistory = 0;
                            if (!teamFromObject.isNull("team_points")) {
                                team1PointHistory += Integer.parseInt(teamFromObject.getString("team_points"));
                                tvpointoffirstteam.setText(teamFromObject.getString("team_points"));
                            }
                            if (!teamFromObject.isNull("games_won")) {
                                team1PointHistory += Integer.parseInt(teamFromObject.getString("games_won"));
                                tvwinmatchoffirstteam.setText(teamFromObject.getString("games_won"));
                            }
                            if (!teamFromObject.isNull("games_drawn")) {
                                team1PointHistory += Integer.parseInt(teamFromObject.getString("games_drawn"));
                                tvdrawmatchoffirstteam.setText(teamFromObject.getString("games_drawn"));
                            }
                            if (!teamFromObject.isNull("games_lost")) {
                                team1PointHistory += Integer.parseInt(teamFromObject.getString("games_lost"));
                                tvlossmatchoffirstteam.setText(teamFromObject.getString("games_lost"));
                            }
                            if (team1PointHistory == 0) {
                                team1Name.setVisibility(View.GONE);
                                team1PointHistoryLayout.setVisibility(View.GONE);
                                view1.setVisibility(View.GONE);
                            }
                        } else if (team2.equals(teamFromObject.getString("team_name"))) {
                            tvnamesecondteam.setText(teamFromObject.getString("team_name"));
                            if (!teamFromObject.isNull("recent_form")) {
                                String recentForm = teamFromObject.getString("recent_form");
                                if (recentForm != null && recentForm.length() > 0) {
                                    initFromDataTeamSecond(recentForm);
                                } else {
                                    noData = true;
                                }
                            }
                            int team2PointHistory = 0;
                            if (!teamFromObject.isNull("team_points")) {
                                team2PointHistory += Integer.parseInt(teamFromObject.getString("team_points"));
                                tvpointofsecondteam.setText(teamFromObject.getString("team_points"));
                            }
                            if (!teamFromObject.isNull("games_won")) {
                                team2PointHistory += Integer.parseInt(teamFromObject.getString("games_won"));
                                tvwinmatchofsecondteam.setText(teamFromObject.getString("games_won"));
                            }
                            if (!teamFromObject.isNull("games_drawn")) {
                                team2PointHistory += Integer.parseInt(teamFromObject.getString("games_drawn"));
                                tvdrawmatchofsecondteam.setText(teamFromObject.getString("games_drawn"));
                            }
                            if (!teamFromObject.isNull("games_lost")) {
                                team2PointHistory += Integer.parseInt(teamFromObject.getString("games_lost"));
                                tvlossmatchofsecondteam.setText(teamFromObject.getString("games_lost"));
                            }
                            if (team2PointHistory == 0) {
                                team2Name.setVisibility(View.GONE);
                                team2PointHistoryLayout.setVisibility(View.GONE);
                                view2.setVisibility(View.GONE);
                            }

                        } else {
                            noData = true;
                        }
                    }
                }
            }

            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (noData) {
            if (contentLayout.getVisibility() == View.GONE) {
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            contentLayout.setVisibility(View.VISIBLE);
        }

        return success;
    }

    private void initFromDataTeamSecond(String recentForm) {
        for (int i = 0; i < recentForm.length(); i++) {
            tvfirstmatchteamsecond[i].setImageDrawable(getBallColor("" + recentForm.charAt(i), getBallColor(recentForm.charAt(i))));
        }
    }

    private void initializeTeamForms(String recentForm) {
        for (int i = 0; i < recentForm.length(); i++) {
            ivfirstmatchteamfirst[i].setImageDrawable(getBallColor("" + recentForm.charAt(i), getBallColor(recentForm.charAt(i))));
        }
    }

    private int getBallColor(char c) {
        Log.i("getBallColor: ", " " + c);
        int color = 0;
        switch (c) {
            case 'W':
            case 'w':
                color = context.getResources().getColor(R.color.green);
                break;
            case 'L':
            case 'l':
                color = context.getResources().getColor(R.color.loose);
                break;
            case 'D':
            case 'd':
                color = context.getResources().getColor(R.color.draw);
                break;
            default:
                color = Color.WHITE;
        }

        return color;
    }

    private Drawable getBallColor(String text, int color) {
        int radius = context.getResources().getDimensionPixelSize(R.dimen.recent_ball_radius);
        int border = context.getResources().getDimensionPixelSize(R.dimen.user_image_border);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig().textColor(Color.WHITE)
                .withBorder(border)
                .width(radius)
                .height(radius)
                .bold()
                .endConfig()
                .buildRound(text, color);
        return drawable;
    }

    public boolean handleContent(String content) {
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

    public class UpcomingFootballMatchFormComponentListener extends CustomComponentListener {

        public UpcomingFootballMatchFormComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout, View contentLayout, SwipeRefreshLayout swipeRefreshLayout) {
            super(requestTag, progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = UpCommingFootballMatchFromFragment.this.handleContent(content);
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderDisplay();
            if (success) {
                //nothing
            } else {
                showErrorLayout();
            }
        }

    }

}
