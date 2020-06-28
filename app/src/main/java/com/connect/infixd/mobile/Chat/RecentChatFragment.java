/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Chat;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.BackgroundTasks.GetUserDetailsBGT;
import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.Interfaces.AddChatSendFriendId;
import com.connect.infixd.mobile.Interfaces.ClearChatBody;
import com.connect.infixd.mobile.Interfaces.NotifyRecentChatLoadFinish;
import com.connect.infixd.mobile.Interfaces.SendChatFriendId;
import com.connect.infixd.mobile.POJOModels.RecentChat;
import com.connect.infixd.mobile.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecentChatFragment extends Fragment {

    private RecyclerView recentChatRecyclerView;
    private AlertDialog dialog;
    private RecentChatAdapter recentChatAdapter;
    private RecyclerView.LayoutManager recentChatLayoutManager;
    private ArrayList<RecentChat> recentChats = new ArrayList<RecentChat>();
    private SendChatFriendId sendChatFriendId;
    private ClearChatBody clearChatBody;
    private FirebaseDatabase rFireBaseDatabase;
    private DatabaseReference recentChatDatabaseReference;
    private DatabaseReference friendrecentChatDBRef;
    private DatabaseReference chatRoomDBRef;
    private String userId;
    private TextView recent_first_tv;
    private TextView recent_second_tv;
    private ImageView recent_first_iv;
    private ImageView recent_second_iv;
    private TextView recent_first_unread_msg_tv;
    private TextView recent_second_unread_msg_tv;
    private RecentChat firstRecentChat = new RecentChat();
    private RecentChat secondRecentChat = new RecentChat();
    private ConstraintLayout firstRecentChatView;
    private ConstraintLayout secondRecentChatView;
    private AddChatSendFriendId addChatSendFriendId;
    private String passFriendId;
    private String passFriendName;
    private String passFriendProfPicURL;
    private NotifyRecentChatLoadFinish notifyRecentChatLoadFinish;
    private ChatActivity chatActivity;
    private boolean isRespondedChat;
    private static final int GROUP_CHAT = 100;
    private static final int USER_CHAT = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recentChatView = inflater.inflate(R.layout.fragment_recent_chat, container, false);

        chatActivity = (ChatActivity) getActivity();

        passFriendId = getArguments().getString("friendId");
        passFriendName = getArguments().getString("friendName");
        passFriendProfPicURL = getArguments().getString("friendProfPicURL");

        userId = chatActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        firstRecentChatView = (ConstraintLayout) recentChatView.findViewById(R.id.recent_chat_first_recent_view);
        secondRecentChatView = (ConstraintLayout) recentChatView.findViewById(R.id.recent_chat_second_recent_view);
        recent_first_tv = (TextView) recentChatView.findViewById(R.id.recent_chat_first_recent_TV);
        recent_second_tv = (TextView) recentChatView.findViewById(R.id.recent_chat_second_recent_TV);
        recent_first_unread_msg_tv = (TextView) recentChatView.findViewById(R.id.recent_chat_first_unread_msg_tv);
        recent_second_unread_msg_tv = (TextView) recentChatView.findViewById(R.id.recent_chat_second_unread_msg_tv);
        recent_first_iv = (ImageView) recentChatView.findViewById(R.id.recent_chat_first_recent_IV);
        recent_second_iv = (ImageView) recentChatView.findViewById(R.id.recent_chat_second_recent_IV);

        recent_first_unread_msg_tv.setVisibility(View.GONE);
        recent_second_unread_msg_tv.setVisibility(View.GONE);

        firstRecentChatView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(firstRecentChat.getFriendId() != null){
                    longClickHandler(firstRecentChat);
                }
                return true;
            }
        });

        secondRecentChatView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(secondRecentChat.getFriendId() != null){
                    longClickHandler(secondRecentChat);
                }
                return true;
            }
        });

        firstRecentChatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recent_first_unread_msg_tv.setVisibility(View.GONE);
                sendChatFriendId.sendFriendId(firstRecentChat);
                doClickActionToRecentChats(firstRecentChat);
            }
        });

        secondRecentChatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recent_second_unread_msg_tv.setVisibility(View.GONE);
                sendChatFriendId.sendFriendId(secondRecentChat);
                doClickActionToRecentChats(secondRecentChat);
            }
        });

        recentChatRecyclerView = (RecyclerView) recentChatView.findViewById(R.id.recentChat_recyclerViewer);
        rFireBaseDatabase = FirebaseDatabase.getInstance();
        recentChatDatabaseReference = rFireBaseDatabase.getReference().child("RECENT_CHATS").child(userId);
        friendrecentChatDBRef = rFireBaseDatabase.getReference().child("RECENT_CHATS");
        chatRoomDBRef = rFireBaseDatabase.getReference().child("CHAT_ROOMS");
        recentChatDatabaseReference.addValueEventListener(valueEventListener);
        recentChatRecyclerView = (RecyclerView) recentChatView.findViewById(R.id.recentChat_recyclerViewer);
        recentChatLayoutManager = new LinearLayoutManager(getContext());
        recentChatRecyclerView.setLayoutManager(recentChatLayoutManager);
        recentChatAdapter = new RecentChatAdapter(recentChats){
            @Override
            public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
                super.onBindViewHolder(holder, position, payloads);
                holder.recentChatTV.setText(recentChats.get(position).getFriendName());
                if(recentChats.get(position).getFriendProfPicURL() != null
                        && !recentChats.get(position).getFriendProfPicURL().isEmpty()){
                    Picasso.with(getContext())
                            .load(recentChats.get(position).getFriendProfPicURL())
                            .into(holder.recent_chat_friend_IV);
                }

                if(recentChats.get(position).getUnReadMessageCount() > 0){
                    holder.unreadMsgTV.setVisibility(View.VISIBLE);
                }
                else{
                    holder.unreadMsgTV.setVisibility(View.GONE);
                }
                holder.recentChatView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendChatFriendId.sendFriendId(recentChats.get(position));
                        doClickActionToRecentChats(recentChats.get(position));
                    }
                });

                holder.recentChatView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        longClickHandler(recentChats.get(position));
                        return true;
                    }
                });

            }
        };
        recentChatRecyclerView.setAdapter(recentChatAdapter);

        return recentChatView;
    }

    // ADD CHAT FROM CONTACTS
    public void updateRecentChatList(Contact friend){
        firstRecentChat = new RecentChat(friend.getID(),friend.getName(),friend.getProfilePicUrl());
        firstRecentChatView.setVisibility(View.VISIBLE);
        recent_first_tv.setText(firstRecentChat.getFriendName());
        if(firstRecentChat.getFriendProfPicURL() != null
                && !firstRecentChat.getFriendProfPicURL().isEmpty()){
            Picasso.with(getContext())
                    .load(firstRecentChat.getFriendProfPicURL())
                    .into(recent_first_iv);
        }

        recentChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<RecentChat> lst = new ArrayList<RecentChat>(); // Result will be holded Here
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    lst.add(dsp.getValue(RecentChat.class)); //add result into array list
                }

                if(lst.size() == 1){
                    if (lst.get(0).getFriendId().equals(firstRecentChat.getFriendId())) {
                        lst.remove(0);
                    }
                    else{
                        secondRecentChatView.setVisibility(View.VISIBLE);
                        secondRecentChat = new RecentChat(lst.get(0).getFriendId(),
                                lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),lst.get(0).getLastUpdate());
                        recent_second_tv.setText(secondRecentChat.getFriendName());
                        if(secondRecentChat.getFriendProfPicURL() != null
                                && !secondRecentChat.getFriendProfPicURL().isEmpty()){
                            Picasso.with(getContext())
                                    .load(secondRecentChat.getFriendProfPicURL())
                                    .into(recent_second_iv);
                        }

                        if(lst.get(0).getUnReadMessageCount() > 0){
                            recent_second_unread_msg_tv.setVisibility(View.VISIBLE);
                        }
                        else{
                            recent_second_unread_msg_tv.setVisibility(View.GONE);
                        }
                    }
                }

                else if(lst.size() > 1){
                    for (int i=0; i<lst.size(); i++) {
                        if (lst.get(i).getFriendId().equals(firstRecentChat.getFriendId())) {
                            lst.remove(i);
                        }
                    }

                    Collections.sort(lst, new Comparator<RecentChat>() {
                        public int compare(RecentChat r1, RecentChat r2) {
                            return r2.getLastUpdate().compareTo(r1.getLastUpdate());
                        }
                    });

                    secondRecentChat = new RecentChat(lst.get(0).getFriendId(),
                            lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),lst.get(0).getLastUpdate());
                    secondRecentChatView.setVisibility(View.VISIBLE);
                    recent_second_tv.setText(secondRecentChat.getFriendName());
                    if(secondRecentChat.getFriendProfPicURL() != null
                            && !secondRecentChat.getFriendProfPicURL().isEmpty()){
                        Picasso.with(getContext())
                                .load(secondRecentChat.getFriendProfPicURL())
                                .into(recent_second_iv);
                    }
                    if(lst.get(0).getUnReadMessageCount() > 0){
                        recent_second_unread_msg_tv.setVisibility(View.VISIBLE);
                    }
                    else{
                        recent_second_unread_msg_tv.setVisibility(View.GONE);
                    }

                    lst.remove(0);
                    recentChats.clear();
                    recentChats.addAll(lst);
                    recentChatAdapter.notifyDataSetChanged();
                }

                // SET CHAT ROOM NAME AND CHAT BODY FRAGMENT
                addChatSendFriendId.addChatSendFriendId(friend);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(isRespondedChat){
            isRespondedChat = false;
            notifyRecentChatLoadFinish.notifyRecentChatLoadFinish(true);
        }

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            refreshRecentChat(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("INFIXD", "onAttach in RecentChatFragment is called");
        sendChatFriendId = (SendChatFriendId) context;
        addChatSendFriendId = (AddChatSendFriendId) context;
        clearChatBody = (ClearChatBody) context;
        notifyRecentChatLoadFinish = (NotifyRecentChatLoadFinish) context;
    }


    // WHEN CLICKING THE RECENT CHATS
    public void doClickActionToRecentChats(RecentChat friend){
        recentChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<RecentChat> lst = new ArrayList<RecentChat>(); // Result will be holded Here
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    lst.add(dsp.getValue(RecentChat.class)); //add result into array list
                }

                Collections.sort(lst, new Comparator<RecentChat>() {
                    public int compare(RecentChat r1, RecentChat r2) {
                        return r2.getLastUpdate().compareTo(r1.getLastUpdate());
                    }
                });

                if(lst.size() == 0){
                    firstRecentChatView.setVisibility(View.VISIBLE);
                    secondRecentChatView.setVisibility(View.GONE);

                    firstRecentChat = friend;
                    recent_first_tv.setText(firstRecentChat.getFriendName());
                    if(firstRecentChat.getFriendProfPicURL() != null
                            && !firstRecentChat.getFriendProfPicURL().isEmpty()){
                        Picasso.with(getContext())
                                .load(firstRecentChat.getFriendProfPicURL())
                                .into(recent_first_iv);
                    }
                }

                else if(lst.size()== 1){
                    if(lst.get(0).getFriendId().equals(friend.getFriendId())){
                        secondRecentChatView.setVisibility(View.GONE);
                        firstRecentChat = friend;
                        recent_first_tv.setText(firstRecentChat.getFriendName());
                        if(firstRecentChat.getFriendProfPicURL() != null
                                && !firstRecentChat.getFriendProfPicURL().isEmpty()){
                            Picasso.with(getContext())
                                    .load(firstRecentChat.getFriendProfPicURL())
                                    .into(recent_first_iv);
                        }
                    }
                    else{
                        firstRecentChatView.setVisibility(View.VISIBLE);
                        secondRecentChatView.setVisibility(View.VISIBLE);
                        firstRecentChat = friend;
                        recent_first_tv.setText(firstRecentChat.getFriendName());
                        if(firstRecentChat.getFriendProfPicURL() != null
                                && !firstRecentChat.getFriendProfPicURL().isEmpty()){
                            Picasso.with(getContext())
                                    .load(firstRecentChat.getFriendProfPicURL())
                                    .into(recent_first_iv);
                        }

                        secondRecentChat = new RecentChat(lst.get(0).getFriendId(),
                                lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),
                                lst.get(0).getChatRoomName(),lst.get(0).getLastUpdate(),lst.get(0).getLastRead());
                        secondRecentChat.setFirstFriendId(lst.get(0).getFirstFriendId());
                        secondRecentChat.setFirstFriendFirstName(lst.get(0).getFirstFriendFirstName());
                        secondRecentChat.setFirstFriendProfPicURL(lst.get(0).getFirstFriendProfPicURL());
                        secondRecentChat.setSecondFriendId(lst.get(0).getSecondFriendId());
                        secondRecentChat.setSecondFriendFirstName(lst.get(0).getSecondFriendFirstName());
                        secondRecentChat.setSecondFriendProfPicURL(lst.get(0).getSecondFriendProfPicURL());
                        recent_second_tv.setText(secondRecentChat.getFriendName());
                        if(secondRecentChat.getFriendProfPicURL() != null
                                && !secondRecentChat.getFriendProfPicURL().isEmpty()){
                            Picasso.with(getContext())
                                    .load(secondRecentChat.getFriendProfPicURL())
                                    .into(recent_second_iv);
                        }
                        if(lst.get(0).getUnReadMessageCount() > 0){
                            recent_second_unread_msg_tv.setVisibility(View.VISIBLE);
                        }
                        else{
                            recent_second_unread_msg_tv.setVisibility(View.GONE);
                        }
                    }

                }

                else if(lst.size() >= 2){
                    firstRecentChatView.setVisibility(View.VISIBLE);
                    secondRecentChatView.setVisibility(View.VISIBLE);

                    firstRecentChat = friend;
                    recent_first_tv.setText(firstRecentChat.getFriendName());
                    if(firstRecentChat.getFriendProfPicURL() != null
                            && !firstRecentChat.getFriendProfPicURL().isEmpty()){
                        Picasso.with(getContext())
                                .load(firstRecentChat.getFriendProfPicURL())
                                .into(recent_first_iv);
                    }

                    for (int i=0; i<lst.size(); i++) {
                        if (lst.get(i).getFriendId().equals(friend.getFriendId())) {
                            lst.remove(i);
                        }
                    }

                    secondRecentChat = new RecentChat(lst.get(0).getFriendId(),
                            lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),
                            lst.get(0).getChatRoomName(),lst.get(0).getLastUpdate(),lst.get(0).getLastRead());
                    secondRecentChat.setFirstFriendId(lst.get(0).getFirstFriendId());
                    secondRecentChat.setFirstFriendFirstName(lst.get(0).getFirstFriendFirstName());
                    secondRecentChat.setFirstFriendProfPicURL(lst.get(0).getFirstFriendProfPicURL());
                    secondRecentChat.setSecondFriendId(lst.get(0).getSecondFriendId());
                    secondRecentChat.setSecondFriendFirstName(lst.get(0).getSecondFriendFirstName());
                    secondRecentChat.setSecondFriendProfPicURL(lst.get(0).getSecondFriendProfPicURL());

                    recent_second_tv.setText(secondRecentChat.getFriendName());
                    if(secondRecentChat.getFriendProfPicURL() != null
                            && !secondRecentChat.getFriendProfPicURL().isEmpty()){
                        Picasso.with(getContext())
                                .load(secondRecentChat.getFriendProfPicURL())
                                .into(recent_second_iv);
                    }

                    if(lst.get(0).getUnReadMessageCount() > 0){
                        recent_second_unread_msg_tv.setVisibility(View.VISIBLE);
                    }
                    else{
                        recent_second_unread_msg_tv.setVisibility(View.GONE);
                    }
                    lst.remove(0);
                    recentChats.clear();
                    recentChats.addAll(lst);
                    recentChatAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void refreshRecentChat(DataSnapshot dataSnapshot){
        ArrayList<RecentChat> lst = new ArrayList<>(); // Result will be holded Here
        for(DataSnapshot dsp : dataSnapshot.getChildren()){
            lst.add(dsp.getValue(RecentChat.class)); //add result into array list
        }

        Collections.sort(lst, new Comparator<RecentChat>() {
            public int compare(RecentChat r1, RecentChat r2) {
                return r2.getLastUpdate().compareTo(r1.getLastUpdate());
            }
        });

        switch (lst.size()){
            case 0:
                firstRecentChat = new RecentChat();
                secondRecentChat = new RecentChat();
                firstRecentChatView.setVisibility(View.GONE);
                secondRecentChatView.setVisibility(View.GONE);
                recentChats.clear();
                recentChatAdapter.notifyDataSetChanged();
                break;

            case 1:
                firstRecentChatView.setVisibility(View.VISIBLE);
                if(firstRecentChat.getFriendId() == null || firstRecentChat.getFriendId().equals(lst.get(0).getFriendId())){

                    secondRecentChatView.setVisibility(View.GONE);

                    if(lst.get(0).getFriendId().equals("GroupChat")){
                        firstRecentChat = new RecentChat(lst.get(0).getFriendId(),
                                lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),
                                lst.get(0).getChatRoomName(),lst.get(0).getLastUpdate(),lst.get(0).getLastRead());
                        firstRecentChat.setFirstFriendId(lst.get(0).getFirstFriendId());
                        firstRecentChat.setFirstFriendFirstName(lst.get(0).getFirstFriendFirstName());
                        firstRecentChat.setFirstFriendProfPicURL(lst.get(0).getFirstFriendProfPicURL());
                        firstRecentChat.setSecondFriendId(lst.get(0).getSecondFriendId());
                        firstRecentChat.setSecondFriendFirstName(lst.get(0).getSecondFriendFirstName());
                        firstRecentChat.setSecondFriendProfPicURL(lst.get(0).getSecondFriendProfPicURL());
                    }
                    else{
                        firstRecentChat = new RecentChat(lst.get(0).getFriendId(),
                                lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),
                                lst.get(0).getChatRoomName(),lst.get(0).getLastUpdate(),lst.get(0).getLastRead());
                    }
                    recent_first_tv.setText(firstRecentChat.getFriendName());
                    if(firstRecentChat.getFriendProfPicURL() != null
                            && !firstRecentChat.getFriendProfPicURL().isEmpty()){
                        Picasso.with(getContext())
                                .load(firstRecentChat.getFriendProfPicURL())
                                .into(recent_first_iv);
                    }

                    if(lst.get(0).getUnReadMessageCount() > 0){
                        recent_first_unread_msg_tv.setVisibility(View.VISIBLE);
                    }
                    lst.clear();

                }
                else {
                    if(lst.get(0).getFriendId().equals("GroupChat")){
                        secondRecentChat = new RecentChat(lst.get(0).getFriendId(),
                                lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),
                                lst.get(0).getChatRoomName(),lst.get(0).getLastUpdate(),lst.get(0).getLastRead());
                        secondRecentChat.setFirstFriendId(lst.get(0).getFirstFriendId());
                        secondRecentChat.setFirstFriendFirstName(lst.get(0).getFirstFriendFirstName());
                        secondRecentChat.setFirstFriendProfPicURL(lst.get(0).getFirstFriendProfPicURL());
                        secondRecentChat.setSecondFriendId(lst.get(0).getSecondFriendId());
                        secondRecentChat.setSecondFriendFirstName(lst.get(0).getSecondFriendFirstName());
                        secondRecentChat.setSecondFriendProfPicURL(lst.get(0).getSecondFriendProfPicURL());
                    }
                    else{
                        secondRecentChat = new RecentChat(lst.get(0).getFriendId(),
                                lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),
                                lst.get(0).getChatRoomName(),lst.get(0).getLastUpdate(),lst.get(0).getLastRead());
                    }

                    recent_second_tv.setText(secondRecentChat.getFriendName());
                    if(secondRecentChat.getFriendProfPicURL() != null
                            && !secondRecentChat.getFriendProfPicURL().isEmpty()){
                        Picasso.with(getContext())
                                .load(secondRecentChat.getFriendProfPicURL())
                                .into(recent_second_iv);
                    }

                    if(lst.get(0).getUnReadMessageCount() > 0){
                        recent_second_unread_msg_tv.setVisibility(View.VISIBLE);
                    }
                    lst.clear();
                }
                break;

            default:
                firstRecentChatView.setVisibility(View.VISIBLE);
                secondRecentChatView.setVisibility(View.VISIBLE);
                if(firstRecentChat.getFriendId() == null || firstRecentChat.getFriendId().equals(lst.get(0).getFriendId())){
                    if(lst.get(0).getFriendId().equals("GroupChat")){
                        firstRecentChat = new RecentChat(lst.get(0).getFriendId(),
                                lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),
                                lst.get(0).getChatRoomName(),lst.get(0).getLastUpdate(),lst.get(0).getLastRead());
                        firstRecentChat.setFirstFriendId(lst.get(0).getFirstFriendId());
                        firstRecentChat.setFirstFriendFirstName(lst.get(0).getFirstFriendFirstName());
                        firstRecentChat.setFirstFriendProfPicURL(lst.get(0).getFirstFriendProfPicURL());
                        firstRecentChat.setSecondFriendId(lst.get(0).getSecondFriendId());
                        firstRecentChat.setSecondFriendFirstName(lst.get(0).getSecondFriendFirstName());
                        firstRecentChat.setSecondFriendProfPicURL(lst.get(0).getSecondFriendProfPicURL());
                    }
                    else{
                        firstRecentChat = new RecentChat(lst.get(0).getFriendId(),
                                lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),
                                lst.get(0).getChatRoomName(),lst.get(0).getLastUpdate(),lst.get(0).getLastRead());
                    }

                    if(lst.get(1).getFriendId().equals("GroupChat")){
                        secondRecentChat = new RecentChat(lst.get(1).getFriendId(),
                                lst.get(1).getFriendName(),lst.get(1).getFriendProfPicURL(),
                                lst.get(1).getChatRoomName(),lst.get(1).getLastUpdate(),lst.get(1).getLastRead());
                        secondRecentChat.setFirstFriendId(lst.get(1).getFirstFriendId());
                        secondRecentChat.setFirstFriendFirstName(lst.get(1).getFirstFriendFirstName());
                        secondRecentChat.setFirstFriendProfPicURL(lst.get(1).getFirstFriendProfPicURL());
                        secondRecentChat.setSecondFriendId(lst.get(1).getSecondFriendId());
                        secondRecentChat.setSecondFriendFirstName(lst.get(1).getSecondFriendFirstName());
                        secondRecentChat.setSecondFriendProfPicURL(lst.get(1).getSecondFriendProfPicURL());
                    }
                    else{
                        secondRecentChat = new RecentChat(lst.get(1).getFriendId(),
                                lst.get(1).getFriendName(),lst.get(1).getFriendProfPicURL(),
                                lst.get(1).getChatRoomName(),lst.get(1).getLastUpdate(),lst.get(1).getLastRead());
                    }

                    recent_first_tv.setText(firstRecentChat.getFriendName());
                    recent_second_tv.setText(secondRecentChat.getFriendName());
                    if(firstRecentChat.getFriendProfPicURL() != null
                            && !firstRecentChat.getFriendProfPicURL().isEmpty()){
                        Picasso.with(getContext())
                                .load(firstRecentChat.getFriendProfPicURL())
                                .into(recent_first_iv);
                    }
                    if(secondRecentChat.getFriendProfPicURL() != null
                            && !secondRecentChat.getFriendProfPicURL().isEmpty()){
                        Picasso.with(getContext())
                                .load(secondRecentChat.getFriendProfPicURL())
                                .into(recent_second_iv);
                    }

                    if(lst.get(0).getUnReadMessageCount() > 0){
                        recent_first_unread_msg_tv.setVisibility(View.VISIBLE);
                    }
                    if(lst.get(1).getUnReadMessageCount() > 0){
                        recent_second_unread_msg_tv.setVisibility(View.VISIBLE);
                    }

                    lst.remove(0);
                    lst.remove(0);

                    recentChats.clear();
                    recentChats.addAll(lst);
                    recentChatAdapter.notifyDataSetChanged();

                }
                else{
                    for (int i=0; i<lst.size(); i++) {
                        if (lst.get(i).getFriendId().equals(firstRecentChat.getFriendId())) {
                            lst.remove(i);
                        }
                    }

                    if(lst.get(0).getFriendId().equals("GroupChat")){
                        secondRecentChat = new RecentChat(lst.get(0).getFriendId(),
                                lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),
                                lst.get(0).getChatRoomName(),lst.get(0).getLastUpdate(),lst.get(0).getLastRead());
                        secondRecentChat.setFirstFriendId(lst.get(0).getFirstFriendId());
                        secondRecentChat.setFirstFriendFirstName(lst.get(0).getFirstFriendFirstName());
                        secondRecentChat.setFirstFriendProfPicURL(lst.get(0).getFirstFriendProfPicURL());
                        secondRecentChat.setSecondFriendId(lst.get(0).getSecondFriendId());
                        secondRecentChat.setSecondFriendFirstName(lst.get(0).getSecondFriendFirstName());
                        secondRecentChat.setSecondFriendProfPicURL(lst.get(0).getSecondFriendProfPicURL());
                    }
                    else{
                        secondRecentChat = new RecentChat(lst.get(0).getFriendId(),
                                lst.get(0).getFriendName(),lst.get(0).getFriendProfPicURL(),
                                lst.get(0).getChatRoomName(),lst.get(0).getLastUpdate(),lst.get(0).getLastRead());
                    }

                    recent_second_tv.setText(secondRecentChat.getFriendName());
                    if(secondRecentChat.getFriendProfPicURL() != null
                            && !secondRecentChat.getFriendProfPicURL().isEmpty()){
                        Picasso.with(getContext())
                                .load(secondRecentChat.getFriendProfPicURL())
                                .into(recent_second_iv);
                    }

                    if(lst.get(0).getUnReadMessageCount() > 0){
                        recent_second_unread_msg_tv.setVisibility(View.VISIBLE);
                    }
                    else{
                        recent_second_unread_msg_tv.setVisibility(View.GONE);
                    }

                    lst.remove(0);
                    recentChats.clear();
                    recentChats.addAll(lst);
                    recentChatAdapter.notifyDataSetChanged();
                }
                break;
        }

        if(passFriendId != null && passFriendName != null &&
                !passFriendId.equals("") && !passFriendName.equals("")){
            isRespondedChat =true;
            Contact contact = new Contact();
            contact.setID(passFriendId);
            contact.setName(passFriendName);
            contact.setProfilePicUrl(passFriendProfPicURL);
            updateRecentChatList(contact);
        }
        else {
            notifyRecentChatLoadFinish.notifyRecentChatLoadFinish(true);
        }


    }

    public void longClickHandler(RecentChat obj){
        if(obj.getFriendId().equals("GroupChat")){
            final String[] items = new String[] { "Delete Chat" };
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.select_dialog_item, items);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    confirmDeleteChatDialogbox(GROUP_CHAT,obj);
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
        }
        else {
            final String[] items = new String[] { "View Profile", "Delete Chat" };
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.select_dialog_item, items);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        GetUserDetailsBGT getUserDetailsBGT = new GetUserDetailsBGT(chatActivity);
                        getUserDetailsBGT.execute("userProfileFriend", obj.getFriendId());
                    } else {
                        confirmDeleteChatDialogbox(USER_CHAT,obj);
                    }
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
        }
    }

    public void confirmDeleteChatDialogbox(int type, RecentChat obj){
        String msg=getResources().getString(R.string.delete_chat_popup_msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(chatActivity);
        builder.setCancelable(true);
        builder.setMessage(msg);

        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(type == GROUP_CHAT){
                            recentChatDatabaseReference.child(obj.getChatRoomName()).removeValue();
                            friendrecentChatDBRef.child(obj.getFirstFriendId()).
                                    child(obj.getChatRoomName()).removeValue();
                            friendrecentChatDBRef.child(obj.getSecondFriendId()).
                                    child(obj.getChatRoomName()).removeValue();
                        }
                        else if(type == USER_CHAT){
                            recentChatDatabaseReference.child(obj.getFriendId()).removeValue();
                            friendrecentChatDBRef.child(obj.getFriendId()).child(userId).removeValue();
                            //recentChatDatabaseReference.child(userId).removeValue();
                            //sendChatFriendId.deleteChatRoom(obj);
                        }
                        chatRoomDBRef.child(obj.getChatRoomName()).removeValue();
                        recentChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                refreshRecentChat(dataSnapshot);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        clearChatBody.clear();
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary_color));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary_color));
    }

}
