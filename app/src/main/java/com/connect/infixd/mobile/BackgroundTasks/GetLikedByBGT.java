/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetLikedByUsersResponse;

import java.util.ArrayList;

public class GetLikedByBGT extends AsyncTask<String, Void, GetLikedByUsersResponse> {
    private Context ctx;
    private Activity activity;
    public ArrayList<String> userIds = new ArrayList<String>();
    public ArrayList<String> names = new ArrayList<String>();
    public ArrayList<String> profilePicUrls = new ArrayList<String>();

    public GetLikedByBGT(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected GetLikedByUsersResponse doInBackground(String... params) {
        String postId = params[0];
        InfixdClient clientApi = new InfixdClient();
        GetLikedByUsersResponse response = clientApi.getLikedByUsers(postId);
        return response;
    }

}
