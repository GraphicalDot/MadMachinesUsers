package com.sports.unity.news.model;

import com.sports.unity.util.JsonObjectCaller;

import org.json.JSONException;

public class NewsJsonCaller extends JsonObjectCaller{

    public String getWebsite() throws JSONException {
        return jsonObject.getString("website");
    }

    public void setWebsite(String website) throws JSONException {
        jsonObject.put( "website", website);
    }

    public String getImage_link() throws JSONException {
        return jsonObject.getString("image_link");
    }

    public void setImage_Link(String imageLink) throws JSONException {
        jsonObject.put( "image_link", imageLink);
    }

    public String getTitle() throws JSONException {
        return jsonObject.getString("title");
    }

    public void setTitle(String title) throws JSONException {
        jsonObject.put( "title", title);
    }

    public String getSummary() throws JSONException {
        return jsonObject.getString("summary");
    }

    public void setSummary(String summary) throws JSONException {
        jsonObject.put( "summary", summary);
    }

    public String getNewsId() throws JSONException {
        return jsonObject.getString("news_id");
    }

    public void setNewsId(String newsId) throws JSONException {
        jsonObject.put( "news_id", newsId);
    }

    public String getNewsLink() throws JSONException {
        return jsonObject.getString("news_link");
    }

    public void setNewsLink(String newsLink) throws JSONException {
        jsonObject.put( "news_link", newsLink);
    }

    public String getCustomSummary() throws JSONException {
        return jsonObject.getString("custom_summary");
    }

    public void setCustomSummary(String customSummary) throws JSONException {
        jsonObject.put( "custom_summary", customSummary);
    }

    public String getPublished() throws JSONException {
        return jsonObject.getString("published");
    }

    public void setPublished(String published) throws JSONException {
        jsonObject.put( "published", published);
    }

    public String getType() throws JSONException {
        String type=null;
        if(!jsonObject.isNull("type")){
            type=jsonObject.getString("type");
        }else if(!jsonObject.isNull("sport_type")){
            type=jsonObject.getString("sport_type");
        }
        return type;
    }

    public void setType(String type) throws JSONException {
        jsonObject.put( "type", type);
    }

    public Long getPublishEpoch() throws JSONException {
        return jsonObject.getLong("publish_epoch");
    }

    public void setPublishEpoch(Long publishEpoch) throws JSONException {
        jsonObject.put( "publish_epoch", publishEpoch);
    }

    public String getFabIcon_link() throws JSONException {
        return jsonObject.getString("favicon");
    }

    public void setFabIcon_Link(String imageLink) throws JSONException {
        jsonObject.put( "favicon", imageLink);
    }

    public String getNews() throws JSONException {
        return jsonObject.getString( "news");
    }

//    @Override
//    public int compareTo(NewsJsonCaller news) {
//        /* For Ascending order*/
//        return (int)(news.publishEpoch-this.publishEpoch);
//
//        /* For Descending order do like this */
//        //return compareage-this.studentage;
//    }

}
