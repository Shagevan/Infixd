/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Groups;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.R;
import com.infixd.client.model.GroupMemberResponse;

import java.util.ArrayList;
import java.util.List;

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {

    private List<GroupMemberResponse> membersInfo = new ArrayList<>();
    private Context ctx;

    public GroupMembersAdapter(Context ctx, List<GroupMemberResponse> membersInfo) {
        this.ctx = ctx;
        this.membersInfo = membersInfo;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView firstNameTv;
        public TextView rankTV;
        public ImageView userPicIV;

        public ViewHolder(View v) {
            super(v);
            firstNameTv = (TextView) v.findViewById(R.id.groupMemberNameTV);
            rankTV = (TextView) v.findViewById(R.id.groupMemberRankNumberTV);
            userPicIV = (ImageView) v.findViewById(R.id.groupMemberIV);
        }
    }

    @Override
    public GroupMembersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_member_row, parent, false);
        GroupMembersAdapter.ViewHolder vh = new GroupMembersAdapter.ViewHolder(v);
        return  vh;
    }

    @Override
    public void onBindViewHolder(GroupMembersAdapter.ViewHolder holder, int position) {
        final String firstName = membersInfo.get(position).getFirstName();
        final String rank = membersInfo.get(position).getRank();
        holder.firstNameTv.setText(firstName);
        holder.rankTV.setText(rank);
    }

    @Override
    public int getItemCount() {
        return membersInfo.size();
    }

}
