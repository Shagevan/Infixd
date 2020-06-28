/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.IntroNotifyResponse;

public class SendDirectFriendRequestByIDBGT extends AsyncTask<String, Void, IntroNotifyResponse> {

    private Context ctx;
    private Activity activity;
    private ProgressDialog progressDialog;
    private String senderId;
    private String targetId;

    public SendDirectFriendRequestByIDBGT(Context ctx)
    {
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
    protected IntroNotifyResponse doInBackground(String... params) {

        senderId = params[0];
        targetId = params[1];

        InfixdClient clientApi = new InfixdClient();
        IntroNotifyResponse introNotifyResponse = clientApi.sendDirectFriendRequestNotification(senderId, targetId);
        return introNotifyResponse;
    }

    @Override
    protected void onPostExecute(IntroNotifyResponse introNotifyResponse) {
        progressDialog.dismiss();
        Toast.makeText(ctx,"Friend Request sent",Toast.LENGTH_SHORT).show();
    }
}
