/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Chat;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.POJOModels.RecentChat;
import com.connect.infixd.mobile.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatAdapter.ViewHolder> {

    ArrayList<RecentChat> recentChats = new ArrayList<RecentChat>();
    StorageReference mStorageRef;

    public RecentChatAdapter(ArrayList<RecentChat> recentChats){
        this.recentChats = recentChats;
    }

    public void removeItem(int position) {
        recentChats.remove(position);
        notifyItemRangeChanged(position, recentChats.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView recentChatTV;
        public TextView unreadMsgTV;
        public ImageView recent_chat_friend_IV;
        public ConstraintLayout recentChatView;

        public ViewHolder(View v) {
            super(v);
            recentChatTV = (TextView) v.findViewById(R.id.recent_contact_name_tv);
            unreadMsgTV = (TextView) v.findViewById(R.id.recent_chat_unread_msg_tv);
            recent_chat_friend_IV = (ImageView) v.findViewById(R.id.recent_chat_friend_IV);
            recentChatView = (ConstraintLayout) v.findViewById(R.id.recent_chat_rec_view);
        }
    }

    @Override
    public RecentChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_row, parent, false);
        RecentChatAdapter.ViewHolder vh = new RecentChatAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecentChatAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return recentChats.size();
    }

}
