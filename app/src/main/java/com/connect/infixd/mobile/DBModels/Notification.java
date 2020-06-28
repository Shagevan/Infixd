/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.connect.infixd.mobile.DBModels;

public class Notification {
    private int _id;
    private String type;
    private String senderId;
    private String senderName;
    private String senderProfPicUrl;
    private String receiverId;
    private String receiverName;
    private String receiverProfPicUrl;
    private String thirdPartyId;
    private String thirdPartyName;
    private String thirdPartyProfPicUrl;
    private String body;
    private String Status;

    public Notification() {
    }

    public Notification(String type, String senderId, String senderName, String receiverId, String receiverName,
                        String thirdPartyId, String thirdPartyName, String body) {
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.thirdPartyId = thirdPartyId;
        this.thirdPartyName = thirdPartyName;
        this.body = body;
    }

    public Notification(String type, String senderId, String senderName, String senderProfPicUrl,
                        String receiverId, String receiverName, String receiverProfPicUrl,
                        String thirdPartyId, String thirdPartyName, String thirdPartyProfPicUrl, String body) {
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderProfPicUrl = senderProfPicUrl;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverProfPicUrl = receiverProfPicUrl;
        this.thirdPartyId = thirdPartyId;
        this.thirdPartyName = thirdPartyName;
        this.thirdPartyProfPicUrl = thirdPartyProfPicUrl;
        this.body = body;
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

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getThirdPartyName() {
        return thirdPartyName;
    }

    public void setThirdPartyName(String thirdPartyName) {
        this.thirdPartyName = thirdPartyName;
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

    public String getSenderProfPicUrl() {
        return senderProfPicUrl;
    }

    public void setSenderProfPicUrl(String senderProfPicUrl) {
        this.senderProfPicUrl = senderProfPicUrl;
    }

    public String getReceiverProfPicUrl() {
        return receiverProfPicUrl;
    }

    public void setReceiverProfPicUrl(String receiverProfPicUrl) {
        this.receiverProfPicUrl = receiverProfPicUrl;
    }

    public String getThirdPartyProfPicUrl() {
        return thirdPartyProfPicUrl;
    }

    public void setThirdPartyProfPicUrl(String thirdPartyProfPicUrl) {
        this.thirdPartyProfPicUrl = thirdPartyProfPicUrl;
    }
}