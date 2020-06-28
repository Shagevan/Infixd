/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.UpdateGroupInfoResponse;

public class UpdateGroupInfoBGT extends AsyncTask<String, String, UpdateGroupInfoResponse> {

    private Context ctx;
    private Activity activity;
    private String userId;
    private String groupName;
    private String newgroupName;
    private String groupDesc ;
    private String externalUrl;
    private String groupPicUrl;

    public UpdateGroupInfoBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }
    @Override
    protected UpdateGroupInfoResponse doInBackground(String... strings) {
        userId = strings[0];
        groupName = strings[1];
        newgroupName = strings[2];
        groupDesc = strings[3];
        externalUrl = strings[4];
        groupPicUrl = strings[5];
        InfixdClient infixdClient = new InfixdClient();
        UpdateGroupInfoResponse response = infixdClient.updateGroupInfo(userId,groupName,
                newgroupName,groupDesc,externalUrl,groupPicUrl);

        return response;
    }

    @Override
    protected void onPostExecute(UpdateGroupInfoResponse response) {

    }

}


