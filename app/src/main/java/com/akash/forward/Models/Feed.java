package com.akash.forward.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by akash on 8/20/2017.
 */

public class Feed {
    private UserInfo userInfo;
    private String imageUrl;
    private String postId;
    private int likesCount = 0;
    private int commentsCount = 0;
    private HashMap<String, Object> timeStamp;

    public Feed() {
        // Default constructor required for calls to DataSnapshot.getValue(Feed.class)
    }

    public Feed(UserInfo userInfo, String imageUrl, String postid) {
        this.userInfo = userInfo;
        this.imageUrl = imageUrl;
        this.postId = postid;
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timeStamp = timestampNow;
    }

    public HashMap<String, Object> getTimeStamp() {
        return timeStamp;
    }
    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timeStamp.get("timestamp");
    }
    public void setTimeStamp(HashMap<String, Object> timeStamp) {
        this.timeStamp = timeStamp;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "userInfo=" + userInfo +
                ", imageUrl='" + imageUrl + '\'' +
                ", postId='" + postId + '\'' +
                ", likesCount=" + likesCount +
                ", commentsCount=" + commentsCount +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
