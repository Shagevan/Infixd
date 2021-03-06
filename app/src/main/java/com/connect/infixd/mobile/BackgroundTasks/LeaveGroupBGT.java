/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetUserGroupsResponse;

public class LeaveGroupBGT extends AsyncTask<String, String, GetUserGroupsResponse> {
    private Context ctx;
    private Activity activity;
    private String userId;
    private String groupName;

    public LeaveGroupBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected GetUserGroupsResponse doInBackground(String... strings) {
        groupName = strings[0];
        userId = strings[1];
        InfixdClient infixdClient = new InfixdClient();
        GetUserGroupsResponse response = infixdClient.leaveGroup(groupName,userId);
        return response;
    }

}

