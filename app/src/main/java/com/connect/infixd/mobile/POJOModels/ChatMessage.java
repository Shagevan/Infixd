/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.POJOModels;

import android.net.Uri;

public class ChatMessage {

    private String text;
    private String name;
    private String userId;
    private String imageUrl;
    private Uri thumbnailURI;
    private Long time;

    public ChatMessage() {
    }

    public ChatMessage(String text, String name,String userId,  String imageUrl, Long time) {
        this.text = text;
        this.name = name;
        this.imageUrl = imageUrl;
        this.time = time;
        this.userId = userId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setText(String text){
        this.text=text;
    }

    public String getText(){
        return text;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Uri getThumbnailURI() {
        return thumbnailURI;
    }

    public void setThumbnailURI(Uri thumbnailURI) {
        this.thumbnailURI = thumbnailURI;
    }
}
