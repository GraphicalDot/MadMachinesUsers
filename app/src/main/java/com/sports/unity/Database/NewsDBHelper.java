package com.sports.unity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.sports.unity.news.model.News;

import java.util.ArrayList;

import static com.sports.unity.Database.SportsUnityContract.NewsEntry;
/**
 * Created by madmachines on 23/10/15.
 */
public class NewsDBHelper extends SQLiteOpenHelper {



    private static final String CREATE_NEWS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            NewsEntry.TABLE_NAME + "( " +
            NewsEntry.COLUMN_NEWS_ID + " VARCHAR UNIQUE " + DBConstants.COMMA_SEP +
            NewsEntry.COLUMN_WEBSITE + " VARCHAR " + DBConstants.COMMA_SEP +
            NewsEntry.COLUMN_IMAGE_URL + " VARCHAR " + DBConstants.COMMA_SEP +
            NewsEntry.COLUMN_IMAGE_CONTENT + " BLOB " + DBConstants.COMMA_SEP +
            NewsEntry.COLUMN_TITLE + " VARCHAR " + DBConstants.COMMA_SEP +
            NewsEntry.COLUMN_SUMMARY + " VARCHAR " + DBConstants.COMMA_SEP +
            NewsEntry.COLUMN_NEWS_LINK + " VARCHAR " + DBConstants.COMMA_SEP +
            NewsEntry.COLUMN_CUSTOM_SUMMARY + " VARCHAR " + DBConstants.COMMA_SEP +
            NewsEntry.COLUMN_PUBLISHED + " VARCHAR " + DBConstants.COMMA_SEP +
            NewsEntry.COLUMN_TYPE + " VARCHAR " + DBConstants.COMMA_SEP +
            NewsEntry.COLUMN_PUBLISH_EPOCH + " INTEGER );";

    private static final String DROP_NEWS_TABLE = "DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME;


    private static NewsDBHelper NEWS_DB_HELPER = null;

    synchronized public static NewsDBHelper getInstance(Context context){
        if( NEWS_DB_HELPER == null ){
            NEWS_DB_HELPER = new NewsDBHelper(context);
        }
        return NEWS_DB_HELPER;
    }

    private NewsDBHelper(Context context) {
        super(context, DBConstants.NEWS_DATABASE_NAME, null, DBConstants.NEWS_DATABASE_VERSION);
    }

    public void saveNewsArticles(ArrayList<News> newsArrayList){
        //TODO
    }

    public ArrayList<News> fetchNewsArticles(){
        //TODO
        return null;
    }

    private void saveNewsArticle(String id, String website, String image_url, String image_content, String title,String summary,
                              String news_link, String custom_summary, String published, String type,String published_epoc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsEntry.COLUMN_NEWS_ID, id);
        contentValues.put(NewsEntry.COLUMN_WEBSITE, website);
        contentValues.put(NewsEntry.COLUMN_IMAGE_URL, image_url);
        contentValues.put(NewsEntry.COLUMN_IMAGE_CONTENT, image_content);
        contentValues.put(NewsEntry.COLUMN_TITLE, title);
        contentValues.put(NewsEntry.COLUMN_SUMMARY, summary);
        contentValues.put(NewsEntry.COLUMN_NEWS_LINK, news_link);
        contentValues.put(NewsEntry.COLUMN_CUSTOM_SUMMARY, custom_summary);
        contentValues.put(NewsEntry.COLUMN_PUBLISHED, published);
        contentValues.put(NewsEntry.COLUMN_TYPE, type);
        contentValues.put(NewsEntry.COLUMN_PUBLISH_EPOCH, published_epoc);

        db.insert(NewsEntry.TABLE_NAME, null, contentValues);
        db.close();
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_NEWS_TABLE);
        onCreate(db);
    }

}
