package com.sports.unity.scores.viewhelper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;

/**
 * Created by amandeep on 27/4/16.
 */
public class MatchCommentaryHelper extends BasicVolleyRequestResponseViewHelper {

    @Override
    public String getFragmentTitle() {
        return "Test";
    }

    @Override
    public String getRequestListenerKey() {
        return "Test";
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        return new MatchCommentaryComponentListener( "Request_Tag", null, null);
    }

    public class MatchCommentaryComponentListener extends CustomComponentListener {

        public MatchCommentaryComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
            super(requestTag, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            return false;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {

        }
    }

}
