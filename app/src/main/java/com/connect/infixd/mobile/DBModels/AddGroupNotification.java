/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.DBModels;

import java.io.Serializable;

public class AddGroupNotification implements Serializable{
    private String _id;
    private String type;
    private String groupName;
    private String senderId;
    private String senderName;
    private String senderProfPicURL;
    private String receiverId;
    private String receiverName;
    private String receiverProfPicURL;
    private String body;
    private String Status;

    public AddGroupNotification() {
    }

    public AddGroupNotification(String type, String groupName, String senderId, String senderName, String senderProfPicURL,
                                String receiverId, String receiverName, String receiverProfPicURL, String body) {
        this.type = type;
        this.groupName = groupName;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderProfPicURL = senderProfPicURL;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverProfPicURL = receiverProfPicURL;
        this.body = body;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderProfPicURL() {
        return senderProfPicURL;
    }

    public void setSenderProfPicURL(String senderProfPicURL) {
        this.senderProfPicURL = senderProfPicURL;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverProfPicURL() {
        return receiverProfPicURL;
    }

    public void setReceiverProfPicURL(String receiverProfPicURL) {
        this.receiverProfPicURL = receiverProfPicURL;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
