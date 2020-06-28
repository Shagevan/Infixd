/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.AutoCompleteResponse;
import com.infixd.client.model.Suggestions;

import java.util.List;

public class GetSuggestionBGT extends AsyncTask<String, Void, List<Suggestions>> {

    private Context ctx;
    private Activity activity;

    public GetSuggestionBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected List<Suggestions> doInBackground(String... params) {

        String userName = params[0];
        String termVal = params[1];
        String noOfSuggestionVal = params[2];

        InfixdClient infixdClient = new InfixdClient();
        AutoCompleteResponse autoCompleteResponse= infixdClient.getSuggestions(userName,termVal, Integer.parseInt(noOfSuggestionVal));
        List<Suggestions> results = autoCompleteResponse.getSuggestions();
        return  results;

    }

}


