/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.connect.infixd.mobile.Wrappers.GetGroupInfoWrapper;
import com.connect.infixd.mobile.Groups.GroupAdminActivity;
import com.connect.infixd.mobile.Groups.SubscribedMemberActivity;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetGroupInfoResponse;

import java.io.Serializable;

public class GetGroupInfoBGT extends AsyncTask<String, Void, GetGroupInfoResponse> {

    private Context ctx;
    private Activity activity;
    private String userId;
    private String groupName;
    private String userPosition;
    private ProgressDialog progressDialog;

    public GetGroupInfoBGT(Context ctx) {
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
    protected GetGroupInfoResponse doInBackground(String... params) {

        userId = params[0];
        groupName = params[1];
        userPosition = params[2];
        InfixdClient clientApi = new InfixdClient();
        GetGroupInfoResponse response = clientApi.getGroupInfo(userId,groupName,userPosition);
        return response;
    }

    @Override
    protected void onPostExecute(GetGroupInfoResponse response) {
        progressDialog.hide();
        if(userPosition.equals("Admin")){
            Intent intent = new Intent(ctx, GroupAdminActivity.class);
            GetGroupInfoWrapper object = new GetGroupInfoWrapper();
            object.setResponse(response);
            intent.putExtra("response", (Serializable) object);
            ctx.startActivity(intent);
        }
        else{
            Intent intent = new Intent(ctx, SubscribedMemberActivity.class);
            GetGroupInfoWrapper object = new GetGroupInfoWrapper();
            object.setResponse(response);
            intent.putExtra("response", (Serializable) object);
            ctx.startActivity(intent);
        }
    }
}

