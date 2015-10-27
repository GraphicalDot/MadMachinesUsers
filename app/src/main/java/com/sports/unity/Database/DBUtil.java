package com.sports.unity.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by amandeep on 24/10/15.
 */
public class DBUtil {

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
        Cursor cursor = db.query( table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return cursor;
    }

    static void clearContentFromTable(SQLiteOpenHelper sqLiteOpenHelper, String table){
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        db.delete( table, null, null);
    }

}
