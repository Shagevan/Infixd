package com.connect.infixd.mobile.intentservices;


import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.connect.infixd.mobile.Services.InfixdFirebaseInstanceIDService;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.UpdateLocationResponse;
import com.infixd.client.model.UserRegisterResponse;
import com.infixd.client.model.UserResponse;

import java.io.Serializable;

public class UserIntentService extends InfixdIntentService {
    public static final String TAG = "com.connect.infixd.mobile.intentservices.UserIntentService";
    public static final String ACTION_DO_REGISTER = TAG + ".register";
    public static final String ACTION_REGISTER_COMPLETE = TAG + ".registersucess";
    public static final String ACTION_REGISTER_NUMBEREXISTS = TAG + ".numberexists";
    public static final String ACTION_HIDE_USER_LOCATION = TAG + ".hidelocation";
    public static final String ACTION_GET_NEARBY_USERS = TAG + ".getnearbyusers";
    public static final String ACTION_NO_NEARBY_USERS = TAG + ".nonearbyusers";

    public static final String DATA_USERID = TAG + ".data.userid";
    public static final String DATA_USERNAME = TAG + ".data.username";
    public static final String DATA_FULLNAME = TAG + ".data.fullname";
    public static final String DATA_FIRSTNAME = TAG + ".data.firstname";
    public static final String DATA_LASTNAME = TAG + ".data.lastname";
    public static final String DATA_MOBILENUMBER = TAG + ".data.mobile";
    public static final String DATA_PROFILE_PIC_URL = TAG + ".data.profilePicUrl";
    public static final String DATA_LATITUDE = TAG + ".data.latitude";
    public static final String DATA_LONGITUDE = TAG + ".data.longitude";
    public static final String DATA_NEAR_USERS = TAG + ".data.nearusers";

    public static final String ACTION_REGISTER_FAILED = TAG + ".registerfailed";
    public static final String ACTION_HIDE_USER_LOCATION_FAILED = TAG + ".hidelocationfailed";
    public static final String ACTION_GET_NEARBY_USERS_FAILED = TAG + ".getnearbyusersfailed";
    public static final String DATA_REGISTER_FAILED = TAG + ".data.registerfailed";
    public static final String DATA_HIDE_USER_LOCATION_FAILED = TAG + ".data.hidelocationfailed";
    public static final String DATA_GET_NEARBY_USERS_FAILED = TAG + ".data.getnearbyusersfailed";

    private static InfixdClient infixdClient = new InfixdClient();

    public UserIntentService() {
        super(UserIntentService.class.getCanonicalName());
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UserIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        String userId;
        Intent userIntent;

        switch (action) {
            case ACTION_DO_REGISTER:
                String firstname = intent.getStringExtra(DATA_FIRSTNAME);
                String lastname = intent.getStringExtra(DATA_LASTNAME);
                String mobilenumber = intent.getStringExtra(DATA_MOBILENUMBER);

                try {
                    UserRegisterResponse userRegisterResponse =
                            performUserRegistration(firstname, lastname, mobilenumber);

                    //if user registration commences without exceptions
                    InfixdFirebaseInstanceIDService.updateToken(this,
                            userRegisterResponse.getUserId());

                    userId = userRegisterResponse.getUserId();
                    String fullName = userRegisterResponse.getName();

                    if ("true".equals(userRegisterResponse.getNumberExistance())) {
                        userIntent = new Intent(ACTION_REGISTER_NUMBEREXISTS);
                        userIntent.putExtra(DATA_PROFILE_PIC_URL, userRegisterResponse.getProfilePicURL());
                    } else {
                        userIntent = new Intent(ACTION_REGISTER_COMPLETE);
                    }

                    userIntent.putExtra(DATA_USERNAME, userId);
                    userIntent.putExtra(DATA_FIRSTNAME, firstname);
                    userIntent.putExtra(DATA_FULLNAME, fullName);
                    userIntent.putExtra(DATA_MOBILENUMBER, mobilenumber);

                } catch (Exception e) {
                    userIntent = new Intent(ACTION_REGISTER_FAILED);
                    userIntent.putExtra(DATA_REGISTER_FAILED,
                            e.getMessage() != null ? e.getMessage() : "Registration Failed");
                }
                //Broadcast the outcome (success or failure)
                LocalBroadcastManager.getInstance(this).sendBroadcast(userIntent);
                break;

            case ACTION_HIDE_USER_LOCATION:
                userId = intent.getStringExtra(DATA_USERID);
                try {
                    UpdateLocationResponse result = hideLocation(userId);
                    if (result != null && result.getUserId() != null) {
                        userIntent = new Intent(ACTION_HIDE_USER_LOCATION);
                    } else {
                        throw new Exception("Hide Location Failed");
                    }

                } catch (Exception e) {
                    userIntent = new Intent(ACTION_HIDE_USER_LOCATION_FAILED);
                    userIntent.putExtra(DATA_HIDE_USER_LOCATION_FAILED,
                            e.getMessage() != null ? e.getMessage() : "Hide Location Failed");
                }
                //Broadcast the outcome (success or failure)
                LocalBroadcastManager.getInstance(this).sendBroadcast(userIntent);
                break;

            case ACTION_GET_NEARBY_USERS:
                userId = intent.getStringExtra(DATA_USERID);
                String latitude = intent.getStringExtra(DATA_LATITUDE);
                String longitude = intent.getStringExtra(DATA_LONGITUDE);
                try {
                    UpdateLocationResponse result = getNearbyUsers(userId,latitude,longitude);
                    if (result != null) {
                        if(result.getLocations() != null){
                            userIntent = new Intent(ACTION_GET_NEARBY_USERS);
                            userIntent.putExtra(DATA_NEAR_USERS, (Serializable) result.getLocations());
                        }
                        else{
                            userIntent = new Intent(ACTION_NO_NEARBY_USERS);
                        }

                    } else {
                        throw new Exception("Get Nearby User Action Failed");
                    }

                } catch (Exception e) {
                    userIntent = new Intent(ACTION_GET_NEARBY_USERS_FAILED);
                    userIntent.putExtra(DATA_GET_NEARBY_USERS_FAILED,
                            e.getMessage() != null ? e.getMessage() : "Get Nearby User Action Failed");
                }
                //Broadcast the outcome (success or failure)
                LocalBroadcastManager.getInstance(this).sendBroadcast(userIntent);
                break;

            default:
        }
    }

    /**
     * Performs user registration network call.
     * Broadcasts the following events:
     * ACTION_REGISTER_FAILED when registration fails due to technical issues.
     * ACTION_REGISTER_NUMBEREXISTS when registration fails due to technical issues.
     * ACTION_REGISTER_COMPLETE upon successful registration.
     *
     * @param firstName
     * @param lastName
     * @param mobileNumber
     */
    private UserRegisterResponse performUserRegistration(String firstName,
                                                         String lastName, String mobileNumber) throws Exception {
        UserResponse user = new UserResponse();
        UserRegisterResponse userRegisterResponse;

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMobileNumber(mobileNumber);

        userRegisterResponse = infixdClient.registerUser(user);

        if (userRegisterResponse == null) {
            throw new Exception("User Registration request failed");
        }

        return userRegisterResponse;
    }

    private UpdateLocationResponse hideLocation(String userId) throws Exception {
        UpdateLocationResponse locationResponse = infixdClient.hideLocation(userId);
        if (locationResponse == null) {
            throw new Exception("Hide Location request failed");
        }
        return locationResponse;
    }

    private UpdateLocationResponse getNearbyUsers(String userId, String latitude, String longitude) throws Exception {
        UpdateLocationResponse locationResponse = infixdClient.updateLocation(userId, latitude, longitude);
        if (locationResponse == null) {
            throw new Exception("Get Nearby Users request failed");
        }
        return locationResponse;
    }
}
