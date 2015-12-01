package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sports.unity.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by madmachines on 26/11/15.
 */
public class EmojiAdapter extends BaseAdapter {

    private  Activity activity;
    private ArrayList<Bitmap> icon;

    public EmojiAdapter(Activity activity, ArrayList<Bitmap> mThumbIds) {
        this.activity = activity;
        this.icon = mThumbIds;

    }

    public int getCount() {
        return icon.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            int stickerSize = (int)activity.getResources().getDimension(R.dimen.stickers_size);

            imageView = new ImageView(activity);
            imageView.setLayoutParams( new GridView.LayoutParams( stickerSize, stickerSize));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(R.drawable.grey_bg_rectangle);
        imageView.setImageBitmap(icon.get(position));
        return imageView;
    }
}
