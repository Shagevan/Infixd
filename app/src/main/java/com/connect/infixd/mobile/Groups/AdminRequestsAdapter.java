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

import com.connect.infixd.mobile.BackgroundTasks.SubscribeToGroupBGT;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.DBHelper.NotificationDBHelper;
import com.connect.infixd.mobile.DBModels.AddGroupNotification;

import java.util.ArrayList;

public class AdminRequestsAdapter extends RecyclerView.Adapter<AdminRequestsAdapter.ViewHolder> {

    private ArrayList<AddGroupNotification> notificationData = new ArrayList<>();
    private Context ctx;
    private String userId;
    private String groupName;

    public AdminRequestsAdapter(Context ctx, String userId, String groupName, ArrayList<AddGroupNotification> notificationData) {
        this.ctx = ctx;
        this.userId = userId;
        this.groupName = groupName;
        this.notificationData = notificationData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userNameTV;
        public ImageView userProfPicIV;
        public ImageView acceptBtn;
        public ImageView rejectBtn;

        public ViewHolder(View v) {
            super(v);
            userNameTV = (TextView) v.findViewById(R.id.adminRequestUserNameTV);
            acceptBtn = (ImageView) v.findViewById(R.id.adminReuestAcceptBtn);
            rejectBtn = (ImageView) v.findViewById(R.id.adminReuestRejectBtn);
            userProfPicIV = (ImageView) v.findViewById(R.id.adninRequestUserPicIV);
        }
    }

    @Override
    public AdminRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_request_row, parent, false);
        AdminRequestsAdapter.ViewHolder vh = new AdminRequestsAdapter.ViewHolder(v);
        return  vh;
    }

    @Override
    public void onBindViewHolder(AdminRequestsAdapter.ViewHolder holder, int position) {
        holder.userNameTV.setText(notificationData.get(position).getSenderName());
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationDBHelper db = new NotificationDBHelper(ctx);
                db.updateAddGroupNotificationStatus(notificationData.get(position).get_id(), "RESPONDED");
                notificationData.remove(position);
                notifyDataSetChanged();
                SubscribeToGroupBGT obj = new SubscribeToGroupBGT(ctx);
                obj.execute(userId,groupName);
            }
        });
        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationDBHelper db = new NotificationDBHelper(ctx);
                db.updateAddGroupNotificationStatus(notificationData.get(position).get_id(), "RESPONDED");
                notificationData.remove(position);
                notifyDataSetChanged();


            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationData.size();
    }


}
