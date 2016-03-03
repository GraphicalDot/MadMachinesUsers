package com.sports.unity.Database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by amandeep on 24/10/15.
 */
public class DBUtil {

    private static final String DIRECTORY_AUDIO = "audio";
    private static final String DIRECTORY_VIDEO = "video";
    private static final String DIRECTORY_IMAGE = "image";

    private static final String EXTERNAL_FILE_NAME_PREFIX = "SPU-";
    private static final String INTERNAL_FILE_NAME_PREFIX = "IN-SPU-";

    private static final String IMAGE_DIRECTORY = "SportsUnity/SportsUnity_Images";
    private static final String VIDEO_DIRECTORY = "SportsUnity/SportsUnity_Videos";
    private static final String AUDIO_DIRECTORY = "SportsUnity/SportsUnity_Audio";

    static long insertContentValuesInTable( SQLiteOpenHelper sqLiteOpenHelper, String tableName, ContentValues contentValues){
        long rowId = -1;

        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        rowId = db.insert(SportsUnityContract.NewsEntry.TABLE_NAME, null, contentValues);
        db.close();

        return rowId;
    }

    static void insertContentValuesInTable( SQLiteOpenHelper sqLiteOpenHelper, String tableName, ArrayList<ContentValues> contentValuesArrayList){
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();

        for(ContentValues contentValues : contentValuesArrayList) {
            db.insert(SportsUnityContract.NewsEntry.TABLE_NAME, null, contentValues);
        }

        db.close();
    }

    static Cursor query(SQLiteOpenHelper sqLiteOpenHelper, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return cursor;
    }

    static void clearContentFromTable(SQLiteOpenHelper sqLiteOpenHelper, String table){
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        db.delete(table, null, null);
    }

    public static void writeContentToInternalFileStorage( Context context, String fileName, byte[] content, boolean append){
        Log.d("File I/O", "start writing");
        if( content != null ){
            FileOutputStream fos = null;
            try{
                int mode = Context.MODE_PRIVATE;
                if( append ){
                    mode = Context.MODE_APPEND;
                }
                context.getExternalFilesDir(null);
                fos = context.openFileOutput( fileName, mode);
                fos.write( content);
                fos.close();
            }catch (Exception e) {
                e.printStackTrace();
            }finally{
                try{
                    fos.close();
                }catch (Exception e) {}
            }
        }
        Log.d("File I/O", "end writing");
    }

    public static byte[] loadContentFromInternalFileStorage( Context context, String fileName){
        Log.d("File I/O", "start reading");
        byte [] content = null;
        InputStream is = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try{
            is = context.openFileInput( fileName);
            byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] chunk = new byte[1024];
            int read = 0;
            while( (read = is.read(chunk) ) != -1 ){
                byteArrayOutputStream.write( chunk, 0, read);
            }

            content = byteArrayOutputStream.toByteArray();
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try{
                byteArrayOutputStream.close();
            }catch (Exception e) {}
            try{
                is.close();
            }catch (Exception e) {}
        }
        Log.d("File I/O", "end reading");
        return content;
    }

    public static void writeContentToExternalFileStorage( Context context, String fileName, byte[] content, String mimeType){
        Log.d("File I/O", "start writing");
        boolean isVisibleInGallery = isExternalFile(fileName);

        File file = new File ( getFilePath( context, mimeType, fileName));
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(content);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            }catch (Exception ex){}
        }

        if( isVisibleInGallery ) {
            addMediaContentToResolver(context, file.getAbsolutePath(), fileName, mimeType);
        }

        Log.d("File I/O", "end writing");
    }

    public static void writeContentToExternalFileStorage( Context context, String sourceFileName, String destinationFileName, String mimeType){
        Log.d("File I/O", "start writing");
        boolean isVisibleInGallery = isExternalFile(destinationFileName);

        File file = new File ( getFilePath(context, mimeType, destinationFileName));
        FileOutputStream out = null;
        FileInputStream fileInputStream = null;
        try {
            out = new FileOutputStream(file);
            fileInputStream = new FileInputStream(sourceFileName);

            byte[] chunk = new byte[1024];
            int read = 0;
            while( (read = fileInputStream.read(chunk)) != -1 ) {
                out.write(chunk, 0, read);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            }catch (Exception ex){}
        }

        if( isVisibleInGallery ) {
            addMediaContentToResolver(context, file.getAbsolutePath(), destinationFileName, mimeType);
        }

        Log.d("File I/O", "end writing");
    }

    public static byte[] loadContentFromExternalFileStorage( Context context, String mimeType, String fileName){
        Log.d("File I/O", "start reading");
        byte [] content = null;
        File file = new File ( getFilePath( context, mimeType, fileName));
        FileInputStream in = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            in = new FileInputStream(file);
            byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] chunk = new byte[1024];
            int read = 0;
            while( (read = in.read(chunk) ) != -1 ){
                byteArrayOutputStream.write( chunk, 0, read);
            }

            content = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                byteArrayOutputStream.close();
            }catch (Exception e) {}
            try {
                in.close();
            }catch (Exception ex){}
        }
        Log.d("File I/O", "end reading");
        return content;
    }

    public static void deleteContentFromExternalFileStorage(Context context, HashMap<String, ArrayList<String>> mapOnType){
        Iterator<String> iterator = mapOnType.keySet().iterator();
        while( iterator.hasNext() ) {
            String mimeType = iterator.next();
            ArrayList<String> fileNames = mapOnType.get(mimeType);
            for (int index = 0; index < fileNames.size(); index++) {
                deleteContentFromExternalFileStorage(context, mimeType, fileNames.get(index));
            }
        }
    }

    public static boolean deleteContentFromExternalFileStorage(Context context, String mimeType, String fileName){
        boolean deleted = false;
        if( fileName != null ) {
            boolean isVisibleInGallery = isExternalFile(fileName);

            if( ! isVisibleInGallery ) {
                File file = new File(getFilePath(context, mimeType, fileName));
                deleted = file.delete();
            } else {
                //nothing
            }
        } else {
            deleted = false;
        }
        return deleted;
    }

    public static boolean isFileExist(Context context, String mimeType, String fileName){
        boolean exist = false;
        if( fileName != null ) {
            File file = new File(getFilePath(context, mimeType, fileName));
            exist = file.exists();
        } else {
            //nothing
        }
        return  exist;
    }

    public static String getUniqueFileName(String mimeType, boolean isVisibleInPhoneGallery){
        String filePrefix = INTERNAL_FILE_NAME_PREFIX;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if( isVisibleInPhoneGallery ) {
                filePrefix = EXTERNAL_FILE_NAME_PREFIX;
            } else {
                //nothing
            }
        } else {
            filePrefix = EXTERNAL_FILE_NAME_PREFIX;
        }


        String fileName = filePrefix + String.valueOf(System.currentTimeMillis());
        if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE) ){
            fileName += ".png";
        } else if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO) ){
            fileName += ".mp3";
        } else if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO) ){
            fileName += ".mp4";
        }
        return fileName;
    }

    public static String getFilePath(Context context, String mimeType, String fileName){
        String dirPath = getExternalStorageDirectoryPath(context, mimeType, isExternalFile(fileName));
        return dirPath + "/" + fileName;
    }

    private static void addMediaContentToResolver(Context context, String filePath, String fileName, String mimeType){
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, filePath);
        values.put(MediaStore.MediaColumns.TITLE, fileName);
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());

        if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE) ) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, fileName.toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, fileName.toLowerCase(Locale.US));

            ContentResolver cr = context.getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO) ) {
            values.put(MediaStore.Video.Media.ALBUM, "Sports Unity");
            values.put(MediaStore.Video.Media.ARTIST, "Sports Unity");

            values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Video.VideoColumns.BUCKET_ID, fileName.toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME, fileName.toLowerCase(Locale.US));
            values.put(MediaStore.Video.Media.CONTENT_TYPE, fileName.toLowerCase(Locale.US));
            values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");

            ContentResolver cr = context.getContentResolver();
            cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        } else if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO) ) {
            values.put(MediaStore.Audio.Media.ALBUM, "Sports Unity");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);

            ContentResolver cr = context.getContentResolver();
            cr.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
        }

//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
    }

    private static boolean isExternalFile(String fileName){
        return fileName.startsWith(EXTERNAL_FILE_NAME_PREFIX);
    }

    private static String getExternalStorageDirectoryPath(Context context, String mimeType, boolean useGalleryPath){
        String path = null;
        String directory = IMAGE_DIRECTORY;
        if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE) ){
            directory = IMAGE_DIRECTORY;
        } else if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO) ){
            directory = VIDEO_DIRECTORY;
        } else if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO) ){
            directory = AUDIO_DIRECTORY;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if( useGalleryPath ) {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directory;
            } else {
                path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            }
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directory;
        }

        File file = new File(path);
        if( ! file.exists() ){
            file.mkdirs();
        }

        return path;
    }

}
