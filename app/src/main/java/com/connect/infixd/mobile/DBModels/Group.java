/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.DBModels;

public class Group {
    private String group_name;
    private String group_photo_url;
    private String noOfMembers;
    private String userPosition;

    public Group(){

    }

    public Group(String group_name, String group_photo_url){
        this.group_name = group_name;
        this.group_photo_url = group_photo_url;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_photo_url() {
        return group_photo_url;
    }

    public void setGroup_photo_url(String group_photo_url) {
        this.group_photo_url = group_photo_url;
    }

    public String getNoOfMembers() {
        return noOfMembers;
    }

    public void setNoOfMembers(String noOfMembers) {
        this.noOfMembers = noOfMembers;
    }

    public String getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(String userPosition) {
        this.userPosition = userPosition;
    }
}
