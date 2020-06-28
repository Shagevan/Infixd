/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.connect.infixd.mobile.Interfaces.GetMembersInfo;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetGroupMemberInfoResponse;

public class GetMembersInfoBGT extends AsyncTask<String, String, GetGroupMemberInfoResponse> {

    private Context ctx;
    private Activity activity;
    private String userId;
    private String groupName;
    private GetMembersInfo getMembersInfo;
    private ProgressDialog progressDialog;

    public GetMembersInfoBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("Loading .... ");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected GetGroupMemberInfoResponse doInBackground(String... strings) {
        userId = strings[0];
        groupName = strings[1];
        InfixdClient infixdClient = new InfixdClient();
        GetGroupMemberInfoResponse response = infixdClient.getGroupMembersInfo(userId,groupName);
        return response;
    }

    @Override
    protected void onPostExecute(GetGroupMemberInfoResponse response) {
        progressDialog.dismiss();
        getMembersInfo = (GetMembersInfo) ctx;
        getMembersInfo.getMembersInfo(response);
    }

}
