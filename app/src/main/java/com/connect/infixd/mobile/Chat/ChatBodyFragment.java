/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.POJOModels.ChatMessage;
import com.connect.infixd.mobile.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ChatBodyFragment extends Fragment {

    private String firstName;
    private String chatRoomName;
    private String userId;
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private RecyclerView chatSpaceRecyclerView;
    private ChatSpaceAdapter chatSpaceAdapter;
    private LinearLayoutManager chatSpaceLayoutManager;
    private List<ChatMessage> friendlyMessages = new ArrayList<>();
    private ChatActivity chatActivity;
    private Context ctx;
    private boolean isGroupChat;
    private static final String SEND_CHAT_IMAGE = "sendChatImage";
    private BroadcastReceiver mNotificationReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View chatbodyView = inflater.inflate(R.layout.fragment_chat_body, container, false);

        chatActivity = (ChatActivity) getActivity();
        mNotificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String imageURL = intent.getStringExtra("ChatImageURL");
                ChatMessage chatMessage = new ChatMessage(null, firstName, userId,
                        "photoURL", System.currentTimeMillis());
                chatMessage.setThumbnailURI(Uri.parse(imageURL));
                friendlyMessages.add(chatMessage);
                chatSpaceAdapter.notifyDataSetChanged();
                chatSpaceRecyclerView.scrollToPosition(friendlyMessages.size() -1);
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(mNotificationReceiver, new IntentFilter(SEND_CHAT_IMAGE));

        chatRoomName = getArguments().getString("chatRoomName");
        isGroupChat = getArguments().getBoolean("isGroupChat");

        if(chatRoomName == null){
            chatRoomName = "CHAT_BOT";
        }
        firstName = chatActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_FIRST_NAME);
        userId = chatActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mChatDatabaseReference = mFireBaseDatabase.getReference().child("CHAT_ROOMS").child(chatRoomName);
        mChatDatabaseReference.addChildEventListener(mChildEventListener);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        chatSpaceRecyclerView = (RecyclerView) chatbodyView.findViewById(R.id.chatBody_recyclerViewer);
        chatSpaceLayoutManager = new LinearLayoutManager(getContext());
        chatSpaceRecyclerView.setLayoutManager(chatSpaceLayoutManager);
        chatSpaceAdapter = new ChatSpaceAdapter(ctx,friendlyMessages,userId,firstName,isGroupChat,chatRoomName);
        chatSpaceRecyclerView.setAdapter(chatSpaceAdapter);
        chatSpaceRecyclerView.scrollToPosition(friendlyMessages.size() -1);

        return chatbodyView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("INFIXD", "onAttach in ChatBodyFragment is called");
        this.ctx = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mNotificationReceiver);
    }

    public void clearChatBody(){
        List<ChatMessage> lst = new ArrayList<ChatMessage>();
        friendlyMessages.addAll(lst);
        chatSpaceAdapter.notifyDataSetChanged();
    }


    ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d("TEST", "onChildAdded for Message: " + dataSnapshot.toString());
            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
            friendlyMessages.add(chatMessage);
            chatSpaceAdapter.notifyDataSetChanged();
            chatSpaceRecyclerView.scrollToPosition(friendlyMessages.size() -1);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}
