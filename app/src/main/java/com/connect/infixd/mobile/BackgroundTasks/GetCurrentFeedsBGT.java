package com.connect.infixd.mobile.BackgroundTasks;


import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.GetCurrentFeedsResponse;

public class GetCurrentFeedsBGT extends AsyncTask<String, Void, GetCurrentFeedsResponse> {
    private Context ctx;

    public GetCurrentFeedsBGT(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected GetCurrentFeedsResponse doInBackground(String... params) {
        String userId = params[0];
        String pageNumber = params[1];
        InfixdClient clientApi = new InfixdClient();
        GetCurrentFeedsResponse addContactResponse = clientApi.getCurrentFeeds(userId, pageNumber);
        return addContactResponse;
    }

    @Override
    protected void onPostExecute(GetCurrentFeedsResponse getCurrentFeedsResponse) {

    }
}
