package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

public class CricketUpcomingMatchSummaryFragment extends Fragment implements CricketUpcomingMatchSummaryHandler.CricketUpcomingMatchSummaryContentListener{

    private TextView tvMatchName;
    private TextView tvMatchDate;
    private TextView tvMatchToss;
    private ProgressBar progressBar;
    String toss = "";
    String matchName="";
    String date = "";
    private LinearLayout errorLayout;
    private CricketUpcomingMatchSummaryHandler cricketUpcomingMatchSummaryHandler;
    public CricketUpcomingMatchSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        String matchId =  i.getStringExtra(INTENT_KEY_ID);
        matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
        toss = i.getStringExtra(INTENT_KEY_TOSS);
        date = i.getStringExtra(INTENT_KEY_DATE);
        cricketUpcomingMatchSummaryHandler = CricketUpcomingMatchSummaryHandler.getInstance(context);
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
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        initErrorLayout(view);

    }
    private void  showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void  hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void handleContent(String content) {
        {
            showProgressBar();
            try {
                JSONObject object = new JSONObject(content);
                boolean success = object.getBoolean("success");
                boolean error = object.getBoolean("error");
                 if( success ) {

                    renderDisplay(object);

                } else {
                    //showErrorLayout(getView());
                    renderDisplay(object);
                }
            }catch (Exception ex){
                ex.printStackTrace();
                showErrorLayout(getView());
            }
        }
    }
    private void initErrorLayout(View view) {
        try {
             errorLayout = (LinearLayout) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.GONE);
        }catch (Exception e){e.printStackTrace();}
    }

    private void showErrorLayout(View view) {

         errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);

    }

    private void renderDisplay(final JSONObject jsonObject) throws JSONException {

        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        hideProgressBar();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //JSONArray dataArray = jsonObject.getJSONArray("data");
                        Log.i("run: ", jsonObject.toString());
                        initData();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

    private void initData() {
        if (matchName != null && !matchName.equalsIgnoreCase("")) {
            tvMatchName.setText(matchName);
        } else {
            tvMatchName.setText("Information is not Available");
        }
        if (date != null && !date.equalsIgnoreCase("")) {
            tvMatchDate.setText(DateUtil.getFormattedDate(date));
        } else {
            tvMatchName.setText("Information is not Available");
        }
        if (toss != null && !toss.equalsIgnoreCase("")) {
            tvMatchToss.setText(toss);
        } else {
            tvMatchToss.setText("Information is not Available");
        }
    }


}
