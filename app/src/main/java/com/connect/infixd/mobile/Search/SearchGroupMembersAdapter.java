/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Search;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.BackgroundTasks.AddRemoveAdminBGT;
import com.infixd.client.model.GroupMemberResponse;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupMembersAdapter extends RecyclerView.Adapter<SearchGroupMembersAdapter.ViewHolder> {
    private List<GroupMemberResponse> membersInfo;
    private List<GroupMemberResponse> filteredResults;
    private Context ctx;
    private Activity activity;
    private String groupName;

    public SearchGroupMembersAdapter(Context context, List<GroupMemberResponse> membersInfo, String groupName) {
        this.membersInfo = membersInfo;
        this.groupName = groupName;
        this.ctx = context;
        this.filteredResults = new ArrayList<>();
        this.filteredResults.addAll(this.membersInfo);
        activity = (Activity) ctx;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userName;
        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.csContactName);
        }
    }

    @Override
    public SearchGroupMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_search_row, parent, false);
        SearchGroupMembersAdapter.ViewHolder vh = new SearchGroupMembersAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SearchGroupMembersAdapter.ViewHolder holder, int position) {
        final String userName = filteredResults.get(position).getFirstName();
        holder.userName.setText(userName);
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddRemoveAdminBGT obj = new AddRemoveAdminBGT(ctx);
                obj.execute(filteredResults.get(position).getUserId(),groupName,"add");

            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredResults.size();
    }

    public void filterSearchResults(String query){

        // DO SEARCH ON SEPARATE THREAD
        new Thread(new Runnable() {
            @Override
            public void run() {

                filteredResults.clear();

                if(TextUtils.isEmpty(query)){
                    filteredResults.addAll(membersInfo);
                }
                else{
                    for (GroupMemberResponse row: membersInfo) {
                        if(row.getFirstName().toLowerCase().contains(query.toLowerCase())){
                            filteredResults.add(row);
                        }
                    }
                }

                // Set on UI Thread
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }

}


