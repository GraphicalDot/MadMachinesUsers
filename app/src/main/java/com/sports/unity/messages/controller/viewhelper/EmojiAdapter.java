package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.messages.controller.model.Stickers;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by madmachines on 26/11/15.
 */
public class EmojiAdapter extends BaseAdapter {

    private Context context;
    private String stickersCategory;

    private ArrayList<String> stickersName = null;

    public EmojiAdapter(Context context, String stickersCategory) {
        this.context = context;
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
            int stickerSize = (int)context.getResources().getDimension(R.dimen.stickers_size);

            imageView = new ImageView(context);
            imageView.setLayoutParams( new GridView.LayoutParams( stickerSize, stickerSize));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            imageView = (ImageView) convertView;
        }

        Glide.with(context)
                .load(Uri.parse("file:///android_asset/"+stickersCategory + "/" + stickersName.get(position)))
                .asBitmap()
                .placeholder(R.drawable.grey_bg_rectangle)
                .into(imageView);

        imageView.setTag(R.id.emoji, stickersCategory + "/" + stickersName.get(position));

        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String selectedStickerPath = (String)view.getTag(R.id.emoji);

                ActivityActionHandler.getInstance().dispatchSendStickerEvent(ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_STICKER, selectedStickerPath);
            }

        });
        return imageView;
    }

}
