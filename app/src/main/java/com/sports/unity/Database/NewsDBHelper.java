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

    public  void saveNewsArticles(ArrayList<News> newsArrayList){
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<>();

        for(News news : newsArrayList){
            contentValuesArrayList.add( getContentValuesObject(news));
        }

        DBUtil.clearContentFromTable( this, NewsEntry.TABLE_NAME);
        DBUtil.insertContentValuesInTable(this, NewsEntry.TABLE_NAME, contentValuesArrayList);
    }

    public ArrayList<News> fetchNewsArticles(){
        String[] projection = {
            NewsEntry.COLUMN_NEWS_ID,
            NewsEntry.COLUMN_WEBSITE,
            NewsEntry.COLUMN_IMAGE_URL,
            NewsEntry.COLUMN_IMAGE_CONTENT,
            NewsEntry.COLUMN_TITLE,
            NewsEntry.COLUMN_SUMMARY,
            NewsEntry.COLUMN_NEWS_LINK,
            NewsEntry.COLUMN_CUSTOM_SUMMARY,
            NewsEntry.COLUMN_PUBLISHED,
            NewsEntry.COLUMN_TYPE,
            NewsEntry.COLUMN_PUBLISH_EPOCH,
        };

        Cursor cursor = DBUtil.query(this,
                NewsEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        ArrayList<News> newsArrayList = new ArrayList<>();

        if ( cursor.moveToFirst() ) {
            do {
                News news = new News();
                news.setNewsId(cursor.getString(0));
                news.setWebsite(cursor.getString(1));
                news.setHdpi(cursor.getString(2));
                //TODO image content
                news.setTitle(cursor.getString(4));
                news.setSummary(cursor.getString(5));
                news.setNewsLink(cursor.getString(6));
                news.setCustomSummary(cursor.getString(7));
                news.setPublished(cursor.getString(8));
                news.setType(cursor.getString(9));
                news.setPublishEpoch(cursor.getLong(10));

                newsArrayList.add(news);
            } while ( cursor.moveToNext() );
        }

        return newsArrayList;
    }

    private void saveNewsArticle(News news) {
        ContentValues contentValues = getContentValuesObject(news);

        DBUtil.insertContentValuesInTable(this, NewsEntry.TABLE_NAME, contentValues);
    }

    private ContentValues getContentValuesObject(News news){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsEntry.COLUMN_NEWS_ID, news.getNewsId());
        contentValues.put(NewsEntry.COLUMN_WEBSITE, news.getWebsite());
        contentValues.put(NewsEntry.COLUMN_IMAGE_URL, news.getHdpi());
        //TODO image content
        contentValues.put(NewsEntry.COLUMN_TITLE, news.getTitle());
        contentValues.put(NewsEntry.COLUMN_SUMMARY, news.getSummary());
        contentValues.put(NewsEntry.COLUMN_NEWS_LINK, news.getNewsLink());
        contentValues.put(NewsEntry.COLUMN_CUSTOM_SUMMARY, news.getCustomSummary());
        contentValues.put(NewsEntry.COLUMN_PUBLISHED, news.getPublished());
        contentValues.put(NewsEntry.COLUMN_TYPE, news.getType());
        contentValues.put(NewsEntry.COLUMN_PUBLISH_EPOCH, news.getPublishEpoch());
        return contentValues;
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
