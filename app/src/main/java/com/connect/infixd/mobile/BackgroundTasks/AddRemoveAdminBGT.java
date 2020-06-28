/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.connect.infixd.mobile.Interfaces.AddRemoveGroupResult;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.AddAdminResponse;

public class AddRemoveAdminBGT extends AsyncTask<String, Void, AddAdminResponse> {
    private Context ctx;
    private Activity activity;
    private ProgressDialog progressDialog;
    private AddRemoveGroupResult addRemoveGroupResult;
    private String userId;
    private String groupName;
    private String code;
    private static final String ADD_ADMIN = "add";
    private static final String REMOVE_ADMIN = "remove";

    public AddRemoveAdminBGT(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle("Please Wait");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected AddAdminResponse doInBackground(String... params) {
        userId = params[0];
        groupName = params[1];
        code = params[2];
        InfixdClient clientApi = new InfixdClient();
        AddAdminResponse response = new AddAdminResponse();
        if(code.equals(ADD_ADMIN)){
            response = clientApi.addAdmin(userId,groupName);
        }else if(code.equals(REMOVE_ADMIN)){
            response = clientApi.removeAdmin(userId,groupName);
        }
        return response;
    }

    @Override
    protected void onPostExecute(AddAdminResponse response) {
        progressDialog.dismiss();
        addRemoveGroupResult = (AddRemoveGroupResult) ctx;
        addRemoveGroupResult.getAddRemoveGroupResult(response);
    }
}
