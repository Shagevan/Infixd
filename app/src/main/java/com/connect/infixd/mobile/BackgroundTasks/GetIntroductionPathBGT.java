/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.connect.infixd.mobile.Introduction.IntroductionPathActivity;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.IntroductionPathsResponse;

public class GetIntroductionPathBGT extends AsyncTask<String, Void, IntroductionPathsResponse> {

    private Context ctx;
    private Activity activity;
    private String sourceId;
    private String targetId;
    private String fullName;
    private Integer expectedPaths;

    public GetIntroductionPathBGT(Context ctx) {
        this.ctx = ctx;
        //this.activity = (Activity) ctx;
    }

    @Override
    protected IntroductionPathsResponse doInBackground(String... params) {
        sourceId = params[0];
        targetId = params[1];
        fullName = params[2];
        expectedPaths = params.length == 4 ? Integer.parseInt(params[3]) : 5;
        InfixdClient clientApi = new InfixdClient();
        IntroductionPathsResponse response = clientApi.getIntroductionPaths(sourceId, targetId, expectedPaths);
        return response;
    }

    @Override
    protected void onPostExecute(IntroductionPathsResponse shortestPathResponse) {

        Intent i = new Intent(ctx, IntroductionPathActivity.class);
        i.putExtra("introductionPathSet", shortestPathResponse);
        i.putExtra("targetName",fullName);
        ctx.startActivity(i);
    }

}
