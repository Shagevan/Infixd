package com.connect.infixd.mobile.POJOModels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetFBPhotosResponse implements Serializable {

    @SerializedName("photos")
    @Expose
    private Photos photos;
    @SerializedName("id")
    @Expose
    private String id;

    public Photos getPhotos() {
        return photos;
    }

    public void setPhotos(Photos photos) {
        this.photos = photos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}