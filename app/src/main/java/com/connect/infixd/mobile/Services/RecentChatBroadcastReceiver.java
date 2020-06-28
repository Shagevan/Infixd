package com.connect.infixd.mobile.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RecentChatBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(RecentChatBroadcastReceiver.class.getSimpleName(), "Recentchat Service Stops!");
        context.startService(new Intent(context, RecentChatBroadcastReceiver.class));;
    }
}
