package com.sports.unity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sports.unity.news.model.NewsJsonCaller;

import org.json.JSONException;
import org.json.JSONObject;

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

    public  void saveNewsArticles(ArrayList<JSONObject> newsArrayList){
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<>();

        for(JSONObject news : newsArrayList){
            ContentValues contentValues = getContentValuesObject(news);
            if( contentValues != null ) {
                contentValuesArrayList.add(contentValues);
            }
        }

        DBUtil.clearContentFromTable( this, NewsEntry.TABLE_NAME);
        DBUtil.insertContentValuesInTable(this, NewsEntry.TABLE_NAME, contentValuesArrayList);
    }

    public ArrayList<JSONObject> fetchNewsArticles(){
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

        ArrayList<JSONObject> newsArrayList = new ArrayList<>();
        NewsJsonCaller newsJsonCaller = new NewsJsonCaller();
        if ( cursor.moveToFirst() ) {
            do {
                JSONObject news = new JSONObject();
                newsJsonCaller.setJsonObject(news);

                try {
                    newsJsonCaller.setNewsId(cursor.getString(0));
                    newsJsonCaller.setWebsite(cursor.getString(1));
                    newsJsonCaller.setImage_Link(cursor.getString(2));
                    newsJsonCaller.setTitle(cursor.getString(4));
                    newsJsonCaller.setSummary(cursor.getString(5));
                    newsJsonCaller.setNewsLink(cursor.getString(6));
                    newsJsonCaller.setCustomSummary(cursor.getString(7));
                    newsJsonCaller.setPublished(cursor.getString(8));
                    newsJsonCaller.setType(cursor.getString(9));
                    newsJsonCaller.setPublishEpoch(cursor.getLong(10));
                }catch (Exception ex){
                    ex.printStackTrace();
                    news = null;
                }

                if( news != null ) {
                    newsArrayList.add(news);
                } else {
                    //nothing
                }
            } while ( cursor.moveToNext() );
        }

        return newsArrayList;
    }

    private void saveNewsArticle(JSONObject news) throws Exception {
        ContentValues contentValues = getContentValuesObject(news);

        DBUtil.insertContentValuesInTable(this, NewsEntry.TABLE_NAME, contentValues);
    }

    private ContentValues getContentValuesObject(JSONObject news) {
        NewsJsonCaller newsJsonCaller = new NewsJsonCaller();
        newsJsonCaller.setJsonObject(news);

        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(NewsEntry.COLUMN_NEWS_ID, newsJsonCaller.getNewsId());
            contentValues.put(NewsEntry.COLUMN_WEBSITE, newsJsonCaller.getWebsite());
            contentValues.put(NewsEntry.COLUMN_IMAGE_URL, newsJsonCaller.getImage_link());
            contentValues.put(NewsEntry.COLUMN_TITLE, newsJsonCaller.getTitle());
            contentValues.put(NewsEntry.COLUMN_SUMMARY, newsJsonCaller.getSummary());
            contentValues.put(NewsEntry.COLUMN_NEWS_LINK, newsJsonCaller.getNewsLink());
            contentValues.put(NewsEntry.COLUMN_CUSTOM_SUMMARY, newsJsonCaller.getCustomSummary());
            contentValues.put(NewsEntry.COLUMN_PUBLISHED, newsJsonCaller.getPublished());
            contentValues.put(NewsEntry.COLUMN_TYPE, newsJsonCaller.getType());
            contentValues.put(NewsEntry.COLUMN_PUBLISH_EPOCH, newsJsonCaller.getPublishEpoch());
        }catch (Exception ex){
            ex.printStackTrace();
            contentValues = null;
        }
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
