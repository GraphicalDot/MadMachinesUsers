package com.sports.unity.common.view;
import android.content.res.Resources;

/**
 * Created by madmachines on 15/3/16.
 */
public class DountUtils {
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}
