/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.SignupProcess;

import android.content.Intent;
import android.os.Bundle;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.Services.InfixdFirebaseInstanceIDService;
import com.connect.infixd.mobile.UserHome.UserHomeActivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class LauncherActivity extends InfixdBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        firstTimeCheck();
    }

    public void firstTimeCheck(){
        Intent intent;
        String userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
        if (userId.isEmpty()) {
            intent = new Intent(this, RegisterPage.class);
        } else {
            InfixdFirebaseInstanceIDService.updateToken(getApplicationContext(), userId);
            intent = new Intent(this, UserHomeActivity.class);
            intent.putExtra("userName", userId);
        }
        startActivity(intent);
        this.finish();
    }
}
