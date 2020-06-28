/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Requests;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.BackgroundTasks.CreateGroupChatPhotoBGTask;
import com.connect.infixd.mobile.Dialogs.DialogFactory;
import com.connect.infixd.mobile.POJOModels.ChatMessage;
import com.connect.infixd.mobile.POJOModels.NotificationRowData;
import com.connect.infixd.mobile.POJOModels.RecentChat;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.UserHome.UserHomeActivity;
import com.connect.infixd.mobile.intentservices.FriendIntentService;
import com.connect.infixd.mobile.intentservices.InfixdIntentService;
import com.connect.infixd.mobile.intentservices.NotificationIntentService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infixd.client.model.RequestResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.connect.infixd.mobile.intentservices.FriendIntentService.DATA_FIRST_FRIEND_ID;
import static com.connect.infixd.mobile.intentservices.FriendIntentService.DATA_FIRST_FRIEND_NAME;
import static com.connect.infixd.mobile.intentservices.FriendIntentService.DATA_FIRST_FRIEND_PROF_PIC_URL;
import static com.connect.infixd.mobile.intentservices.FriendIntentService.DATA_SECOND_FRIEND_ID;
import static com.connect.infixd.mobile.intentservices.FriendIntentService.DATA_SECOND_FRIEND_NAME;
import static com.connect.infixd.mobile.intentservices.FriendIntentService.DATA_SECOND_FRIEND_PROF_PIC_URL;
import static com.connect.infixd.mobile.intentservices.FriendIntentService.DATA_USER_ID;
import static com.connect.infixd.mobile.intentservices.FriendIntentService.DATA_USER_NAME;
import static com.connect.infixd.mobile.intentservices.FriendIntentService.DATA_USER_PROF_PIC_URL;

public class NotificationFragment extends Fragment implements InfixdIntentService.Receiver{

    private RecyclerView notification_recyclerview;
    private RecyclerView.LayoutManager layoutManager;
    private NotificationFragmentAdapter adapter;
    private ArrayList<NotificationRowData> notificationData = new ArrayList<NotificationRowData>();
    private String notificationType;
    private InfixdIntentService.BroadcastReceiver mBroadCastReciever;
    private View notificationView;
    private UserHomeActivity userHomeActivity;
    private String userId;
    private Button inviteBtn;

    private ConstraintLayout fiEmptyView;
    private ConstraintLayout introEmptyView;
    private ConstraintLayout drEmptyView;

    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private DatabaseReference recentChatDBRef;
    private DatabaseReference firstFriendRecentChatDBRef;
    private DatabaseReference secondFriendRecentChatDBRef;

    public static final String REQUESTING_DF = "REQUESTING_DF";
    public static final String REQUESTING_FI = "REQUESTING_FI";
    public static final String REQUESTING_INTRO = "REQUESTING_INTRO";
    public static final String GROUP_PROFILE_PIC_URL = "https://firebasestorage.googleapis.com/v0/b/strapd-162605.appspot.com/o/Profile_GroupFriend-RoundedBlack-512.png?alt=media&token=756dbce2-a2c8-4995-8763-dde6ab1b5d89";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        notificationView = inflater.inflate(R.layout.fragment_notification, container, false);
        notificationType = getArguments().getString("type");
        userHomeActivity = (UserHomeActivity) getActivity();
        userId = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        fiEmptyView = (ConstraintLayout) notificationView.findViewById(R.id.introLayout);
        introEmptyView = (ConstraintLayout) notificationView.findViewById(R.id.IRlayout);
        drEmptyView = (ConstraintLayout) notificationView.findViewById(R.id.DRlayout);
        notification_recyclerview = (RecyclerView) notificationView.findViewById(R.id.notification_rec_viewer);

        notification_recyclerview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        notification_recyclerview.setLayoutManager(layoutManager);
        adapter = new NotificationFragmentAdapter(getContext(), notificationData);
        notification_recyclerview.setAdapter(adapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FriendIntentService.ACTION_ACCEPT_FRIEND_REQUEST_SUCCESS);
        intentFilter.addAction(FriendIntentService.ACTION_ACCEPT_FRIEND_REQUEST_FAIL);
        intentFilter.addAction(FriendIntentService.ACTION_SEND_FORWARD_INTRODUCTION_REQUEST_SUCCESS);
        intentFilter.addAction(FriendIntentService.ACTION_SEND_FORWARD_INTRODUCTION_REQUEST_FAIL);
        intentFilter.addAction(FriendIntentService.ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST_SUCCESS);
        intentFilter.addAction(FriendIntentService.ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST_FAIL);
        intentFilter.addAction(FriendIntentService.ACTION_REJECT_FRIEND_REQUEST);
        intentFilter.addAction(FriendIntentService.ACTION_REJECT_INTRODUCTION_REQUEST);
        intentFilter.addAction(FriendIntentService.ACTION_REJECT_FORWARD_INTRODUCTION_REQUEST);
        intentFilter.addAction(NotificationIntentService.ACTION_GET_ALL_REQUEST);
        intentFilter.addAction(NotificationIntentService.ACTION_GET_ALL_REQUEST_FAILED);
        intentFilter.addAction(NotificationIntentService.ACTION_NO_REQUEST);

        mBroadCastReciever = new InfixdIntentService.BroadcastReceiver(this);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBroadCastReciever, intentFilter);

        inviteBtn = (Button) notificationView.findViewById(R.id.inviteButton);
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userHomeActivity.inviteFriends();
            }
        });

        fetchNotifications();
        return notificationView;
    }

    private void fetchNotifications(){
        Intent intent = new Intent(getContext(), NotificationIntentService.class);
        intent.setAction(NotificationIntentService.ACTION_GET_ALL_REQUEST);
        intent.putExtra(NotificationIntentService.DATA_USER_ID, userId);
        getContext().startService(intent);
    }

    private void setNotificationData(String type, List<RequestResponse> requestResponses){
        for (RequestResponse requestResponse: requestResponses) {
            if(requestResponse.getType().equals(type)){
                NotificationRowData obj = new NotificationRowData();
                obj.setType(requestResponse.getType());
                obj.setSenderId(requestResponse.getSenderId());
                obj.setSenderName(requestResponse.getSenderfirstName());
                obj.setSenderProfPicURL(requestResponse.getSenderProfilePicURL());
                obj.setTargetId(requestResponse.getTargetId());
                obj.setTargetName(requestResponse.getTargetfirstName());
                obj.setTargetProfPicURL(requestResponse.getTargetProfilePicURL());
                notificationData.add(obj);
            }
        }

        if(notificationData.size() == 0){
            notification_recyclerview.setVisibility(View.GONE);
            switch (notificationType){
                case REQUESTING_DF:
                    drEmptyView.setVisibility(View.VISIBLE);
                    break;
                case REQUESTING_INTRO:
                    introEmptyView.setVisibility(View.VISIBLE);
                    break;
                case REQUESTING_FI:
                    fiEmptyView.setVisibility(View.VISIBLE);
                    break;
            }
        }
        else{
            notification_recyclerview.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) switch (intent.getAction()) {
            case FriendIntentService.ACTION_ACCEPT_FRIEND_REQUEST_SUCCESS:
                DialogFactory.getInstance().make("Friend Accepted", userHomeActivity.findViewById(R.id.drawer_layout)).show();
                break;
            case FriendIntentService.ACTION_ACCEPT_FRIEND_REQUEST_FAIL:
                DialogFactory.getInstance().make("Server Error", userHomeActivity.findViewById(R.id.drawer_layout)).show();
                break;
            case FriendIntentService.ACTION_SEND_FORWARD_INTRODUCTION_REQUEST_SUCCESS:
                DialogFactory.getInstance().make("Introduction Request Forwarded", userHomeActivity.findViewById(R.id.drawer_layout)).show();
                break;
            case FriendIntentService.ACTION_SEND_FORWARD_INTRODUCTION_REQUEST_FAIL:
                DialogFactory.getInstance().make("Server Error", userHomeActivity.findViewById(R.id.drawer_layout)).show();
                break;
            case FriendIntentService.ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST_SUCCESS:
                DialogFactory.getInstance().make("Forward Introduction Request Accepted", userHomeActivity.findViewById(R.id.drawer_layout)).show();
                createGroupChat(
                        intent.getStringExtra(DATA_USER_ID),
                        intent.getStringExtra(DATA_USER_NAME),
                        intent.getStringExtra(DATA_USER_PROF_PIC_URL),
                        intent.getStringExtra(DATA_FIRST_FRIEND_ID),
                        intent.getStringExtra(DATA_FIRST_FRIEND_NAME),
                        intent.getStringExtra(DATA_FIRST_FRIEND_PROF_PIC_URL),
                        intent.getStringExtra(DATA_SECOND_FRIEND_ID),
                        intent.getStringExtra(DATA_SECOND_FRIEND_NAME),
                        intent.getStringExtra(DATA_SECOND_FRIEND_PROF_PIC_URL)
                );
                break;
            case FriendIntentService.ACTION_ACCEPT_FORWARD_INTRODUCTION_REQUEST_FAIL:
                DialogFactory.getInstance().make("Server Error", userHomeActivity.findViewById(R.id.drawer_layout)).show();
                break;
            case NotificationIntentService.ACTION_GET_ALL_REQUEST:
                List<RequestResponse> requestResponses = (List<RequestResponse>) intent.getSerializableExtra(NotificationIntentService.DATA_NOTIFICATIONS);
                setNotificationData(notificationType,requestResponses);
                break;
            case NotificationIntentService.ACTION_NO_REQUEST:

                break;
            case NotificationIntentService.ACTION_GET_ALL_REQUEST_FAILED:
                DialogFactory.getInstance().make("Server Error", userHomeActivity.findViewById(R.id.drawer_layout)).show();
                break;
            default:
                DialogFactory.getInstance().make("Request Declined",userHomeActivity.findViewById(R.id.drawer_layout)).show();
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserHomeActivity){
            userHomeActivity= (UserHomeActivity) context;
        }
    }

    public void createGroupChat(String userId, String userName, String userProfPicURL,
                                String firstFriendId, String firstFriendName, String firstFriendProfPicURL,
                                String secondFriendId, String secondFriendName, String secondFriendProfPicURL){

        if(userId!= null && firstFriendId!= null && secondFriendId!= null){

            String chatRoomName = generateGroupChatRoomName(userId,firstFriendId,secondFriendId);
            String myGroupName = firstFriendName.charAt(0)+"."+secondFriendName.charAt(0);
            String firstFrGroupName = userName.charAt(0)+"."+secondFriendName.charAt(0);
            String secondFrGroupName = userName.charAt(0)+"."+firstFriendName.charAt(0);

            mFireBaseDatabase = FirebaseDatabase.getInstance();
            mChatDatabaseReference = mFireBaseDatabase.getReference().child("CHAT_ROOMS").child(chatRoomName);

            recentChatDBRef = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(userId).child(chatRoomName);
            firstFriendRecentChatDBRef = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(firstFriendId).child(chatRoomName);
            secondFriendRecentChatDBRef = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(secondFriendId).child(chatRoomName);

            Long date = System.currentTimeMillis();
            ChatMessage chatMessage = new ChatMessage("Hi Guys ... ", firstFriendName, userId , null,date );
            mChatDatabaseReference.push().setValue(chatMessage);

            if(isStoragePermissionGiven()){
                CreateGroupChatPhotoBGTask createGroupChatPhotoBGTask = new CreateGroupChatPhotoBGTask(getContext());
                createGroupChatPhotoBGTask.execute(userId, userName, userProfPicURL, firstFriendId,
                        firstFriendName, firstFriendProfPicURL, secondFriendId, secondFriendName, secondFriendProfPicURL);
            }
            else{
                recentChatDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RecentChat recentChat = (new RecentChat("GroupChat",myGroupName,GROUP_PROFILE_PIC_URL,chatRoomName,date,date));
                        recentChat.setFirstFriendId(firstFriendId);
                        recentChat.setFirstFriendFirstName(firstFriendName);
                        recentChat.setFirstFriendProfPicURL(firstFriendProfPicURL);
                        recentChat.setSecondFriendId(secondFriendId);
                        recentChat.setSecondFriendFirstName(secondFriendName);
                        recentChat.setSecondFriendProfPicURL(secondFriendProfPicURL);
                        dataSnapshot.getRef().setValue(recentChat);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                firstFriendRecentChatDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RecentChat recentChat = (new RecentChat("GroupChat",firstFrGroupName,GROUP_PROFILE_PIC_URL,chatRoomName,date,date));
                        recentChat.setFirstFriendId(userId);
                        recentChat.setFirstFriendFirstName(userName);
                        recentChat.setFirstFriendProfPicURL(userProfPicURL);
                        recentChat.setSecondFriendId(secondFriendId);
                        recentChat.setSecondFriendFirstName(secondFriendName);
                        recentChat.setSecondFriendProfPicURL(secondFriendProfPicURL);
                        dataSnapshot.getRef().setValue(recentChat);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                secondFriendRecentChatDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RecentChat recentChat = (new RecentChat("GroupChat",secondFrGroupName,GROUP_PROFILE_PIC_URL,chatRoomName,date,date));
                        recentChat.setFirstFriendId(userId);
                        recentChat.setFirstFriendFirstName(userName);
                        recentChat.setFirstFriendProfPicURL(userProfPicURL);
                        recentChat.setSecondFriendId(firstFriendId);
                        recentChat.setSecondFriendFirstName(firstFriendName);
                        recentChat.setSecondFriendProfPicURL(firstFriendProfPicURL);
                        dataSnapshot.getRef().setValue(recentChat);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

        }

    }

    public String generateGroupChatRoomName (String first, String second, String third){
        int firstIntVal = generateHashCode(first);
        int secondIntVal = generateHashCode(second);
        int thirdIntVal = generateHashCode(third);
        int[] nums={firstIntVal,secondIntVal,thirdIntVal};
        Arrays.sort(nums);
        String Name = String.valueOf(nums[0]).concat(String.valueOf(nums[1])).concat(String.valueOf(nums[2]));
        return Name;
    }

    public int generateHashCode(String string){
        int hash = 0;
        if (string.length() == 0) return hash;
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            hash = ((hash<<5)-hash)+character;
            hash = hash & hash; // Convert to 32bit integer
        }
        return hash;
    }

    public boolean isStoragePermissionGiven(){
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        else{
            return true;
        }
    }

}
