/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.DBModels;

public class Post {
    private int _id;
    private String type;
    private String body;
    private String nameOfCreator;
    private String idOfCreator;
    private String sharedUserName;
    private String sharedUserId;
    private String caption;
    private String imageUri;
    private String textBackgroundColor;
    private String profilePicUrl;
    private String Status;
    private String creatorProfilePic;
    private String postId;
    private String time;
    private String date;
    private String noOfLikes;
    private String noOfShares;

    public Post (){

    }

    public Post(String type, String body, String nameOfCreator, String idOfCreator, String sharedUserName,
                String sharedUserId, String caption, String imageUri, String textBackgroundColor,
                String profilePicUrl, String creatorProfilePic, String postId, String time,
                String date, String noOfLikes, String noOfShares) {
        this.type = type;
        this.body = body;
        this.nameOfCreator = nameOfCreator;
        this.idOfCreator = idOfCreator;
        this.sharedUserName = sharedUserName;
        this.sharedUserId = sharedUserId;
        this.caption = caption;
        this.imageUri = imageUri;
        this.textBackgroundColor = textBackgroundColor;
        this.profilePicUrl = profilePicUrl;
        this.creatorProfilePic = creatorProfilePic;
        this.postId = postId;
        this.time = time;
        this.date = date;
        this.noOfLikes = noOfLikes;
        this.noOfShares = noOfShares;
    }

    public Post (String type, String nameOfCreator, String idOfCreator, String caption, String imageUri,
                 String body, String textBackgroundColor, String profilePicUrl, String postId, String creatorProfilePic,
                 String time, String date, String noOfLikes, String noOfShares){
        this.type = type;
        this.body = body;
        this.nameOfCreator = nameOfCreator;
        this.idOfCreator = idOfCreator;
        this.caption = caption;
        this.imageUri = imageUri;
        this.textBackgroundColor = textBackgroundColor;
        this.profilePicUrl = profilePicUrl;
        this.postId = postId;
        this.creatorProfilePic = creatorProfilePic;
        this.time = time;
        this.date = date;
        this.noOfLikes = noOfLikes;
        this.noOfShares = noOfShares;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNameOfCreator() {
        return nameOfCreator;
    }

    public void setNameOfCreator(String nameOfCreator) {
        this.nameOfCreator = nameOfCreator;
    }

    public String getIdOfCreator() {
        return idOfCreator;
    }

    public void setIdOfCreator(String idOfCreator) {
        this.idOfCreator = idOfCreator;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getTextBackgroundColor() {
        return textBackgroundColor;
    }

    public void setTextBackgroundColor(String textBackgroundColor) {
        this.textBackgroundColor = textBackgroundColor;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getCreatorProfilePic() {
        return creatorProfilePic;
    }

    public void setCreatorProfilePic(String creatorProfilePic) {
        this.creatorProfilePic = creatorProfilePic;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(String noOfLikes) {
        this.noOfLikes = noOfLikes;
    }

    public String getNoOfShares() {
        return noOfShares;
    }

    public void setNoOfShares(String noOfShares) {
        this.noOfShares = noOfShares;
    }

    public String getSharedUserName() {
        return sharedUserName;
    }

    public void setSharedUserName(String sharedUserName) {
        this.sharedUserName = sharedUserName;
    }

    public String getSharedUserId() {
        return sharedUserId;
    }

    public void setSharedUserId(String sharedUserId) {
        this.sharedUserId = sharedUserId;
    }
}
