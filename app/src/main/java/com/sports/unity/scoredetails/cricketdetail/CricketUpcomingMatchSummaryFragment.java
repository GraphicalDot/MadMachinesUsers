package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class CricketUpcomingMatchSummaryFragment extends Fragment implements CricketUpcomingMatchSummaryHandler.CricketUpcomingMatchSummaryContentListener{

    private TextView tvMatchName;
    private TextView tvMatchDate;
    private TextView tvMatchToss;
    public CricketUpcomingMatchSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String matchId =  getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        CricketUpcomingMatchSummaryHandler cricketUpcomingMatchSummaryHandler = CricketUpcomingMatchSummaryHandler.getInstance(context);
        cricketUpcomingMatchSummaryHandler.addListener(this);
        cricketUpcomingMatchSummaryHandler.requestCricketUpcommingMatchSummary(matchId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cricket_upcoming_match_summery, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {
        tvMatchName = (TextView) view.findViewById(R.id.tv_match_name);
        tvMatchDate = (TextView) view.findViewById(R.id.tv_match_date);
        tvMatchToss = (TextView) view.findViewById(R.id.tv_match_toss);
        initErrorLayout(view);

    }

    @Override
    public void handleContent(JSONObject object) {
        {
            try {

                boolean success = object.getBoolean("success");
                boolean error = object.getBoolean("error");

                if( success ) {

                    renderDisplay(object);

                } else {
                    Toast.makeText(getActivity(), R.string.match_not_exist, Toast.LENGTH_SHORT).show();
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

        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = jsonObject.getJSONObject("data");
                        Log.i("run: ", jsonObject.toString());
                        tvMatchName.setText(data.getString(""));
                        tvMatchDate.setText(data.getString(""));
                        tvMatchToss.setText(data.getString(""));


                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }
}
