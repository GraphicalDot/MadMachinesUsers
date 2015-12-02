package com.sports.unity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by amandeep on 24/10/15.
 */
public class DBUtil {

    private static final String mediaDirectoryName = "SportsUnity_Media";

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

    public static void writeContentToFile( Context context, String fileName, byte[] content, boolean append){
        if( content != null ){
            FileOutputStream fos = null;
            try{
                int mode = Context.MODE_PRIVATE;
                if( append ){
                    mode = Context.MODE_APPEND;
                }
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
    }

    public static byte[] loadContentFromFile( Context context, String fileName){
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
        return content;
    }

    private static File createMediaDirectoryIfNotExist(Context context){
        return context.getDir( mediaDirectoryName, Context.MODE_PRIVATE);
    }

}
