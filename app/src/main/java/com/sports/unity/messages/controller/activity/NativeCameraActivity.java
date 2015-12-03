/*
 * Copyright (c) 2014 Rex St. John on behalf of AirPair.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sports.unity.messages.controller.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.ThreadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class NativeCameraActivity extends CustomAppCompatActivity implements View.OnClickListener {

    /**
     * Safe method for getting a camera instance.
     * @return
     */
    public static Camera getCameraInstance( int cameraId){
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e){
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    private String flashStatus = Camera.Parameters.FLASH_MODE_ON;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private Camera mCamera;

    private CameraPreview mPreview;
    private View mCameraView;

    private byte[] content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_camera);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        View view = findViewById(R.id.root);

        boolean opened = safeCameraOpenInView(view);
        if(opened == false){
            Log.i("Camera","Error, Camera failed to open");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

//        closeCameraAndView();
    }

    @Override
    protected void onStop() {
        super.onStop();

        closeCameraAndView();
    }

    @Override
    public void onClick(View view) {
        handleClickEvent(view, view.getId());
    }

    private void initView(){

        {
            /*
             * check for flash
             */
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                View view = findViewById(R.id.flash);
                view.setVisibility(View.VISIBLE);
            } else {
                View view = findViewById(R.id.flash);
                view.setVisibility(View.GONE);
            }
        }

        {
            /*
             * check for two camera's
             */
            int cameraCount = Camera.getNumberOfCameras();
            if ( cameraCount == 2 ) {
                View view = findViewById(R.id.switch_camera);
                view.setVisibility(View.VISIBLE);
            } else {
                View view = findViewById(R.id.switch_camera);
                view.setVisibility(View.GONE);
            }
        }

        addClickListener();
    }

    /**
     * Recommended "safe" way to open the camera.
     * @param view
     * @return
     */
    private boolean safeCameraOpenInView(View view) {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance(cameraId);
        mCameraView = view;
        qOpened = (mCamera != null);

        if(qOpened == true){
            FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);

            if( preview.getChildCount() == 0 ){
                mPreview = new CameraPreview( getBaseContext(), mCamera, view);
                preview.addView(mPreview);
            } else {
                mPreview = (CameraPreview)preview.getChildAt(0);
                mPreview.setCamera(mCamera);
            }
            mPreview.startCameraPreview();
        }
        return qOpened;
    }

    private void closeCameraAndView(){
        releaseCameraAndPreview();

//        ViewGroup view = (ViewGroup)findViewById(R.id.camera_preview);
//        view.removeAllViews();
    }

    /**
     * Clear any existing preview / camera.
     */
    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(mPreview != null){
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }

    private void addClickListener(){
        {
            View view = findViewById(R.id.flash);
            view.setOnClickListener(this);
        }

        {
            View view = findViewById(R.id.switch_camera);
            view.setOnClickListener(this);
        }

        {
            View view = findViewById(R.id.send_camera_picture);
            view.setOnClickListener(this);
        }

        {
            View view = findViewById(R.id.discard_camera_picture);
            view.setOnClickListener(this);
        }

        {
            View view = findViewById(R.id.capture);
            view.setOnClickListener(this);
        }
    }

    private void handleClickEvent(View view, int id){
        if( id == R.id.discard_camera_picture ) {
            discardPictureTaken();
        } else if( id == R.id.send_camera_picture ) {
            handleSendMedia();
            finish();
        } else if( id == R.id.flash ) {
            changeFlashStatus();
            changeFlashContent();
        } else if( id == R.id.switch_camera ) {
            animateCamera(view);
            changeCamera();
        } else if( id == R.id.capture ){

            {
                View tempView = findViewById(R.id.camera_preview);
                tempView.setVisibility(View.GONE);
            }
            {
                View tempView = findViewById(R.id.camera_config_layout);
                tempView.setVisibility(View.GONE);
            }

            mCamera.takePicture(null, null, mPicture);
        }
    }

    private void animateCamera(final View view){
        final RotateAnimation rotate = new RotateAnimation( 0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setInterpolator(new FastOutSlowInInterpolator());
        rotate.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        view.startAnimation(rotate);
        view.setClickable(false);

    }

    private void changeCamera(){
        if( cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ){
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        safeCameraOpenInView(mCameraView);
    }

    private void changeFlashStatus(){
        if( flashStatus.equals(Camera.Parameters.FLASH_MODE_ON) ) {
            flashStatus = Camera.Parameters.FLASH_MODE_OFF;
        } else if( flashStatus.equals(Camera.Parameters.FLASH_MODE_OFF) ) {
            flashStatus = Camera.Parameters.FLASH_MODE_ON;
        }
    }

    private void changeFlashContent(){
        ImageView flash = (ImageView)findViewById(R.id.flash);
        if( flashStatus.equals(Camera.Parameters.FLASH_MODE_ON) ) {
            flash.setImageResource( R.drawable.ic_flash_on);
        } else if( flashStatus.equals(Camera.Parameters.FLASH_MODE_OFF) ) {
            flash.setImageResource( R.drawable.ic_flash_disabled);
        }

        {
            Camera.Parameters p = mCamera.getParameters();
            p.setFlashMode(flashStatus);
            mCamera.setParameters(p);

            mPreview.startCameraPreview();
        }
    }

    private void showPictureViewsToSend(byte [] pictureContent) {
        closeCameraAndView();

        {
            View view = findViewById(R.id.send_discard_layout);
            view.setVisibility(View.VISIBLE);
        }

        {
            /*
             * rotate image content came from camera.
             */
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(pictureContent, 0, pictureContent.length);

            Matrix matrix = new Matrix();
            if( cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ) {
                matrix.postRotate(90);
            } else {
                matrix.preScale( -1, 1);
                matrix.postRotate(90);
            }

            Log.i("Captured Image Size ", originalBitmap.getWidth() + " : " + originalBitmap.getHeight());

            Bitmap rotatedBitMap = Bitmap.createBitmap( originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, false);
            originalBitmap.recycle();

            ImageView imageView = (ImageView)findViewById(R.id.taken_picture);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(rotatedBitMap);

            {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotatedBitMap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                content = byteArrayOutputStream.toByteArray();
            }
        }
    }

    private void discardPictureTaken() {
        content = null;

        {
            View view = findViewById(R.id.send_discard_layout);
            view.setVisibility(View.GONE);
        }

        {
            View view = findViewById(R.id.taken_picture);
            view.setVisibility(View.GONE);
        }

        {
            View view = findViewById(R.id.camera_config_layout);
            view.setVisibility(View.VISIBLE);
        }

        {
            View view = findViewById(R.id.camera_preview);
            view.setVisibility(View.VISIBLE);
        }

        safeCameraOpenInView(mCameraView);
    }

    /**
     * Picture Callback for handling a picture capture and saving it out to a file.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            showPictureViewsToSend(data);
        }
    };

    private void handleSendMedia(){
        new ThreadTask( content){

            @Override
            public Object process() {
                byte[] byteArray = (byte[])object;
                String fileName = String.valueOf(System.currentTimeMillis());

                DBUtil.writeContentToFile(getBaseContext(), fileName, byteArray, false);
                return fileName;
            }

            @Override
            public void postAction(Object object) {
                String fileName = (String)object;
                addActionToCorrespondingActivity(ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_IMAGE, fileName, object);
            }

        }.start();

    }

    private boolean addActionToCorrespondingActivity(String key, String mimeType, String fileName, Object bytes) {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        activityActionHandler.addActionOnHold( ActivityActionHandler.CHAT_SCREEN_KEY, new ActivityActionHandler.ActionItem( mimeType, fileName, bytes));
        return success;
    }

    /**
     * Used to return the camera File output.
     * @return
     */
    private File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "UltimateCameraGuideApp");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Camera Guide", "Required media storage does not exist");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

//        DialogHelper.showDialog( "Success!","Your picture has been saved!",getActivity());

        return mediaFile;
    }


    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        // SurfaceHolder
        private SurfaceHolder mHolder;

        // Our Camera.
        private Camera mCamera;

        // Parent Context.
        private Context mContext;

        // Camera Sizing (For rotation, orientation changes)
        private Camera.Size mPreviewSize;

        // List of supported preview sizes
        private List<Camera.Size> mSupportedPreviewSizes;

        private int cameraOrientation = 0;

        // View holding this camera.
        private View mCameraView;

        public CameraPreview(Context context, Camera camera, View cameraView) {
            super(context);

            // Capture the context
            mCameraView = cameraView;
            mContext = context;
            setCamera(camera);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setKeepScreenOn(true);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        /**
         * Begin the preview of the camera input.
         */
        public void startCameraPreview() {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Extract supported preview and flash modes from the camera.
         *
         * @param camera
         */
        private void setCamera(Camera camera) {
            mCamera = camera;

            computeCameraOrientation();

            Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mPreviewSize = getBestAspectPreviewSize( display.getWidth(), display.getHeight(), mCamera.getParameters());

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setFlashMode(flashStatus);
            parameters.setPictureSize( mPreviewSize.width, mPreviewSize.height);
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setJpegQuality(85);
            mCamera.setParameters(parameters);

            requestLayout();
        }

        /**
         * The Surface has been created, now tell the camera where to draw the preview.
         *
         * @param holder
         */
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Dispose of the camera preview.
         *
         * @param holder
         */
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }

        /**
         * React to surface changed events
         *
         * @param holder
         * @param format
         * @param w
         * @param h
         */
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

//            // stop preview before making changes
//            try {
//                mCamera.stopPreview();
//
//                //change camera settings
//
//                mCamera.startPreview();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

//        /**
//         * Calculate the measurements of the layout
//         *
//         * @param widthMeasureSpec
//         * @param heightMeasureSpec
//         */
//        @Override
//        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//
//            setMeasuredDimension(width, height);
//
//            if (mSupportedPreviewSizes != null) {
//                mPreviewSize = getBestAspectPreviewSize(width, height, mCamera.getParameters());
//            }
//        }

//        /**
//         * Update the layout based on rotation and orientation changes.
//         *
//         * @param changed
//         * @param left
//         * @param top
//         * @param right
//         * @param bottom
//         */
//        @Override
//        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//            if (changed) {
//                final int width = right - left;
//                final int height = bottom - top;
//
//                int previewWidth = width;
//                int previewHeight = height;
//
//                Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//                int displayOrientation = display.getRotation();
//
//                if (mPreviewSize != null) {
//                    switch (displayOrientation) {
//                        case Surface.ROTATION_0:
//                            previewWidth = mPreviewSize.height;
//                            previewHeight = mPreviewSize.width;
//
//                            cameraOrientation = 90;
//                            mCamera.setDisplayOrientation(cameraOrientation);
//                            break;
//                        case Surface.ROTATION_90:
//                            previewWidth = mPreviewSize.width;
//                            previewHeight = mPreviewSize.height;
//                            break;
//                        case Surface.ROTATION_180:
//                            previewWidth = mPreviewSize.height;
//                            previewHeight = mPreviewSize.width;
//                            break;
//                        case Surface.ROTATION_270:
//                            previewWidth = mPreviewSize.width;
//                            previewHeight = mPreviewSize.height;
//
//                            cameraOrientation = 180;
//                            mCamera.setDisplayOrientation(cameraOrientation);
//                            break;
//                    }
//                }
//
//                final int scaledChildHeight = previewHeight * width / previewWidth;
//                mCameraView.layout(0, height - scaledChildHeight, width, height);
//            }
//        }

        public Camera.Size getBestAspectPreviewSize( int width, int height, Camera.Parameters parameters) {
            Log.i("Native Camera", "Display Size " + width + " : " + height);

            Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            double targetRatio;
            if (cameraOrientation == 90 || cameraOrientation == 270) {
                targetRatio = (double) height / width;
            } else {
                targetRatio = (double) width / height;
            }

            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            Collections.sort(sizes, Collections.reverseOrder(new SizeComparator()));


            for (Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;

                if (Math.abs(ratio - targetRatio) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(ratio - targetRatio);
                }

            }

            Log.i("Native Camera", "Optimal Size " + optimalSize.width + " : " + optimalSize.height);
            return (optimalSize);
        }

        private void computeCameraOrientation(){
            Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int displayOrientation = display.getRotation();

            switch (displayOrientation) {
                case Surface.ROTATION_0:
                    cameraOrientation = 90;
                    mCamera.setDisplayOrientation(cameraOrientation);
                    break;
                case Surface.ROTATION_90:
                    cameraOrientation = 0;
                    mCamera.setDisplayOrientation(cameraOrientation);
                    break;
                case Surface.ROTATION_180:
                    cameraOrientation = 0;
                    mCamera.setDisplayOrientation(cameraOrientation);
                    break;
                case Surface.ROTATION_270:
                    cameraOrientation = 180;
                    mCamera.setDisplayOrientation(cameraOrientation);
                    break;
            }
        }

        private class SizeComparator implements Comparator<Camera.Size> {

            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int left = lhs.width * lhs.height;
                int right = rhs.width * rhs.height;

                if (left < right) {
                    return (-1);
                } else if (left > right) {
                    return (1);
                }

                return (0);
            }

        }
    }

}
