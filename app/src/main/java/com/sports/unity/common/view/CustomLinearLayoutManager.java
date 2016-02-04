package com.sports.unity.common.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Manish on 03-02-2016.
 */
public class CustomLinearLayoutManager extends LinearLayoutManager
{
    private int[] mMeasuredDimension = new int[2];

    public CustomLinearLayoutManager(Context context)
    {
        super(context);
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout)
    {
        super(context, orientation, reverseLayout);
    }

    public CustomLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                                     int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec,
                          int heightSpec)
    {
        final int widthMode  = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize  = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);
        int       width      = 0;
        int       height     = 0;
        for (int i = 0; i < getItemCount(); i++)
        {
            measureScrapChild(recycler, i,
                              View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                              View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                              mMeasuredDimension);
            if (getOrientation() == HORIZONTAL)
            {
                width = width + mMeasuredDimension[0];
                if (i == 0)
                {
                    height = mMeasuredDimension[1];
                }
            } else
            {
                height = height + mMeasuredDimension[1];
                if (i == 0)
                {
                    width = mMeasuredDimension[0];
                }
            }
        }
        switch (widthMode)
        {
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        switch (heightMode)
        {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        setMeasuredDimension(width, height);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                   int heightSpec, int[] measuredDimension)
    {
        View view = recycler.getViewForPosition(position);
        if (view != null)
        {
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childWidthSpec =
                    ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(),
                                                  p.width);
            int childHeightSpec =
                    ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(),
                                                  p.height);
            view.measure(childWidthSpec, childHeightSpec);
            Rect outRect = new Rect();
            calculateItemDecorationsForChild(view, outRect);
            measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
            measuredDimension[1] =
                    view.getMeasuredHeight() + p.bottomMargin + p.topMargin + outRect.bottom +
                            outRect.top;
            recycler.recycleView(view);
        }
    }
}

