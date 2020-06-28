/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.connect.infixd.mobile.Dialogs.DialogFactory;
import com.connect.infixd.mobile.Functions.ImageViewerActivity;
import com.connect.infixd.mobile.POJOModels.ChatMessage;
import com.connect.infixd.mobile.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatSpaceAdapter extends RecyclerView.Adapter<ChatSpaceAdapter.ViewHolder> {

    private List<ChatMessage> friendlyMsgs = new ArrayList<>();
    private String userId;
    private LayoutInflater layoutInflater;
    private boolean isGroupChat;
    private Context ctx;
    private ChatActivity chatActivity;
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private String chatRoomName;
    private String firstName;
    private static final String UPDATE_RECENT_CHAT = "updateRecentChat";

    public ChatSpaceAdapter(Context ctx, List<ChatMessage> friendlyMessages,
                            String userId, String firstName, boolean isGroupChat,String chatRoomName){
        this.friendlyMsgs = friendlyMessages;
        this.userId = userId;
        this.firstName = firstName;
        this.layoutInflater = LayoutInflater.from(ctx);
        this.isGroupChat = isGroupChat;
        this.ctx = ctx;
        this.chatActivity = (ChatActivity) ctx;
        this.chatRoomName = chatRoomName;
    }

    public void addChatMessage(ChatMessage message){
        friendlyMsgs.add(message);
        notifyDataSetChanged();
    }
    public void removeChatMessage(int position){
        friendlyMsgs.remove(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView chatFriendMessageImageView;
        public ImageView chatUserMessageImageView;
        public TextView friend_messageTextView;
        public TextView friendNameTV;
        public TextView friend_dateTimeTextView;
        public TextView user_messageTextView;
        public TextView user_dateTimeTextView;
        public View chat_friend_row;
        public View chat_user_row;
        public ConstraintLayout friendNameLayout;
        public ProgressBar progress;

        public ViewHolder(View v) {
            super(v);
            friend_messageTextView = (TextView) v.findViewById(R.id.chat_friend_messageTextView);
            friend_dateTimeTextView = (TextView) v.findViewById(R.id.chat_friend_DateTV);
            user_messageTextView = (TextView) v.findViewById(R.id.chat_user_messageTextView);
            user_dateTimeTextView = (TextView) v.findViewById(R.id.chat_user_DateTV);
            friendNameTV = (TextView) v.findViewById(R.id.friendNameTV);
            friendNameLayout = (ConstraintLayout) v.findViewById(R.id.friendNameLayout);
            chatFriendMessageImageView = (ImageView) v.findViewById(R.id.chat_friend_messageImageView);
            chatUserMessageImageView = (ImageView) v.findViewById(R.id.chat_user_messageImageView);
            chat_friend_row = v.findViewById(R.id.chat_friend_row);
            chat_user_row = v.findViewById(R.id.chat_user_row);
            progress = (ProgressBar) v.findViewById(R.id.progress_user);

            chatFriendMessageImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, ImageViewerActivity.class);
                    intent.putExtra("PICTURE_URL", friendlyMsgs.get(getAdapterPosition()).getImageUrl());
                    ctx.startActivity(intent);
                }
            });

            chatUserMessageImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, ImageViewerActivity.class);
                    intent.putExtra("PICTURE_URL", friendlyMsgs.get(getAdapterPosition()).getImageUrl());
                    ctx.startActivity(intent);
                }
            });

        }
    }

    @Override
    public ChatSpaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.chat_space_row, parent, false);
        ChatSpaceAdapter.ViewHolder vh = new ChatSpaceAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ChatSpaceAdapter.ViewHolder holder, int position) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(friendlyMsgs.get(position).getTime()));
        String time = DateFormat.format("hh:mm", cal).toString();

        String chatMessageUserId = friendlyMsgs.get(position).getUserId();
        boolean isPhoto = friendlyMsgs.get(position).getImageUrl() != null;

        if(chatMessageUserId.equals(this.userId)){
            holder.chat_friend_row.setVisibility(View.GONE);
            if(isPhoto){
                if(friendlyMsgs.get(position).getThumbnailURI() != null){
                    holder.user_messageTextView.setVisibility(View.GONE);
                    holder.chatUserMessageImageView.setVisibility(View.VISIBLE);
                    holder.user_dateTimeTextView.setText(time);
                    holder.progress.setVisibility(View.VISIBLE);
                    storeChatImageUrlToChatMessage(position,friendlyMsgs.get(position).getThumbnailURI(),
                            holder.progress,holder.chatUserMessageImageView);
                }
                else{
                    holder.user_messageTextView.setVisibility(View.GONE);
                    holder.chatUserMessageImageView.setVisibility(View.VISIBLE);
                    Picasso.with(holder.chatUserMessageImageView.getContext())
                            .load(friendlyMsgs.get(position).getImageUrl())
                            .resize(450,450)
                            .centerCrop()
                            .into(holder.chatUserMessageImageView);
                    holder.user_dateTimeTextView.setText(time);
                }

            }
            else {
                holder.user_messageTextView.setText(friendlyMsgs.get(position).getText());
                holder.user_dateTimeTextView.setText(time);
            }
        }
        else{
            holder.chat_user_row.setVisibility(View.GONE);
            if(isPhoto){
                holder.friend_messageTextView.setVisibility(View.GONE);
                holder.chatFriendMessageImageView.setVisibility(View.VISIBLE);

                Picasso.with(holder.chatFriendMessageImageView.getContext())
                        .load(friendlyMsgs.get(position).getImageUrl())
                        .resize(450,450)
                        .centerCrop()
                        .into(holder.chatFriendMessageImageView);
                holder.friend_dateTimeTextView.setText(time);
            }
            else {
            if(isGroupChat){
                holder.friendNameLayout.setVisibility(View.VISIBLE);
                holder.friendNameTV.setText(friendlyMsgs.get(position).getName());
            }

            holder.friend_messageTextView.setText(friendlyMsgs.get(position).getText());
            holder.friend_dateTimeTextView.setText(time);
            }
        }
    }

    @Override
    public int getItemCount() {
        return friendlyMsgs.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public void storeChatImageUrlToChatMessage(int position, Uri selectedImageUri,
                                               ProgressBar progress, ImageView imageView){
        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mChatDatabaseReference = mFireBaseDatabase.getReference().child("CHAT_ROOMS").child(chatRoomName);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");
        StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

        // Upload file to Firebase Storage
        photoRef.putFile(selectedImageUri)
                .addOnSuccessListener(chatActivity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        ChatMessage chatMessage = new ChatMessage(null, firstName, userId,
                                downloadUrl.toString(), System.currentTimeMillis());
                        mChatDatabaseReference.push().setValue(chatMessage);
                        progress.setVisibility(View.GONE);
                        friendlyMsgs.remove(position);
                        notifyDataSetChanged();

                        Intent intent = new Intent(UPDATE_RECENT_CHAT);
                        intent.putExtra("action",true);
                        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    DialogFactory.getInstance().make(DialogFactory.REPORT_ERROR,
                            chatActivity.getCurrentFocus(), view -> { /* TODO: Handle report */}).show();

                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Double completedTaskVal = (100.0 * taskSnapshot.getBytesTransferred())
                                / taskSnapshot.getTotalByteCount();
                        progress.setMax(100);
                        progress.setProgress(completedTaskVal.intValue());

                        Picasso.with(ctx)
                                .load(selectedImageUri) // thumbnail url goes here
                                .resize(450, 450)
                                .into(imageView);

                    }
                });
    }
}
