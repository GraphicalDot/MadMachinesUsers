package com.sports.unity.scores.controller.fragment;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by madmachines on 8/3/16.
 */
public class MatchListWrapperDTO implements Comparable<MatchListWrapperDTO> {
    private String day;
    private String leagueName;
    private ArrayList<JSONObject> list;
    private Long epochTime;
    private String sportsType;
    private String status;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<JSONObject> getList() {
        return list;
    }

    public String getStatus() {
        return status;
    }

    public void setList(ArrayList<JSONObject> list) {
        this.list = list;
    }

    public Long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(Long epochTime) {
        this.epochTime = epochTime;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getSportsType() {
        return sportsType;
    }

    public void setSportsType(String sportsType) {
        this.sportsType = sportsType;
    }

    @Override
    public int compareTo(MatchListWrapperDTO another) {
        return this.epochTime.compareTo(another.epochTime);
    }

    public void reorderList() {
        try {
            ArrayList<JSONObject> completedMatchesList = new ArrayList<>();

            for (int index = 0; index < list.size(); index++) {
                JSONObject jsonObject = list.get(0);
                String status = null;
                if (!jsonObject.isNull("status")) {
                    status = jsonObject.getString("status");
                } else {
                    status = jsonObject.getString("match_status");
                }

                if ("f".equalsIgnoreCase(status) || "ft".equalsIgnoreCase(status) || "aet".equalsIgnoreCase(status)) {
                    list.remove(0);
                    completedMatchesList.add(jsonObject);
                } else {
                    break;
                }
            }

            list.addAll(completedMatchesList);

            if (list.size() > 0) {
                JSONObject jsonObject = list.get(0);
                if (!jsonObject.isNull("match_date_epoch")) {
                    epochTime = jsonObject.getLong("match_date_epoch");
                } else {
                    epochTime = jsonObject.getLong("match_time");
                }

                if (!jsonObject.isNull("status")) {
                    status = jsonObject.getString("status");
                } else {
                    status = jsonObject.getString("match_status");
                }
            } else {
                //nothing
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

//    public static int count = 0;
//    private static long currentTime = 1461322170;
//
//    public void createDummyContent(){
//        try {
//            if (list.size() > 0) {
//                JSONObject jsonObject = list.get(0);
//                list.clear();
//
//                count++;
//                if( count == 1 ){
//                    epochTime = currentTime - (2*60*60);
//
//                    {
//                        JSONObject copy = copy(jsonObject);
//                        copy.put("status", "F");
//                        copy.put("match_status", "FT");
//                        if( ! jsonObject.isNull("match_date_epoch") ) {
//                            copy.put("match_date_epoch", epochTime);
//                        } else {
//                            copy.put("match_time", epochTime);
//                        }
//                        list.add(copy);
//                    }
//                    {
//                        JSONObject copy = copy(jsonObject);
//                        copy.put("status", "F");
//                        copy.put("match_status", "FT");
//                        copy.put("live", false);
//                        copy.put("away_team_score", "?");
//                        if( ! jsonObject.isNull("match_date_epoch") ) {
//                            copy.put("match_date_epoch", epochTime);
//                        } else {
//                            copy.put("match_time", epochTime);
//                        }
//                        list.add(copy);
//                    }
//                    {
//                        JSONObject copy = copy(jsonObject);
//                        copy.put("status", "N");
//                        copy.put("match_status", "25:30");
//                        copy.put("live", false);
//                        copy.put("away_team_score", "?");
//                        if( ! jsonObject.isNull("match_date_epoch") ) {
//                            copy.put("match_date_epoch", epochTime + (4 * 60 * 60));
//                        } else {
//                            copy.put("match_time", epochTime + (4 * 60 * 60));
//                        }
//                        list.add(copy);
//                    }
//                } else if( count == 2 ){
//                    epochTime = currentTime - (2*60*60);
//
//                    {
//                        JSONObject copy = copy(jsonObject);
//                        copy.put("status", "N");
//                        copy.put("match_status", "24:30");
//                        copy.put("live", false);
//                        copy.put("away_team_score", "?");
//                        if( ! jsonObject.isNull("match_date_epoch") ) {
//                            copy.put("match_date_epoch", epochTime + (4 * 60 * 60));
//                        } else {
//                            copy.put("match_time", epochTime + (4 * 60 * 60));
//                        }
//                        list.add(copy);
//                    }
//                    {
//                        JSONObject copy = copy(jsonObject);
//                        copy.put("status", "N");
//                        copy.put("match_status", "28:30");
//                        copy.put("live", false);
//                        copy.put("away_team_score", "?");
//                        if( ! jsonObject.isNull("match_date_epoch") ) {
//                            copy.put("match_date_epoch", epochTime + (7 * 60 * 60));
//                        } else {
//                            copy.put("match_time", epochTime + (7 * 60 * 60));
//                        }
//                        list.add(copy);
//                    }
//                } else if( count == 3 ){
//                    epochTime = currentTime - (2*60*60);
//
//                    {
//                        JSONObject copy = copy(jsonObject);
//                        copy.put("status", "N");
//                        copy.put("match_status", "24:30");
//                        copy.put("live", false);
//                        copy.put("away_team_score", "?");
//                        if( ! jsonObject.isNull("match_date_epoch") ) {
//                            copy.put("match_date_epoch", epochTime + (3 * 60 * 60));
//                        } else {
//                            copy.put("match_time", epochTime + (3 * 60 * 60));
//                        }
//                        list.add(copy);
//                    }
//                }
//
//
//
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//    }
//
//    private JSONObject copy(JSONObject jsonObject) throws Exception {
//        JSONObject copy = new JSONObject();
//        Iterator<String> iterator = jsonObject.keys();
//        while( iterator.hasNext() ){
//            String key = iterator.next();
//            copy.put( key, jsonObject.get(key));
//        }
//        return copy;
//    }

    @Override
    public String toString() {
        return "MatchListWrapperDTO{" +
                "day='" + day + '\'' +
                ", leagueName='" + leagueName + '\'' +
                ", list=" + list +
                ", epochTime=" + epochTime +
                ", sportsType='" + sportsType + '\'' +
                '}';
    }
}
