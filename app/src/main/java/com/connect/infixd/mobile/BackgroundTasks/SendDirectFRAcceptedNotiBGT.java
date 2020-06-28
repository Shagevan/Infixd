/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.IntroNotifyResponse;

public class SendDirectFRAcceptedNotiBGT extends AsyncTask<String, Void, IntroNotifyResponse> {

    private Context ctx;
    private Activity activity;
    private String senderId;
    private String receiverId;

    public SendDirectFRAcceptedNotiBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected IntroNotifyResponse doInBackground(String... params) {
        senderId = params[0];
        receiverId = params[1];
        InfixdClient clientApi = new InfixdClient();
        IntroNotifyResponse introNotifyResponse = clientApi.sendDirectFriendRequestAcceptedNotification(senderId,receiverId);
        return introNotifyResponse;
    }

}


