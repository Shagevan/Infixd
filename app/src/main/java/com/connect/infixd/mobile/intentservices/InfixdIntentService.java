/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.intentservices;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public abstract class InfixdIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public InfixdIntentService(String name) {
        super(name);
    }

    public static class BroadcastReceiver extends android.content.BroadcastReceiver {
        private Receiver mReceiver;

        public BroadcastReceiver(Receiver receiver) {
            super();
            mReceiver = receiver;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mReceiver != null)
                mReceiver.onReceive(context, intent);
        }

        public void unregister(Context context) {
            if (context != null) {
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
            }
        }
    }

    public interface Receiver {
        void onReceive(Context context, Intent intent);
    }
}
