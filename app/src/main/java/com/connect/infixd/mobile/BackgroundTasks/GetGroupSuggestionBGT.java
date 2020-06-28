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

public class GetGroupSuggestionBGT extends AsyncTask<String, Void, List<Suggestions>> {
    private Context ctx;
    private Activity activity;
    public GetGroupSuggestionBGT(Context ctx)
    {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected List<Suggestions> doInBackground(String... params) {
        String termVal = params[0];
        String noOfSuggestionVal = params[1];
        InfixdClient infixdClient = new InfixdClient();
        AutoCompleteResponse autoCompleteResponse= infixdClient.getGroupSuggestions(termVal, Integer.parseInt(noOfSuggestionVal));
        List<Suggestions> results = autoCompleteResponse.getSuggestions();
        return results;
    }

}
