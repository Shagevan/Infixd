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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.connect.infixd.mobile.BackgroundTasks.GetGroupInfoBGT;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.DBModels.Group;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private ArrayList<Group> groups = new ArrayList<Group>();
    private String userId;
    private Context ctx;
    private StorageReference mStorageRef;

    public GroupsAdapter(Context ctx, String userId, ArrayList<Group> groups) {
        this.ctx = ctx;
        this.groups = groups;
        this.userId = userId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView groupName;
        public TextView noOfGroupMembersTV;
        public TextView adminTV;
        public View groupRow;
        public ImageView groupProfPicIV;

        public ViewHolder(View v) {
            super(v);
            groupName = (TextView) v.findViewById(R.id.groupName);
            noOfGroupMembersTV = (TextView) v.findViewById(R.id.noOfGroupMembers);
            adminTV = (TextView) v.findViewById(R.id.groupAdminTV);
            groupProfPicIV = (ImageView) v.findViewById(R.id.groupsImageView);
            groupRow = v.findViewById(R.id.groupRow);
        }
    }

    @Override
    public GroupsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return  vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.groupName.setText(groups.get(position).getGroup_name());
        holder.noOfGroupMembersTV.setText("Members : " + groups.get(position).getNoOfMembers());
        holder.adminTV.setVisibility(View.GONE);

        if(groups.get(position).getGroup_photo_url() != null){
            if(!groups.get(position).getGroup_photo_url().isEmpty() ){
                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(groups.get(position).getGroup_photo_url());
                // Load the Profile Picture image using Glide
                Glide.with(ctx)
                        .using(new FirebaseImageLoader())
                        .load(mStorageRef)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(holder.groupProfPicIV);
            }
        }

        if(groups.get(position).getUserPosition().equals("Admin")){
            holder.adminTV.setVisibility(View.VISIBLE);
        }
        holder.groupRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetGroupInfoBGT obj = new GetGroupInfoBGT(ctx);
                obj.execute(userId,groups.get(position).getGroup_name(),groups.get(position).getUserPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }


}
