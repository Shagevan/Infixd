/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetPassedByUsersResponse;

import java.util.ArrayList;

public class GetPassedByBGT extends AsyncTask<String, Void, GetPassedByUsersResponse> {
    private Context ctx;
    private Activity activity;

    public ArrayList<String> userIds = new ArrayList<String>();
    public ArrayList<String> names = new ArrayList<String>();
    public ArrayList<String> profilePicUrls = new ArrayList<String>();

    public GetPassedByBGT(Context ctx) {
        this.ctx = ctx;
        this.activity = (Activity) ctx;
    }

    @Override
    protected GetPassedByUsersResponse doInBackground(String... params) {
        String postId = params[0];

        InfixdClient clientApi = new InfixdClient();
        GetPassedByUsersResponse response = clientApi.getPassedByUsers(postId);
        return response;
    }

}
