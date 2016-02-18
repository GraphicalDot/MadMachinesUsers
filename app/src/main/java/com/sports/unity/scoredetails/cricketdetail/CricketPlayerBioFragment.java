package com.sports.unity.scoredetails.cricketdetail;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by madmachines on 15/2/16.
 */
public class CricketPlayerBioFragment extends Fragment implements CricketPlayerbioHandler.ContentListener {

    private CircleImageView playerProfileImage;
    private TextView playerName;
    private TextView playerNationName;
    private TextView tvPlayerDateOfPlace;
    private TextView tvPlayerDateOfBirth;
    private TextView tvPlayerbattingStyle;
    private TextView tvPlayerBowingStyle;
    private TextView tvPlayerMajorTeam;
    private ImageView ivDown;
    private ImageView ivDownSecond;
    public CricketPlayerBioFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       String playerId =  getActivity().getIntent().getStringExtra("playerId");
        playerId = "6f65e8cd45ae14c916cf2c1c69b6102c";
        CricketPlayerbioHandler cricketPlayerbioHandler = CricketPlayerbioHandler.getInstance(context);
        cricketPlayerbioHandler.addListener(this);
        cricketPlayerbioHandler.requestData(playerId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player_cricket_bio, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {
        playerProfileImage = (CircleImageView) view.findViewById(R.id.cricket_player_profile_image);
        playerName = (TextView) view.findViewById(R.id.player_name);
        playerNationName = (TextView) view.findViewById(R.id.tv_player_nation_name);
        initErrorLayout(view);

    }

    @Override
    public void handleContent(String content) {
        try {

            JSONObject jsonObject = new JSONObject(content);

            boolean success = jsonObject.getBoolean("success");
            boolean error = jsonObject.getBoolean("error");

            if( success ) {

                renderDisplay(jsonObject);

            } else {
                Toast.makeText(getActivity(), R.string.player_details_not_exists, Toast.LENGTH_SHORT).show();
                showErrorLayout(getView());
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getActivity(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
        }
    }
    private void initErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);

        TextView oops = (TextView) errorLayout.findViewById(R.id.oops);
        oops.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoLight());

        TextView something_wrong = (TextView) errorLayout.findViewById(R.id.something_wrong);
        something_wrong.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoLight());
    }

    private void showErrorLayout(View view) {

            LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.VISIBLE);

    }

    private void renderDisplay(JSONObject jsonObject) throws JSONException {
        final JSONObject data = (JSONObject) jsonObject.get("data");
        final JSONObject playerInfo = (JSONObject) data.get("info");
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!playerInfo.isNull("Full Name")) {
                            playerName.setText(playerInfo.getString("Full Name"));
                        }
                        if (!playerInfo.isNull("Born")) {
                            tvPlayerDateOfBirth.setText(playerInfo.getString("Born"));
                        }
                    } catch (Exception ex) {

                    }
                }
            });
        }

    }
}
