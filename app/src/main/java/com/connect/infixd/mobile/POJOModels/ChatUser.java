package com.connect.infixd.mobile.POJOModels;


import java.io.Serializable;

public class ChatUser implements Serializable{

    private String userId;
    private String userFirstName;
    private String userProfPicURL;

    public ChatUser(String userId, String userFirstName, String userProfPicURL) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userProfPicURL = userProfPicURL;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserProfPicURL() {
        return userProfPicURL;
    }

    public void setUserProfPicURL(String userProfPicURL) {
        this.userProfPicURL = userProfPicURL;
    }
}
