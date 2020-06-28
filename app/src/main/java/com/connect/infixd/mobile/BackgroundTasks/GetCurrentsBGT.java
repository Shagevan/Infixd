/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetAllPostsResponse;

import java.util.ArrayList;

public class GetCurrentsBGT extends AsyncTask<String, Void, GetAllPostsResponse> {
    private Context ctx;
    private Activity activity;
    public ArrayList<String> postIds = new ArrayList<String>();
    public ArrayList<String> captions = new ArrayList<String>();
    public ArrayList<String> imageUris = new ArrayList<String>();
    public ArrayList<String> textBackgroudColors = new ArrayList<String>();
    public ArrayList<String> dates = new ArrayList<String>();
    public ArrayList<String> times = new ArrayList<String>();
    public ArrayList<String> noOfLikes = new ArrayList<String>();
    public ArrayList<String> noOfShares = new ArrayList<String>();

    public GetCurrentsBGT(Context ctx) {
            this.ctx = ctx;
            activity = (Activity) ctx;
        }

        @Override
        protected GetAllPostsResponse doInBackground(String... params) {
            String userId = params[0];

            InfixdClient clientApi = new InfixdClient();
            GetAllPostsResponse getAllPostsResponse = clientApi.getAllCurrents(userId);
            return getAllPostsResponse;
        }

}
