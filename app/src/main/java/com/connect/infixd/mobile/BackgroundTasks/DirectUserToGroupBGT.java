/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetGroupInfoResponse;

public class DirectUserToGroupBGT extends AsyncTask<String, String, GetGroupInfoResponse> {

    private Context ctx;
    private Activity activity;
    private ProgressDialog progressDialog;
    private String userId;
    private String groupName;

    public DirectUserToGroupBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("please wait");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected GetGroupInfoResponse doInBackground(String... strings) {
        userId = strings[0];
        groupName = strings[1];
        InfixdClient infixdClient = new InfixdClient();
        GetGroupInfoResponse response = infixdClient.getUserPositionInGroup(userId,groupName);
        return response;
    }

    @Override
    protected void onPostExecute(GetGroupInfoResponse response) {
        progressDialog.dismiss();

    }

}

