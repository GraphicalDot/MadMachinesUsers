package com.sports.unity.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by manish on 19/02/16.
 */
public abstract class CustomTextView extends TextView {
    public CustomTextView(Context context) {
        super(context);
        setFontTypeface(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFontTypeface(context);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFontTypeface(context);
    }

    public abstract void setFontTypeface(Context ctx);
}
