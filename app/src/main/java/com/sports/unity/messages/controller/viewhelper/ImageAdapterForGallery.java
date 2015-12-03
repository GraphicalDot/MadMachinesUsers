package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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

import com.android.volley.toolbox.ImageLoader;
import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.ThreadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;


import org.jivesoftware.smackx.si.packet.StreamInitiation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static android.support.v7.internal.widget.TintTypedArray.obtainStyledAttributes;

/**
 * Created by madmachines on 20/11/15.
 */
public class ImageAdapterForGallery extends RecyclerView.Adapter<ImageAdapterForGallery.ViewHolder> implements View.OnClickListener {

    private Activity activity;
    private RecyclerView recyclerView = null;

    private int keyboardHeight;

    private ArrayList<String> filePath = null;
    private HashMap<Integer,Bitmap> imageContent = new HashMap<>();

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
            imageView.setLayoutParams(new FrameLayout.LayoutParams(keyboardHeight,keyboardHeight));
        }
    }

    @Override
    public ImageAdapterForGallery.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery, parent, false);
        v.setLayoutParams(new FrameLayout.LayoutParams(keyboardHeight,keyboardHeight));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageAdapterForGallery.ViewHolder holder, final int position) {
        holder.imageView.setTag( position);
        holder.imageView.setOnClickListener(this);

//        boolean available = fetchImage(position, holder);
//        if( available ){
//            holder.imageView.setImageBitmap( imageContent.get(position));
//        } else {
//            holder.imageView.setImageResource( R.drawable.images);
//        }


          Picasso.with(activity)
                  .load(new File(filePath.get(position)))
                  .centerCrop()
                  .resize(keyboardHeight,keyboardHeight)
                  .placeholder(R.drawable.grey_bg_rectangle)
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
        int position = (Integer)view.getTag();

        FrameLayout parentLayout = ((FrameLayout) view.getParent());
        FrameLayout overlayLayout = (FrameLayout)activity.getLayoutInflater().inflate(R.layout.send_overlay_gallery, parentLayout, false);
        overlayLayout.setLayoutParams(new FrameLayout.LayoutParams(keyboardHeight,keyboardHeight));

        parentLayout.addView(overlayLayout);

        ImageView sendImageView = (ImageView)overlayLayout.getChildAt(0);
        sendImageView.setTag(position);
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

//    private boolean fetchImage(final int position, final ImageAdapterForGallery.ViewHolder viewHolder){
//        boolean available = false;
//        if( imageContent.containsKey(position)){
//            available = true;
//        } else {
//
//            Thread thread = new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//                        Bitmap imagebitmap = ProfileCreationActivity.decodeSampleImage( new File(filePath.get(position)), keyboardHeight, keyboardHeight);
//                        imageContent.put(position, imagebitmap);
//
//                        if( viewHolder.imageView.getTag() == position){
//                            activity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    viewHolder.imageView.setImageBitmap( imageContent.get(position));
//                                }
//                            });
//                        } else {
//                            //nothing
//                        }
//
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        ;
//                    }
//                }
//
//            });
//            thread.start();
//        }
//        return available;
//    }

    private void handleSendMedia(){
        ImageView imageView = (ImageView)selectedViewForSend;
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        new ThreadTask( bitmap){
            private byte[] mediaContent = null;

            @Override
            public Object process() {
                Bitmap bitmap = (Bitmap)object;
                String fileName = String.valueOf(System.currentTimeMillis());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                mediaContent = stream.toByteArray();

                DBUtil.writeContentToFile( activity.getBaseContext(), fileName, mediaContent, false);
                return fileName;
            }

            @Override
            public void postAction(Object object) {
                String fileName = (String)object;
                sendActionToCorrespondingActivityListener(ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_IMAGE, fileName, mediaContent);
            }

        }.start();

    }

    private boolean sendActionToCorrespondingActivityListener(String key, String mimeType, Object messageContent, Object mediaContent) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(key);

        if (actionListener != null) {
            actionListener.handleMediaContent( mimeType, messageContent, mediaContent);
            success = true;
        }
        return success;
    }

}