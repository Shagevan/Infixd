/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Requests;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.POJOModels.NotificationRowData;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.UserHome.UserHomeActivity;
import com.connect.infixd.mobile.intentservices.FriendIntentService;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationFragmentAdapter extends RecyclerSwipeAdapter<NotificationFragmentAdapter.ViewHolder> {

    private ArrayList<NotificationRowData> notifications = new ArrayList<NotificationRowData>();
    private Context ctx;
    public static final String REQUESTING_DF = "REQUESTING_DF";
    public static final String REQUESTING_FI = "REQUESTING_FI";
    public static final String REQUESTING_INTRO = "REQUESTING_INTRO";
    private UserHomeActivity userHomeActivity;

    public NotificationFragmentAdapter(Context context, ArrayList<NotificationRowData> notifications) {
        this.notifications = notifications;
        this.ctx = context;
        this.userHomeActivity = (UserHomeActivity) context;
    }

    public void removeItem(int position) {
        notifications.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, notifications.size());
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public SwipeLayout swipeLayout;
        public TextView tvAccept;
        public TextView tvReject;

        public TextView senderNameTV;
        public TextView targetNameTV;
        public ImageView intro_in_between_IV;
        public ImageView senderIV;
        public ImageView targetIV;

        public ViewHolder(View v) {
            super(v);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvAccept = (TextView) itemView.findViewById(R.id.tvAccept);
            tvReject = (TextView) itemView.findViewById(R.id.tvReject);

            senderNameTV = (TextView) v.findViewById(R.id.sender_user_tv);
            targetNameTV = (TextView) v.findViewById(R.id.target_user_tv);
            intro_in_between_IV = (ImageView) v.findViewById(R.id.intro_inbetwen_IV);
            senderIV = (ImageView) v.findViewById(R.id.sender_user_iv);
            targetIV = (ImageView) v.findViewById(R.id.target_user_iv);

            String userId = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
            String userName = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_FIRST_NAME);
            String userProfilePicUrl = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL);

            tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String type = notifications.get(position).getType();
                    switch (type){
                        case REQUESTING_DF:
                            Intent drIntent = new Intent(ctx, FriendIntentService.class);
                            drIntent.setAction(FriendIntentService.ACTION_ACCEPT_FRIEND_REQUEST);
                            drIntent.putExtra(FriendIntentService.DATA_SENDER_ID, userId);
                            drIntent.putExtra(FriendIntentService.DATA_TARGET_ID, notifications.get(position).getSenderId());
                            ctx.startService(drIntent);
                            break;
                        case REQUESTING_INTRO:
                            Intent irIntent = new Intent(ctx, FriendIntentService.class);
                            irIntent.setAction(FriendIntentService.ACTION_SEND_FORWARD_INTRODUCTION_REQUEST);
                            irIntent.putExtra(FriendIntentService.DATA_SENDER_ID, userId);
                            irIntent.putExtra(FriendIntentService.DATA_TARGET_ID, notifications.get(position).getSenderId());
                            irIntent.putExtra(FriendIntentService.DATA_RECEIVER_ID, notifications.get(position).getTargetId());
                            ctx.startService(irIntent);
                            break;
                        case REQUESTING_FI:
                            Intent firIntent = new Intent(ctx, FriendIntentService.class);
                            firIntent.setAction(FriendIntentService.ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST);
                            firIntent.putExtra(FriendIntentService.DATA_SENDER_ID, userId);
                            firIntent.putExtra(FriendIntentService.DATA_SENDER_NAME, userName);
                            firIntent.putExtra(FriendIntentService.DATA_SENDER_PROF_PIC_URL, userProfilePicUrl);
                            firIntent.putExtra(FriendIntentService.DATA_RECEIVER_ID, notifications.get(position).getSenderId());
                            firIntent.putExtra(FriendIntentService.DATA_RECEIVER_NAME, notifications.get(position).getSenderName());
                            firIntent.putExtra(FriendIntentService.DATA_RECEIVER_PROF_PIC_URL, notifications.get(position).getSenderProfPicURL());
                            firIntent.putExtra(FriendIntentService.DATA_TARGET_ID, notifications.get(position).getTargetId());
                            firIntent.putExtra(FriendIntentService.DATA_TARGET_NAME, notifications.get(position).getTargetName());
                            firIntent.putExtra(FriendIntentService.DATA_TARGET_PROF_PIC_URL, notifications.get(position).getTargetProfPicURL());
                            ctx.startService(firIntent);
                            break;
                    }
                    removeItem(position);
                }
            });

            tvReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String type = notifications.get(position).getType();
                    switch (type){
                        case REQUESTING_DF:
                            Intent drIntent = new Intent(ctx, FriendIntentService.class);
                            drIntent.setAction(FriendIntentService.ACTION_REJECT_FRIEND_REQUEST);
                            drIntent.putExtra(FriendIntentService.DATA_SENDER_ID, userId);
                            drIntent.putExtra(FriendIntentService.DATA_TARGET_ID, notifications.get(position).getSenderId());
                            ctx.startService(drIntent);
                            break;
                        case REQUESTING_INTRO:
                            Intent irIntent = new Intent(ctx, FriendIntentService.class);
                            irIntent.setAction(FriendIntentService.ACTION_REJECT_INTRODUCTION_REQUEST);
                            irIntent.putExtra(FriendIntentService.DATA_SENDER_ID, userId);
                            irIntent.putExtra(FriendIntentService.DATA_TARGET_ID, notifications.get(position).getSenderId());
                            irIntent.putExtra(FriendIntentService.DATA_RECEIVER_ID, notifications.get(position).getTargetId());
                            ctx.startService(irIntent);
                            break;
                        case REQUESTING_FI:
                            Intent firIntent = new Intent(ctx, FriendIntentService.class);
                            firIntent.setAction(FriendIntentService.ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST);
                            firIntent.putExtra(FriendIntentService.DATA_SENDER_ID, userId);
                            firIntent.putExtra(FriendIntentService.DATA_RECEIVER_ID, notifications.get(position).getSenderId());
                            firIntent.putExtra(FriendIntentService.DATA_TARGET_ID, notifications.get(position).getTargetId());
                            ctx.startService(firIntent);
                            break;
                    }
                    removeItem(position);
                }
            });
        }
    }


    @Override
    public NotificationFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.introduction_request_notification_row, parent, false);
        NotificationFragmentAdapter.ViewHolder vh = new NotificationFragmentAdapter.ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(NotificationFragmentAdapter.ViewHolder holder, int position) {

        switch (notifications.get(position).getType()){
            case REQUESTING_DF:
                holder.intro_in_between_IV.setImageResource(R.drawable.nav_direct_request_icon);
                break;
            case REQUESTING_INTRO:
                holder.intro_in_between_IV.setImageResource(R.drawable.nav_introduction_icon);
                break;
            case REQUESTING_FI:
                holder.intro_in_between_IV.setImageResource(R.drawable.nav_introduction_request_icon);
                break;
        }

        holder.senderNameTV.setText(notifications.get(position).getSenderName());
        String senderProfilePicUrl = notifications.get(position).getSenderProfPicURL();
        if(senderProfilePicUrl != null){
            if(!senderProfilePicUrl.isEmpty()){
                Picasso.with(ctx)
                        .load(senderProfilePicUrl)
                        .into(holder.senderIV);
            }
        }

        if(notifications.get(position).getType().equals(REQUESTING_DF)){
            String firstName = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_FIRST_NAME);
            String userProfilePicUrl = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL);
            if(userProfilePicUrl != null){
                if(!userProfilePicUrl.isEmpty()){
                    Picasso.with(ctx)
                            .load(userProfilePicUrl)
                            .into(holder.targetIV);
                }
            }
            holder.targetNameTV.setText(firstName);
        }
        else{
            String targetProfilePicUrl = notifications.get(position).getTargetProfPicURL();
            if(targetProfilePicUrl != null){
                if(!targetProfilePicUrl.isEmpty()){
                    Picasso.with(ctx)
                            .load(targetProfilePicUrl)
                            .into(holder.targetIV);
                }
            }
            holder.targetNameTV.setText(notifications.get(position).getTargetName());
        }

        (holder).swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        // Drag From Left
        (holder).swipeLayout.addDrag(SwipeLayout.DragEdge.Left, (holder).swipeLayout.findViewById(R.id.bottom_wrapper1));

        // Disable Drag From Right
        (holder).swipeLayout.addDrag(SwipeLayout.DragEdge.Right, null);
        (holder).swipeLayout.setRightSwipeEnabled(false);

        // Handling different events when swiping
        (holder).swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
