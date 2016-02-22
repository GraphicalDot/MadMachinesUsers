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

import org.json.JSONException;
import org.json.JSONObject;

public class CricketUpcomingMatchScoreCardFragment extends Fragment implements CricketUpcomingMatchScoreCardHandler.UpcommingCricketMatchContentListener{
  private TextView textView;
    public CricketUpcomingMatchScoreCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String matchId =  getActivity().getIntent().getStringExtra("matchId");
        matchId = "rsaeng_2015_t20_01";
        CricketUpcomingMatchScoreCardHandler cricketUpcomingMatchScoreCardHandler = CricketUpcomingMatchScoreCardHandler.getInstance(context);
        cricketUpcomingMatchScoreCardHandler.addListener(this);
        cricketUpcomingMatchScoreCardHandler.requestCompletdMatchScoreCard(matchId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_empty_view, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {
        textView = (TextView) view.findViewById(R.id.tv_empty_view);
        textView.setText(R.string.scorecard_not_exist);
        initErrorLayout(view);

    }

    @Override
    public void handleContent(JSONObject object) {
        {
            try {
                boolean success = object.getBoolean("success");
                boolean error = object.getBoolean("error");

                if( success ) {



                } else {
                    Toast.makeText(getActivity(), R.string.match_not_exist, Toast.LENGTH_SHORT).show();
                    showErrorLayout(getView());
                }
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getActivity(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
            }
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



}
