package com.sports.unity.scoredetails.cricketdetail;

import android.support.annotation.NonNull;

import com.sports.unity.scoredetails.cricketdetail.JsonParsers.CricketMatchScoreJsonParser;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBattingCardDTO;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBowlingCardDTO;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketFallOfWicketCardDTO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madmachines on 21/3/16.
 */
public class CricketMatchScoreCardUtil {

    @NonNull
    public static LiveAndCompletedCricketFallOfWicketCardDTO getLiveAndCompletedCricketFallOfWicketCardDTO(CricketMatchScoreJsonParser cricketMatchScoreJsonParser, int k, JSONObject fallOfWicketObject) throws JSONException {
        cricketMatchScoreJsonParser.setFallofWicketObject(fallOfWicketObject);
        LiveAndCompletedCricketFallOfWicketCardDTO fallOfWickets = new LiveAndCompletedCricketFallOfWicketCardDTO();
        fallOfWickets.setTvBowlerName(cricketMatchScoreJsonParser.getFallOfName());
        fallOfWickets.setTvOverNumber(cricketMatchScoreJsonParser.getFallOfWicketOver() + "ovs");
        fallOfWickets.setTvWicket(cricketMatchScoreJsonParser.getFallOfWicketScore());
        return fallOfWickets;
    }

    @NonNull
    public static LiveAndCompletedCricketBowlingCardDTO getLiveAndCompletedCricketBowlingCardDTO(CricketMatchScoreJsonParser cricketMatchScoreJsonParser, JSONObject bowlingObject) throws JSONException {
        cricketMatchScoreJsonParser.setBowllingObject(bowlingObject);
        LiveAndCompletedCricketBowlingCardDTO bowling = new LiveAndCompletedCricketBowlingCardDTO();
        bowling.setTvRuns(cricketMatchScoreJsonParser.getBowlerRuns());
        bowling.setTvBowlerName(cricketMatchScoreJsonParser.getBowlerName());
        bowling.setPlayerId(cricketMatchScoreJsonParser.getBowlerId());
        bowling.setTvExtra(cricketMatchScoreJsonParser.getBowlerExtra());
        bowling.setTvMiddenOver(cricketMatchScoreJsonParser.getBowlerMaidenOvers());
        bowling.setTvWicket(cricketMatchScoreJsonParser.getBowlerWicket());
        bowling.setTvOver(cricketMatchScoreJsonParser.getBowlerOvers());
        return bowling;
    }

    @NonNull
    public static LiveAndCompletedCricketBattingCardDTO getLiveAndCompletedCricketBattingCardDTO(CricketMatchScoreJsonParser cricketMatchScoreJsonParser, JSONObject battingObject) throws JSONException {
        cricketMatchScoreJsonParser.setBattingObject(battingObject);
        LiveAndCompletedCricketBattingCardDTO liveAndCompletedCricketBattingCardDTO = new LiveAndCompletedCricketBattingCardDTO();
        liveAndCompletedCricketBattingCardDTO.setPlayerId(cricketMatchScoreJsonParser.getBatsManId());
        liveAndCompletedCricketBattingCardDTO.setTvPlayerName(cricketMatchScoreJsonParser.getBatsManName());
        liveAndCompletedCricketBattingCardDTO.setTvBallPlayByPlayer(cricketMatchScoreJsonParser.getBall());
        liveAndCompletedCricketBattingCardDTO.setTvSrRateOfPlayer(cricketMatchScoreJsonParser.getBatsManStrikeRate());
        liveAndCompletedCricketBattingCardDTO.setTvFourGainByPlayer(cricketMatchScoreJsonParser.getBatsManFours());
        liveAndCompletedCricketBattingCardDTO.setTvSixGainByPlayer(cricketMatchScoreJsonParser.getBatsManSix());
        liveAndCompletedCricketBattingCardDTO.setTvPlayerRun(cricketMatchScoreJsonParser.getBatsManRun());
        liveAndCompletedCricketBattingCardDTO.setTvWicketBy(cricketMatchScoreJsonParser.getBatsmanStatus());
        return liveAndCompletedCricketBattingCardDTO;
    }









}
