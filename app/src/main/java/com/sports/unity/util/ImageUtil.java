package com.sports.unity.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.ByteBuffer;

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

                    if (selectedImageUri.getScheme().equalsIgnoreCase("file")) {
                        selectedImageFilePath = selectedImageUri.getPath();
                    } else if (selectedImageUri.getScheme().equalsIgnoreCase("content")) {
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
                    compressedContent = getAndSetScaledDownAndCompressedImageToView(selectedImageFilePath, imageView, requiredWidth, requiredHeight);

                    success = true;
                } else {
                    success = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (success == false) {
                Toast.makeText(context, "Unable to access this file since it is locked :( Select another image.", Toast.LENGTH_SHORT).show();
            }
        }
        return compressedContent;
    }

    public static String getBaseEncoded_ThumbnailImage(Context context, String fileName) {
        byte[] content = null;
        String encodedImage = null;
        try {
            Bitmap scaledBitmap = getScaledBitmap(fileName, context.getResources().getDimensionPixelSize(R.dimen.media_msg_content_width), context.getResources().getDimensionPixelSize(R.dimen.media_msg_content_height));
            Bitmap blurBitmap = blur(context, scaledBitmap);
            Bitmap croppedBitmap = getCroppedBitmap(blurBitmap, context.getResources().getDimensionPixelSize(R.dimen.media_msg_content_width), context.getResources().getDimensionPixelSize(R.dimen.media_msg_content_height));
            content = getCompressedBytes(croppedBitmap, 50);
            encodedImage = Base64.encodeToString(content, Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return encodedImage;
    }

    public static String getBaseEncoded_ThumbnailImage(Context context, Bitmap bitmap) {
        byte[] content = null;
        String encodedImage = null;
        try {
            Bitmap scaledBitmap = getScaledBitmap(bitmap, context.getResources().getDimensionPixelSize(R.dimen.media_msg_content_width), context.getResources().getDimensionPixelSize(R.dimen.media_msg_content_height));
            Bitmap blurBitmap = blur(context, scaledBitmap);
            Bitmap croppedBitmap = getCroppedBitmap(blurBitmap, context.getResources().getDimensionPixelSize(R.dimen.media_msg_content_width), context.getResources().getDimensionPixelSize(R.dimen.media_msg_content_height));
            content = getCompressedBytes(croppedBitmap, 50);
            encodedImage = Base64.encodeToString(content, Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return encodedImage;
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
//
//    public static Bitmap getCompressedBitmap(String filePath, int requiredWidth, int requiredHeight) throws Exception {
//        Bitmap bitmap = getDecodedSampleBitmap(filePath, requiredWidth, requiredHeight);
//        byte[] content = ImageUtil.getCompressedBytes(bitmap);
//        bitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
//        return bitmap;
//    }

    public static byte[] getScaledDownBytes(String filePath, DisplayMetrics displayMetrics) throws Exception {
        float screenRatio = displayMetrics.widthPixels / (float) displayMetrics.heightPixels;

        int requiredWidth = 0;
        int requiredHeight = 0;

        if (screenRatio >= 1) {
            requiredWidth = 1500;
            requiredHeight = (int) (requiredWidth / screenRatio);
        } else {
            requiredHeight = 1500;
            requiredWidth = (int) (requiredHeight * screenRatio);
        }

        return getScaledDownBytes(filePath, requiredWidth, requiredHeight);
    }

    private static byte[] getScaledDownAndCompressedBytes(String filePath, int requiredWidth, int requiredHeight) throws Exception {
        Bitmap bitmap = getDecodedSampleBitmap(filePath, requiredWidth, requiredHeight);
        byte[] content = getCompressedBytes(bitmap);
        return content;
    }

    private static byte[] getScaledDownBytes(String filePath, int requiredWidth, int requiredHeight) throws Exception {
        Bitmap bitmap = getDecodedSampleBitmap(filePath, requiredWidth, requiredHeight);
        byte[] content = getBytes(bitmap);
        return content;
    }

    public static Bitmap getScaledBitmap(String filePath, int requiredWidth, int requiredHeight) throws Exception {
        File file = new File(filePath);
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        return getScaledBitmap(bitmap, requiredWidth, requiredHeight);
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int requiredWidth, int requiredHeight) throws Exception {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleByHeight = requiredHeight > 0 ? (float) requiredHeight / height : 1;
        float scaleByWidth = requiredWidth > 0 ? (float) requiredWidth / width : 1;
        float scaleFactor = requiredWidth < requiredHeight ? scaleByWidth : scaleByHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//        bitmap.recycle();

        return scaledBitmap;
    }

    public static Bitmap getCroppedBitmap(String filePath, int requiredWidth, int requiredHeight) throws Exception {
        File file = new File(filePath);
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        return getCroppedBitmap(bitmap, requiredWidth, requiredHeight);
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, int requiredWidth, int requiredHeight) throws Exception {
        int width = bitmap.getWidth();
        if (requiredWidth > width) {
            requiredWidth = width;
        }

        int height = bitmap.getHeight();
        if (requiredHeight > height) {
            requiredHeight = height;
        }

        int topOffset = 0;
        if (requiredHeight > 0) {
            topOffset = (height - requiredHeight) / 2;
        }

        int leftOffset = 0;
        if (requiredWidth > 0) {
            leftOffset = (width - requiredWidth) / 2;
        }

        Bitmap croppedBmp = Bitmap.createBitmap(bitmap, leftOffset, topOffset, requiredWidth, requiredHeight);
        return croppedBmp;
    }

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static Bitmap blur(Context ctx, Bitmap image) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(ctx);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    private static byte[] getAndSetScaledDownAndCompressedImageToView(String filePath, ImageView imageView, int requiredWidth, int requiredHeight) throws Exception {
        byte[] content = ImageUtil.getScaledDownAndCompressedBytes(filePath, requiredWidth, requiredHeight);

        Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
        imageView.setImageBitmap(bitmap);

        return content;
    }

    public static Bitmap getDecodedSampleBitmap(String filePath, int requiredWidth, int requiredHeight) throws Exception {
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

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int sampleScaleSize = 1;

        while ((options.outWidth * options.outHeight) / (sampleScaleSize * sampleScaleSize) >= reqWidth * reqHeight) {
            sampleScaleSize *= 2;
        }

        {
            //To prevent image from scaling down too much
            int requiredResolution = reqWidth * reqHeight;
            if (requiredResolution > 500 * 500) {
                if (sampleScaleSize > 1 && (options.outWidth * options.outHeight) / (sampleScaleSize * sampleScaleSize) < 500 * 500) {
                    sampleScaleSize /= 2;
                }
            }
        }

        return sampleScaleSize;
    }

    private static Bitmap rotateImage(Bitmap bitmap, int degree) {
        Bitmap rotatedBitmap = null;
        if (bitmap != null) {
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
        if (cursor != null) {
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

    public static byte[] getBytes(Bitmap bitmap) {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            bytes = byteArrayOutputStream.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (Exception ex) {
            }
        }

        return bytes;
    }

    public static byte[] getCompressedBytes(Bitmap bitmap) {
        return getCompressedBytes(bitmap, 100);
    }

    public static byte[] getCompressedBytes(Bitmap bitmap, int quality) {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

            bytes = byteArrayOutputStream.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (Exception ex) {
            }
        }

        return bytes;
    }

//    public static Bitmap getCompressedBitmap(Bitmap bitmap) {
//        return getCompressedBitmap(bitmap, 85);
//    }
//
//    public static Bitmap getCompressedBitmap(Bitmap bitmap, int quality) {
//        Bitmap compressedBitmap = null;
//        byte[] content = getCompressedBytes(bitmap, quality);
//        if (content != null) {
//            compressedBitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
//        }
//        return compressedBitmap;
//    }

    public static final String getPathforURI(Context context, Uri URI, String metaData) {
        String path = "";
        if (URI.toString().equals("content://co.sports.unity.fileprovider/image/image.jpg")) {
            File outputDir = context.getCacheDir();
            File imagePath = new File(outputDir, "shot");
            File tempFile = new File(imagePath, "image.jpg");
            path = ImageUtil.getFilePathFromURI(context, Uri.fromFile(tempFile), metaData);
            return path;
        }
        if (URI.getScheme().equals("content")) {
            path = ImageUtil.getFilePathFromURI(context, URI, metaData);
        } else if (URI.getScheme().equals("file")) {
            path = URI.getPath();
        }
        return path;
    }

    public static final long getFileSize(Context context, Uri URI, String scheme) {
        String path = null;
        long fileSizeInBytes = 0;
        if (scheme.equals("content")) {
            Cursor returnCursor =
                    context.getContentResolver().query(URI, null, null, null, null);
    /*
     * Get the column indexes of the data in the Cursor,
     * move to the first row in the Cursor, get the data,
     * and display it.
     */
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            fileSizeInBytes = returnCursor.getLong(sizeIndex);
        } else if (scheme.equals("file")) {
            path = URI.getPath();
            File f = new File(path);
            fileSizeInBytes = f.length();
        }
        return fileSizeInBytes;
    }

    public static final String getFilePathFromURI(Context context, Uri contentUri, String meta) {
        try {
            String[] proj = {meta};
            Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndex(meta);
            cursor.moveToFirst();
            Log.i("filepath", cursor.getString(column_index));
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return contentUri.getPath();
        }
    }

    public static final String getMimeType(Context ctx, Uri URI) {
        String mimeType = null;
        if (URI != null) {
            String scheme = URI.getScheme();
            if (scheme.equals("content")) {
               /* if (URI.getPath().contains("image")) {
                    mimeType = "image";
                } else if (URI.getPath().contains("video")) {
                    mimeType = "video";
                } else if (URI.getPath().contains("audio")) {
                    mimeType = "audio";
                }*/
                mimeType = ctx.getContentResolver().getType(URI);
            } else if (scheme.equals("file")) {
                String contentType = URLConnection.guessContentTypeFromName(URI.getPath());
                mimeType = contentType.substring(0, contentType.indexOf("/"));
            }
        }
        return mimeType;
    }

}
