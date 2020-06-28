/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.DBHelper.SearchSuggestionDBHelper;
import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.User.UserProfileForFriends;
import com.connect.infixd.mobile.UserHome.UserHomeActivity;
import com.connect.infixd.mobile.intentservices.NotificationIntentService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetContactsResponse;
import com.infixd.client.model.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class InfixdFirebaseMessagingService extends FirebaseMessagingService{

    public static final String INTRODUCTION_NOTIFICATION = "introductionNotification";
    public static final String FORWARD_INTRODUCTION_NOTIFICATION = "forwardIntroductionNotification";
    public static final String INTRODUCTION_FRIEND_REQUEST_ACCEPTED = "introductionFRA";
    public static final String DIRECT_FRIEND_REQUEST = "directFriendRequest";
    public static final String SUCCESSFULLY_CONNECTED_NOTIFICATION = "scNotification";
    public static final String DIRECT_FRIEND_REQUEST_ACCEPTED = "directFRA";
    public static final String SHARED_POST_CREATOR_NOTIFICATION = "sharedPostCreatorNotification";

    private String senderId;
    private String thirdPartyId;
    private boolean isAppOnForeground;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private static InfixdClient infixdClient = new InfixdClient();

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private Bundle getUserDetails(String userId){
        InfixdClient clientApi = new InfixdClient();
        UserResponse userResponse = clientApi.getUserDetails(userId);

        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userResponse.getUserId());
        bundle.putString("FULL_NAME", userResponse.getName());
        bundle.putString("FIRST_NAME", userResponse.getFirstName());
        bundle.putString("LAST_NAME", userResponse.getLastName());
        bundle.putString("MOBILE_NUMBER", userResponse.getMobileNumber());
        bundle.putString("ABOUT_ME", userResponse.getAboutMe());
        bundle.putString("EMAIL_ID", userResponse.getEmailAddress());
        bundle.putString("FB_LINK", userResponse.getFblink());
        bundle.putString("TWITTER_LINK", userResponse.getTwitterLink());
        bundle.putString("GP_LINK", userResponse.getGooglePlusLink());
        bundle.putString("LINKEDIN_LINK", userResponse.getLinkedInLink());
        bundle.putString("INSTAGRAM_LINK", userResponse.getInstagramLink());
        bundle.putString("PROF_PIC_URL", userResponse.getProfPicUrl());
        bundle.putString("PROFESSION", userResponse.getProfession());
        bundle.putString("EDUCATION", userResponse.getEducation());
        bundle.putString("LOCATION", userResponse.getCity());
        bundle.putString("MOBILE_NO_PERMISSION", userResponse.getPhoneNoPermission());
        bundle.putString("FRIENDSHIP_METER_PERMISSION", userResponse.getFriendshipMeterPermission());

        return bundle;
    }

    private void sendPushNotification(String notificationBody, PendingIntent pendingIntent){
        builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_done_white_18dp)
                .setLights(Color.RED, 3000, 3000)
                .setDefaults(android.app.Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentTitle("Infixd")
                .setContentText(notificationBody);
        if(pendingIntent != null){
            builder.setContentIntent(pendingIntent);
        }
        manager = (NotificationManager) getSystemService(InfixdFirebaseMessagingService.this.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            isAppOnForeground = isAppOnForeground(getApplicationContext());
            Intent intent;
            PendingIntent pendingIntent;

            switch (remoteMessage.getData().get("title")){

                case INTRODUCTION_NOTIFICATION:
                    if(isAppOnForeground){
                        // Notify in Navigation Drawer
                        Intent serviceIntent = new Intent(this, NotificationIntentService.class);
                        serviceIntent.setAction(NotificationIntentService.ACTION_NOTIFY_INTRODUCTION_REQUEST);
                        serviceIntent.putExtra(NotificationIntentService.DATA_USER_ID,remoteMessage.getData().get("receiverId"));
                        startService(serviceIntent);
                    }
                    else{
                        // Send Push Notification
                        intent= new Intent(this, UserHomeActivity.class);
                        intent.setAction(NotificationIntentService.ACTION_NOTIFY_INTRODUCTION_REQUEST);
                        pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        sendPushNotification(remoteMessage.getData().get("body"),pendingIntent);
                    }
                    break;

                case FORWARD_INTRODUCTION_NOTIFICATION:
                    if(isAppOnForeground){
                        // Notify in Navigation Drawer
                        Intent serviceIntent = new Intent(this, NotificationIntentService.class);
                        serviceIntent.setAction(NotificationIntentService.ACTION_NOTIFY_FORWARD_INTRODUCTION_REQUEST);
                        serviceIntent.putExtra(NotificationIntentService.DATA_USER_ID,remoteMessage.getData().get("receiverId"));
                        startService(serviceIntent);
                    }
                    else{
                        // Send Push Notification
                        intent= new Intent(this, UserHomeActivity.class);
                        intent.setAction(NotificationIntentService.ACTION_NOTIFY_FORWARD_INTRODUCTION_REQUEST);
                        pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        sendPushNotification(remoteMessage.getData().get("body"),pendingIntent);
                    }

                    break;

                case INTRODUCTION_FRIEND_REQUEST_ACCEPTED:
                    thirdPartyId = remoteMessage.getData().get("targetId");
                    intent= new Intent(this, UserProfileForFriends.class);
                    intent.putExtras(getUserDetails(thirdPartyId));
                    pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    sendPushNotification(remoteMessage.getData().get("body"),pendingIntent);
                    break;

                case DIRECT_FRIEND_REQUEST:
                    if(isAppOnForeground){
                        // Notify in Navigation Drawer
                        Intent serviceIntent = new Intent(this, NotificationIntentService.class);
                        serviceIntent.setAction(NotificationIntentService.ACTION_NOTIFY_DIRECT_FRIEND_REQUEST);
                        serviceIntent.putExtra(NotificationIntentService.DATA_USER_ID,remoteMessage.getData().get("receiverId"));
                        startService(serviceIntent);
                    }
                    else{
                        // Send Push Notification
                        intent= new Intent(this, UserHomeActivity.class);
                        intent.setAction(NotificationIntentService.ACTION_NOTIFY_DIRECT_FRIEND_REQUEST);
                        pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        sendPushNotification(remoteMessage.getData().get("body"),pendingIntent);
                    }
                    break;

                case DIRECT_FRIEND_REQUEST_ACCEPTED:
                    senderId = remoteMessage.getData().get("senderId");
                    GetContactsResponse getContactsResponse = infixdClient.getContacts(remoteMessage.getData().get("receiverId"));
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

                    intent= new Intent(this, UserProfileForFriends.class);
                    intent.putExtras(getUserDetails(senderId));
                    pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    sendPushNotification(remoteMessage.getData().get("body"),pendingIntent);
                    break;

                case SUCCESSFULLY_CONNECTED_NOTIFICATION:
                    sendPushNotification(remoteMessage.getData().get("body"),null);
                    break;

                case SHARED_POST_CREATOR_NOTIFICATION:
                    // Send Push Notification
                    intent= new Intent(this, UserHomeActivity.class);
                    intent.setAction(InfixdApp.ACTION_NOTIFY_SHARED_POST_CREATOR);
                    pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    sendPushNotification(remoteMessage.getData().get("body"),pendingIntent);
                    break;

            }

        }
    }
}
