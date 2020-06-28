/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Wrappers;

import com.connect.infixd.mobile.DBModels.AddGroupNotification;

import java.io.Serializable;
import java.util.ArrayList;

public class AddGroupNotificationWrapper implements Serializable{

    private ArrayList<AddGroupNotification> notificationData = new ArrayList<>();

    public ArrayList<AddGroupNotification> getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(ArrayList<AddGroupNotification> notificationData) {
        this.notificationData = notificationData;
    }
}
