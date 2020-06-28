/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.IntroductionRequestResponse;

public class SendIntroductionRequestBGT extends AsyncTask<String, Void, IntroductionRequestResponse> {

    private Context ctx;
    private Activity activity;
    private String senderId;
    private String receiverId;
    private String targetId;

    public SendIntroductionRequestBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected IntroductionRequestResponse doInBackground(String... params) {
        senderId = params[0];
        receiverId = params[1];
        targetId = params[2];
        InfixdClient clientApi = new InfixdClient();
        IntroductionRequestResponse response = clientApi.sendIntroductionRequest(senderId,
                receiverId, targetId);
        return response;
    }

    @Override
    protected void onPostExecute(IntroductionRequestResponse introResponse) {
        Toast.makeText(ctx, "Introduction request sent to " + introResponse.getReceiverName() + "" + " asking " +
                "introduce you to " + introResponse.getTargetName() + ".", Toast.LENGTH_SHORT).show();
    }
}
