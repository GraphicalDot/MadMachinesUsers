package com.sports.unity.messages.controller.activity;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.util.Constants;

public class ImageOrVideoViewActivity extends CustomAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_or_video_view);

        String fileName = getIntent().getStringExtra(Constants.INTENT_KEY_FILENAME);
        String mimeType = getIntent().getStringExtra(Constants.INTENT_KEY_MIMETYPE);

        initView(fileName, mimeType);
    }

    private void initView(String fileName, String mimeType){
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        VideoView videoView = (VideoView)findViewById(R.id.video_view);

        if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO) ){
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            playVideo(videoView, fileName);
        } else if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE) ){
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            imageView.setImageBitmap(BitmapFactory.decodeFile(DBUtil.getFilePath(this, fileName)));
        }
    }

    private void playVideo(VideoView videoView, String fileName){
        try {
            Uri uri = Uri.parse(DBUtil.getFilePath(this, fileName));

            videoView.setZOrderOnTop(true);
            videoView.setMediaController(new MediaController(this));
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
