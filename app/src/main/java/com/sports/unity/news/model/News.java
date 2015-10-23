package com.sports.unity.news.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by madmachines on 13/10/15.
 */
public class News implements Comparable<News>{

    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("hdpi")
    @Expose
    private String hdpi;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("news_id")
    @Expose
    private String newsId;
    @SerializedName("news_link")
    @Expose
    private String newsLink;
    @SerializedName("custom_summary")
    @Expose
    private String customSummary;
    @SerializedName("published")
    @Expose
    private String published;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("publish_epoch")
    @Expose
    private Long publishEpoch;

    /**
     * @return The website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @param website The website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * @return The hdpi
     */
    public String getHdpi() {
        return hdpi;
    }

    /**
     * @param hdpi The hdpi
     */
    public void setHdpi(String hdpi) {
        this.hdpi = hdpi;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary The summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return The newsId
     */
    public String getNewsId() {
        return newsId;
    }

    /**
     * @param newsId The news_id
     */
    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    /**
     * @return The newsLink
     */
    public String getNewsLink() {
        return newsLink;
    }

    /**
     * @param newsLink The news_link
     */
    public void setNewsLink(String newsLink) {
        this.newsLink = newsLink;
    }

    /**
     * @return The customSummary
     */
    public String getCustomSummary() {
        return customSummary;
    }

    /**
     * @param customSummary The custom_summary
     */
    public void setCustomSummary(String customSummary) {
        this.customSummary = customSummary;
    }

    /**
     * @return The published
     */
    public String getPublished() {
        return published;
    }

    /**
     * @param published The published
     */
    public void setPublished(String published) {
        this.published = published;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The publishEpoch
     */
    public Long getPublishEpoch() {
        return publishEpoch;
    }

    /**
     * @param publishEpoch The publish_epoch
     */
    public void setPublishEpoch(Long publishEpoch) {
        this.publishEpoch = publishEpoch;
    }

    @Override
    public int compareTo(News news) {
        /* For Ascending order*/
        return (int)(news.publishEpoch-this.publishEpoch);

        /* For Descending order do like this */
        //return compareage-this.studentage;
    }
}
