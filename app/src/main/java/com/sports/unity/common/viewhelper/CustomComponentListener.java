package com.sports.unity.common.viewhelper;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    private View contentLayout = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    private ProgressBar progressBar = null;
    private ViewGroup errorLayout = null;

    private String requestTag = null;
    private boolean componentPaused = false;

    private int requestStatus = VolleyCallComponentHelper.REQUEST_STATUS_NONE;

    public CustomComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
        this(requestTag, progressBar, errorLayout, null, null);
    }

    public CustomComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout, View contentLayout, SwipeRefreshLayout swipeRefreshLayout){
        this.progressBar = progressBar;
        this.errorLayout = errorLayout;

        this.contentLayout = contentLayout;
        this.swipeRefreshLayout = swipeRefreshLayout;

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
        //nothing
    }

    protected boolean isContentLayoutAvailable(){
        return contentLayout != null && contentLayout.getVisibility() == View.VISIBLE;
    }

    protected void initProgress(){
        if(progressBar != null) {
            progressBar.getIndeterminateDrawable().setColorFilter(progressBar.getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    protected void showErrorLayout(){
        requestStatus = VolleyCallComponentHelper.REQUEST_STATUS_FAILED;
        if( ! isContentLayoutAvailable() ) {
            if (errorLayout != null) {
                errorLayout.setVisibility(View.VISIBLE);
                renderAppropriateErrorLayout(errorLayout);
            }
        } else {
            if( errorLayout != null ) {
                Context context = errorLayout.getContext();
                if( CommonUtil.isInternetConnectionAvailable(errorLayout.getContext()) ) {
                    Toast.makeText(context, R.string.oops_try_again, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.common_message_internet_not_available, Toast.LENGTH_SHORT).show();
                }
            } else {
                //nothing
            }
        }
    }

    protected void hideErrorLayout(){
        if( errorLayout != null  ) {
            errorLayout.setVisibility(View.GONE);
        }
    }

    protected void showProgress(){
        if( swipeRefreshLayout != null ){
            swipeRefreshLayout.setRefreshing(true);
        }
        if( ! isContentLayoutAvailable() ) {
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void hideProgress(){
        if( swipeRefreshLayout != null ){
            swipeRefreshLayout.setRefreshing(false);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    abstract public boolean handleContent(String tag, String content);
    abstract public void handleErrorContent(String tag);

    abstract public void changeUI(String tag);

}
