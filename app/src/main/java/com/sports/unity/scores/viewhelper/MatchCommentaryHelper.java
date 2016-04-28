package com.sports.unity.scores.viewhelper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scores.model.ScoresContentHandler;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by amandeep on 27/4/16.
 */
public class MatchCommentaryHelper extends BasicVolleyRequestResponseViewHelper {

    private HashMap<String, String> requestParameters = null;

    public MatchCommentaryHelper(){

    }

    @Override
    public String getFragmentTitle() {
        return "Commentary";
    }

    @Override
    public String getRequestListenerKey() {
        return "Commentary";
    }

    @Override
    public String getRequestTag() {
        return "CommentaryRequestTag";
    }

    @Override
    public String getRequestCallName() {
        return ScoresContentHandler.CALL_NAME_MATCH_COMMENTARIES;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        return new MatchCommentaryComponentListener( getRequestTag(), null, null);
    }

    public void setRequestParameters(HashMap<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public class MatchCommentaryComponentListener extends CustomComponentListener {

        public MatchCommentaryComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
            super(requestTag, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            JSONObject jsonObject = null;
            try{
                jsonObject = new JSONObject(content);
                if( jsonObject != null ){

                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return true;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {

        }

    }

}
