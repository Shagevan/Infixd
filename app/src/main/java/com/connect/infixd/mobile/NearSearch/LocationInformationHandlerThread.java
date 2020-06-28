package com.connect.infixd.mobile.NearSearch;


import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.infixd.client.InfixdClient;
import com.infixd.client.model.UpdateLocationResponse;

public class LocationInformationHandlerThread extends HandlerThread {
    private static final String TAG = "LocationInfoHandler";
    private static final String LATEST_LOCATION_UPDATE_TAG = TAG + "latestLocationUpdateTag";

    private Handler workerHandler;
    private LocationUdateReceiver receivingActivity;
    private boolean locationSharingEnabled = false;
    private InfixdClient clientApi;

    public LocationInformationHandlerThread(LocationUdateReceiver receiver) {
        super(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        Log.i(TAG, "Instance created!");
        clientApi = new InfixdClient();
        receivingActivity = receiver;
    }

    @Override
    protected void onLooperPrepared() {
        workerHandler = new Handler(getLooper());
    }

    public void sendLocationToServer(String senderId, double lat, double lon) {
        if (workerHandler != null) {
            Log.i(TAG, "New near by friends request. Lat = " +  lat
                    + " Lon = " + lon + " senderId = " + senderId);

            workerHandler.removeCallbacksAndMessages(null); //remove everything
            workerHandler.post(new LocationUpdate(lat, lon, senderId, clientApi, receivingActivity));
        }
    }

    public interface LocationUdateReceiver {
        void handleLocationUpdate(UpdateLocationResponse locationResponse);
    }

    private static class LocationUpdate implements Runnable {
        private String lon;
        private String lat;
        private String senderId;
        private InfixdClient client;
        private LocationUdateReceiver receiver;

        LocationUpdate(double lat, double lon, String senderId, InfixdClient client,
                       LocationUdateReceiver receiver) {
            this.lon = Double.toString(lon);
            this.lat = Double.toString(lat);
            this.senderId = senderId;
            this.client = client;
            this.receiver = receiver;
        }

        @Override
        public void run() {
            try {
                UpdateLocationResponse locationResponse = client.updateLocation(senderId, lat, lon);
                Log.i(TAG, "Retrieved locations successfully");
                receiver.handleLocationUpdate(locationResponse);
            } catch (Exception e) {
                Log.e(TAG, "Location update failed", e);
                receiver.handleLocationUpdate(null);
            }

        }
    }
}
