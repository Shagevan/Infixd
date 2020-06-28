/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.connect.infixd.mobile.BackgroundTasks.DirectUserToGroupBGT;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Wrappers.GetGroupInfoWrapper;
import com.connect.infixd.mobile.Groups.GroupAdminActivity;
import com.connect.infixd.mobile.Groups.GroupPublicProfileActivity;
import com.connect.infixd.mobile.Groups.SubscribedMemberActivity;
import com.infixd.client.model.GetGroupInfoResponse;
import com.infixd.client.model.Suggestions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchGroupAdapter extends RecyclerView.Adapter<SearchGroupAdapter.ViewHolder> {
    private List<Suggestions> suggestions = new ArrayList<>();
    private Context ctx;
    private Activity activity;
    private String userId;

    public SearchGroupAdapter(Context context, String userId, List<Suggestions> suggestions) {
        this.suggestions = suggestions;
        this.userId = userId;
        this.ctx = context;
        activity = (Activity) ctx;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView groupName;
        public ViewHolder(View v) {
            super(v);
            groupName = (TextView) v.findViewById(R.id.csContactName);
        }
    }

    @Override
    public SearchGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_search_row, parent, false);
        SearchGroupAdapter.ViewHolder vh = new SearchGroupAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SearchGroupAdapter.ViewHolder holder, int position) {
        final String suggestion = suggestions.get(position).getName_suggestion();
        holder.groupName.setText(Html.fromHtml(suggestion));
        holder.groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // RUN BACKGROUND TASK AND OPEN SUITABLE GROUP PROFILE
                new DirectUserToGroupBGT(ctx){
                    @Override
                    protected void onPostExecute(GetGroupInfoResponse response) {
                        super.onPostExecute(response);
                        String position = response.getUserPosition();
                        if(position.equals("Admin")){

                            Intent intent = new Intent(ctx, GroupAdminActivity.class);
                            GetGroupInfoWrapper object = new GetGroupInfoWrapper();
                            object.setResponse(response);
                            intent.putExtra("response", (Serializable) object);
                            ctx.startActivity(intent);
                        }
                        else if(position.equals("Subscriber")){

                            Intent intent = new Intent(ctx, SubscribedMemberActivity.class);
                            GetGroupInfoWrapper object = new GetGroupInfoWrapper();
                            object.setResponse(response);
                            intent.putExtra("response", (Serializable) object);
                            ctx.startActivity(intent);
                        }
                        else {

                            Intent intent = new Intent(ctx, GroupPublicProfileActivity.class);
                            GetGroupInfoWrapper object = new GetGroupInfoWrapper();
                            object.setResponse(response);
                            intent.putExtra("response", (Serializable) object);
                            ctx.startActivity(intent);
                        }
                    }
                }.execute(userId,suggestions.get(position).getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

}


