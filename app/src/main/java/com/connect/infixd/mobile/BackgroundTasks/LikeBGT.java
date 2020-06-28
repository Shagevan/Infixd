/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.LikePostResponse;

public class LikeBGT extends AsyncTask<String, Void, LikePostResponse> {
    private Context ctx;
    private Activity activity;

    public LikeBGT(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected LikePostResponse doInBackground(String... params) {
        String userId = params[0];
        String postId = params[1];

        InfixdClient clientApi = new InfixdClient();
        LikePostResponse response = clientApi.likePost(userId, postId);
        return response;
    }

    @Override
    protected void onPostExecute(LikePostResponse response) {
        String noOfLikes = response.getNoOfLikes();

    }
}
