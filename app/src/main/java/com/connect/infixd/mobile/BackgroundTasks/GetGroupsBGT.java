/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.connect.infixd.mobile.Interfaces.GetGroups;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetUserGroupsResponse;

public class GetGroupsBGT extends AsyncTask<String, String, GetUserGroupsResponse> {
    private Context ctx;
    private Activity activity;
    private String userId;
    private GetGroups getGroups;
    public GetGroupsBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected GetUserGroupsResponse doInBackground(String... strings) {
        userId = strings[0];
        InfixdClient infixdClient = new InfixdClient();
        GetUserGroupsResponse response = infixdClient.getUserGroups(userId);
        return response;
    }

    @Override
    protected void onPostExecute(GetUserGroupsResponse response) {
        getGroups = (GetGroups) ctx;
        getGroups.getGroups(response);
    }

}
