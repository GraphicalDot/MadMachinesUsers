package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.controller.FilterActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scoredetails.FragementInterface;
import com.sports.unity.scoredetails.model.CricketScoreCard;
import com.sports.unity.scores.controller.fragment.MatchListAdapter;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cfeindia on 3/2/16.
 */
public class CricketMatchDetailFragment extends Fragment implements FragementInterface<CricketScoreCard>{


private static final String ARG_COLUMN_COUNT = "column-count";

private int mColumnCount = 1;
private OnListFragmentInteractionListener mListener;

        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
        public CricketMatchDetailFragment()
        {
        }

        public static CricketMatchDetailFragment newInstance(int columnCount)
        {
            CricketMatchDetailFragment fragment = new CricketMatchDetailFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_COLUMN_COUNT, columnCount);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            if (getArguments() != null)
            {
                mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View view = inflater.inflate(R.layout.fragment_cricket_match_score_detail, container, false);
//             View view = inflater.inflate(R)


            return view;
        }


        @Override
        public void onAttach(Context context)
        {
            super.onAttach(context);
            if (context instanceof OnListFragmentInteractionListener)
            {
                mListener = (OnListFragmentInteractionListener) context;
            } else
            {
                throw new RuntimeException(context.toString()
                        +
                        " must implement OnListFragmentChildInteractionListener");
            }
        }

        @Override
        public void onDetach()
        {
            super.onDetach();
            mListener = null;
        }

    @Override
    public List<CricketScoreCard> getItems() {
        return null;
    }

    /**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface OnListFragmentInteractionListener
{
    void onListFragmentInteraction(CricketScoreCard item);
}
}
