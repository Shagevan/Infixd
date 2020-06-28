/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.intentservices;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.connect.infixd.mobile.DBHelper.SearchSuggestionDBHelper;
import com.connect.infixd.mobile.DBModels.Contact;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetContactsResponse;
import com.infixd.client.model.SendRequestResponse;
import com.infixd.client.model.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class FriendIntentService extends InfixdIntentService {
    private static final String TAG = "FriendIntentService";
    public static final String ACTION_SEND_DIRECT_REQUEST_BY_ID = TAG + ".ActionSendDirectRequestById";
    public static final String ACTION_SEND_DIRECT_REQUEST_BY_NUMBER = TAG + ".ActionSendDirectRequestByNumber";
    public static final String ACTION_SEND_INTRODUCTION_REQUEST = TAG + ".ActionSendIntroductionRequest";
    public static final String ACTION_SEND_FORWARD_INTRODUCTION_REQUEST = TAG + ".ActionSendFrowardIntroRequest";
    public static final String ACTION_ACCEPT_FRIEND_REQUEST = TAG + ".ActionAcceptFriendRequest";
    public static final String ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST = TAG + ".ActionAcceptForwardIntroRequest";
    public static final String ACTION_REJECT_FRIEND_REQUEST = TAG + ".ActionRejectFriendRequest";
    public static final String ACTION_REJECT_FORWARD_INTRODUCTION_REQUEST = TAG + ".ActionRejectForwardIntroRequest";
    public static final String ACTION_REJECT_INTRODUCTION_REQUEST = TAG + ".ActionRejectIntroRequest";

    public static final String DATA_SENDER_ID = ".data.senderId";
    public static final String DATA_RECEIVER_ID = ".data.receiverId";
    public static final String DATA_TARGET_ID = ".data.targetId";
    public static final String DATA_SENDER_NAME = ".data.senderName";
    public static final String DATA_RECEIVER_NAME = ".data.receiverName";
    public static final String DATA_TARGET_NAME = ".data.targetName";
    public static final String DATA_SENDER_PROF_PIC_URL = ".data.senderProfPicURL";
    public static final String DATA_RECEIVER_PROF_PIC_URL = ".data.receiverProfPicURL";
    public static final String DATA_TARGET_PROF_PIC_URL = ".data.targetProfPicURL";
    public static final String DATA_USER_ID = ".data.userId";
    public static final String DATA_USER_NAME = ".data.userName";
    public static final String DATA_USER_PROF_PIC_URL = ".data.userProfPicURL";
    public static final String DATA_FIRST_FRIEND_ID = ".data.firstFriendId";
    public static final String DATA_FIRST_FRIEND_NAME = ".data.firstFriendName";
    public static final String DATA_FIRST_FRIEND_PROF_PIC_URL = ".data.firstFriendProfPicURL";
    public static final String DATA_SECOND_FRIEND_ID = ".data.secondFriendId";
    public static final String DATA_SECOND_FRIEND_NAME = ".data.secondFriendName";
    public static final String DATA_SECOND_FRIEND_PROF_PIC_URL = ".data.secondFriendProfPicURL";
    public static final String DATA_TARGET_PHONE = ".data.targetPhone";
    public static final String DATA_MESSAGE = ".data.message";

    public static final String ACTION_SEND_DIRECT_REQUEST_BY_NUMBER_SUCCESS = ACTION_SEND_DIRECT_REQUEST_BY_NUMBER + ".success";
    public static final String ACTION_SEND_DIRECT_REQUEST_BY_ID_SUCCESS = ACTION_SEND_DIRECT_REQUEST_BY_ID + ".success";
    public static final String ACTION_SEND_INTRODUCTION_REQUEST_SUCCESS = ACTION_SEND_INTRODUCTION_REQUEST + ".success";
    public static final String ACTION_SEND_FORWARD_INTRODUCTION_REQUEST_SUCCESS = ACTION_SEND_FORWARD_INTRODUCTION_REQUEST + ".success";
    public static final String ACTION_ACCEPT_FRIEND_REQUEST_SUCCESS = ACTION_ACCEPT_FRIEND_REQUEST + ".success";
    public static final String ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST_SUCCESS = ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST + ".success";

    public static final String ACTION_SEND_DIRECT_REQUEST_BY_NUMBER_FAIL = ACTION_SEND_DIRECT_REQUEST_BY_NUMBER + ".fail";
    public static final String ACTION_SEND_DIRECT_REQUEST_BY_ID_FAIL = ACTION_SEND_DIRECT_REQUEST_BY_ID + ".fail";
    public static final String ACTION_SEND_INTRODUCTION_REQUEST_FAIL = ACTION_SEND_INTRODUCTION_REQUEST + ".fail";
    public static final String ACTION_SEND_FORWARD_INTRODUCTION_REQUEST_FAIL = ACTION_SEND_FORWARD_INTRODUCTION_REQUEST + ".fail";
    public static final String ACTION_ACCEPT_FRIEND_REQUEST_FAIL = ACTION_ACCEPT_FRIEND_REQUEST + ".fail";
    public static final String ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST_FAIL = ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST + ".fail";
    public static final String ACTION_REJECT_FRIEND_REQUEST_FAIL = ACTION_ACCEPT_FRIEND_REQUEST + ".fail";
    public static final String ACTION_REJECT_INTRODUCTION_REQUEST_FAIL = ACTION_REJECT_INTRODUCTION_REQUEST + ".fail";
    public static final String ACTION_REJECT_FORWARD_INTRODUCTION_REQUEST_FAIL = ACTION_REJECT_FORWARD_INTRODUCTION_REQUEST + ".fail";

    public static final String DIRECT_FRIEND_REQUEST_BY_ID = "directFriendRequestById";
    public static final String DIRECT_FRIEND_REQUEST_BY_NUMBER = "directFriendRequestByNo";
    public static final String INTRODUCTION_REQUEST = "introductionRequest";
    public static final String FORWARD_INTRODUCTION_REQUEST = "forwardIntroductionRequest";
    public static final String REQUESTING_DF = "REQUESTING_DF";
    public static final String REQUESTING_FI = "REQUESTING_FI";
    public static final String REQUESTING_INTRO = "REQUESTING_INTRO";

    private static InfixdClient infixdClient = new InfixdClient();
    public FriendIntentService() {
        super("FriendIntentService");
        Log.d(TAG, "Service instance created.");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) switch (intent.getAction()) {
            case ACTION_SEND_DIRECT_REQUEST_BY_NUMBER:
                sendDirectRequestByNumber(
                        intent.getStringExtra(DATA_SENDER_ID),
                        intent.getStringExtra(DATA_TARGET_PHONE)
                );
                break;
            case ACTION_SEND_DIRECT_REQUEST_BY_ID:
                sendDirectRequestById(
                        intent.getStringExtra(DATA_SENDER_ID),
                        intent.getStringExtra(DATA_TARGET_ID)
                );
                break;
            case ACTION_SEND_INTRODUCTION_REQUEST:
                sendIntroductionRequest(
                        intent.getStringExtra(DATA_SENDER_ID),
                        intent.getStringExtra(DATA_RECEIVER_ID),
                        intent.getStringExtra(DATA_TARGET_ID)
                );
                break;
            case ACTION_SEND_FORWARD_INTRODUCTION_REQUEST:
                sendForwardIntroductionRequest(
                        intent.getStringExtra(DATA_SENDER_ID),
                        intent.getStringExtra(DATA_TARGET_ID),
                        intent.getStringExtra(DATA_RECEIVER_ID)
                );
                break;

            case ACTION_ACCEPT_FRIEND_REQUEST:
                acceptFriendRequest(
                        intent.getStringExtra(DATA_SENDER_ID),
                        intent.getStringExtra(DATA_TARGET_ID)
                );
                break;
            case ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST:
                acceptForwardIntroductionRequest(
                        intent.getStringExtra(DATA_SENDER_ID),
                        intent.getStringExtra(DATA_RECEIVER_ID),
                        intent.getStringExtra(DATA_TARGET_ID),
                        intent.getStringExtra(DATA_SENDER_NAME),
                        intent.getStringExtra(DATA_RECEIVER_NAME),
                        intent.getStringExtra(DATA_TARGET_NAME),
                        intent.getStringExtra(DATA_SENDER_PROF_PIC_URL),
                        intent.getStringExtra(DATA_RECEIVER_PROF_PIC_URL),
                        intent.getStringExtra(DATA_TARGET_PROF_PIC_URL)
                );
                break;
            case ACTION_REJECT_FRIEND_REQUEST:
                rejectFriendRequest(
                        intent.getStringExtra(DATA_SENDER_ID),
                        intent.getStringExtra(DATA_TARGET_ID)
                );
                break;
            case ACTION_REJECT_INTRODUCTION_REQUEST:
                rejectIntroductionRequest(
                        intent.getStringExtra(DATA_SENDER_ID),
                        intent.getStringExtra(DATA_RECEIVER_ID),
                        intent.getStringExtra(DATA_TARGET_ID)
                );
                break;
            case ACTION_REJECT_FORWARD_INTRODUCTION_REQUEST:
                rejectForwardIntroductionRequest(
                        intent.getStringExtra(DATA_SENDER_ID),
                        intent.getStringExtra(DATA_RECEIVER_ID),
                        intent.getStringExtra(DATA_TARGET_ID)
                );
                break;
        }
    }

    /**
     * Sends a direct request. Broadcasts ACTION_SEND_DIRECT_REQUEST_BY_NUMBER_SUCCESS if successful.
     * ACTION_SEND_DIRECT_REQUEST_BY_NUMBER_FAIL if failed.
     *
     * @param senderId
     * @param targetPhone
     * @return
     */
    private void sendDirectRequestByNumber(String senderId, String targetPhone) {
        if (senderId != null && targetPhone != null) {
            try {
                SendRequestResponse response = infixdClient.sendRequest(
                        DIRECT_FRIEND_REQUEST_BY_NUMBER,senderId, targetPhone,null);
                if(response != null){
                    Intent successIntent = new Intent(ACTION_SEND_DIRECT_REQUEST_BY_NUMBER_SUCCESS);
                    successIntent.putExtra(DATA_MESSAGE,response.getMessage());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);
                }
            } catch (Exception e) {
                Intent failureIntent = new Intent(ACTION_SEND_DIRECT_REQUEST_BY_NUMBER_FAIL);
                failureIntent.putExtra(DATA_MESSAGE,"Server error");
                LocalBroadcastManager.getInstance(this).sendBroadcast(failureIntent);
                Log.e(TAG, "Failed to send direct friend request", e);
            }
        }
    }

    private void sendDirectRequestById(String senderId, String targetId) {
        if (senderId != null && targetId != null) {
            try {
                SendRequestResponse response = infixdClient.sendRequest(
                        DIRECT_FRIEND_REQUEST_BY_ID,senderId, targetId,null);
                if(response != null){
                    Intent successIntent = new Intent(ACTION_SEND_DIRECT_REQUEST_BY_ID_SUCCESS);
                    successIntent.putExtra(DATA_MESSAGE,response.getMessage());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);
                }
            } catch (Exception e) {
                Intent failureIntent = new Intent(ACTION_SEND_DIRECT_REQUEST_BY_ID_FAIL);
                failureIntent.putExtra(DATA_MESSAGE,"Server error");
                LocalBroadcastManager.getInstance(this).sendBroadcast(failureIntent);
                Log.e(TAG, "Failed to send direct friend request", e);
            }
        }
    }

    private void sendIntroductionRequest(String senderId, String receiverId, String targetId) {
        if (senderId != null && receiverId != null && targetId != null) {
            try {
                SendRequestResponse response = infixdClient.sendRequest(
                        INTRODUCTION_REQUEST,senderId, receiverId,targetId);
                Intent successIntent = new Intent(ACTION_SEND_INTRODUCTION_REQUEST_SUCCESS);
                successIntent.putExtra(DATA_MESSAGE,response.getMessage());
                LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);
            } catch (Exception e) {
                Intent failureIntent = new Intent(ACTION_SEND_INTRODUCTION_REQUEST_FAIL);
                failureIntent.putExtra(DATA_MESSAGE,"Server error");
                LocalBroadcastManager.getInstance(this).sendBroadcast(failureIntent);
                Log.e(TAG, "Failed to send Introduction request", e);
            }
        }
    }

    private void sendForwardIntroductionRequest(String senderId, String targetId, String receiverId) {
        if (senderId != null && targetId != null && receiverId != null) {
            try {
                SendRequestResponse response = infixdClient.sendRequest(
                        FORWARD_INTRODUCTION_REQUEST,senderId, receiverId,targetId);
                infixdClient.deleteRequest(REQUESTING_INTRO,targetId, senderId,receiverId);
                infixdClient.notifyAcceptedForwardNotification(targetId,senderId,receiverId);
                Intent successIntent = new Intent(ACTION_SEND_FORWARD_INTRODUCTION_REQUEST_SUCCESS);
                successIntent.putExtra(DATA_MESSAGE,response.getMessage());
                LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);
            } catch (Exception e) {
                Intent failureIntent = new Intent(ACTION_SEND_FORWARD_INTRODUCTION_REQUEST_FAIL);
                LocalBroadcastManager.getInstance(this).sendBroadcast(failureIntent);
                failureIntent.putExtra(DATA_MESSAGE,"Server error");
                Log.e(TAG, "Failed to send Forward Introduction request", e);
            }
        }
    }

    private void acceptFriendRequest(String senderId, String targetId) {
        if (senderId != null && targetId != null) {
            try {
                infixdClient.addContact(senderId, targetId);
                infixdClient.deleteRequest(REQUESTING_DF,targetId, senderId,null);
                infixdClient.sendDirectFriendRequestAcceptedNotification(senderId,targetId);
                GetContactsResponse getContactsResponse = infixdClient.getContacts(senderId);
                List<UserResponse> userResponses = getContactsResponse.getFriends();
                ArrayList<Contact> contacts = new ArrayList<Contact>();
                for (UserResponse response : userResponses){
                    Contact contact = new Contact();
                    contact.setID(response.getUserId());
                    contact.setName(response.getFirstName());
                    contact.setPhoneNumber(response.getMobileNumber());
                    contact.setProfilePicUrl(response.getProfPicUrl());
                    contacts.add(contact);
                }
                SearchSuggestionDBHelper db = new SearchSuggestionDBHelper(getBaseContext());
                db.addContacts(contacts);

                Intent successIntent = new Intent(ACTION_ACCEPT_FRIEND_REQUEST_SUCCESS);
                LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);
            } catch (Exception e) {
                Intent failureIntent = new Intent(ACTION_ACCEPT_FRIEND_REQUEST_FAIL);
                LocalBroadcastManager.getInstance(this).sendBroadcast(failureIntent);
                Log.e(TAG, "Failed to Accept Friend request", e);
            }
        }
    }

    private void acceptForwardIntroductionRequest(String senderId, String receiverId, String targetId,
                                                  String senderName, String receiverName, String targetName,
                                                  String senderProfPicURL, String receiverProfPicURL, String targetProfPicURL) {
        if (senderId != null && receiverId != null && targetId != null) {
            try {
                infixdClient.addContact(senderId, targetId);
                infixdClient.deleteRequest(REQUESTING_FI,receiverId, senderId,targetId);
                infixdClient.sendSuccesfullyConnectedNotification(senderId, receiverId, targetId);
                infixdClient.sendIntroductionFriendRequestAcceptedNotification(targetId,receiverId,senderId);
                GetContactsResponse getContactsResponse = infixdClient.getContacts(senderId);
                List<UserResponse> userResponses = getContactsResponse.getFriends();
                ArrayList<Contact> contacts = new ArrayList<Contact>();
                for (UserResponse response : userResponses){
                    Contact contact = new Contact();
                    contact.setID(response.getUserId());
                    contact.setName(response.getFirstName());
                    contact.setPhoneNumber(response.getMobileNumber());
                    contact.setProfilePicUrl(response.getProfPicUrl());
                    contacts.add(contact);
                }
                SearchSuggestionDBHelper db = new SearchSuggestionDBHelper(getBaseContext());
                db.addContacts(contacts);
                Intent successIntent = new Intent(ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST_SUCCESS);
                successIntent.putExtra(FriendIntentService.DATA_USER_ID, senderId);
                successIntent.putExtra(FriendIntentService.DATA_USER_NAME, senderName);
                successIntent.putExtra(FriendIntentService.DATA_USER_PROF_PIC_URL, senderProfPicURL);
                successIntent.putExtra(FriendIntentService.DATA_FIRST_FRIEND_ID, receiverId);
                successIntent.putExtra(FriendIntentService.DATA_FIRST_FRIEND_NAME, receiverName);
                successIntent.putExtra(FriendIntentService.DATA_FIRST_FRIEND_PROF_PIC_URL, receiverProfPicURL);
                successIntent.putExtra(FriendIntentService.DATA_SECOND_FRIEND_ID, targetId);
                successIntent.putExtra(FriendIntentService.DATA_SECOND_FRIEND_NAME, targetName);
                successIntent.putExtra(FriendIntentService.DATA_SECOND_FRIEND_PROF_PIC_URL, targetProfPicURL);
                LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);
            } catch (Exception e) {
                Intent failureIntent = new Intent(ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST_FAIL);
                LocalBroadcastManager.getInstance(this).sendBroadcast(failureIntent);
                Log.e(TAG, "Failed to Accept Friend request", e);
            }
        }
    }

    private void rejectFriendRequest(String senderId, String targetId) {
        if (senderId != null && targetId != null) {
            try {
                infixdClient.deleteRequest(REQUESTING_DF,targetId, senderId,null);
                Intent successIntent = new Intent(ACTION_REJECT_FRIEND_REQUEST);
                LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);
            } catch (Exception e) {
                Intent failureIntent = new Intent(ACTION_REJECT_FRIEND_REQUEST_FAIL);
                LocalBroadcastManager.getInstance(this).sendBroadcast(failureIntent);
                Log.e(TAG, "Failed to reject Friend request", e);
            }
        }
    }

    private void rejectIntroductionRequest(String senderId, String receiverId, String targetId) {
        if (senderId != null && receiverId != null && targetId != null) {
            try {
                infixdClient.deleteRequest(REQUESTING_INTRO,targetId, senderId,receiverId);
                Intent successIntent = new Intent(ACTION_REJECT_INTRODUCTION_REQUEST);
                LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);
            } catch (Exception e) {
                Intent failureIntent = new Intent(ACTION_REJECT_INTRODUCTION_REQUEST_FAIL);
                LocalBroadcastManager.getInstance(this).sendBroadcast(failureIntent);
                Log.e(TAG, "Failed to Reject Introduction request", e);
            }
        }
    }

    private void rejectForwardIntroductionRequest(String senderId, String receiverId, String targetId) {
        if (senderId != null && receiverId != null && targetId != null) {
            try {
                infixdClient.deleteRequest(REQUESTING_FI,receiverId, senderId,targetId);
                Intent successIntent = new Intent(ACTION_REJECT_FORWARD_INTRODUCTION_REQUEST);
                LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);
            } catch (Exception e) {
                Intent failureIntent = new Intent(ACTION_REJECT_FORWARD_INTRODUCTION_REQUEST_FAIL);
                LocalBroadcastManager.getInstance(this).sendBroadcast(failureIntent);
                Log.e(TAG, "Failed to reject Forward Introduction request", e);
            }
        }
    }

}
