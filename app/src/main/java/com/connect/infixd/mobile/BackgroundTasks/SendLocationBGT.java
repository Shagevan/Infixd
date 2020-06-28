/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.connect.infixd.mobile.Interfaces.GetNearUsers;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.Location;
import com.infixd.client.model.UpdateLocationResponse;

import java.util.List;

public class SendLocationBGT extends AsyncTask<String, Void, UpdateLocationResponse> {
    private Context ctx;
    private Activity activity;
    private ProgressDialog progressDialog;
    private String senderId;
    private String latitude;
    private String longitude;
    private GetNearUsers getNearUsers;

    public SendLocationBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle("Please Wait");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected UpdateLocationResponse doInBackground(String... params) {

        senderId = params[0];
        latitude = params[1];
        longitude = params[2];

        InfixdClient clientApi = new InfixdClient();
        UpdateLocationResponse locationResponse = clientApi.updateLocation(senderId, latitude, longitude);
        return locationResponse;
    }

    @Override
    protected void onPostExecute(UpdateLocationResponse locationResponse) {
        progressDialog.dismiss();
        List<Location> nearByUsers = locationResponse.getLocations();
        getNearUsers = (GetNearUsers) ctx;
        getNearUsers.getUserDetails(nearByUsers);
    }

}
