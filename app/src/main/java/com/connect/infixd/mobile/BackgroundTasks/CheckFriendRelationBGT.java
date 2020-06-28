/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.CheckRelationshipResponse;

public class CheckFriendRelationBGT extends AsyncTask<String, Void, CheckRelationshipResponse> {

    private Context ctx;
    private Activity activity;
    private String userId;
    private String contactId;

    public CheckFriendRelationBGT(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected CheckRelationshipResponse doInBackground(String... params) {
        userId = params[0];
        contactId = params[1];
        InfixdClient clientApi = new InfixdClient();
        CheckRelationshipResponse checkRelationshipResponse = clientApi.checkFriendOfRelationship(userId,contactId);
        return checkRelationshipResponse;
    }

    @Override
    protected void onPostExecute(CheckRelationshipResponse checkRelationshipResponse) {
        if(checkRelationshipResponse.isRelationship()){
            GetUserDetailsBGT getUserDetailsBGT = new GetUserDetailsBGT(ctx);
            getUserDetailsBGT.execute("userProfileFriend", contactId);
        }
        else {
            GetUserDetailsBGT getUserDetailsBGT = new GetUserDetailsBGT(ctx);
            getUserDetailsBGT.execute("userProfileNotFriend", contactId);
        }
    }

}
