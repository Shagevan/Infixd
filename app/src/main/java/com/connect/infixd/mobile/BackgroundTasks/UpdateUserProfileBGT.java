/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.UpdateUserProfileWrapper;

public class UpdateUserProfileBGT extends AsyncTask<UpdateUserProfileWrapper, Void, String> {

    private Context ctx;
    private Activity activity;

    public UpdateUserProfileBGT(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected String doInBackground(UpdateUserProfileWrapper... params) {
        InfixdClient infixdClient = new InfixdClient();
        infixdClient.updateUserProfile(params[0]);
        return "user details are updated";
    }

    @Override
    protected void onPostExecute(String response) {

    }
}
