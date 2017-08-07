package com.example.jpar4.mitfahren.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jpar4 on 2017-06-02.
 */

public class MainItem {
    int itemNum;
    String userName;
    boolean isUserLike;
    int postLikeCount;
    String postImgUrl="";
    String postText;
    JSONObject likeCheck = new JSONObject();
    String picName;
    //Map<String, Boolean> likeCheck= new HashMap();

    public MainItem(int itemNum, String userName, boolean isUserLike, int postLikeCount, String postImgUrl, String postText, JSONObject likeCheck, String picName) {
        this.itemNum = itemNum;
        this.userName = userName;
        this.isUserLike = isUserLike;
        this.postLikeCount = postLikeCount;
        this.postImgUrl = postImgUrl;
        this.postText = postText;
        this.likeCheck = likeCheck;
        this.picName = picName;
    }

    public  JSONObject getLikeCheck() {
        return likeCheck;
    }

    public void addLikeCheck(String UserID){

        try {
            likeCheck.put(UserID, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public void delLikeCheck(String UserID){
        likeCheck.remove(UserID);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isUserLike() {
        return isUserLike;
    }

    public void setUserLike(boolean userLike) {
        isUserLike = userLike;
    }

    public int getPostLikeCount() {
        return postLikeCount;
    }

    public void setPostLikeCount(int postLikeCount) {
        this.postLikeCount = postLikeCount;
    }

    public String getPostImgUrl() {
        return postImgUrl;
    }

    public void setPostImgUrl(String postImgUrl) {
        this.postImgUrl = postImgUrl;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }
}
