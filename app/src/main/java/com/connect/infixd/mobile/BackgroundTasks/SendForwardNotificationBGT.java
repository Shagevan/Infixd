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

public class SendForwardNotificationBGT extends AsyncTask<String, Void, IntroNotifyResponse> {

    private Context ctx;
    private Activity activity;;
    private String senderId;
    private String receiverId;
    private String subjectId;

    public SendForwardNotificationBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected IntroNotifyResponse doInBackground(String... params) {

        senderId = params[0];
        subjectId = params[1];
        receiverId = params[2];

        InfixdClient clientApi = new InfixdClient();
        IntroNotifyResponse introNotifyResponse = clientApi.sendForwardNotification(senderId,subjectId,receiverId);
        return introNotifyResponse;
    }

    @Override
    protected void onPostExecute(IntroNotifyResponse introNotifyResponse) {
    }

}

