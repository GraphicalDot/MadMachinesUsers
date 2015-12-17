package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.messages.controller.model.Stickers;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.zip.Inflater;

/**
 * Created by madmachines on 26/11/15.
 */

public class AdapterForEmoji extends PagerAdapter implements AdapterView.OnItemClickListener {

    private Context context;
    private GridView gridView;
    private ViewGroup layout;

    private int titles[] = {R.drawable.ic_football, R.drawable.ic_basketball, R.drawable.ic_cricket, R.drawable.ic_tennis, R.drawable.ic_f1};


    public AdapterForEmoji(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int resId = 0;
        ViewGroup viewgroup = null;
        switch (position) {
            case 0:
                resId = R.layout.football_emoji;
                viewgroup = loadStickersGridView(resId, collection, inflater, "footballStickers");
                collection.addView(viewgroup);
                break;
            case 1:
                resId = R.layout.basketball_emoji;
                viewgroup = loadStickersGridView(resId, collection, inflater, "basketballStickers");
                collection.addView(viewgroup);
                break;
            case 2:
                resId = R.layout.cricket_emoji;
                viewgroup = loadStickersGridView(resId, collection, inflater, "cricketStickers");
                collection.addView(viewgroup);
                break;
            case 3:
                resId = R.layout.tennis_emoji;
                viewgroup = loadStickersGridView(resId, collection, inflater, "tennisStickers");
                collection.addView(viewgroup);
                break;
            case 4:
                resId = R.layout.f1_emoji;
                viewgroup = loadStickersGridView(resId, collection, inflater, "f1Stickers");
                collection.addView(viewgroup);
                break;
        }

        return viewgroup;
    }


    public ViewGroup loadStickersGridView(int resId, ViewGroup collection, LayoutInflater inflater, String stickerCategory) {
        layout = (ViewGroup) inflater.inflate(resId, collection, false);

        gridView = (GridView) layout.findViewById(R.id.emoji);

        gridView.setAdapter(new EmojiAdapter(context, stickerCategory));
        gridView.setOnItemClickListener(this);

        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = context.getResources().getDrawable(titles[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedStickerPath = (String)view.getTag(R.id.emoji);

        sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_STICKER, selectedStickerPath);
    }

    private boolean sendActionToCorrespondingActivityListener(String key, String mimeType, Object data) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent( 1, mimeType, data, null);
            success = true;
        }
        return success;
    }

}
