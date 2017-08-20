package com.akash.forward.Models;

/**
 * Created by akash on 8/20/2017.
 */

public class Feed {
    private UserInfo userInfo;
    private String imageUrl;
    private int likesCount = 0;
    private int commentsCount = 0;

    public Feed(){
        // Default constructor required for calls to DataSnapshot.getValue(Feed.class)
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

    @Override
    public String toString() {
        return "Feed{" +
                "userInfo=" + userInfo +
                ", imageUrl='" + imageUrl + '\'' +
                ", likesCount=" + likesCount +
                ", commentsCount=" + commentsCount +
                '}';
    }
}
