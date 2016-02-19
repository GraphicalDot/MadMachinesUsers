package com.sports.unity.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sports.unity.common.model.FontTypeface;

/**
 * Created by manish on 19/02/16.
 */
public abstract class CustomRobotoLightTextView extends TextView {
    public CustomRobotoLightTextView(Context context) {
        super(context);
        setFont(context);
    }

    public CustomRobotoLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public CustomRobotoLightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context);
    }

    private void setFont(Context ctx) {
        setTypeface(FontTypeface.getInstance(ctx).getRobotoLight());
    }

}
