package com.sports.unity.messages.controller.viewhelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ImageUtil;
import com.sports.unity.util.ThreadTask;

import java.io.FileInputStream;
import java.net.URLConnection;
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

    @Override
    public ImageAdapterForGallery.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery, parent, false);
        v.setLayoutParams(new FrameLayout.LayoutParams(keyboardHeight, keyboardHeight));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageAdapterForGallery.ViewHolder holder, final int position) {

        holder.imageView.setTag(R.layout.layout_gallery, position);
        holder.imageView.setOnClickListener(this);

        boolean hasVideoContent = false;
        String durationAsString = null;

        FileInputStream fileInputStream = null;
        try {
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            Log.d("Image Adapter", "" + filePath.get(position));
//            fileInputStream = new FileInputStream(filePath.get(position));
//
//            retriever.setDataSource(fileInputStream.getFD());
//            hasVideoContent = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
//            durationAsString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            hasVideoContent = isVideoFile(filePath.get(position));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!hasVideoContent) {

            holder.textView.setVisibility(View.GONE);

            Glide.with(activity)
                    .load(filePath.get(position))
                    .centerCrop()
                    .placeholder(R.drawable.grey_bg_rectangle)
                    .crossFade()
                    .into(holder.imageView);
        } else {
            holder.textView.setVisibility(View.VISIBLE);

            durationAsString = getVideoDuration(filePath.get(position));

            long timeInmillisec = Long.parseLong(durationAsString);
            if (timeInmillisec == 0) {
                holder.textView.setVisibility(View.GONE);
            } else {
                long duration = timeInmillisec / 1000;
                long hours = duration / 3600;
                long minutes = (duration - hours * 3600) / 60;

                long seconds = duration - (hours * 3600 + minutes * 60);

                StringBuilder stringBuilder = new StringBuilder("");

                stringBuilder.append(minutes);
                stringBuilder.append(":");
                stringBuilder.append(seconds);

                holder.textView.setText(stringBuilder.toString());
            }

            Glide.with(activity)
                    .load(filePath.get(position))
                    .centerCrop()
                    .placeholder(R.drawable.grey_bg_rectangle)
                    .crossFade()
                    .into(holder.imageView);
        }

//        retriever.release();

    }

    private String getVideoDuration(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        String duration = null;
        try {
            retriever.setDataSource(path);
            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }

        if (duration == null) {
            return "0";
        } else {
            return duration;
        }
    }

    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("video") == 0;
    }

    @Override
    public int getItemCount() {
        return filePath.size();
    }

    @Override
    public void onClick(View view) {
        activateSendOverlay(view);
    }

    private void activateSendOverlay(View view) {
        if (selectedViewForSend != null) {
            deactivateSendOverlay();
        }

        selectedViewForSend = view;
        // int position = (Integer)view.getTag();

        FrameLayout parentLayout = ((FrameLayout) view.getParent());
        FrameLayout overlayLayout = (FrameLayout) activity.getLayoutInflater().inflate(R.layout.send_overlay_gallery, parentLayout, false);
        overlayLayout.setLayoutParams(new FrameLayout.LayoutParams(keyboardHeight, keyboardHeight));

        parentLayout.addView(overlayLayout);

        ImageView sendImageView = (ImageView) overlayLayout.getChildAt(0);
        sendImageView.setOnClickListener(sendClickListener);

        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void deactivateSendOverlay() {
        if (selectedViewForSend != null) {
            FrameLayout parentLayout = ((FrameLayout) selectedViewForSend.getParent());

            if (parentLayout.getChildCount() > 1) {
                parentLayout.removeViewAt(2);
            }
            selectedViewForSend = null;
        }

        recyclerView.clearOnScrollListeners();
    }

    private void handleSendMedia() {
        ImageView imageView = (ImageView) selectedViewForSend;


        final int position = (Integer) imageView.getTag(R.layout.layout_gallery);
        final String file = filePath.get(position);

        try {
            new ThreadTask(null) {

                private String thumbnailImage = null;
                private boolean hasVideoContent = false;

                @Override
                public Object process() {
                    hasVideoContent = isVideoFile(filePath.get(position));

                    String fileName = null;
                    try {
                        if (!hasVideoContent) {
                            fileName = DBUtil.getUniqueFileName(SportsUnityDBHelper.MIME_TYPE_IMAGE, false);
                            this.object = ImageUtil.getScaledDownBytes(file, activity.getResources().getDisplayMetrics());

                            DBUtil.writeContentToExternalFileStorage(activity.getBaseContext(), fileName, (byte[]) this.object, SportsUnityDBHelper.MIME_TYPE_IMAGE);
                            thumbnailImage = PersonalMessaging.createThumbnailImageAsBase64(activity, SportsUnityDBHelper.MIME_TYPE_IMAGE, fileName);
                        } else {
                            fileName = DBUtil.getUniqueFileName(SportsUnityDBHelper.MIME_TYPE_VIDEO, false);
                            this.object = fileName;

                            DBUtil.writeContentToExternalFileStorage(activity.getBaseContext(), file, fileName, SportsUnityDBHelper.MIME_TYPE_VIDEO);
                            thumbnailImage = PersonalMessaging.createThumbnailImageAsBase64(activity, SportsUnityDBHelper.MIME_TYPE_VIDEO, fileName);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return fileName;
                }

                @Override
                public void postAction(Object object) {
                    String fileName = (String) object;
                    Object mediaContent = this.object;

                    if (!hasVideoContent) {
                        ActivityActionHandler.getInstance().dispatchSendMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_IMAGE, fileName, thumbnailImage, mediaContent);
                    } else {
                        ActivityActionHandler.getInstance().dispatchSendMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_VIDEO, fileName, thumbnailImage, mediaContent);
                    }
                }

            }.start();
        } catch (Exception ex) {
            ex.printStackTrace();

            Toast.makeText(activity, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View v) {
            super(v);

            imageView = (ImageView) v.findViewById(com.sports.unity.R.id.img);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(keyboardHeight, keyboardHeight));
            imageView.setDrawingCacheEnabled(true);

            textView = (TextView) v.findViewById(R.id.duration);
            textView.setVisibility(View.VISIBLE);

        }
    }

}