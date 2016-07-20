package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.GlobalContentItemObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by madmachines on 15/7/16.
 */
public class ScoreCardExpandableListAdapter extends BaseExpandableListAdapter {

    public static final int TYPE_BATTING_HEADER = 1;
    public static final int TYPE_BOWLING_HEADER = 2;
    public static final int TYPE_FALL_OF_WICKETS_HEADER = 3;

    public static final int TYPE_CONTENT_BATTING = 4;
    public static final int TYPE_CONTENT_BOWLING = 5;
    public static final int TYPE_CONTENT_FALL_OF_WICKETS = 6;

    public static final int TYPE_EXTRAS_TOTAL = 7;

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<GlobalContentItemObject>> listDataChild;

    public ScoreCardExpandableListAdapter(Context context, List<String> listDataHeader,
                                          HashMap<String, ArrayList<GlobalContentItemObject>> listDataChild) {
        this.context = context;
//        if (listDataHeader.size() > 0)
//            this.listDataHeader.addAll(listDataHeader);
//        if (listDataChild.size() > 0)
//            this.listDataChild.putAll(listDataChild);
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        int layoutId = 0;

        int itemType = 0;

        GlobalContentItemObject globalContentItemObject = (GlobalContentItemObject) getChild(groupPosition, childPosition);

        itemType = globalContentItemObject.getType();
        layoutId = getLayoutId(layoutId, itemType);

        LayoutInflater infalInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(layoutId, null);
        convertView.setTag(itemType);

        displayData(itemType, groupPosition, childPosition, convertView, globalContentItemObject);

        return convertView;
    }

    private int getLayoutId(int layoutId, int itemType) {

        switch (itemType) {
            case TYPE_BATTING_HEADER:
                layoutId = R.layout.cricket_scorecard_batting_header;
                break;
            case TYPE_BOWLING_HEADER:
                layoutId = R.layout.cricket_scorecard_bowling_header;
                break;
            case TYPE_FALL_OF_WICKETS_HEADER:
                layoutId = R.layout.cricket_scorecard_fall_of_wickets_header;
                break;
            case TYPE_CONTENT_BATTING:
                layoutId = R.layout.fragment_live_cricket_batting_card;
                break;
            case TYPE_CONTENT_BOWLING:
                layoutId = R.layout.fragment_live_cricket_bowling_card;
                break;
            case TYPE_CONTENT_FALL_OF_WICKETS:
                layoutId = R.layout.fragment_live_cricket_fall_of_wickets_card;
                break;
            case TYPE_EXTRAS_TOTAL:
                layoutId = R.layout.cricket_scorecard_extras_total;
                break;
        }
        return layoutId;
    }

    private void displayData(int itemType, int groupPosition, int childPosition, View convertView, GlobalContentItemObject globalContentItemObject) {
        switch (itemType) {
            case TYPE_BATTING_HEADER:
                displayBattingHeader(convertView, globalContentItemObject);
                break;
            case TYPE_BOWLING_HEADER:
                //do nothing
                break;
            case TYPE_FALL_OF_WICKETS_HEADER:
                //do nothing
                break;
            case TYPE_CONTENT_BATTING:
                displayBattingData(convertView, globalContentItemObject);
                break;
            case TYPE_CONTENT_BOWLING:
                displayBowlingData(convertView, globalContentItemObject);
                break;
            case TYPE_CONTENT_FALL_OF_WICKETS:
                displayFallOfWicketsData(convertView, globalContentItemObject);
                break;
            case TYPE_EXTRAS_TOTAL:
                displayExtra(convertView, globalContentItemObject);
                break;
        }
    }

    private void displayBattingHeader(View convertView, GlobalContentItemObject globalContentItemObject) {
        TextView name = (TextView) convertView.findViewById(R.id.tv_team_first_name);
        TextView overs = (TextView) convertView.findViewById(R.id.tv_match_over);

        JSONObject innings = (JSONObject) globalContentItemObject.getObject();
        try {
            name.setText(innings.getString("short_name") + " " + innings.getString("runs") + "/" + innings.getString("wickets"));
            overs.setText("(" + innings.getString("overs") + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayExtra(View convertView, GlobalContentItemObject globalContentItemObject) {
        TextView total = (TextView) convertView.findViewById(R.id.tv_total_run_first_team);
        TextView runRate = (TextView) convertView.findViewById(R.id.tv_run_rate_first_team);
        TextView extras = (TextView) convertView.findViewById(R.id.tv_extra_run_team_first);

        JSONObject innings = (JSONObject) globalContentItemObject.getObject();
        try {
            total.setText(innings.getString("runs"));
            runRate.setText(innings.getString("run_rate"));
            extras.setText("Extras " + innings.getString("extra"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayFallOfWicketsData(View convertView, GlobalContentItemObject globalContentItemObject) {
        TextView wickets = (TextView) convertView.findViewById(R.id.tv_wicket);
        TextView nameOfBowler = (TextView) convertView.findViewById(R.id.tv_bowler_name);
        TextView over = (TextView) convertView.findViewById(R.id.tv_over_number);

        JSONObject fallOfWicketsObject = (JSONObject) globalContentItemObject.getObject();

        try {
            wickets.setText(fallOfWicketsObject.getString("fow_score"));
            nameOfBowler.setText(fallOfWicketsObject.getString("name"));
            over.setText(fallOfWicketsObject.getString("fow_over"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void displayBowlingData(View convertView, GlobalContentItemObject globalContentItemObject) {
        TextView bowlerName = (TextView) convertView.findViewById(R.id.tv_bowler_name);
        TextView overs = (TextView) convertView.findViewById(R.id.tv_over);
        TextView maidenOver = (TextView) convertView.findViewById(R.id.tv_midden_over);
        TextView runs = (TextView) convertView.findViewById(R.id.tv_runs);
        TextView wickets = (TextView) convertView.findViewById(R.id.tv_wicket);
        TextView extras = (TextView) convertView.findViewById(R.id.tv_extra);

        JSONObject bowlingObject = (JSONObject) globalContentItemObject.getObject();

        try {
            bowlerName.setText(bowlingObject.getString("bowler_name"));
            overs.setText(bowlingObject.getString("overs"));
            maidenOver.setText(bowlingObject.getString("maidens"));
            runs.setText(bowlingObject.getString("runs"));
            wickets.setText(bowlingObject.getString("wickets"));
            extras.setText(bowlingObject.getString("extras"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayBattingData(View convertView, GlobalContentItemObject globalContentItemObject) {
        TextView batsmanName = (TextView) convertView.findViewById(R.id.tv_player_name);
        TextView wicketBy = (TextView) convertView.findViewById(R.id.tv_wicket_by);
        TextView runs = (TextView) convertView.findViewById(R.id.tv_player_run);
        TextView balls = (TextView) convertView.findViewById(R.id.tv_ball_play_by_player);
        TextView fours = (TextView) convertView.findViewById(R.id.tv_four_gain_by_player);
        TextView sixes = (TextView) convertView.findViewById(R.id.tv_six_gain_by_player);
        TextView strikeRate = (TextView) convertView.findViewById(R.id.tv_sr_rate_of_player);

//        GlobalContentItemObject globalContentItemObject = (GlobalContentItemObject) getChild(groupPosition, childPosition);
        JSONObject battingObject = (JSONObject) globalContentItemObject.getObject();

        try {
            batsmanName.setText(battingObject.getString("batsman_name"));
            wicketBy.setText(battingObject.getString("how_out"));
            runs.setText(battingObject.getString("runs"));
            balls.setText(battingObject.getString("balls"));
            fours.setText(battingObject.getString("four"));
            sixes.setText(battingObject.getString("six"));
            strikeRate.setText(battingObject.getString("strike_rate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

//    @Override
//    public int getChildType(int groupPosition, int childPosition) {
//        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition).getType();
//    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        LayoutInflater infalInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.list_group, null);


        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.innings);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        ImageView expanseIndicator = (ImageView) convertView.findViewById(R.id.iv_down);
        if (isExpanded) {
            expanseIndicator.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_arrow_gray));
        } else {
            expanseIndicator.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_down_arrow_gray));
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void updateData(List<String> listDataHeader, HashMap<String, ArrayList<GlobalContentItemObject>> listDataChild) {

        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }


}
