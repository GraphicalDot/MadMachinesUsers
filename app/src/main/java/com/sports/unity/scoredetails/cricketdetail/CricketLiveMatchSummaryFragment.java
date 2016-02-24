package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;


public class CricketLiveMatchSummaryFragment extends Fragment implements  CricketLiveMatchSummaryHandler.LiveCricketMatchSummaryContentListener {

    private ImageView ivFirstBall;
    private ImageView ivSecondBall;
    private ImageView ivThirdBall;
    private ImageView ivFourthBall;
    private ImageView ivFifthBall;
    private ImageView ivSixthBall;
    private ImageView ivFirstPlayer;
    private TextView tvFirstPlayerName;
    private TextView tvFirstPlayerRunRate;
    private TextView tvFirstPlayerRunOnBall;
    private TextView tvPartnershipRecord;
    private TextView tvSecondPlayerName;
    private TextView tvSecondPlayerRunRate;
    private TextView tvSecondPlayerRunOnBall;
    private ImageView ivPlayerSecond;
    private ImageView ivUppComingPlayerFirst;
    private ImageView ivUppComingPlayerSecond;
    private ImageView ivUppComingPlayerThird;
    private TextView tvSecondUpComingPlayerName;
    private TextView tvThirdUpComingPlayerName;
    private TextView tvFirstUpComingPlayerName;
    private TextView tvFirstUpComingPlayerRunRate;
    private TextView tvSecondUpComingPlayerRunRate;
    private TextView tvThirdUpComingPlayerRunRate;
    private ImageView ivBowlerProfile;
    private TextView tvBowlerName;
    private TextView tvBowlerOverlabel;
    private TextView tvBowlerWRun;
    private TextView tvBowlerEcon;
    private TextView tvBowlerOver;
    private TextView tvBowlerWr;
   public CricketLiveMatchSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String matchId =  getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
       CricketLiveMatchSummaryHandler cricketLiveMatchSummaryHandler = CricketLiveMatchSummaryHandler.getInstance(context);
        cricketLiveMatchSummaryHandler.addListener(this);
        cricketLiveMatchSummaryHandler.requestLiveMatchSummary(matchId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cricket_live_match_summery, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {
        ivFirstBall = (ImageView) view.findViewById(R.id.iv_first_ball);
        ivSecondBall = (ImageView) view.findViewById(R.id.iv_second_ball);
        ivThirdBall = (ImageView) view.findViewById(R.id.iv_third_ball);
        ivFourthBall = (ImageView) view.findViewById(R.id.iv_fourth_ball);
        ivFifthBall = (ImageView) view.findViewById(R.id.iv_fifth_ball);
        ivSixthBall = (ImageView) view.findViewById(R.id.iv_sixth_ball);
        ivFirstPlayer = (ImageView) view.findViewById(R.id.iv_player_first);
        tvFirstPlayerName = (TextView) view.findViewById(R.id.tv_first_player_name);
        tvFirstPlayerRunRate = (TextView) view.findViewById(R.id.tv_first_player_run_rate);
        tvFirstPlayerRunOnBall = (TextView) view.findViewById(R.id.tv_first_player_run_on_ball);
        tvPartnershipRecord = (TextView) view.findViewById(R.id.tv_partnership_record);
        tvSecondPlayerName = (TextView) view.findViewById(R.id.tv_first_player_name);
        tvSecondPlayerRunRate = (TextView) view.findViewById(R.id.tv_first_player_run_rate);
        tvSecondPlayerRunOnBall = (TextView) view.findViewById(R.id.tv_second_player_run_on_ball);
        ivPlayerSecond = (ImageView) view.findViewById(R.id.iv_player_first);
        ivUppComingPlayerFirst = (ImageView) view.findViewById(R.id.iv_upp_coming_player_first);
        ivUppComingPlayerSecond = (ImageView) view.findViewById(R.id.iv_up_coming_player_second);
        ivUppComingPlayerThird = (ImageView) view.findViewById(R.id.iv_up_coming_player_third);
        tvSecondUpComingPlayerName = (TextView) view.findViewById(R.id.tv_second_up_coming_player_name);
        tvThirdUpComingPlayerName = (TextView) view.findViewById(R.id.tv_third_up_coming_player_name);
        tvFirstUpComingPlayerName = (TextView) view.findViewById(R.id.tv_first_up_coming_player_name);
        tvFirstUpComingPlayerRunRate = (TextView) view.findViewById(R.id.tv_first_up_coming_player_run_rate);
        tvSecondUpComingPlayerRunRate = (TextView) view.findViewById(R.id.tv_second_up_coming_player_run_rate);
        tvThirdUpComingPlayerRunRate = (TextView) view.findViewById(R.id.tv_third_up_coming_player_run_rate);
        ivBowlerProfile = (ImageView) view.findViewById(R.id.iv_bowler_profile);
        tvBowlerName = (TextView) view.findViewById(R.id.tv_bowler_name);
        tvBowlerOverlabel = (TextView) view.findViewById(R.id.tv_bowler_over_label);
        tvBowlerWRun = (TextView) view.findViewById(R.id.tv_bowler_W_Run);
        tvBowlerEcon = (TextView) view.findViewById(R.id.tv_bowler_econ);
        tvBowlerOver = (TextView) view.findViewById(R.id.tv_bowler_over);
        tvBowlerWr = (TextView) view.findViewById(R.id.tv_bowler_wr);

        initErrorLayout(view);

    }

    @Override
    public void handleContent(String content) {

        try {
            JSONObject object = new JSONObject(content);
            boolean success = object.getBoolean("success");
            boolean error = object.getBoolean("error");

            if (success) {

                renderDisplay(object);

            } else {
                Toast.makeText(getActivity(), R.string.match_not_exist, Toast.LENGTH_SHORT).show();
                showErrorLayout(getView());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
        }

    }
    private void initErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);

    }

    private void showErrorLayout(View view) {

        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);

    }

    private void renderDisplay(final JSONObject scoreCard) throws JSONException {

        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i("run: ", scoreCard.toString());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

}
