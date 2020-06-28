/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.UserRegisterResponse;
import com.infixd.client.model.UserResponse;

public class RegisterBGT extends AsyncTask<String, Void, UserRegisterResponse> {

    private Context ctx;
    private Activity activity;
    private String fName;
    private String lName;
    private String mobileNumber;

    public RegisterBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected UserRegisterResponse doInBackground(String... params) {

        fName = params[0];
        lName = params[1];
        mobileNumber = params[2];

        UserResponse user = new UserResponse();
        user.setFirstName(fName);
        user.setLastName(lName);
        user.setMobileNumber(mobileNumber);

        InfixdClient clientApi = new InfixdClient();
        UserRegisterResponse userRegisterResponse = clientApi.registerUser(user);
        return userRegisterResponse;
    }

    @Override
    protected void onPostExecute(UserRegisterResponse userRegisterResponse) {

    }

}
