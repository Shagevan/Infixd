package com.connect.infixd.mobile.intentservices;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetAllRequestsResponse;
import com.infixd.client.model.RequestResponse;

import java.io.Serializable;
import java.util.HashMap;

public class NotificationIntentService  extends InfixdIntentService {
    public static final String TAG = "com.connect.infixd.mobile.intentservices.NotificationIntentService";
    public static final String ACTION_NOTIFY_DIRECT_FRIEND_REQUEST = TAG + ".directFriendRequest";
    public static final String ACTION_NOTIFY_INTRODUCTION_REQUEST = TAG + ".introductionRequest";
    public static final String ACTION_NOTIFY_FORWARD_INTRODUCTION_REQUEST = TAG + ".forwardIntroductionRequest";
    public static final String ACTION_GET_ALL_REQUEST = TAG + ".getAllRequests";
    public static final String ACTION_NO_REQUEST = TAG + ".noRequests";
    public static final String ACTION_GET_NO_OF_REQUEST = TAG + ".getNoOfRequests";
    public static final String ACTION_GET_ALL_REQUEST_FAILED = ACTION_GET_ALL_REQUEST + ".failed";

    public static final String DATA_NUMBER_OF_NOTIFICATIONS = TAG + ".data.noOfNotifications";
    public static final String DATA_NUMBER_OF_DF_NOTIFICATIONS = TAG + ".data.noOfDFNotifications";
    public static final String DATA_NUMBER_OF_FI_NOTIFICATIONS = TAG + ".data.noOfFINotifications";
    public static final String DATA_NUMBER_OF_INTRO_NOTIFICATIONS = TAG + ".data.noOfIntroNotifications";
    public static final String DATA_NOTIFICATIONS = TAG + ".data.notifications";
    public static final String DATA_USER_ID = TAG + ".data.userId";
    public static final String DATA_GET_ALL_REQUEST_FAILED = ACTION_GET_ALL_REQUEST_FAILED + ".data";

    public static final String REQUESTING_DF = "REQUESTING_DF";
    public static final String REQUESTING_FI = "REQUESTING_FI";
    public static final String REQUESTING_INTRO = "REQUESTING_INTRO";

    private static InfixdClient infixdClient = new InfixdClient();
    public NotificationIntentService() {
        super(UserIntentService.class.getCanonicalName());
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        String countVal;
        Intent notificationIntent;
        switch (action) {
            case ACTION_NOTIFY_DIRECT_FRIEND_REQUEST:
                try {
                    String userId = intent.getStringExtra(DATA_USER_ID);
                    countVal = getNumberOfNotificationsByType(userId,REQUESTING_DF);
                    notificationIntent = new Intent(ACTION_NOTIFY_DIRECT_FRIEND_REQUEST);
                    notificationIntent.putExtra(DATA_NUMBER_OF_NOTIFICATIONS, countVal);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ACTION_NOTIFY_INTRODUCTION_REQUEST:
                try {
                    String userId = intent.getStringExtra(DATA_USER_ID);
                    countVal = getNumberOfNotificationsByType(userId,REQUESTING_INTRO);
                    notificationIntent = new Intent(ACTION_NOTIFY_INTRODUCTION_REQUEST);
                    notificationIntent.putExtra(DATA_NUMBER_OF_NOTIFICATIONS, countVal);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ACTION_NOTIFY_FORWARD_INTRODUCTION_REQUEST:
                try {
                    String userId = intent.getStringExtra(DATA_USER_ID);
                    countVal = getNumberOfNotificationsByType(userId,REQUESTING_FI);
                    notificationIntent = new Intent(ACTION_NOTIFY_FORWARD_INTRODUCTION_REQUEST);
                    notificationIntent.putExtra(DATA_NUMBER_OF_NOTIFICATIONS, countVal);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ACTION_GET_ALL_REQUEST:
                try {
                    String userId = intent.getStringExtra(DATA_USER_ID);
                    GetAllRequestsResponse result = getAllRequests(userId);
                    if(result.getRequestResponses() != null){
                        notificationIntent = new Intent(ACTION_GET_ALL_REQUEST);
                        notificationIntent.putExtra(DATA_NOTIFICATIONS, (Serializable) result.getRequestResponses());
                    }
                    else{
                        notificationIntent = new Intent(ACTION_NO_REQUEST);
                    }

                } catch (Exception e) {
                    notificationIntent = new Intent(ACTION_GET_ALL_REQUEST_FAILED);
                    notificationIntent.putExtra(DATA_GET_ALL_REQUEST_FAILED,
                            e.getMessage() != null ? e.getMessage() : "Get Nearby User Action Failed");
                }
                //Broadcast the outcome (success or failure)
                LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);
                break;

            case ACTION_GET_NO_OF_REQUEST:
                try {
                    String userId = intent.getStringExtra(DATA_USER_ID);
                    HashMap<String, String> notifications = getNumberOfAllNotifications(userId);
                    notificationIntent = new Intent(ACTION_GET_NO_OF_REQUEST);
                    notificationIntent.putExtra(DATA_NUMBER_OF_DF_NOTIFICATIONS, notifications.get(REQUESTING_DF));
                    notificationIntent.putExtra(DATA_NUMBER_OF_FI_NOTIFICATIONS, notifications.get(REQUESTING_FI));
                    notificationIntent.putExtra(DATA_NUMBER_OF_INTRO_NOTIFICATIONS, notifications.get(REQUESTING_INTRO));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(notificationIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
        }
    }

    private GetAllRequestsResponse getAllRequests(String userId) throws Exception {
        GetAllRequestsResponse getAllRequestsResponse = infixdClient.getAllRequests(userId);

        if (getAllRequestsResponse == null) {
            throw new Exception("User Registration request failed");
        }
        return getAllRequestsResponse;
    }

    private String getNumberOfNotificationsByType(String userId, String type)throws Exception{
        GetAllRequestsResponse response = infixdClient.getAllRequests(userId);
        int count = 0;
        if(response != null && response.getRequestResponses()!= null){
            for (RequestResponse requestResponse: response.getRequestResponses()) {
                if(requestResponse.getType().equals(type)) count ++;
            }
        }
        else {
            throw new Exception("Request failed");
        }
        return String.valueOf(count);
    }

    private HashMap<String, String> getNumberOfAllNotifications(String userId)throws Exception{

        HashMap<String, String> notifications=new HashMap<String, String>();
        int countDF = 0;
        int countFI = 0;
        int countIntro = 0;

        GetAllRequestsResponse response = infixdClient.getAllRequests(userId);
        if(response != null && response.getRequestResponses()!= null){
            for (RequestResponse requestResponse: response.getRequestResponses()) {
                if(requestResponse.getType().equals(REQUESTING_DF)) {
                    countDF ++;
                }
                else if(requestResponse.getType().equals(REQUESTING_FI)){
                    countFI ++;
                }
                else if(requestResponse.getType().equals(REQUESTING_INTRO)){
                    countIntro ++;
                }
            }

            if(countDF == 0) notifications.put(REQUESTING_DF,String.valueOf(""));
            else notifications.put(REQUESTING_DF,String.valueOf(countDF));

            if(countFI == 0) notifications.put(REQUESTING_FI,String.valueOf(""));
            else notifications.put(REQUESTING_FI,String.valueOf(countFI));

            if(countIntro == 0) notifications.put(REQUESTING_INTRO,String.valueOf(""));
            else notifications.put(REQUESTING_INTRO,String.valueOf(countIntro));

        }

        else {
            throw new Exception("Request failed");
        }
        return notifications;
    }



}

