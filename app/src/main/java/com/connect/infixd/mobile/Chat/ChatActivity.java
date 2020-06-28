/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.DBHelper.FriendshipMeterDBHelper;
import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.Interfaces.AddChatSendFriendId;
import com.connect.infixd.mobile.Interfaces.ClearChatBody;
import com.connect.infixd.mobile.Interfaces.NotifyRecentChatLoadFinish;
import com.connect.infixd.mobile.Interfaces.SendChatFriendId;
import com.connect.infixd.mobile.POJOModels.ChatMessage;
import com.connect.infixd.mobile.POJOModels.RecentChat;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Search.SearchContactsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends InfixdBaseActivity implements SendChatFriendId,
        ClearChatBody, AddChatSendFriendId, NotifyRecentChatLoadFinish{

    private EditText message_edit_text;
    private FloatingActionButton send_fab;
    private String userId;
    private String mUsername;
    private String userProfPicURL;
    private String chatRoomName;
    private boolean isMessageTyped = false;
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private DatabaseReference recentChatDatabaseReference;
    private DatabaseReference friendRecentChatDatabaseReference;
    private DatabaseReference firstFriendRecentChatDBRef;
    private DatabaseReference secondFriendRecentChatDBRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private String chatFriendId;
    private String friendName;
    private String chatFriendProfURL;
    private Toolbar toolbar;
    private static final String RECENT_CHAT_FRAGMENT = "RECENT_CHAT_FRAGMENT";
    private static final String CHAT_BODY_FRAGMENT = "CHAT_BODY_FRAGMENT";
    private static final int RC_PHOTO_PICKER = 2;
    private static final int ADD_NEW_CHAT = 100;
    private static final String ACTION_PASS_TO_CHAT = "action_pass_to_chat";
    private static final String SEND_CHAT_IMAGE = "sendChatImage";
    private static final String UPDATE_RECENT_CHAT = "updateRecentChat";
    private int unSeenMessageCount = 0;
    private String passFriendId;
    private String passFriendName;
    private String passFriendProfPicURL;
    private ChatBodyFragment chatBodyFragment;
    private RecentChatFragment recentChatFragment;
    private LinearLayout chatView;
    private ConstraintLayout chatLoadingView;
    private ConstraintLayout chatBodyEmptyView;
    private boolean isGroupMessage;
    private BroadcastReceiver mReceiver;
    private String firstFriendID;
    private String secondFriendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
            this.isGroupMessage = savedInstanceState.getBoolean("isGroupChat");
            this.chatRoomName = savedInstanceState.getString("charRoomName");
            this.firstFriendID = savedInstanceState.getString("firstFriendID");
            this.secondFriendId = savedInstanceState.getString("secondFriendId");
            this.chatFriendId = savedInstanceState.getString("chatFriendId");
            this.friendName = savedInstanceState.getString("friendName");
            this.chatFriendProfURL = savedInstanceState.getString("chatFriendProfURL");

            if(isGroupMessage){
                this.recentChatDatabaseReference = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(userId).child(chatRoomName);
                this.firstFriendRecentChatDBRef = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(firstFriendID).child(chatRoomName);
                this.secondFriendRecentChatDBRef = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(secondFriendId).child(chatRoomName);

            }
            else{
                this.recentChatDatabaseReference = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(userId).child(chatFriendId);
                this.friendRecentChatDatabaseReference = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(chatFriendId).child(userId);
            }
        }

        setContentView(R.layout.activity_new_chat);
        Log.v("INFIXD", "onCreate is called");
        chatView = (LinearLayout) findViewById(R.id.chatView);
        chatLoadingView = (ConstraintLayout) findViewById(R.id.chatLoadingView);
        chatBodyEmptyView = (ConstraintLayout) findViewById(R.id.newChatLayout);
        showProgressBar();
        doOnCreate();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isGroupChat",isGroupMessage);
        outState.putString("charRoomName",chatRoomName);
        outState.putString("firstFriendID",firstFriendID);
        outState.putString("secondFriendId",secondFriendId);
        outState.putString("chatFriendId",chatFriendId);
        outState.putString("friendName",friendName);
        outState.putString("chatFriendProfURL",chatFriendProfURL);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
            this.isGroupMessage = savedInstanceState.getBoolean("isGroupChat");
            this.chatRoomName = savedInstanceState.getString("charRoomName");
            this.firstFriendID = savedInstanceState.getString("firstFriendID");
            this.secondFriendId = savedInstanceState.getString("secondFriendId");
            this.chatFriendId = savedInstanceState.getString("chatFriendId");
            this.friendName = savedInstanceState.getString("friendName");
            this.chatFriendProfURL = savedInstanceState.getString("chatFriendProfURL");

            if(isGroupMessage){
                this.recentChatDatabaseReference = mFireBaseDatabase.getReference()
                        .child("RECENT_CHATS").child(userId).child(chatRoomName);
                this.firstFriendRecentChatDBRef = mFireBaseDatabase.getReference()
                        .child("RECENT_CHATS").child(firstFriendID).child(chatRoomName);
                this.secondFriendRecentChatDBRef = mFireBaseDatabase.getReference()
                        .child("RECENT_CHATS").child(secondFriendId).child(chatRoomName);
            }
            else{
                this.recentChatDatabaseReference = mFireBaseDatabase.getReference()
                        .child("RECENT_CHATS").child(userId).child(chatFriendId);
                this.friendRecentChatDatabaseReference = mFireBaseDatabase.getReference()
                        .child("RECENT_CHATS").child(chatFriendId).child(userId);
            }
        }
    }

    public void doOnCreate(){
        if(getIntent().getAction().equals(ACTION_PASS_TO_CHAT)){
            passFriendId = getIntent().getStringExtra("friendId");
            passFriendName = getIntent().getStringExtra("friendName");
            passFriendProfPicURL = getIntent().getStringExtra("friendProfPicURL");
        }

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
        mUsername = getPreferenceValue(InfixdApp.STRING_PREFERENCE_FIRST_NAME);
        userProfPicURL = getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL);

        toolbar = (Toolbar) findViewById(R.id.chatToolBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white_color));
        toolbar.setTitle("Chat");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity.this.finish();
            }
        });

        if(passFriendId == null && passFriendName == null){
            chatBodyFragment = new ChatBodyFragment();
            Bundle args1 = new Bundle();
            args1.putString("friendId","default");
            args1.putBoolean("isGroupChat",false);
            chatBodyFragment.setArguments(args1);

            recentChatFragment = new RecentChatFragment();
            Bundle args2 = new Bundle();
            args2.putString("friendId","");
            args2.putString("friendName","");
            recentChatFragment.setArguments(args2);

        }
        else{
            String pChatRoomName = generateChatRoomName(userId,passFriendId);

            chatBodyFragment = new ChatBodyFragment();
            Bundle args1 = new Bundle();
            args1.putString("chatRoomName",pChatRoomName);
            args1.putBoolean("isGroupChat",false);
            chatBodyFragment.setArguments(args1);

            recentChatFragment = new RecentChatFragment();
            Bundle args2 = new Bundle();
            args2.putString("friendId",passFriendId);
            args2.putString("friendName",passFriendName);
            args2.putString("friendProfPicURL",passFriendProfPicURL);
            recentChatFragment.setArguments(args2);

        }

        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.add(R.id.chat_body_container, chatBodyFragment,CHAT_BODY_FRAGMENT);
        transaction1.commitAllowingStateLoss();

        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
        transaction2.add(R.id.recent_chat_container, recentChatFragment,RECENT_CHAT_FRAGMENT);
        transaction2.commitAllowingStateLoss();

        send_fab = (FloatingActionButton) findViewById(R.id.send_fab);
        message_edit_text = (EditText) findViewById(R.id.message_edit_text);
        send_fab.setEnabled(false);
        message_edit_text.addTextChangedListener(textWatcher);
        message_edit_text.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(InfixdApp.DEFAULT_MSG_LENGTH_LIMIT)});
        send_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMessageTyped){
                    // GO TO PHOTO PICK ACTION (CHOSE IMAGE)
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(
                            Intent.createChooser(intent, "Complete action using"),
                            RC_PHOTO_PICKER);
                }
                else {
                    Long timeStamp = System.currentTimeMillis();
                    ChatMessage chatMessage = new ChatMessage(message_edit_text.getText().toString(),
                            mUsername, userId, null, timeStamp );
                    mChatDatabaseReference.push().setValue(chatMessage);
                    updateRecentChat(timeStamp);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_chat:
                Intent i = new Intent(this, SearchContactsActivity.class);
                startActivityForResult(i, ADD_NEW_CHAT);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
            if (charSequence.toString().trim().length() > 0 ){
                send_fab.setImageResource(R.drawable.ic_send_white_24dp);
                isMessageTyped =true;
            }
            else if(charSequence.toString().trim().length() == 0 ) {
                send_fab.setImageResource(R.drawable.ic_pick_image_white_24dp);
                isMessageTyped =false;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    public void sendFriendId(RecentChat recentChat) {

        if(recentChat.getFriendId().equals("GroupChat")){
            this.isGroupMessage = true;
            this.chatRoomName = generateGroupChatRoomName(userId,recentChat.getFirstFriendId(),
                    recentChat.getSecondFriendId());

            String toolbarname = recentChat.getFirstFriendFirstName()+ " | "
                    + recentChat.getSecondFriendFirstName();
            toolbar.setTitle(toolbarname);

            this.firstFriendID = recentChat.getFirstFriendId();
            this.secondFriendId = recentChat.getSecondFriendId();

            this.recentChatDatabaseReference = mFireBaseDatabase.getReference().
                    child("RECENT_CHATS").child(userId).child(chatRoomName);
            this.firstFriendRecentChatDBRef = mFireBaseDatabase.getReference().
                    child("RECENT_CHATS").child(firstFriendID).child(chatRoomName);
            this.secondFriendRecentChatDBRef = mFireBaseDatabase.getReference().
                    child("RECENT_CHATS").child(secondFriendId).child(chatRoomName);

        }
        else{
            this.isGroupMessage = false;
            this.chatRoomName = generateChatRoomName(userId,recentChat.getFriendId());
            this.chatFriendId = recentChat.getFriendId();
            this.friendName = recentChat.getFriendName();
            this.chatFriendProfURL = recentChat.getFriendProfPicURL();

            toolbar.setTitle(recentChat.getFriendName());

            this.recentChatDatabaseReference = mFireBaseDatabase.getReference().
                    child("RECENT_CHATS").child(userId).child(chatFriendId);
            this.friendRecentChatDatabaseReference = mFireBaseDatabase.getReference().
                    child("RECENT_CHATS").child(chatFriendId).child(userId);

        }
        mChatDatabaseReference = mFireBaseDatabase.getReference().child("CHAT_ROOMS").
                child(chatRoomName);
        send_fab.setEnabled(true);

        //UPDATE LAST SEEN STATUS
        updateLastSeenTimeToRecentChats(recentChatDatabaseReference);

        // Create fragment and give it an argument specifying the article it should show
        ChatBodyFragment chatBodyFragment = new ChatBodyFragment();
        Bundle args = new Bundle();
        args.putString("chatRoomName",chatRoomName);
        args.putBoolean("isGroupChat",isGroupMessage);
        chatBodyFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.chat_body_container, chatBodyFragment);
        transaction.commitAllowingStateLoss();

        chatBodyEmptyView.setVisibility(View.GONE);

    }

    private void registerBroadcastReceiver(){
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getBooleanExtra("action",false)) updateRecentChat(System.currentTimeMillis());
            }
        };
        LocalBroadcastManager.getInstance(this).
                registerReceiver(mReceiver, new IntentFilter(UPDATE_RECENT_CHAT));
    }

    private void unRegisterBroadcastReceiver(){
        if(mReceiver != null){
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            } catch(IllegalArgumentException e) {

            }
        }
    }

    private void updateRecentChat(Long timeStamp){
        if(isGroupMessage){
            recentChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataSnapshot.getRef().child("lastUpdate").setValue(timeStamp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            firstFriendRecentChatDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        RecentChat recentChat = dataSnapshot.getValue(RecentChat.class);
                        mChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                for(DataSnapshot dsp : dataSnapshot1.getChildren()){
                                    ChatMessage message = dsp.getValue(ChatMessage.class); //get each chat message
                                    if(recentChat.getLastRead() != null){
                                        if((message.getTime() > recentChat.getLastRead())
                                                && !message.getUserId().equals(recentChat.getFirstFriendId())){
                                            unSeenMessageCount = unSeenMessageCount + 1;
                                        }
                                    }
                                    else{
                                        if(!message.getUserId().equals(recentChat.getFirstFriendId())){
                                            unSeenMessageCount = unSeenMessageCount + 1;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        Map<String, Object> updates = new HashMap<String, Object>();
                        updates.put("lastUpdate", timeStamp);
                        updates.put("unReadMessageCount", unSeenMessageCount);
                        dataSnapshot.getRef().updateChildren(updates);
                        unSeenMessageCount = 0;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            secondFriendRecentChatDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        RecentChat recentChat = dataSnapshot.getValue(RecentChat.class);
                        mChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                for(DataSnapshot dsp : dataSnapshot1.getChildren()){
                                    ChatMessage message = dsp.getValue(ChatMessage.class); //get each chat message
                                    if(recentChat.getLastRead() != null){
                                        if((message.getTime() > recentChat.getLastRead())
                                                && !message.getUserId().equals(recentChat.getSecondFriendId())){
                                            unSeenMessageCount = unSeenMessageCount + 1;
                                        }
                                    }
                                    else{
                                        if(!message.getUserId().equals(recentChat.getSecondFriendId())){
                                            unSeenMessageCount = unSeenMessageCount + 1;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        Map<String, Object> updates = new HashMap<String, Object>();
                        updates.put("lastUpdate", timeStamp);
                        updates.put("unReadMessageCount", unSeenMessageCount);
                        dataSnapshot.getRef().updateChildren(updates);
                        unSeenMessageCount = 0;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        else{
            recentChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataSnapshot.getRef().child("lastUpdate").setValue(timeStamp);
                    }
                    else{
                        dataSnapshot.getRef().setValue(new RecentChat(chatFriendId,
                                friendName,chatFriendProfURL,chatRoomName,timeStamp,timeStamp));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            friendRecentChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        RecentChat recentChat = dataSnapshot.getValue(RecentChat.class);
                        mChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                for(DataSnapshot dsp : dataSnapshot1.getChildren()){
                                    ChatMessage message = dsp.getValue(ChatMessage.class); //get each chat message
                                    if(recentChat.getLastRead() != null){
                                        if((message.getTime() >recentChat.getLastRead())
                                                && message.getUserId().equals(userId)){
                                            unSeenMessageCount = unSeenMessageCount + 1;
                                        }
                                    }
                                    else{
                                        if(message.getUserId().equals(userId)){
                                            unSeenMessageCount = unSeenMessageCount + 1;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        Map<String, Object> updates = new HashMap<String, Object>();
                        updates.put("lastUpdate", timeStamp);
                        updates.put("unReadMessageCount", unSeenMessageCount);
                        dataSnapshot.getRef().updateChildren(updates);
                        unSeenMessageCount = 0;
                    }
                    else{
                        mChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                                    ChatMessage message = dsp.getValue(ChatMessage.class); //get each chat message
                                    if(message.getUserId().equals(userId)){
                                        unSeenMessageCount = unSeenMessageCount + 1;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        dataSnapshot.getRef().setValue(new RecentChat(userId,mUsername, userProfPicURL,
                                chatRoomName,timeStamp,timeStamp,unSeenMessageCount));
                        unSeenMessageCount = 0;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            // Update FriendShip Meter in SQLite DB
            FriendshipMeterDBHelper db = new FriendshipMeterDBHelper(ChatActivity.this);
            db.updateMessageCount(chatFriendId);
            db.getAllFMRows().size();
        }
        message_edit_text.setText("");
    }


    public String generateChatRoomName (String first, String second){
        String Name;
        int firstIntVal = generateHashCode(first);
        int secondIntVal = generateHashCode(second);

        if(firstIntVal>secondIntVal){
            Name = first.concat(second);
        }
        else {
            Name = second.concat(first);
        }
        return Name;
    }

    public String generateGroupChatRoomName (String first, String second, String third){
        String Name = null;
        if(first!= null && second!= null && third!= null){
            int firstIntVal = generateHashCode(first);
            int secondIntVal = generateHashCode(second);
            int thirdIntVal = generateHashCode(third);
            int[] nums={firstIntVal,secondIntVal,thirdIntVal};
            Arrays.sort(nums);
            Name = String.valueOf(nums[0]).concat(String.valueOf(nums[1])).concat(String.valueOf(nums[2]));
        }
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

    @Override
    public void onBackPressed() {
       ChatActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NEW_CHAT) {
            if(resultCode == Activity.RESULT_OK){
                Contact contact = (Contact) data.getSerializableExtra("friendDetail");
                RecentChatFragment recentChatFragment = (RecentChatFragment) getSupportFragmentManager()
                        .findFragmentByTag(RECENT_CHAT_FRAGMENT);
                recentChatFragment.updateRecentChatList(contact);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Intent intent = new Intent(SEND_CHAT_IMAGE);
            intent.putExtra("ChatImageURL",selectedImageUri.toString());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public void clear() {
        ChatBodyFragment chatBodyFragment = new ChatBodyFragment();
        Bundle args = new Bundle();
        chatBodyFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.chat_body_container, chatBodyFragment);
        transaction.commitAllowingStateLoss();
    }

    public void updateLastSeenTimeToRecentChats(DatabaseReference recentChatDatabaseReference){
        recentChatDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> updates = new HashMap<String, Object>();
                    updates.put("unReadMessageCount", 0);
                    updates.put("lastRead", System.currentTimeMillis());
                    dataSnapshot.getRef().updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void addChatSendFriendId(Contact friend) {
        chatRoomName = generateChatRoomName(userId,friend.getID());
        this.chatFriendId = friend.getID();
        this.friendName = friend.getName();
        this.chatFriendProfURL = friend.getProfilePicUrl();

        toolbar.setTitleTextColor(getResources().getColor(R.color.white_color));
        toolbar.setTitle(friendName);

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mChatDatabaseReference = mFireBaseDatabase.getReference().child("CHAT_ROOMS").
                child(chatRoomName);
        send_fab.setEnabled(true);

        recentChatDatabaseReference = mFireBaseDatabase.getReference().child("RECENT_CHATS").
                child(userId).child(chatFriendId);
        friendRecentChatDatabaseReference = mFireBaseDatabase.getReference().child("RECENT_CHATS").
                child(chatFriendId).child(userId);

        // Create fragment and give it an argument specifying the article it should show
        ChatBodyFragment chatBodyFragment = new ChatBodyFragment();
        Bundle args = new Bundle();
        args.putString("chatRoomName",chatRoomName);
        args.putBoolean("isGroupChat",false);
        chatBodyFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.chat_body_container, chatBodyFragment);
        transaction.commitAllowingStateLoss();
        chatBodyEmptyView.setVisibility(View.GONE);
    }

    public void showProgressBar(){
        chatView.setVisibility(View.GONE);
        chatLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideProgresbar(){
        chatView.setVisibility(View.VISIBLE);
        chatLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void notifyRecentChatLoadFinish(boolean state) {
        hideProgresbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcastReceiver();
    }
}
