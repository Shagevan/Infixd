package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.UpdateLocationResponse;

public class HideLocationBGT extends AsyncTask<String, Void, UpdateLocationResponse> {

    private Context ctx;
    private Activity activity;
    private ProgressDialog progressDialog;
    private String userId;

    public HideLocationBGT(Context ctx)
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
        userId = params[0];
        InfixdClient clientApi = new InfixdClient();
        UpdateLocationResponse locationResponse = clientApi.hideLocation(userId);
        return locationResponse;
    }

    @Override
    protected void onPostExecute(UpdateLocationResponse locationResponse) {
        progressDialog.dismiss();
    }
}
