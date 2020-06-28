/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.springframework.util.StringUtils;

public class InfixdFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "InfixFCMIDService";

    @Override
    public void onTokenRefresh() {
        String infixdToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + infixdToken);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("infixdToken", 0);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString("infixdToken", infixdToken);
        edt.commit();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userName = prefs.getString(InfixdApp.STRING_PREFERENCE_USER_ID, "");

        if (!StringUtils.isEmpty(userName)) {
            updateToken(getApplicationContext(), userName);
        }

    }

    public static void updateToken(Context ctx, String userId) {
        UpdateTokenTask updateTokenTask = new UpdateTokenTask();
        SharedPreferences prefe = ctx.getSharedPreferences("infixdToken", 0);
        String infixdToken = prefe.getString("infixdToken", "");
        if(!StringUtils.isEmpty(infixdToken)){
            updateTokenTask.execute(userId,infixdToken);
        }
    }
}
