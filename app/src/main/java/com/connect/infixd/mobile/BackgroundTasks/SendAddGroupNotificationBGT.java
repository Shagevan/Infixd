/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.AddGroupResponse;

public class SendAddGroupNotificationBGT extends AsyncTask<String, String, AddGroupResponse> {
    private Context ctx;
    private Activity activity;
    private String userId;
    private String groupName;

    public SendAddGroupNotificationBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected AddGroupResponse doInBackground(String... strings) {
        userId = strings[0];
        groupName = strings[1];
        InfixdClient infixdClient = new InfixdClient();
        AddGroupResponse response = infixdClient.sendAddGroupNotification(userId,groupName);
        return response;
    }

}