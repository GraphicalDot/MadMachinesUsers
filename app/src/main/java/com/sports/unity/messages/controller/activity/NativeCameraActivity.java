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

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ImageUtil;
import com.sports.unity.util.ThreadTask;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NativeCameraActivity extends CustomAppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    /**
     * Safe method for getting a camera instance.
     *
     * @return
     */
    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    private static final int VIDEO_MAX_DURATION = 15;

    private String flashStatus = Camera.Parameters.FLASH_MODE_ON;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private Camera mCamera;

    private CameraPreview mPreview;
    private View mCameraView;

    private byte[] content = null;
    private String videoContentOutputFilename = null;
    private String contentMimeType = null;

    private MediaRecorder recorder = null;
    private Timer timer = null;

    private boolean captureImageClicked = false;

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
        if (opened == false) {
            Log.i("Camera", "Error, Camera failed to open");
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

        cancelVideoRecording();
        closeCameraAndView();
    }

    @Override
    public void onBackPressed() {
        clearDiscardedContent();

        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        handleClickEvent(view, view.getId());
    }

    @Override
    public boolean onLongClick(View v) {
        handleLongClickEvent(v);
        return false;
    }

    private void initView() {

        {
            String sendTo = getIntent().getStringExtra(Constants.INTENT_KEY_PHONE_NUMBER);
            TextView sendToView = (TextView) findViewById(R.id.identity);
            sendToView.setText(sendTo);
        }

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
            if (cameraCount == 2) {
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
     *
     * @param view
     * @return
     */
    private boolean safeCameraOpenInView(View view) {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance(cameraId);
        mCameraView = view;
        qOpened = (mCamera != null);

        if (qOpened == true) {
            FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);

            if (preview.getChildCount() == 0) {
                mPreview = new CameraPreview(getBaseContext(), mCamera, view);
                preview.addView(mPreview);
            } else {
                mPreview = (CameraPreview) preview.getChildAt(0);
                mPreview.setCamera(mCamera);
            }
            mPreview.startCameraPreview();
        }
        return qOpened;
    }

    private void closeCameraAndView() {
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
        if (mPreview != null) {
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }

    private void addClickListener() {
        {
            View view = findViewById(R.id.flash);
            view.setOnClickListener(this);
        }

        {
            View view = findViewById(R.id.switch_camera);
            view.setOnClickListener(this);
        }

        {
            View view = findViewById(R.id.send_camera_content);
            view.setOnClickListener(this);
        }

        {
            View view = findViewById(R.id.discard_camera_content);
            view.setOnClickListener(this);
        }

        {
            View view = findViewById(R.id.capture);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }
    }

    private void handleClickEvent(View view, int id) {
        if (id == R.id.discard_camera_content) {
            discardContent();
        } else if (id == R.id.send_camera_content) {
            handleSendMedia();
        } else if( id == R.id.flash ) {
            if( mCamera != null ) {
                changeFlashStatus();
                changeFlashContent();
            }
        } else if( id == R.id.switch_camera ) {
            animateCamera(view);
            changeCamera();
        } else if( id == R.id.capture ){
            if( ! captureImageClicked && mCamera != null  ) {
                captureImageClicked = true;
                mCamera.takePicture(null, null, mPicture);
                findViewById(R.id.capture).setEnabled(false);
            } else {
                //nothing
            }
        }
    }

    private void handleLongClickEvent(final View view) {
        int id = view.getId();
        if (id == R.id.capture) {

            view.setOnTouchListener(new CustomTouchListener(view));

            prepareLayoutForVideo();
            prepareForVideoRecording();

            startTimer();
        }
    }

    private void prepareForVideoRecording() {
        recorder = new MediaRecorder();

        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            recorder.setOrientationHint(90);
        } else {
            if (mPreview.cameraOrientation == 90) {
                recorder.setOrientationHint(270);
            } else if (mPreview.cameraOrientation == 270) {
                recorder.setOrientationHint(90);
            }
        }

        mCamera.unlock();
        recorder.setCamera(mCamera);

        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        videoContentOutputFilename = DBUtil.getUniqueFileName(SportsUnityDBHelper.MIME_TYPE_VIDEO, UserUtil.isSaveInAppCaptureMediaToGallery());
        recorder.setOutputFile(DBUtil.getFilePath(this, SportsUnityDBHelper.MIME_TYPE_VIDEO, videoContentOutputFilename));

        boolean success = false;
        try {
            recorder.prepare();

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (success) {
            recorder.start();
        } else {
            //TODO
        }

    }

    private void cancelVideoRecording() {
        cancelTimer();

        TextView holdMessage = (TextView) findViewById(R.id.hold_message);
        holdMessage.setText(R.string.message_to_hold_button_for_video);

        releaseMediaRecorder();

        discardLayoutForVideo();

        //TODO
    }

    private void doneVideoRecording() {
        contentMimeType = SportsUnityDBHelper.MIME_TYPE_VIDEO;

        cancelTimer();

        TextView holdMessage = (TextView) findViewById(R.id.hold_message);
        holdMessage.setText(R.string.message_to_hold_button_for_video);

        releaseMediaRecorder();
        closeCameraAndView();

        discardLayoutForVideo();
        prepareLayoutToSendContent();

        playVideo();
    }

    private void prepareLayoutForVideo() {
        {
            View tempView = findViewById(R.id.flash);
            tempView.setVisibility(View.GONE);
        }

        {
            View tempView = findViewById(R.id.switch_camera);
            tempView.setVisibility(View.GONE);
        }

        {
            TextView holdMessage = (TextView) findViewById(R.id.hold_message);
            holdMessage.setText(R.string.message_video_recoding_on);
        }

        {
            ImageView view = (ImageView) findViewById(R.id.capture);
            view.setImageResource(R.drawable.ic_record_video);
        }

        {
            View tempView = findViewById(R.id.top_recording_layout);
            tempView.setVisibility(View.VISIBLE);

            TextView view = (TextView) tempView.findViewById(R.id.video_recording_time);
            view.setText("00:" + VIDEO_MAX_DURATION);
        }
    }

    private void discardLayoutForVideo() {
        {
            View tempView = findViewById(R.id.camera_config_layout);
            tempView.setBackgroundColor(getResources().getColor(R.color.semi_transparent));
        }

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            View tempView = findViewById(R.id.flash);
            tempView.setVisibility(View.VISIBLE);
        }

        int cameraCount = Camera.getNumberOfCameras();
        if (cameraCount == 2) {
            View tempView = findViewById(R.id.switch_camera);
            tempView.setVisibility(View.VISIBLE);
        }

        {
            ImageView view = (ImageView) findViewById(R.id.capture);
            view.setImageResource(R.drawable.ic_capture);
        }

        {
            View tempView = findViewById(R.id.top_recording_layout);
            tempView.setVisibility(View.GONE);
        }
    }

    private void insideOfRecordButton() {
        {
            View view = findViewById(R.id.camera_config_layout);
            view.setBackgroundColor(getResources().getColor(R.color.semi_transparent));
        }
    }

    private void outsideOfRecordButton() {
        {
            View view = findViewById(R.id.camera_config_layout);
            view.setBackgroundColor(getResources().getColor(R.color.red_semi_transparent));
        }
    }

    private void playVideo() {
        try {
            Uri uri = Uri.parse(DBUtil.getFilePath(this.getBaseContext(), SportsUnityDBHelper.MIME_TYPE_VIDEO, videoContentOutputFilename));

            VideoView videoView = (VideoView) findViewById(R.id.video_container);
            videoView.setVisibility(View.VISIBLE);

            videoView.setMediaController(new MediaController(this));
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.setZOrderMediaOverlay(true);
            findViewById(R.id.send_discard_layout).bringToFront();
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                    return true;
                }
            });
            videoView.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void releaseMediaRecorder() {
        if (recorder != null) {
            recorder.reset();
            recorder.release();
            recorder = null;
            mCamera.lock();
        }
    }

    private void animateCamera(final View view) {
        final RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
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

    private void changeCamera() {
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        safeCameraOpenInView(mCameraView);
    }

    private void changeFlashStatus() {
        if (flashStatus.equals(Camera.Parameters.FLASH_MODE_ON)) {
            flashStatus = Camera.Parameters.FLASH_MODE_OFF;
        } else if (flashStatus.equals(Camera.Parameters.FLASH_MODE_OFF)) {
            flashStatus = Camera.Parameters.FLASH_MODE_ON;
        }
    }

    private void changeFlashContent() {
        ImageView flash = (ImageView) findViewById(R.id.flash);
        if (flashStatus.equals(Camera.Parameters.FLASH_MODE_ON)) {
            flash.setImageResource(R.drawable.ic_flash_on);
        } else if (flashStatus.equals(Camera.Parameters.FLASH_MODE_OFF)) {
            flash.setImageResource(R.drawable.ic_flash_disabled);
        }

        {
            Camera.Parameters p = mCamera.getParameters();
            setFlashMode(p);
            mCamera.setParameters(p);

            mPreview.startCameraPreview();
        }
    }

    private void showPictureViewsToSend(byte[] pictureContent) {
        prepareLayoutToSendContent();

        {
            /*
             * rotate image content came from camera.
             */
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(pictureContent, 0, pictureContent.length);

            Matrix matrix = new Matrix();
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                matrix.postRotate(mPreview.cameraOrientation);
            } else {
                matrix.preScale(-1, 1);
                matrix.postRotate(mPreview.cameraOrientation);
            }

            Log.i("Captured Image Size ", originalBitmap.getWidth() + " : " + originalBitmap.getHeight());

            Bitmap rotatedBitMap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, false);
            originalBitmap.recycle();

            ImageView imageView = (ImageView) findViewById(R.id.taken_picture);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(rotatedBitMap);

            content = ImageUtil.getCompressedBytes(rotatedBitMap);
        }
    }

    private void prepareLayoutToSendContent() {
        closeCameraAndView();

        {
            View tempView = findViewById(R.id.camera_preview);
            tempView.setVisibility(View.GONE);
        }

        {
            View tempView = findViewById(R.id.camera_config_layout);
            tempView.setVisibility(View.GONE);
        }

        {
            View view = findViewById(R.id.send_discard_layout);
            view.setVisibility(View.VISIBLE);
        }

    }

    private void discardContent() {
        content = null;

        clearDiscardedContent();

        {
            View view = findViewById(R.id.send_discard_layout);
            view.setVisibility(View.GONE);
        }

        {
            VideoView view = (VideoView) findViewById(R.id.video_container);
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

    private void clearDiscardedContent() {
        DBUtil.deleteContentFromExternalFileStorage(this, SportsUnityDBHelper.MIME_TYPE_VIDEO, videoContentOutputFilename);
    }

    /**
     * Picture Callback for handling a picture capture and saving it out to a file.
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            contentMimeType = SportsUnityDBHelper.MIME_TYPE_IMAGE;
            showPictureViewsToSend(data);
            findViewById(R.id.capture).setEnabled(true);
            captureImageClicked = false;
        }

    };

    private void handleSendMedia() {
        new ThreadTask(content) {

            private String thumbnailImage = null;

            @Override
            public Object process() {
                String fileName = null;
                if (contentMimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
                    byte[] byteArray = (byte[]) object;
                    fileName = DBUtil.getUniqueFileName(SportsUnityDBHelper.MIME_TYPE_IMAGE, UserUtil.isSaveInAppCaptureMediaToGallery());

                    DBUtil.writeContentToExternalFileStorage(getBaseContext(), fileName, byteArray, contentMimeType);
                } else if (contentMimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
                    fileName = videoContentOutputFilename;
                }

                thumbnailImage = PersonalMessaging.createThumbnailImageAsBase64(NativeCameraActivity.this, contentMimeType, fileName);

                return fileName;
            }

            @Override
            public void postAction(Object object) {
                String fileName = (String) object;
                ActivityActionHandler.getInstance().pendingDispatchSendMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, contentMimeType, fileName, thumbnailImage, this.object);

                NativeCameraActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        NativeCameraActivity.this.finish();
                    }

                });
            }

        }.start();

    }

    private void startTimer() {
        cancelTimer();

        TextView textView = (TextView) findViewById(R.id.video_recording_time);
        textView.setTag(VIDEO_MAX_DURATION);

        timer = new Timer();
        timer.schedule(new VideoTimerTask(textView), 1000, 1000);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private class VideoTimerTask extends TimerTask {

        private TextView textView = null;
        private int count = VIDEO_MAX_DURATION;

        private VideoTimerTask(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void run() {
            count--;

            if (count > -1) {
                NativeCameraActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(count < 10 ? "00:0" + count : "00:" + count);
                        textView.setTag(count);
                    }
                });
            } else {
                releaseMediaRecorder();
            }

        }

    }

    private void setFocusMode(Camera.Parameters parameters) {
        List<String> supportedFocusMode = parameters.getSupportedFocusModes();
        if (supportedFocusMode != null && supportedFocusMode.size() > 0) {
            String focusMode = null;
            if (supportedFocusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
            } else if (supportedFocusMode.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                focusMode = Camera.Parameters.FOCUS_MODE_AUTO;
            } else if (supportedFocusMode.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                focusMode = Camera.Parameters.FOCUS_MODE_INFINITY;
            } else {
                focusMode = supportedFocusMode.get(0);
            }

            parameters.setFocusMode(focusMode);
        } else {
            //nothing
        }
    }

    private void setFlashMode(Camera.Parameters parameters) {
        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes != null && supportedFlashModes.size() > 0) {
            if (supportedFlashModes.contains(flashStatus)) {
                parameters.setFlashMode(flashStatus);
            } else {
                //nothing
            }
        } else {
            //nothing
        }
    }

    public static int getCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
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

//            computeCameraOrientation();
            cameraOrientation = getCameraDisplayOrientation(NativeCameraActivity.this, cameraId, mCamera);
            camera.setDisplayOrientation(cameraOrientation);
            Log.d("Native Camera", "Camera Display orientation " + cameraOrientation);

            Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mPreviewSize = getBestAspectPreviewSize(display.getWidth(), display.getHeight(), mCamera.getParameters());

            Camera.Parameters parameters = mCamera.getParameters();

            setFocusMode(parameters);
            setFlashMode(parameters);

            parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);
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

        public Camera.Size getBestAspectPreviewSize(int width, int height, Camera.Parameters parameters) {
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


            for (int index = 0; index < 4 && index < sizes.size(); index++) {
                Camera.Size size = sizes.get(index);
                double ratio = (double) size.width / size.height;

                if (Math.abs(ratio - targetRatio) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(ratio - targetRatio);
                }

            }

            Log.i("Native Camera", "Optimal Size " + optimalSize.width + " : " + optimalSize.height);
            return (optimalSize);
        }

        private void computeCameraOrientation() {
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

    class CustomTouchListener implements View.OnTouchListener {

        private static final int INSIDE_STATUS = 0;
        private static final int OUTSIDE_STATUS = 1;

        private Rect recordButtonRect = null;
        private int[] location = null;

        private int lastTouchStatus = INSIDE_STATUS;


        private CustomTouchListener(View view) {
            location = new int[2];
            view.getLocationOnScreen(location);
            recordButtonRect = new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            boolean success = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {

                    break;
                }
                case MotionEvent.ACTION_UP: {


                    int pointX = location[0] + (int) event.getX();
                    int pointY = location[1] + (int) event.getY();

                    TextView textView = (TextView) NativeCameraActivity.this.findViewById(R.id.video_recording_time);
                    Integer videoDuration = VIDEO_MAX_DURATION - (Integer) textView.getTag();

                    if (recordButtonRect.contains(pointX, pointY) && videoDuration > 0) {
                        doneVideoRecording();
                    } else {
                        cancelVideoRecording();
                    }

                    view.setOnTouchListener(null);

                    success = true;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    int pointX = location[0] + (int) event.getX();
                    int pointY = location[1] + (int) event.getY();

                    Log.i("Native Camera", "Move " + pointX + " : " + pointY);

                    if (recordButtonRect.contains(pointX, pointY)) {
                        if (lastTouchStatus == OUTSIDE_STATUS) {
                            lastTouchStatus = INSIDE_STATUS;
                            insideOfRecordButton();
                        } else {
                            //nothing
                        }
                    } else {
                        if (lastTouchStatus == INSIDE_STATUS) {
                            lastTouchStatus = OUTSIDE_STATUS;
                            outsideOfRecordButton();
                        } else {
                            //nothing
                        }
                    }
                    break;
                }
            }
            return success;
        }

    }

}
