package com.sports.unity.messages.controller.viewhelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sports.unity.R;

import java.util.ArrayList;

/**
 * Created by amandeep on 5/1/16.
 */
public class DiscreteSeekBar extends SeekBar {

    private ArrayList<Integer> levels = new ArrayList<>();

    public DiscreteSeekBar(Context context) {
        this(context, null);
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void drawPoints(Canvas canvas){

        initLevels();

        int height = getResources().getDimensionPixelSize(R.dimen.seekbar_point_size)/2;
        int itemSize = height;

        final int thumbWidth = getThumb().getIntrinsicWidth();

        int progressBarWidth = getWidth();
        int progressBarHeight = getHeight();

        int sidePadding = getPaddingLeft();
        progressBarWidth -= sidePadding*2;

        for(int level : levels) {
            int position = (progressBarWidth * level) / 10000;

            if( position == 0 ){
                position -= itemSize/2;
            } else if( position == progressBarWidth ){
                position -= itemSize/2;
            }

            Rect progressRect = new Rect();
            progressRect.set( sidePadding + position, progressBarHeight / 2 - itemSize / 2,
                    sidePadding + position + itemSize, progressBarHeight / 2 + itemSize / 2);

            Drawable checkPoints = getResources().getDrawable(R.drawable.grey_circle);
            checkPoints.setBounds(progressRect);
            checkPoints.draw(canvas);

        }
    }

    private void initLevels(){
        if( levels.size() == 0 ){
            levels.add( 0 );
            levels.add( (int)((2.5f * 10000) / 10) );
            levels.add( (int)((5.0f * 10000) / 10) );
            levels.add( (int)((7.5f * 10000) / 10) );
            levels.add( (int)((10.0f * 10000) / 10) );
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        drawPoints(canvas);

        super.onDraw(canvas);
    }

    public class ProgressItem {

        public int color;
        public float progressItemPercentage;

        public ProgressItem(int color, float percentage){
            this.color = color;
            this.progressItemPercentage = percentage;
        }

    }

}
