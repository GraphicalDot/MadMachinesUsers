package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sports.unity.R;


public class CompletedMatchScoreCardFragment extends Fragment implements CompletedMatchScoreCardHandler.ContentListener{




    private OnFragmentInteractionListener mListener;

    public CompletedMatchScoreCardFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        CompletedMatchScoreCardHandler completedMatchScoreCardHandler = CompletedMatchScoreCardHandler.getInstance();
        completedMatchScoreCardHandler.requestScoreCardDetail();
        return inflater.inflate(R.layout.fragment_completed_match_score_card, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void handleContent(int responseCode) {
        if(responseCode == 0){
        displayResult();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void displayResult(){

    }


}
