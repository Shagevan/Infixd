/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.SignupProcess.LauncherActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class InfixdUploadService extends InfixdStorageService{

    private static final String TAG = "MyUploadService";
    private static final int NOTIF_ID_DOWNLOAD = 0;
    String CODE = "";


    // [START declare_ref]
    private StorageReference mStorageRef;
    // [END declare_ref]

    byte[] data;

    private String userId;

    @Override
    public void onCreate() {
        super.onCreate();

        // [START get_storage_ref]
           mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://strapd-162605.appspot.com");
        // [END get_storage_ref]
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);
        if (InfixdApp.ACTION_UPLOAD.equals(intent.getAction())) {
            CODE = intent.getStringExtra("code");
            userId = intent.getStringExtra(InfixdApp.STRING_PREFERENCE_USER_ID);
            Uri fileUri = intent.getParcelableExtra("pc");
            String category = intent.getStringExtra("category");
            uploadFromUri(fileUri, category);
        }

        return START_REDELIVER_INTENT;
    }

    // [START upload_from_uri]
    private void uploadFromUri(final Uri fileUri, final String category) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        StorageReference photoRef;

        // [START_EXCLUDE]
        taskStarted();
        // [END_EXCLUDE]

        // [START get_child_ref]
        // Get a reference to store file at photos/<FILENAME>.jpg

        if(CODE.equals(InfixdApp.ACTION_UPLOAD_PROFILE_PHOTO)){
            photoRef = mStorageRef.child(category).child(userId);
        }

        else if (CODE.equals(InfixdApp.ACTION_UPLOAD_POST_IMAGE)){
            showUploadProgressNotification();
            photoRef = mStorageRef.child(category)
                    .child(fileUri.getLastPathSegment());
        }
        // just kept for another contexts
        else {
            showUploadProgressNotification();
            photoRef = mStorageRef.child(category)
                    .child(fileUri.getLastPathSegment());
        }

        // [END get_child_ref]
        // Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());

        photoRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri:onSuccess");

                        // Get the public storage URL & download URL
                        String storageUrl = taskSnapshot.getMetadata().getReference().toString();
                        String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                        // [START_EXCLUDE]
                        broadcastUploadFinished(CODE,storageUrl, downloadUrl);
                        showUploadFinishedNotification(downloadUrl.toString(), fileUri.toString());
                        taskCompleted();
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        // [START_EXCLUDE]
                        broadcastUploadFinished(CODE, null, fileUri.toString());
                        showUploadFinishedNotification(null, fileUri.toString());
                        taskCompleted();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END upload_from_uri]

    /**
     * Broadcast finished upload (success or failure).
     * @return true if a running receiver received the broadcast.
     */

    private void broadcastUploadFinished(String code, @Nullable String storageUrl, @Nullable String downloadUrl) {

        boolean success = storageUrl != null;

        String action = success ? InfixdApp.UPLOAD_COMPLETED : InfixdApp.UPLOAD_ERROR;

        Intent broadcast = new Intent("android.intent.action.MAIN")
                .putExtra(InfixdApp.EXTRA_STORAGE_URL, storageUrl)
                .putExtra(InfixdApp.EXTRA_DOWNLOAD_URL, downloadUrl);
                this.sendBroadcast(broadcast);
    }

    /**
     * Show a notification for a finished upload.
     */
    private void showUploadFinishedNotification(@Nullable String downloadUrl, @Nullable String fileUri) {
        // Make Intent to MainActivity
        Intent intent = new Intent(this, LauncherActivity.class)
                .putExtra(InfixdApp.EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(InfixdApp.EXTRA_FILE_URI, fileUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Make PendingIntent for notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* requestCode */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Set message and icon based on success or failure
        boolean success = downloadUrl != null;
        String message = success ? "Upload finished" : "Upload failed";
        int icon = success ? R.drawable.ic_check_white_24 : R.drawable.ic_error_white_24dp;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIF_ID_DOWNLOAD, builder.build());
    }

    /**
     * Show notification with an indeterminate upload progress bar.
     */
    private void showUploadProgressNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_file_upload_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Uploading...")
                .setProgress(0, 0, true)
                .setOngoing(true)
                .setAutoCancel(false);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIF_ID_DOWNLOAD, builder.build());
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(InfixdApp.UPLOAD_COMPLETED);
        filter.addAction(InfixdApp.UPLOAD_ERROR);

        return filter;
    }
}