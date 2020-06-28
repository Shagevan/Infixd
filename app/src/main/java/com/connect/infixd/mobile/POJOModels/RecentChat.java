/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.POJOModels;

public class RecentChat {

    private String FriendId;
    private String FriendName;
    private String FriendProfPicURL;
    private String ChatRoomName;
    private Long LastUpdate;
    private Long LastRead;
    private int unReadMessageCount;
    private String userId;
    private String userFirstName;
    private String userProfPicURL;
    private String firstFriendId;
    private String firstFriendFirstName;
    private String firstFriendProfPicURL;
    private String secondFriendId;
    private String secondFriendFirstName;
    private String secondFriendProfPicURL;

    public RecentChat() {
    }

    public RecentChat(String friendId, String friendName) {
        FriendId = friendId;
        FriendName = friendName;
    }

    public RecentChat(String friendId, String friendName, String friendProfPicURL) {
        FriendId = friendId;
        FriendName = friendName;
        FriendProfPicURL = friendProfPicURL;
    }

    public RecentChat(String friendId, String friendName,String friendProfPicURL,
                      Long lastUpdate) {
        FriendId = friendId;
        FriendName = friendName;
        LastUpdate = lastUpdate;
        FriendProfPicURL = friendProfPicURL;
    }

    public RecentChat(String friendId, String friendName,String friendProfPicURL,
                      String chatRoomName, Long lastUpdate, Long lastRead) {
        FriendId = friendId;
        FriendName = friendName;
        ChatRoomName = chatRoomName;
        LastUpdate = lastUpdate;
        LastRead = lastRead;
        FriendProfPicURL = friendProfPicURL;
    }

    public RecentChat(String friendId, String friendName,String friendProfPicURL,
                      String chatRoomName, Long lastUpdate, Long lastRead, int unReadMessageCount) {
        FriendId = friendId;
        FriendName = friendName;
        ChatRoomName = chatRoomName;
        LastUpdate = lastUpdate;
        LastRead = lastRead;
        this.unReadMessageCount = unReadMessageCount;
        FriendProfPicURL = friendProfPicURL;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }

    public String getFriendName() {
        return FriendName;
    }

    public void setFriendName(String friendName) {
        FriendName = friendName;
    }

    public Long getLastUpdate() {
        return LastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        LastUpdate = lastUpdate;
    }

    public String getChatRoomName() {
        return ChatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        ChatRoomName = chatRoomName;
    }

    public Long getLastRead() {
        return LastRead;
    }

    public void setLastRead(Long lastRead) {
        LastRead = lastRead;
    }

    public int getUnReadMessageCount() {
        return unReadMessageCount;
    }

    public void setUnReadMessageCount(int unReadMessageCount) {
        this.unReadMessageCount = unReadMessageCount;
    }

    public String getFriendProfPicURL() {
        return FriendProfPicURL;
    }

    public void setFriendProfPicURL(String friendProfPicURL) {
        FriendProfPicURL = friendProfPicURL;
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

    public String getFirstFriendId() {
        return firstFriendId;
    }

    public void setFirstFriendId(String firstFriendId) {
        this.firstFriendId = firstFriendId;
    }

    public String getFirstFriendFirstName() {
        return firstFriendFirstName;
    }

    public void setFirstFriendFirstName(String firstFriendFirstName) {
        this.firstFriendFirstName = firstFriendFirstName;
    }

    public String getFirstFriendProfPicURL() {
        return firstFriendProfPicURL;
    }

    public void setFirstFriendProfPicURL(String firstFriendProfPicURL) {
        this.firstFriendProfPicURL = firstFriendProfPicURL;
    }

    public String getSecondFriendId() {
        return secondFriendId;
    }

    public void setSecondFriendId(String secondFriendId) {
        this.secondFriendId = secondFriendId;
    }

    public String getSecondFriendFirstName() {
        return secondFriendFirstName;
    }

    public void setSecondFriendFirstName(String secondFriendFirstName) {
        this.secondFriendFirstName = secondFriendFirstName;
    }

    public String getSecondFriendProfPicURL() {
        return secondFriendProfPicURL;
    }

    public void setSecondFriendProfPicURL(String secondFriendProfPicURL) {
        this.secondFriendProfPicURL = secondFriendProfPicURL;
    }
}
