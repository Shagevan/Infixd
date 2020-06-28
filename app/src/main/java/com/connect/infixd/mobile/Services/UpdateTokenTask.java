/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
*/

package com.connect.infixd.mobile.Services;

import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.UpdateMobileResponse;

public class UpdateTokenTask extends AsyncTask<String, Void, UpdateMobileResponse> {

    @Override
    protected UpdateMobileResponse doInBackground(String... params) {
        String userId = params[0];
        String infixdToken = params[1];
        InfixdClient clientApi = new InfixdClient();
        try {
            return clientApi.updateDeviceToken(userId, infixdToken);
        } catch (Exception e) {
            //TODO: properly handlet his
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(UpdateMobileResponse updateMobileResponse) {
        super.onPostExecute(updateMobileResponse);
    }
}
