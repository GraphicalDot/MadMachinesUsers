package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.ThreadTask;

import java.io.ByteArrayOutputStream;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by madmachines on 20/11/15.
 */
public class ImageAdapterForGallery extends RecyclerView.Adapter<ImageAdapterForGallery.ViewHolder> implements View.OnClickListener {

    private Activity activity;
    private RecyclerView recyclerView = null;

    private int keyboardHeight;

    private ArrayList<String> filePath = null;
    private HashMap<Integer, Bitmap> imageContent = new HashMap<>();

    private View selectedViewForSend = null;

    private View.OnClickListener sendClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            handleSendMedia();
            deactivateSendOverlay();
        }

    };

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            deactivateSendOverlay();
        }

    };

    public ImageAdapterForGallery(Activity activity, RecyclerView recyclerView, ArrayList<String> path, int keyboardHeight) {
        this.filePath = path;
        this.activity = activity;
        this.keyboardHeight = keyboardHeight;
        this.recyclerView = recyclerView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View v) {
            super(v);

            imageView = (ImageView) v.findViewById(com.sports.unity.R.id.img);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(keyboardHeight, keyboardHeight));
            imageView.setDrawingCacheEnabled(true);
        }
    }

    @Override
    public ImageAdapterForGallery.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery, parent, false);
        v.setLayoutParams(new FrameLayout.LayoutParams(keyboardHeight, keyboardHeight));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageAdapterForGallery.ViewHolder holder, final int position) {
        holder.imageView.setId(position);
        holder.imageView.setOnClickListener(this);

        Glide.with(activity)
                .load(filePath.get(position))
                .centerCrop()
                .placeholder(R.drawable.grey_bg_rectangle)
                .crossFade()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return filePath.size();
    }

    @Override
    public void onClick(View view) {
        activateSendOverlay(view);
    }

    private void activateSendOverlay(View view){
        if( selectedViewForSend != null ){
            deactivateSendOverlay();
        }

        selectedViewForSend = view;
       // int position = (Integer)view.getTag();

        FrameLayout parentLayout = ((FrameLayout) view.getParent());
        FrameLayout overlayLayout = (FrameLayout)activity.getLayoutInflater().inflate(R.layout.send_overlay_gallery, parentLayout, false);
        overlayLayout.setLayoutParams(new FrameLayout.LayoutParams(keyboardHeight,keyboardHeight));

        parentLayout.addView(overlayLayout);

        ImageView sendImageView = (ImageView)overlayLayout.getChildAt(0);
        sendImageView.setOnClickListener(sendClickListener);

        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener( scrollListener);
    }

    private void deactivateSendOverlay(){
        if( selectedViewForSend != null ) {
            FrameLayout parentLayout = ((FrameLayout) selectedViewForSend.getParent());
            while( parentLayout.getChildCount() > 1 ){
                parentLayout.removeViewAt(1);
            }

            selectedViewForSend = null;
        }

        recyclerView.clearOnScrollListeners();
    }

    private void handleSendMedia(){
        ImageView imageView = (ImageView)selectedViewForSend;

        int position = imageView.getId();

        File file = new File(filePath.get(position));

        //BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        //Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);
        Bitmap bitmap = ProfileCreationActivity.decodeSampleImage(file,keyboardHeight,keyboardHeight);

      // Bitmap bitmap = imageView.getDrawingCache();


        new ThreadTask( bitmap){
            private byte[] mediaContent = null;

            @Override
            public Object process() {
                Bitmap bitmap = (Bitmap)object;
                String fileName = DBUtil.getUniqueFileName(activity.getBaseContext(), SportsUnityDBHelper.MIME_TYPE_IMAGE);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                mediaContent = stream.toByteArray();

                DBUtil.writeContentToExternalFileStorage(activity.getBaseContext(), fileName, mediaContent);
                return fileName;
            }

            @Override
            public void postAction(Object object) {
                String fileName = (String)object;
                sendActionToCorrespondingActivityListener( 1, ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_IMAGE, fileName, mediaContent);
            }

        }.start();

    }

    private boolean sendActionToCorrespondingActivityListener(int id, String key, String mimeType, Object messageContent, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent( id, mimeType, messageContent, mediaContent);
            success = true;
        }
        return success;
    }

}