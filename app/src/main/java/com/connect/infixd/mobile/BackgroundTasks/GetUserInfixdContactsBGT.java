/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.connect.infixd.mobile.Interfaces.GetFriendsDetails;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetContactsResponse;
import com.infixd.client.model.UserResponse;

import java.util.List;

public class GetUserInfixdContactsBGT extends AsyncTask<String, String, List<UserResponse>> {

    private Context ctx;
    private Activity activity;
    private String userName;
    private GetFriendsDetails getfrnds;

    public GetUserInfixdContactsBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected List<UserResponse> doInBackground(String... strings) {
        userName = strings[0];
        InfixdClient infixdClient = new InfixdClient();
        try {
            GetContactsResponse getContactsResponse = infixdClient.getContacts(userName);
            List<UserResponse> userResponses = getContactsResponse.getFriends();
            return userResponses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<UserResponse> userResponses) {
        getfrnds = (GetFriendsDetails) ctx;
        getfrnds.getFriendsDetails(userResponses);
    }

}
