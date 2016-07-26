package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.GlobalContentItemObject;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class CricketScoreCardHelper extends BasicVolleyRequestResponseViewHelper {

    ScoreCardExpandableListAdapter listAdapter;
    ExpandableListView expListView;

    List<String> listDataHeader = new ArrayList<String>();
    HashMap<String, ArrayList<GlobalContentItemObject>> listDataChild = new HashMap<>();


    private String title;
    private String matchStatus = "";
    private TextView emptyListText;

    private JSONObject response;
    private HashMap<String, String> parameters;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View contentLayout = null;


    public CricketScoreCardHelper(String title, String matchStatus) {
        this.title = title;
        this.matchStatus = matchStatus;
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_completed_match_score_card;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return "CompletedMatchScorecardListenerKey";
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        MatchScoreCardComponentListener matchScoreCardComponentListener = new MatchScoreCardComponentListener(getRequestTag(), progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        return matchScoreCardComponentListener;
    }

    @Override
    public String getRequestTag() {
        return "CompletedCricketMatchScoreCardRequestTag";
    }

    @Override
    public String getRequestCallName() {
        boolean upcoming = ScoresUtil.isCricketMatchUpcoming(matchStatus);
        String callName = null;
        if (upcoming) {
            callName = null;
        } else {
            callName = ScoresContentHandler.CALL_NAME_CRICKET_MATCH_SCORECARD;
        }
        return callName;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return parameters;
    }

    @Override
    public void requestContent() {
        boolean upcoming = ScoresUtil.isCricketMatchUpcoming(matchStatus);
        if (upcoming) {
            //do nothing
        } else {
            super.requestContent();
        }
    }

    @Override
    public void initialiseViews(View view) {
        initView(view);

    }

    public void setRequestParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    private void initView(View view) {
        Context context = view.getContext();

        contentLayout = view.findViewById(R.id.content_layout);
        boolean upcoming = ScoresUtil.isCricketMatchUpcoming(matchStatus);
        if (upcoming) {
            contentLayout.setVisibility(View.VISIBLE);
        } else {
            contentLayout.setVisibility(View.GONE);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                requestContent();
            }

        });

        emptyListText = (TextView) view.findViewById(R.id.empty_scorecard);
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        listAdapter = new ScoreCardExpandableListAdapter(context, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setDivider(null);
        expListView.setChildDivider(null);
        expListView.setEmptyView(emptyListText);
        expListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
            }
        });
    }


    private boolean handleContent(String content) {
        boolean success = false;
        try {
            JSONObject jsonObject = new JSONObject(content);
            success = jsonObject.getBoolean("success");
            if (success) {
                response = jsonObject;
            } else {
                //nothing
            }

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;

    }

    private boolean renderDisplay() {
        boolean success = false;
        try {
            if (!response.isNull("data")) {
                Log.i("response", String.valueOf(response));
                JSONArray jsonArray = response.getJSONArray("data");
                JSONObject dataObject = jsonArray.getJSONObject(0);
                Log.i("data", String.valueOf(dataObject));
                setScoreCard(dataObject);

                contentLayout.setVisibility(View.VISIBLE);
                success = true;
            } else {
                success = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    private void setScoreCard(JSONObject dataObject) throws JSONException {
        listDataHeader.clear();
        listDataChild.clear();
        JSONObject scoreCard = dataObject.getJSONObject("scorecard");
        Iterator iterator = scoreCard.keys();
        iterator = sortIterator(iterator);

        while (iterator.hasNext()) {

            String iteratorKey = (String) iterator.next();

            JSONArray batting;
            JSONArray bowling;
            JSONArray fallOfWickets;

            ArrayList<GlobalContentItemObject> finalListToDisplay = new ArrayList<>();

            JSONObject jsonObject = (JSONObject) scoreCard.get(iteratorKey);

            String header = jsonObject.keys().next();

            JSONObject innings = (JSONObject) jsonObject.get(header);

            batting = innings.getJSONArray("batting");
            for (int i = 0; i < batting.length(); i++) {
                if (i == 0) {
                    finalListToDisplay.add(new GlobalContentItemObject(ScoreCardExpandableListAdapter.TYPE_BATTING_HEADER, innings));
                }
                finalListToDisplay.add(new GlobalContentItemObject(ScoreCardExpandableListAdapter.TYPE_CONTENT_BATTING, batting.get(i)));
            }

            finalListToDisplay.add(new GlobalContentItemObject(ScoreCardExpandableListAdapter.TYPE_EXTRAS_TOTAL, innings));

            bowling = innings.getJSONArray("bowling");
            for (int j = 0; j < bowling.length(); j++) {
                if (j == 0) {
                    finalListToDisplay.add(new GlobalContentItemObject(ScoreCardExpandableListAdapter.TYPE_BOWLING_HEADER, null));
                }
                finalListToDisplay.add(new GlobalContentItemObject(ScoreCardExpandableListAdapter.TYPE_CONTENT_BOWLING, bowling.get(j)));
            }

            if (!innings.isNull("fall_of_wickets")) {
                fallOfWickets = innings.getJSONArray("fall_of_wickets");
                for (int k = 0; k < fallOfWickets.length(); k++) {
                    if (k == 0) {
                        finalListToDisplay.add(new GlobalContentItemObject(ScoreCardExpandableListAdapter.TYPE_FALL_OF_WICKETS_HEADER, null));
                    }
                    finalListToDisplay.add(new GlobalContentItemObject(ScoreCardExpandableListAdapter.TYPE_CONTENT_FALL_OF_WICKETS, fallOfWickets.get(k)));
                }
            }

            String headerText;
            if (dataObject.getString("match_name").contains("Test") || dataObject.getString("match_name").contains("TEST") || dataObject.getString("series_name").contains("TEST")) {
                if (!innings.isNull("inning_name")) {
                    headerText = innings.getString("inning_name");
                } else {
                    headerText = header;
                }
            } else {
                if (!innings.isNull("long_name")) {
                    headerText = innings.getString("long_name");
                } else {
                    headerText = innings.getString("short_name");
                }
            }
            listDataHeader.add(headerText);
            listDataChild.put(headerText, finalListToDisplay);
        }

        listAdapter.updateData(listDataHeader, listDataChild);
        listAdapter.notifyDataSetChanged();
        expListView.expandGroup(0);

    }

    private Iterator sortIterator(Iterator iterator) {
        List<String> sortedList = new ArrayList<>();
        while (iterator.hasNext()) {
            sortedList.add((String) iterator.next());
        }
        Collections.sort(sortedList);
        return sortedList.iterator();
    }


    public class MatchScoreCardComponentListener extends CustomComponentListener {

        public MatchScoreCardComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout, View contentLayout, SwipeRefreshLayout swipeRefreshLayout) {
            super(requestTag, progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = CricketScoreCardHelper.this.handleContent(content);
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
