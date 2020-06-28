/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.DBModels;

public class FriendshipMeterRow {
    private String friendId;
    private int messageCount;

    public FriendshipMeterRow() {
    }

    public FriendshipMeterRow(String friendId, int messageCount) {
        this.friendId = friendId;
        this.messageCount = messageCount;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
}
