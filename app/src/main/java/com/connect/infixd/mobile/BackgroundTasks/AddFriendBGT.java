/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.AddContactResponse;

public class AddFriendBGT extends AsyncTask<String, Void, AddContactResponse> {
    private Context ctx;
    private Activity activity;

    public AddFriendBGT(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected AddContactResponse doInBackground(String... params) {
        String userId = params[0];
        String targetId = params[1];
        InfixdClient clientApi = new InfixdClient();
        AddContactResponse addContactResponse = clientApi.addContact(userId, targetId);
        clientApi.sendDirectFriendRequestAcceptedNotification(userId,targetId);
        return addContactResponse;
    }

    @Override
    protected void onPostExecute(AddContactResponse addContactResponse) {

    }
}


