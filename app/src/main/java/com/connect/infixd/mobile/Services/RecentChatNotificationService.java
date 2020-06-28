package com.connect.infixd.mobile.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.connect.infixd.mobile.Chat.ChatActivity;
import com.connect.infixd.mobile.POJOModels.RecentChat;
import com.connect.infixd.mobile.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecentChatNotificationService extends Service implements ValueEventListener {

    private FirebaseDatabase rFireBaseDatabase;
    private DatabaseReference recentChatDatabaseReference;
    private String userId;
    private static final String RESTART_RECENT_CHAT_NOTIFICATION_SERVICE = "RecentChatNotificationService.RestartService";
    private static final String NOTIFY_NEW_MESSAGE = "RecentChatNotificationService.NotifyNewMessage";
    private boolean isNewMessage;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private Intent intent;
    private PendingIntent pendingIntent;

    public RecentChatNotificationService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public RecentChatNotificationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            userId = intent.getStringExtra("userId");
            if(userId != null && !userId.isEmpty()){
                rFireBaseDatabase = FirebaseDatabase.getInstance();
                recentChatDatabaseReference = rFireBaseDatabase.getReference().child("RECENT_CHATS").child(userId);
                recentChatDatabaseReference.addValueEventListener(this);
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(RESTART_RECENT_CHAT_NOTIFICATION_SERVICE);
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists()){
            isNewMessage = false;
            ArrayList<RecentChat> lst = new ArrayList<RecentChat>(); // Result will be holded Here

            for(DataSnapshot dsp : dataSnapshot.getChildren()){
                lst.add(dsp.getValue(RecentChat.class)); //add result into array list
            }

            for (RecentChat rChat : lst){
                if(rChat.getUnReadMessageCount()>0){
                    isNewMessage = true;
                    sendNewMessagePushNotification();
                    break;
                }
            }
        }

        Intent broadcastIntent = new Intent(NOTIFY_NEW_MESSAGE);
        broadcastIntent.putExtra("isNewMessage",isNewMessage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void sendNewMessagePushNotification(){
        intent= new Intent(this, ChatActivity.class);
        intent.setAction("Fresh_Chat");
        pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_done_white_18dp)
                .setLights(Color.RED, 3000, 3000)
                .setDefaults(android.app.Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentTitle("Infixd")
                .setContentText("You have received new message on Infixd");
        if(pendingIntent != null){
            builder.setContentIntent(pendingIntent);
        }
        manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
