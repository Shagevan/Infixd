/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Currents;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.BackgroundTasks.CheckFriendRelationBGT;
import com.connect.infixd.mobile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReactedUsersAdapter extends RecyclerView.Adapter<ReactedUsersAdapter.ViewHolder> {

    private ArrayList<String> userIds = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> profilePicUrls = new ArrayList<String>();
    private Context ctx;
    private String userId;

    public ReactedUsersAdapter(Context context, ArrayList<String> userIds,
                               ArrayList<String> names, ArrayList<String> profilePicUrls, String userId) {
        this.userIds = userIds;
        this.names = names;
        this.profilePicUrls = profilePicUrls;
        this.ctx = context;
        this.userId = userId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView user_name_text_view;
        public ImageView profile_pic_image_view;

        public ViewHolder(View v) {
            super(v);
            user_name_text_view = (TextView) v.findViewById(R.id.contactUserName);
            profile_pic_image_view = (ImageView) v.findViewById(R.id.contactimageView);
            View reactedUserRow = v.findViewById(R.id.contactsrowID);
            reactedUserRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckFriendRelationBGT checkFriendRelationBGT = new CheckFriendRelationBGT(ctx);
                    checkFriendRelationBGT.execute(userId,userIds.get(getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public ReactedUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String userName = names.get(position);
        final String profilePicUrl = profilePicUrls.get(position);
        if(profilePicUrl != null && !profilePicUrl.isEmpty()){
            Picasso.with(ctx)
                    .load(profilePicUrl)
                    .into(holder.profile_pic_image_view);
        }
        holder.user_name_text_view.setText(userName);
    }

    @Override
    public int getItemCount() {
        return userIds.size();
    }
}
