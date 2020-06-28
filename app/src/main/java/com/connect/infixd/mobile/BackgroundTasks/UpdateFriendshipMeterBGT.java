/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.connect.infixd.mobile.Interfaces.UpdateFriendShipMeterResult;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.UpdateFriendshipMeterResponse;

import java.util.HashMap;

public class UpdateFriendshipMeterBGT extends AsyncTask<String, Void, UpdateFriendshipMeterResponse> {

    private Context ctx;
    private Activity activity;
    private HashMap<String,String> count = new HashMap<String, String>();
    private UpdateFriendShipMeterResult updateFriendShipMeterResult;

    public UpdateFriendshipMeterBGT(Context ctx, HashMap<String,String> count)
    {
        this.ctx = ctx;
        this.count = count;
        activity = (Activity) ctx;
    }

    @Override
    protected UpdateFriendshipMeterResponse doInBackground(String... params) {
        String userName = params[0];
        InfixdClient clientApi = new InfixdClient();
        UpdateFriendshipMeterResponse updateFriendshipMeterResponse = clientApi.updateFriendshipMeter(userName,count);
        return updateFriendshipMeterResponse;
    }

    @Override
    protected void onPostExecute(UpdateFriendshipMeterResponse updateFriendshipMeterResponse) {
        updateFriendShipMeterResult = (UpdateFriendShipMeterResult) ctx;
        updateFriendShipMeterResult.getUpdateFriendshipMeterResult(updateFriendshipMeterResponse.getMessage());
    }

}

