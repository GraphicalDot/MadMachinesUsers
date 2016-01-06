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

//    private ArrayList<ProgressItem> mProgressItemsList = new ArrayList<>();

    private ArrayList<Integer> levels = new ArrayList<>();

    public DiscreteSeekBar(Context context) {
        this(context, null);
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void drawPoints(Canvas canvas){

        initLevels();

        int progressBarWidth = getWidth();
        int progressBarHeight = getHeight();
        final int thumbWidth = getThumb().getIntrinsicWidth();

        int height = getResources().getDimensionPixelSize(R.dimen.seekbar_point_size)/2;
        int itemSize = height;

        for(int level : levels) {
            int position = (progressBarWidth * level) / 10000;

            if( position == 0 ){
                position += (thumbWidth/2 - itemSize/2);
            } else if( position == progressBarWidth ){
                position -= (thumbWidth/2 + itemSize/2);
            }

            Rect progressRect = new Rect();
            progressRect.set(position, progressBarHeight / 2 - itemSize / 2,
                    position + itemSize, progressBarHeight / 2 + itemSize / 2);

            Drawable checkPoints = getResources().getDrawable(R.drawable.red_circle);
            checkPoints.setBounds(progressRect);
            checkPoints.draw(canvas);

//            {
//                LinearLayout layout = new LinearLayout(getContext());
//
//                TextView textView = new TextView(getContext());
//                textView.setVisibility(View.VISIBLE);
//                textView.setText(""+level);
//                layout.addView(textView);
//
//                layout.measure(canvas.getWidth(), canvas.getHeight());
//                layout.layout( progressRect.left, progressRect.top, progressRect.right, progressRect.bottom);
//
//                // To place the text view somewhere specific:
////                canvas.translate(0, 0);
//
//                layout.draw(canvas);
//            }

        }
    }

    private void initLevels(){
        if( levels.size() == 0 ){
            levels.add( 0 * (10000/10) );
            levels.add( 6 * (10000/10) );
            levels.add( 10 * (10000/10) );
        }
    }

//    private void init(){
//        if( mProgressItemsList.size() == 0 ){
//            mProgressItemsList.add( new ProgressItem(R.color.app_theme_blue, 50));
//            mProgressItemsList.add( new ProgressItem(R.color.gray1, 50));
//        }
//    }

//    private void drawRainBow(Canvas canvas){
//
//        init();
//
//        int progressBarWidth = getWidth();
//        int progressBarHeight = getHeight();
//        int thumboffset = getThumbOffset();
//        int lastProgressX = 0;
//        int progressItemWidth, progressItemRight;
//        for (int i = 0; i < mProgressItemsList.size(); i++) {
//            ProgressItem progressItem = mProgressItemsList.get(i);
//            Paint progressPaint = new Paint();
//            progressPaint.setColor(getResources().getColor(
//                    progressItem.color));
//
//            progressItemWidth = (int) (progressItem.progressItemPercentage
//                    * progressBarWidth / 100);
//
//            progressItemRight = lastProgressX + progressItemWidth;
//
//            // for last item give right to progress item to the width
//            if (i == mProgressItemsList.size() - 1
//                    && progressItemRight != progressBarWidth) {
//                progressItemRight = progressBarWidth;
//            }
//            Rect progressRect = new Rect();
//            progressRect.set(lastProgressX, thumboffset / 2,
//                    progressItemRight, progressBarHeight - thumboffset / 2);
//            canvas.drawRect(progressRect, progressPaint);
//            lastProgressX = progressItemRight;
//        }
//    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        drawRainBow(canvas);
        drawPoints(canvas);

        super.onDraw(canvas);
    }

//    public DiscreteSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
//        this(context, attrs, defStyleAttr, 0);
//    }
//
//    public DiscreteSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public class ProgressItem {

        public int color;
        public float progressItemPercentage;

        public ProgressItem(int color, float percentage){
            this.color = color;
            this.progressItemPercentage = percentage;
        }

    }

}
