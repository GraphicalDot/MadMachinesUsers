package com.sports.unity.scores.model.cricket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by madmachines on 8/10/15.
 */
public class Result {


    @SerializedName("tournament")

    @Expose
    private String tournament;
    @SerializedName("venue")
    @Expose
    private String venue;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("runs")
    @Expose
    private String runs;
    @SerializedName("additional")
    @Expose
    private String additional;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("wkts")
    @Expose
    private String wkts;
    @SerializedName("match_desc")
    @Expose
    private String matchDesc;
    @SerializedName("live")
    @Expose
    private String live;
    @SerializedName("blng")
    @Expose
    private String blng;
    @SerializedName("bttng")
    @Expose
    private String bttng;
    @SerializedName("mch_num")
    @Expose
    private String mchNum;
    @SerializedName("overs")
    @Expose
    private String overs;
    @SerializedName("match_day_epoch")
    @Expose
    private Integer matchDayEpoch;


    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The dateTime
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime The date_time
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return The runs
     */
    public String getRuns() {
        return runs;
    }

    /**
     * @param runs The runs
     */
    public void setRuns(String runs) {
        this.runs = runs;
    }

    /**
     * @return The additional
     */
    public String getAdditional() {
        return additional;
    }

    /**
     * @param additional The additional
     */
    public void setAdditional(String additional) {
        this.additional = additional;
    }

    /**
     * @return The startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime The start_time
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return The wkts
     */
    public String getWkts() {
        return wkts;
    }

    /**
     * @param wkts The wkts
     */
    public void setWkts(String wkts) {
        this.wkts = wkts;
    }

    /**
     * @return The matchDesc
     */
    public String getMatchDesc() {
        return matchDesc;
    }

    /**
     * @param matchDesc The match_desc
     */
    public void setMatchDesc(String matchDesc) {
        this.matchDesc = matchDesc;
    }

    /**
     * @return The live
     */
    public String getLive() {
        return live;
    }

    /**
     * @param live The live
     */
    public void setLive(String live) {
        this.live = live;
    }

    /**
     * @return The blng
     */
    public String getBlng() {
        return blng;
    }

    /**
     * @param blng The blng
     */
    public void setBlng(String blng) {
        this.blng = blng;
    }

    /**
     * @return The bttng
     */
    public String getBttng() {
        return bttng;
    }

    /**
     * @param bttng The bttng
     */
    public void setBttng(String bttng) {
        this.bttng = bttng;
    }

    /**
     * @return The mchNum
     */
    public String getMchNum() {
        return mchNum;
    }

    /**
     * @param mchNum The mch_num
     */
    public void setMchNum(String mchNum) {
        this.mchNum = mchNum;
    }

    /**
     * @return The overs
     */
    public String getOvers() {
        return overs;
    }

    /**
     * @param overs The overs
     */
    public void setOvers(String overs) {
        this.overs = overs;
    }

    /**
     * @return The matchDayEpoch
     */
    public Integer getMatchDayEpoch() {
        return matchDayEpoch;
    }

    /**
     * @param matchDayEpoch The match_day_epoch
     */
    public void setMatchDayEpoch(Integer matchDayEpoch) {
        this.matchDayEpoch = matchDayEpoch;
    }

}