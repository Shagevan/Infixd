/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.connect.infixd.mobile.R;

public class InfixdBaseActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;

    public Context getBaseApplicationContext(){
        return  getApplication();
    }

    public void saveSharedPreference(String key, String value){
        editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void saveSharedPreference(String key, boolean value){
        editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getPreferenceValue(String key){
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(key,"");
    }

    public void inviteFriends(){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", getResources().getString(R.string.invite_friends_message));
        startActivity(sendIntent);
    }

}
