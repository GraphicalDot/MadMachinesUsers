package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.sports.unity.R;
import com.sports.unity.messages.controller.model.Stickers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by madmachines on 26/11/15.
 */

public class AdapterForEmoji extends PagerAdapter {

    private Activity activity;

    private int titles[] = {R.drawable.ic_football, R.drawable.ic_basketball, R.drawable.ic_cricket, R.drawable.ic_tennis, R.drawable.ic_f1};
    private int SelectedTitles[] = {R.drawable.ic_football, R.drawable.ic_basketball, R.drawable.ic_cricket, R.drawable.ic_tennis, R.drawable.ic_f1};


    public AdapterForEmoji(Activity activity) {
        this.activity = activity;
    }


    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int resId = position;
        ViewGroup viewgroup = null;
        switch (position) {
            case 0:
                resId = R.layout.football_emoji;
                viewgroup = LoadFootballEmoji(resId, collection, inflater);
                collection.addView(viewgroup);
                break;
            case 1:
                resId = R.layout.basketball_emoji;
                viewgroup = LoadBasketballEmoji(resId, collection, inflater);
                collection.addView(viewgroup);
                break;
            case 2:
                resId = R.layout.cricket_emoji;
                viewgroup = LoadCricketEmoji(resId, collection, inflater);
                collection.addView(viewgroup);
                break;
            case 3:
                resId = R.layout.tennis_emoji;
                viewgroup = LoadTennisEmoji(resId, collection, inflater);
                collection.addView(viewgroup);
                break;
            case 4:
                resId = R.layout.f1_emoji;
                viewgroup = LoadF1Emoji(resId, collection, inflater);
                collection.addView(viewgroup);
                break;
        }

        return viewgroup;
    }

    public ViewGroup LoadFootballEmoji(int resId, ViewGroup collection, LayoutInflater inflater) {

        ArrayList<Bitmap> emoji = Stickers.getInstance().getStickers("footballStickers");
        ViewGroup layout = (ViewGroup) inflater.inflate(resId, collection, false);
        GridView football_emoji = (GridView) layout.findViewById(R.id.emoji);

        football_emoji.setAdapter(new EmojiAdapter(activity, emoji));

        return layout;

    }

    public ViewGroup LoadBasketballEmoji(int resId, ViewGroup collection, LayoutInflater inflater) {

        ArrayList<Bitmap> emoji = Stickers.getInstance().getStickers("basketballStickers");
        ViewGroup layout = (ViewGroup) inflater.inflate(resId, collection, false);
        GridView football_emoji = (GridView) layout.findViewById(R.id.emoji);

        football_emoji.setAdapter(new EmojiAdapter(activity, emoji));

        return layout;

    }

    public ViewGroup LoadCricketEmoji(int resId, ViewGroup collection, LayoutInflater inflater) {

        ArrayList<Bitmap> emoji = Stickers.getInstance().getStickers("cricketStickers");
        ViewGroup layout = (ViewGroup) inflater.inflate(resId, collection, false);
        GridView football_emoji = (GridView) layout.findViewById(R.id.emoji);

        football_emoji.setAdapter(new EmojiAdapter(activity, emoji));

        return layout;

    }

    public ViewGroup LoadTennisEmoji(int resId, ViewGroup collection, LayoutInflater inflater) {

        ArrayList<Bitmap> emoji = Stickers.getInstance().getStickers("tennisStickers");
        ViewGroup layout = (ViewGroup) inflater.inflate(resId, collection, false);
        GridView football_emoji = (GridView) layout.findViewById(R.id.emoji);

        football_emoji.setAdapter(new EmojiAdapter(activity, emoji));

        return layout;

    }

    public ViewGroup LoadF1Emoji(int resId, ViewGroup collection, LayoutInflater inflater) {

        ArrayList<Bitmap> emoji = Stickers.getInstance().getStickers("f1Stickers");
        ViewGroup layout = (ViewGroup) inflater.inflate(resId, collection, false);
        GridView football_emoji = (GridView) layout.findViewById(R.id.emoji);

        football_emoji.setAdapter(new EmojiAdapter(activity, emoji));

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
        Drawable image = activity.getResources().getDrawable(titles[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

}
