package com.sports.unity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by amandeep on 24/10/15.
 */
public class DBUtil {

    private static final String DIRECTORY_AUDIO = "audio";
    private static final String DIRECTORY_VIDEO = "video";
    private static final String DIRECTORY_IMAGE = "image";

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

    public static void writeContentToExternalFileStorage( Context context, String fileName, byte[] content){
        Log.d("File I/O", "start writing");
        File dirPath = new File(getExternalStorageDirectoryPath(context));
        if( ! dirPath.exists() ){
            dirPath.mkdir();
        }

        File file = new File ( getFilePath( context, fileName));
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

        Log.d("File I/O", "end writing");
    }

    public static byte[] loadContentFromExternalFileStorage( Context context, String fileName){
        Log.d("File I/O", "start reading");
        byte [] content = null;
        File dirPath = new File(getExternalStorageDirectoryPath(context));
        if( ! dirPath.exists() ){
            dirPath.mkdir();
        }

        File file = new File ( getFilePath( context, fileName));
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

    public static String getUniqueFileName(Context context, String mimeType){
        String fileName = String.valueOf(System.currentTimeMillis());
        if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE) ){
            fileName += ".png";
        } else if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO) ){
            fileName += ".mp4";
        } else if( mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO) ){
            fileName += ".mp4";
        }
        return fileName;
    }

    public static String getFilePath(Context context, String fileName){
        String dirPath = getExternalStorageDirectoryPath(context);
        return dirPath + "/" + fileName;
    }

    private static String getExternalStorageDirectoryPath(Context context){
        StringBuilder stringBuilder = new StringBuilder( context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath());
        return stringBuilder.toString();
    }

}
