package com.sports.unity.common.viewhelper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.util.CommonUtil;

/**
 * Created by amandeep on 27/4/16.
 */
public abstract class CustomComponentListener {

    public static void renderAppropriateErrorLayout(ViewGroup errorLayout){
        if( CommonUtil.isInternetConnectionAvailable(errorLayout.getContext()) ){
            {
                View view = errorLayout.findViewById(R.id.data_error);
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            {
                View view = errorLayout.findViewById(R.id.connection_error);
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        } else {
            {
                View view = errorLayout.findViewById(R.id.data_error);
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }

            {
                View view = errorLayout.findViewById(R.id.connection_error);
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private ProgressBar progressBar = null;
    private ViewGroup errorLayout = null;

    private String requestTag = null;
    private boolean componentPaused = false;

    private int requestStatus = VolleyCallComponentHelper.REQUEST_STATUS_NONE;

    public CustomComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
        this.progressBar = progressBar;
        this.errorLayout = errorLayout;

        this.requestTag = requestTag;

        initErrorLayout();
        initProgress();
    }

    public String getRequestTag() {
        return requestTag;
    }

    public boolean isComponentPaused() {
        return componentPaused;
    }

    void setComponentPaused(boolean componentPaused) {
        this.componentPaused = componentPaused;
    }

    void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    protected void initErrorLayout(){
        if( errorLayout != null ) {
            TextView oops = (TextView) errorLayout.findViewById(R.id.oops);
//                oops.setTypeface(FontTypeface.getInstance(CustomVolleyCallerActivity.this).getRobotoLight());

            TextView something_wrong = (TextView) errorLayout.findViewById(R.id.something_wrong);
//                something_wrong.setTypeface(FontTypeface.getInstance(CustomVolleyCallerActivity.this).getRobotoLight());
        }
    }

    protected void initProgress(){
        if(progressBar != null) {
            progressBar.getIndeterminateDrawable().setColorFilter(progressBar.getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    protected void showErrorLayout(){
        requestStatus = VolleyCallComponentHelper.REQUEST_STATUS_FAILED;
        if( errorLayout != null  ) {
            errorLayout.setVisibility(View.VISIBLE);
            renderAppropriateErrorLayout(errorLayout);
        }
    }

    protected void hideErrorLayout(){
        if( errorLayout != null  ) {
            errorLayout.setVisibility(View.GONE);
        }
    }

    protected void showProgress(){

        if( progressBar != null ) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgress(){
        if( progressBar != null ) {
            progressBar.setVisibility(View.GONE);
        }
    }

    abstract public boolean handleContent(String tag, String content);
    abstract public void handleErrorContent(String tag);

    abstract public void changeUI(String tag);

}
