/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
* Unauthorized copying of this file, via any medium is strictly prohibited
* Proprietary and confidential
 * */

package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.CreatePostResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewPostBGT extends AsyncTask<String, Void, CreatePostResponse> {
    private Context ctx;
    private Activity activity;

    public NewPostBGT(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected CreatePostResponse doInBackground(String... params) {
        String userId = params[0];
        String caption = params[1];
        String imageUri = params[2];
        String textBackgroundColor = params[3];

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(c.getTime());
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        String time = tf.format(c.getTime());
        long timeStamp = System.currentTimeMillis();

        InfixdClient clientApi = new InfixdClient();
        CreatePostResponse response = clientApi.createNewPost(userId, caption, imageUri, textBackgroundColor,
                time, date, String.valueOf(timeStamp));
        return response;
    }

}
