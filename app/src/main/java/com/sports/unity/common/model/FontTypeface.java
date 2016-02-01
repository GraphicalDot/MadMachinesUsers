package com.sports.unity.common.model;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by madmachines on 12/10/15.
 */
public class FontTypeface {

    private  static FontTypeface FONT_TYPE_FACE = null;

    synchronized public static FontTypeface getInstance( Context context){
        if( FONT_TYPE_FACE == null ){
            FONT_TYPE_FACE = new FontTypeface(context);
        }
        return FONT_TYPE_FACE;
    }

    public static void clean(){
        FONT_TYPE_FACE = null;
    }

    private Typeface robotoLight;
    private Typeface robotoMedium;
    private Typeface robotoRegular;
    private Typeface robotoCondensedBold;
    private Typeface robotoCondensedRegular;
    private Typeface robotoSlabRegular;
    private Typeface robotoSlabBold;

    private FontTypeface(Context context) {
        robotoLight = Typeface.createFromAsset( context.getAssets(), "Roboto-Light.ttf");
        robotoMedium = Typeface.createFromAsset( context.getAssets(), "Roboto-Medium.ttf");
        robotoRegular = Typeface.createFromAsset( context.getAssets(), "Roboto-Regular.ttf");
        robotoCondensedBold = Typeface.createFromAsset( context.getAssets(), "RobotoCondensed-Bold.ttf");
        robotoCondensedRegular = Typeface.createFromAsset( context.getAssets(), "RobotoCondensed-Regular.ttf");
        robotoSlabRegular = Typeface.createFromAsset( context.getAssets(), "RobotoSlab-Regular.ttf");
        robotoSlabBold=Typeface.createFromAsset( context.getAssets(), "RobotoSlab-Bold.ttf");


    }

    public Typeface getRobotoLight() {
        return robotoLight;
    }

    public Typeface getRobotoRegular() {
        return robotoRegular;
    }

    public Typeface getRobotoMedium() {
        return robotoMedium;
    }

    public Typeface getRobotoCondensedBold() {
        return robotoCondensedBold;
    }

    public Typeface getRobotoCondensedRegular() {
        return robotoCondensedRegular;
    }

    public Typeface getRobotoSlabRegular() {
        return robotoSlabRegular;
    }

    public Typeface getRobotoSlabBold() {
        return robotoSlabBold;
    }
}
