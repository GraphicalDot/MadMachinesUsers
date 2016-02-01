package com.sports.unity.messages.controller.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.util.SortedList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by madmachines on 26/11/15.
 */
public class Stickers {

    private static Stickers STICKERS = null;

    public static Stickers getInstance(){
        if( STICKERS == null ){
            STICKERS = new Stickers();
        }
        return STICKERS;
    }

    private HashMap<String, ArrayList<String>> stickersMap = new HashMap<>();
    private HashMap<String, HashMap<String,Bitmap>> stickersBitmapHashMap = new HashMap<>();

    private Stickers(){

    }

    public ArrayList<String> getStickers(String category){
        ArrayList<String> stickers = stickersMap.get(category);
        return stickers;
    }

    public Bitmap getStickerBitmap(String category, String name){
        HashMap<String,Bitmap> map = stickersBitmapHashMap.get(category);
        Bitmap bitmap = map.get(name);
        return bitmap;
    }

    public void loadAllStickers(Context context){
        try {
            loadStickersFromAsset(context, "footballStickers");
            loadStickersFromAsset(context, "cricketStickers");
//            loadStickersFromAsset(context, "basketballStickers");
//            loadStickersFromAsset(context, "tennisStickers");
//            loadStickersFromAsset(context, "f1Stickers");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void loadStickersFromAsset(Context context, String folderName) {
        if( ! stickersBitmapHashMap.containsKey(folderName) ){
            stickersBitmapHashMap.put( folderName, new HashMap<String, Bitmap>());
        } else {
            //nothing
        }

        try {
            AssetManager assetManager = context.getAssets();

            if( ! stickersMap.containsKey(folderName) ) {
                String[] fileNames = assetManager.list(folderName);

                ArrayList<String> arrayList = new ArrayList<>();
                for(String name:fileNames) {
                    arrayList.add(name);
                }

                stickersMap.put( folderName, arrayList);
            } else {
                //nothing
            }

//            ArrayList<String> stickersName = stickersMap.get(folderName);
//            HashMap<String,Bitmap> stickers = stickersBitmapHashMap.get(folderName);
//            for (String name : stickersName) {
//                if ( ! stickers.containsKey(name) ) {
//                    InputStream is = assetManager.open(folderName + "/" + name);
//                    Bitmap bitmap = BitmapFactory.decodeStream(is);
//                    stickers.put(name, bitmap);
//                } else {
//                    //nothing
//                }
//            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void loadStickerFromAsset(Context context, String folderName, String stickerName) {
        if( ! stickersBitmapHashMap.containsKey(folderName) ){
            stickersBitmapHashMap.put( folderName, new HashMap<String, Bitmap>());
        } else {
            //nothing
        }

        try {
            AssetManager assetManager = context.getAssets();

            HashMap<String, Bitmap> stickers = stickersBitmapHashMap.get(folderName);
            if (!stickers.containsKey(stickerName)) {
                InputStream is = assetManager.open(folderName + "/" + stickerName);
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                stickers.put(stickerName, bitmap);
            } else {
                //nothing
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
