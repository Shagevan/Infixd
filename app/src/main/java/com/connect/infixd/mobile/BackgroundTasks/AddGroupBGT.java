/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.connect.infixd.mobile.Interfaces.AddGroupResult;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.GroupRegisterResponse;

public class AddGroupBGT extends AsyncTask <String, Void, GroupRegisterResponse> {
    private Context ctx;
    private Activity activity;
    private ProgressDialog progressDialog;
    private AddGroupResult addGroupResult;

    public AddGroupBGT(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Adding the group.....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected GroupRegisterResponse doInBackground(String... params) {

        String userId = params[0];
        String groupName = params[1];
        String groupPhotoUri = params[2];
        String description = params[3];
        String websiteUrl = params[4];

        InfixdClient clientApi = new InfixdClient();
        GroupRegisterResponse groupRegisterResponse = clientApi.addGroup(groupName, userId, groupPhotoUri, description,
                websiteUrl, "","", "", null, null, "");

        return groupRegisterResponse;
    }

    @Override
    protected void onPostExecute(GroupRegisterResponse response) {
        progressDialog.dismiss();
        addGroupResult = (AddGroupResult) ctx;
        addGroupResult.getGroups(response.getGroups());
    }
}
