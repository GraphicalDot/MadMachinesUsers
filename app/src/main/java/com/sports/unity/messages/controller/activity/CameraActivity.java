package com.sports.unity.messages.controller.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;

public class CameraActivity extends CustomAppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.popup_window_camera);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap picture = (Bitmap) data.getExtras().get("data");//this is your bitmap image and now you can do whatever you want with this
            Toast.makeText(context,"Save",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(CameraActivity.this,ChatScreenActivity.class);
            setResult(RESULT_OK,intent);
            finish();
        }
        else
        {
            Toast.makeText(context,"Error",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(CameraActivity.this,ChatScreenActivity.class);
            setResult(RESULT_CANCELED,intent);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

}
