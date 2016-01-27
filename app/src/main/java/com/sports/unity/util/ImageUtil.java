package com.sports.unity.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by amandeep on 13/1/16.
 */
public class ImageUtil {

    public static final int SMALL_THUMB_IMAGE_SIZE = 100;

    public static byte[] handleImageAndSetToView(Intent data, ImageView imageView, int requiredWidth, int requiredHeight) {
        byte[] compressedContent = null;
        if (imageView != null) {
            Context context = imageView.getContext();
            boolean success = false;
            try {
                String selectedImageFilePath = null;
                if (data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    Log.d("Image Util", "Un-Accessible Selected Image Uri " + selectedImageUri);

                    if( selectedImageUri.getScheme().equalsIgnoreCase("file") ){
                        selectedImageFilePath = selectedImageUri.getPath();
                    } else if( selectedImageUri.getScheme().equalsIgnoreCase("content") ) {
                        selectedImageFilePath = ImageUtil.getRealPathFromURI(selectedImageUri, context);
                    }

                    Log.d("Image Util", "Accessible Selected Image Path " + selectedImageFilePath);
                } else {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    Uri selectedImageUri = ImageUtil.getImageUri(context, bitmap);

                    selectedImageFilePath = ImageUtil.getRealPathFromURI(selectedImageUri, context);
                    Log.d("Image Util", "Accessible Selected Image Path " + selectedImageFilePath);
                }

                if (selectedImageFilePath != null) {
                    compressedContent = getAndSetCompressedImageToView(selectedImageFilePath, imageView, requiredWidth, requiredHeight);

                    success = true;
                } else {
                    success = false;
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            if (success == false) {
                Toast.makeText(context, "Unable to access this file since it is locked :( Select another image.", Toast.LENGTH_SHORT).show();
            }
        }
        return compressedContent;
    }

    public static Bitmap rotateImageIfRequired(Bitmap img, File selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

//    public static Bitmap getBitmapFromUri(Uri uri, Context context) throws IOException {
//        ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
//        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//        parcelFileDescriptor.close();
//        return image;
//    }

    public static Bitmap getCompressedBitmap(String filePath, int requiredWidth, int requiredHeight) throws Exception {
        Bitmap bitmap = getDecodedSampleBitmap(filePath, requiredWidth, requiredHeight);
        byte[] content = ImageUtil.getCompressedBytes(bitmap);
        bitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
        return bitmap;
    }

    public static byte[] getCompressedBytes(String filePath, int requiredWidth, int requiredHeight) throws Exception {
        Bitmap bitmap = getDecodedSampleBitmap(filePath, requiredWidth, requiredHeight);
        byte[] content = ImageUtil.getCompressedBytes(bitmap);
        return content;
    }

    private static byte[] getAndSetCompressedImageToView(String filePath, ImageView imageView, int requiredWidth, int requiredHeight) throws Exception {
        byte[] content = ImageUtil.getCompressedBytes(filePath, requiredWidth, requiredHeight);

        Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
        imageView.setImageBitmap(bitmap);

        return content;
    }

    private static Bitmap getDecodedSampleBitmap(String filePath, int requiredWidth, int requiredHeight) throws Exception {
        File file = new File(filePath);
        Bitmap bitmap = ImageUtil.decodeSampleImage(file, requiredWidth, requiredHeight);
        bitmap = ImageUtil.rotateImageIfRequired(bitmap, file);
        return bitmap;
    }

    private static Bitmap decodeSampleImage(File file, int width, int height) throws Exception {
//            System.gc(); // First of all free some memory

        // Decode image size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(file), null, options);

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = calculateInSampleSize(options, width, height);
        o2.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
    }

//    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeResource(res, resId, options);
//    }
//
//    private static Bitmap decodeSampleImage(String filePath, int width, int height) throws Exception {
//        return decodeSampleImage(new File(filePath), width, height);
//    }
//
//    private static Bitmap decodeSampleImage(Uri uri, int width, int height, Context context) throws Exception {
//        return decodeSampleImage( new File(getRealPathFromURI(uri, context)), width, height);
//    }

    private static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int sampleScaleSize = 1;

//        while (options.outWidth / sampleScaleSize / 2 >= reqWidth && options.outHeight / sampleScaleSize / 2 >= reqHeight) {
//            sampleScaleSize *= 2;
//        }

        int sampleScaleSize_WithWidth = options.outWidth / reqWidth;
        int sampleScaleSize_WithHeight = options.outHeight / reqHeight;

        if( sampleScaleSize_WithHeight < sampleScaleSize_WithWidth ){
            sampleScaleSize = sampleScaleSize_WithHeight;
        } else {
            sampleScaleSize = sampleScaleSize_WithWidth;
        }

        return sampleScaleSize;
    }

    private static Bitmap rotateImage(Bitmap bitmap, int degree) {
        Bitmap rotatedBitmap = null;
        if( bitmap != null ) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
        } else {
            //nothing
        }
        return rotatedBitmap;
    }

    public static String getRealPathFromURI(Uri uri, Context context) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if( cursor != null ) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            path = cursor.getString(idx);
        } else {
            //nothing
        }
        return path;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static byte[] getCompressedBytes(Bitmap bitmap){
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            bytes = byteArrayOutputStream.toByteArray();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            try{
                byteArrayOutputStream.close();
            }catch (Exception ex){}
        }

        return bytes;
    }

    public static Bitmap getCompressedBitmap(Bitmap bitmap){
        Bitmap compressedBitmap = null;
        byte[] content = getCompressedBytes(bitmap);
        if( content != null ) {
            compressedBitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
        }
        return compressedBitmap;
    }

}
