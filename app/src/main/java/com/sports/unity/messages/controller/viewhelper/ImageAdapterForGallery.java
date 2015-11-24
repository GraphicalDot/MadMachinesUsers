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
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;


import org.jivesoftware.smackx.si.packet.StreamInitiation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static android.support.v7.internal.widget.TintTypedArray.obtainStyledAttributes;

/**
 * Created by madmachines on 20/11/15.
 */
public class ImageAdapterForGallery extends RecyclerView.Adapter<ImageAdapterForGallery.ViewHolder> {

    private ArrayList<String> filePath = null;
    private Activity activity;
    private int keyboardHeight;
    Drawable placeholder;


    private HashMap<Integer,Bitmap> imageContent = new HashMap<>();

    public ImageAdapterForGallery(Activity activity, ArrayList<String> path, int keyboardHeight, Drawable placeholder) {
        this.filePath = path;
        this.activity = activity;
        this.keyboardHeight = keyboardHeight;
        this.placeholder = placeholder;
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View v) {
            super(v);

            imageView = (ImageView) v.findViewById(com.sports.unity.R.id.img);
//            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(keyboardHeight,keyboardHeight);
//            imageView.setLayoutParams(parms);
            imageView.setPadding(0, 0, 2, 0);

        }
    }

    @Override
    public ImageAdapterForGallery.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageAdapterForGallery.ViewHolder holder, final int position) {
        holder.imageView.setTag( position);
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
                  .placeholder(placeholder)
                  .into(holder.imageView);


//        RequestCreator requestCreator = Picasso.with(activity).load( imagePath);
//        requestCreator = requestCreator.centerCrop();
//        requestCreator = requestCreator.resize(keyboardHeight, keyboardHeight);
//        requestCreator.into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return filePath.size();
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

}