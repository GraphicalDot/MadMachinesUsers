package com.sports.unity.messages.controller.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

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

    private HashMap<String, ArrayList<Bitmap>> stickersMap = new HashMap<>();

    private Stickers(){

    }

    public ArrayList<Bitmap> getStickers(String category){
        ArrayList<Bitmap> stickers = stickersMap.get(category);
        return stickers;
    }

    public void loadAllStickers(Context context){
        try {
            loadStickersFromAssset(context, "footballStickers");
            loadStickersFromAssset(context, "basketballStickers");
            loadStickersFromAssset(context, "cricketStickers");
            loadStickersFromAssset(context, "tennisStickers");
            loadStickersFromAssset(context, "f1Stickers");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void loadStickersFromAssset(Context context, String folderName) throws IOException {
        if( ! stickersMap.containsKey(folderName) ) {
            AssetManager assetManager = context.getAssets();
            String[] fileNames = assetManager.list(folderName);
            ArrayList<Bitmap> stickers = new ArrayList<>();
            for (String name : fileNames) {
                InputStream is = assetManager.open(folderName + "/" + name);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                stickers.add(bitmap);
            }

            stickersMap.put(folderName, stickers);
        } else {
            //nothing
        }
    }

}
