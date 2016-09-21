package com.sports.unity.news.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.sports.unity.R;

/**
 * Created by madmachines on 3/8/16.
 */
public class SlantView extends View {
    private Context mContext;
    Paint paint;
    Path path;
    int imageHeight = (int) getResources().getDimension(R.dimen.news_slant_view_height);

    public SlantView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        mContext = ctx;
        setWillNotDraw(false);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int w = getWidth(), h = getHeight();
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(0, 0);
        path.lineTo(w, imageHeight / 2);
        path.lineTo(w, imageHeight);
        path.lineTo(0, imageHeight);
        path.close();
        canvas.drawPath(path, paint);
    }
}
