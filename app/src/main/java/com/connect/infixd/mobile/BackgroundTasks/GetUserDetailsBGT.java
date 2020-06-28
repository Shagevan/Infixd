/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.connect.infixd.mobile.User.UserProfile;
import com.connect.infixd.mobile.User.UserProfileForFriends;
import com.connect.infixd.mobile.User.UserProfileForNotFriends;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.UserResponse;

public class GetUserDetailsBGT extends AsyncTask<String, Void, UserResponse> {

    private Context ctx;
    private Activity activity;
    private String userName;
    private String activityCheck;

    public GetUserDetailsBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected UserResponse doInBackground(String... params) {
        activityCheck = params[0];
        userName = params[1];
        InfixdClient clientApi = new InfixdClient();
        UserResponse userResponse = clientApi.getUserDetails(userName);
        return userResponse;
    }

    @Override
    protected void onPostExecute(UserResponse userResponse) {

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
        bundle.putStringArrayList("FB_PHOTOS", userResponse.getFbPhotos());

        if (activityCheck.equals("userProfile")){
            Intent intent = new Intent(ctx, UserProfile.class);
            intent.putExtras(bundle);
            ctx.startActivity(intent);
        }
        else if (activityCheck.equals("userProfileFriend")){
            Intent i = new Intent(ctx,UserProfileForFriends.class);
            i.putExtras(bundle);
            ctx.startActivity(i);
        }
        else if (activityCheck.equals("userProfileNotFriend")){
            Intent i = new Intent(ctx,UserProfileForNotFriends.class);
            i.putExtras(bundle);
            ctx.startActivity(i);
        }

    }

}
