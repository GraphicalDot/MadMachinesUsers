package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sports.unity.R;
import com.sports.unity.messages.controller.model.Stickers;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by madmachines on 26/11/15.
 */
public class EmojiAdapter extends BaseAdapter {

    private Activity activity;
    private String stickersCategory;

    private ArrayList<String> stickersName = null;

    public EmojiAdapter(Activity activity, String stickersCategory) {
        this.activity = activity;
        this.stickersCategory = stickersCategory;

        stickersName = Stickers.getInstance().getStickers(stickersCategory);
    }

    public int getCount() {
        return stickersName.size();
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
        imageView.setImageBitmap(Stickers.getInstance().getStickerBitmap(stickersCategory, stickersName.get(position)));
        imageView.setTag( stickersCategory + "/" + stickersName.get(position));
        return imageView;
    }
}
