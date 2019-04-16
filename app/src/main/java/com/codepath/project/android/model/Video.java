package com.codepath.project.android.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anmallya on 12/4/2016.
 */

public class Video {

    private String thumbnail;
    private String url;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static ArrayList<Video> geVideoArray(JSONArray videoJson){
        int length = videoJson.length();
        ArrayList<Video> videoList = new ArrayList<>();
        for(int i = 0; i  < length; i++){
            Video v = new Video();
            try{
                JSONObject j = videoJson.getJSONObject(i);
                v.setThumbnail(j.getString("thumbnail"));
                v.setUrl(j.getString("url"));
                videoList.add(v);
            } catch(org.json.JSONException e){
                System.out.println("JSON parsing exception: "+e);
            }
        }
        return videoList;
    }
}
