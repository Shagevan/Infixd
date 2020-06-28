/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.SharePostResponse;


public class SharePostBGT extends AsyncTask<String, Void, SharePostResponse> {
    private Context ctx;
    private Activity activity;

    public SharePostBGT(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected SharePostResponse doInBackground(String... params) {
        String userId = params[0];
        String postId = params[1];

        long timeStamp = System.currentTimeMillis();

        InfixdClient clientApi = new InfixdClient();
        SharePostResponse sharePostResponse = clientApi.sharePost(userId, postId,String.valueOf(timeStamp));
        return sharePostResponse;
    }

}
