package com.sports.unity.common.view;

import android.content.Context;
import android.util.AttributeSet;

import com.sports.unity.common.model.FontTypeface;

/**
 * Created by manish on 19/02/16.
 */
public class CustomRobotoCondenseBoldTextView extends CustomTextView {


    public CustomRobotoCondenseBoldTextView(Context context) {
        super(context);
    }

    public CustomRobotoCondenseBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRobotoCondenseBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setFontTypeface(Context ctx) {
        setTypeface(FontTypeface.getInstance(ctx).getRobotoCondensedBold());
    }
}
