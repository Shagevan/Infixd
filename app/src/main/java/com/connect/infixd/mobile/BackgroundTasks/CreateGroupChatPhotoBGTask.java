package com.connect.infixd.mobile.BackgroundTasks;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.connect.infixd.mobile.Dialogs.DialogFactory;
import com.connect.infixd.mobile.POJOModels.RecentChat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class CreateGroupChatPhotoBGTask extends AsyncTask<String, Void, Boolean> {
    private Context ctx;
    private Activity activity;
    private static final int MY_RECENT_CHAT = 10;
    private static final int FIRST_FRIEND_RECENT_CHAT = 20;
    private static final int SECOND_FRIEND_RECENT_CHAT = 60;
    public static final String DEFAULT_PROFILE_PIC_URL = "https://firebasestorage.googleapis.com/v0/b/strapd-162605.appspot.com/o/photos%2F0195f255-6d5b-4789-be72-5234b0066057?alt=media&token=ac044a3d-bd9e-44bd-b793-6e1d99414ddd";

    private FirebaseStorage mFirebaseStorage;
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private DatabaseReference recentChatDBRef;
    private DatabaseReference firstFriendRecentChatDBRef;
    private DatabaseReference secondFriendRecentChatDBRef;
    private StorageReference groupProfPicStorageRef;

    public ProgressDialog mProgressDialog;

    String userId;
    String userName;
    String userProfPicURL;
    String firstFriendId;
    String firstFriendName;
    String firstFriendProfPicURL;
    String secondFriendId;
    String secondFriendName;
    String secondFriendProfPicURL;
    Bitmap myGroupBitmap;
    Bitmap firstFrGroupBitmap;
    Bitmap secondFrGroupBitmap;
    String chatRoomName;
    String myGroupName;
    String firstFrGroupName;
    String secondFrGroupName;
    Long date;

    public CreateGroupChatPhotoBGTask(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(ctx);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        userId = params[0];
        userName = params[1];
        userProfPicURL = params[2];
        firstFriendId = params[3];
        firstFriendName = params[4];
        firstFriendProfPicURL = params[5];
        secondFriendId = params[6];
        secondFriendName = params[7];
        secondFriendProfPicURL = params[8];

        if(userProfPicURL == null || userProfPicURL.isEmpty()){
            userProfPicURL = DEFAULT_PROFILE_PIC_URL;
        }

        if(firstFriendProfPicURL == null || firstFriendProfPicURL.isEmpty()){
            firstFriendProfPicURL = DEFAULT_PROFILE_PIC_URL;
        }

        if(secondFriendProfPicURL == null || secondFriendProfPicURL.isEmpty()){
            secondFriendProfPicURL = DEFAULT_PROFILE_PIC_URL;
        }

        date = System.currentTimeMillis();

        myGroupBitmap = combineImages(getBitmapFromURL(firstFriendProfPicURL),
                getBitmapFromURL(secondFriendProfPicURL));

        firstFrGroupBitmap = combineImages(getBitmapFromURL(userProfPicURL),
                getBitmapFromURL(secondFriendProfPicURL));

        secondFrGroupBitmap = combineImages(getBitmapFromURL(userProfPicURL),
                getBitmapFromURL(firstFriendProfPicURL));

        chatRoomName = generateGroupChatRoomName(userId,firstFriendId,secondFriendId);
        myGroupName = firstFriendName.charAt(0)+"."+secondFriendName.charAt(0);
        firstFrGroupName = userName.charAt(0)+"."+secondFriendName.charAt(0);
        secondFrGroupName = userName.charAt(0)+"."+firstFriendName.charAt(0);

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mChatDatabaseReference = mFireBaseDatabase.getReference().child("CHAT_ROOMS").child(chatRoomName);

        recentChatDBRef = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(userId).child(chatRoomName);
        firstFriendRecentChatDBRef = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(firstFriendId).child(chatRoomName);
        secondFriendRecentChatDBRef = mFireBaseDatabase.getReference().child("RECENT_CHATS").child(secondFriendId).child(chatRoomName);

        createGroupChat(MY_RECENT_CHAT,myGroupBitmap);
        createGroupChat(FIRST_FRIEND_RECENT_CHAT,firstFrGroupBitmap);
        createGroupChat(SECOND_FRIEND_RECENT_CHAT,secondFrGroupBitmap);

        return true;

    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /** Create a File for saving Group ProfilePhoto **/
    private  File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Infixd/Media/UserProfilePhoto");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="Infixd"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public void createGroupChat(int type, Bitmap bitmap){
        Uri selectedImageUri = null;
        try {
            File file = getOutputMediaFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            selectedImageUri = Uri.fromFile(file);
            out.flush();
            out.close();
        } catch(Exception e){
            e.getMessage();
        }

        // Get a reference to store file at chat_photos/<FILENAME>
        mFirebaseStorage = FirebaseStorage.getInstance();
        groupProfPicStorageRef = mFirebaseStorage.getReference().child("Group_profile_photos");
        StorageReference photoRef = groupProfPicStorageRef.child(selectedImageUri.getLastPathSegment());

        // Upload file to Firebase Storage
        photoRef.putFile(selectedImageUri)
                .addOnSuccessListener(activity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // When the image has successfully uploaded, we get its download URL
                        @SuppressWarnings("VisibleForTests")
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if(type == MY_RECENT_CHAT){
                            recentChatDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    RecentChat recentChat = (new RecentChat("GroupChat",myGroupName,downloadUrl.toString(),chatRoomName,date,date));
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
                        }
                        else if(type == FIRST_FRIEND_RECENT_CHAT){
                            firstFriendRecentChatDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    RecentChat recentChat = (new RecentChat("GroupChat",firstFrGroupName,downloadUrl.toString(),chatRoomName,date,date));
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

                        }
                        else if(type == SECOND_FRIEND_RECENT_CHAT){
                            secondFriendRecentChatDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    RecentChat recentChat = (new RecentChat("GroupChat",secondFrGroupName,downloadUrl.toString(),chatRoomName,date,date));
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
                })
                .addOnFailureListener(e -> {
                    DialogFactory.getInstance().make(DialogFactory.REPORT_ERROR,
                            activity.getCurrentFocus(), view -> { /* TODO: Handle report */}).show();

                });
    }

    public Bitmap getBitmapFromURL(String src) {
        URL url = null;
        Bitmap image = null;
        try {
            url = new URL(src);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            image = BitmapFactory.decodeStream(url.openStream());
            image = Bitmap.createScaledBitmap(image, 120, 120, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public Bitmap combineImages(Bitmap c, Bitmap s) {
        // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        return cs;
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
}