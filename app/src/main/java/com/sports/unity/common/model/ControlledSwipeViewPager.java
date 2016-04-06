package com.sports.unity.common.model;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ControlledSwipeViewPager extends ViewPager {

    private boolean isSwipingEnabled = true;

    public ControlledSwipeViewPager(Context context) {
        super(context);
    }

    public ControlledSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        if(!isSwipingEnabled){
            return false;
        }else{
            return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        if(!isSwipingEnabled){
            return false;
        }else{
            return super.onTouchEvent(event);
        }
    }

    public void setPagingEnabled(boolean isEnabled) {
        isSwipingEnabled = isEnabled;
    }
}