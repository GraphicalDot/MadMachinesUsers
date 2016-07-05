package com.sports.unity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.sports.unity.common.controller.UserProfileActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Created by Mad on 20-Jun-16.
 */
public class CropImageFragment extends Fragment {

    private CropImageView cropImageView;
    private Bitmap profileImage;
    private int screenHeight;
    private int screenWidth;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_crop, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        cropImageView = (CropImageView) view.findViewById(R.id.crop_image_view);
        cropImageView.setGuidelines(CropImageView.Guidelines.ON);
        adjustBitmapAndSetForCropping();
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setAutoZoomEnabled(false);
        cropImageView.setImageBitmap(profileImage);
        cropImageView.setAspectRatio(1, 1);
        cropImageView.invalidate();
        Button okButton = (Button) view.findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageAndFinish();
            }
        });
        Button cancelButton = (Button) view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCropping();
            }
        });
    }

    private void cancelCropping() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(ProfileCreationActivity.CROP_FRAGMENT_TAG);
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    private void cropImageAndFinish() {
        profileImage = cropImageView.getCroppedImage();
        int imageWidth = profileImage.getWidth();
        int imageHeight = profileImage.getHeight();
        float sr = 1;
        sr = ((float) 720) / imageWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(sr, sr);
        profileImage = Bitmap.createBitmap(profileImage, 0, 0, imageWidth, imageHeight, matrix, false);
        if (getActivity() instanceof ProfileCreationActivity) {
            ((ProfileCreationActivity) getActivity()).setProfileImage(profileImage);
        } else if (getActivity() instanceof UserProfileActivity) {
            ((UserProfileActivity) getActivity()).setProfileImage(profileImage,true);
        }
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(ProfileCreationActivity.CROP_FRAGMENT_TAG);
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    private void adjustBitmapAndSetForCropping() {
        int imageWidth = profileImage.getWidth();
        int imageHeight = profileImage.getHeight();
        float sx = 1;
        float sy = 1;
        if (imageWidth > imageHeight) {
            sx = ((float) screenWidth) / imageWidth;
            Matrix matrix = new Matrix();
            matrix.postScale(sx, sx);
            profileImage = Bitmap.createBitmap(profileImage, 0, 0, imageWidth, imageHeight, matrix, false);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(profileImage.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            cropImageView.setLayoutParams(params);
        } else if (imageHeight > imageWidth) {
            sy = ((float) screenHeight) / imageHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(sy, sy);
            profileImage = Bitmap.createBitmap(profileImage, 0, 0, imageWidth, imageHeight, matrix, false);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, profileImage.getHeight());
            params.gravity = Gravity.CENTER;
            cropImageView.setLayoutParams(params);
        }
    }

    public void setProfileImage(Bitmap bitmap) {
        profileImage = bitmap;
    }
}
