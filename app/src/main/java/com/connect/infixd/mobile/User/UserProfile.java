/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.User;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.BackgroundTasks.UpdateUserProfileBGT;
import com.connect.infixd.mobile.Functions.CropProfilePictureActivity;
import com.connect.infixd.mobile.POJOModels.Datum;
import com.connect.infixd.mobile.POJOModels.GetFBPhotosResponse;
import com.connect.infixd.mobile.POJOModels.Photos;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Retrofit.GraphAPIInterface;
import com.connect.infixd.mobile.Retrofit.RetrofitClient;
import com.connect.infixd.mobile.Services.InfixdUploadService;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.infixd.client.model.UpdateUserProfileWrapper;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Response;

public class UserProfile extends InfixdBaseActivity {

    private ImageView profile_picture_imageview;
    private ConstraintLayout bio_edit;
    private ConstraintLayout profession_edit;
    private ConstraintLayout education_edit;
    private ConstraintLayout location_edit;
    private ImageView fbPermissionIV;
    private ImageView gpPermissionIV;
    private ImageView twitterPermissionIV;
    private ImageView instagramPermissionIV;
    private ImageView mobileNoPermissionIV;
    private TextView about_me_data_textview;
    private TextView profession_data_textview;
    private TextView education_data_textview;
    private TextView location_data_textview;
    private TextView fullName_textview;
    private Uri savedImageUri;
    private Uri tempPicURI;
    private AlertDialog dialog;
    private BroadcastReceiver mReceiver;
    private ProgressBar progressBar;
    private RecyclerView userFbPhotosRV;
    private GridLayoutManager gridLayoutManager;
    private FBPhotosAdapter fbPhotosAdapter;
    private ScrollView scrollView;
    private CardView fbPhotosCardView;
    private String userId;
    private String phoneNoPermission;
    private String fbLink;
    private String gpLink;
    private String twitterLink;
    private String instagramLink;
    private String aboutMe = "";
    private String profession = "";
    private String education = "";
    private String location = "";
    private ArrayList<String> fbPhotosURL;
    private boolean isFbLinkGiven;
    private boolean isGpLinkGiven;
    private boolean isTwitterLinkGiven;
    private boolean isInstaLinkGiven;
    private boolean isPhoneNoPermissionGiven;

    private TwitterAuthClient twitterAuthClient;
    private CallbackManager callbackManager;
    private LoginManager manager;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private GoogleApiClient mGoogleApiClient;
    private GraphAPIInterface graphAPIInterface;

    private static final int PICK_FROM_CAMERA = 100;
    private static final int PICK_FROM_FILE = 200;
    private static final int CROP_IMAGE = 300;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 500;
    private static final String TAG = "UserProfile";
    private static final int GOOGLE_SIGN_IN = 9001;
    private static  int TWITTER_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Bundle bundle = getIntent().getExtras();
        userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
        graphAPIInterface = RetrofitClient.getClient().create(GraphAPIInterface.class);

        phoneNoPermission = bundle.getString("MOBILE_NO_PERMISSION");
        aboutMe = bundle.getString("ABOUT_ME");
        profession = bundle.getString("PROFESSION");
        education = bundle.getString("EDUCATION");
        location = bundle.getString("LOCATION");
        fbLink = bundle.getString("FB_LINK");
        gpLink = bundle.getString("GP_LINK");
        twitterLink = bundle.getString("TWITTER_LINK");
        instagramLink = bundle.getString("INSTAGRAM_LINK");

        // Initialize Views
        profile_picture_imageview = (ImageView) findViewById(R.id.ProfilePicForNtFrndIV);
        fbPermissionIV = (ImageView) findViewById(R.id.facebook_imageview);
        gpPermissionIV = (ImageView) findViewById(R.id.google_plus);
        twitterPermissionIV = (ImageView) findViewById(R.id.twitter_imageview);
        instagramPermissionIV = (ImageView) findViewById(R.id.insta_imageview);
        mobileNoPermissionIV = (ImageView) findViewById(R.id.callIcon);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        fbPhotosCardView = (CardView) findViewById(R.id.fbPhotosCardView);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        userFbPhotosRV = (RecyclerView) findViewById(R.id.my_recycler_view);
        userFbPhotosRV.setFocusable(false);

        if(bundle.getStringArrayList("FB_PHOTOS") != null){
            fbPhotosCardView.setVisibility(View.GONE);
            userFbPhotosRV.setVisibility(View.VISIBLE);
            fbPhotosURL = bundle.getStringArrayList("FB_PHOTOS");
        }
        else {
            fbPhotosURL = new ArrayList<>();
        }

        // set a GridLayoutManager with 2 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2, LinearLayoutManager.HORIZONTAL,false);
        userFbPhotosRV.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        fbPhotosAdapter = new FBPhotosAdapter(UserProfile.this, fbPhotosURL);
        userFbPhotosRV.setAdapter(fbPhotosAdapter); // set the Adapter to RecyclerView

        bio_edit = (ConstraintLayout) findViewById(R.id.about_me_edit);
        profession_edit = (ConstraintLayout) findViewById(R.id.profession_edit);
        location_edit = (ConstraintLayout) findViewById(R.id.location_edit);
        education_edit = (ConstraintLayout) findViewById(R.id.education_edit);
        progressBar = (ProgressBar)findViewById(R.id.progress_dialog);

        fullName_textview = (TextView) findViewById(R.id.fullNameUserProfileTV);
        about_me_data_textview = (TextView) findViewById(R.id.about_me_data_textview);
        profession_data_textview = (TextView) findViewById(R.id.profession_data_textview);
        education_data_textview = (TextView) findViewById(R.id.education_data_textview);
        location_data_textview = (TextView) findViewById(R.id.location_data_textview);

        String profilePicURL = getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL);

        fullName_textview.setText(getPreferenceValue(InfixdApp.STRING_PREFERENCE_FULL_NAME));

        if(phoneNoPermission != null && phoneNoPermission.equals("1")){
            isPhoneNoPermissionGiven = true;
            mobileNoPermissionIV.setImageResource(R.drawable.call_icon_green);
        }

        if(fbLink != null && !fbLink.equals("empty")){
            isFbLinkGiven = true;
            fbPermissionIV.setImageResource(R.drawable.fb);
        }

        if(gpLink != null && !gpLink.equals("empty")){
            isGpLinkGiven = true;
            gpPermissionIV.setImageResource(R.drawable.ggl);
        }

        if(twitterLink != null && !twitterLink.equals("empty")){
            isTwitterLinkGiven = true;
            twitterPermissionIV.setImageResource(R.drawable.twt);
        }

        if(instagramLink != null && !instagramLink.equals("empty")){
            isInstaLinkGiven = true;
            instagramPermissionIV.setImageResource(R.drawable.instagram);
        }

        if (!profilePicURL.isEmpty()) {
            Picasso.with(this)
                    .load(profilePicURL)
                    .into(profile_picture_imageview);
        }

        if(aboutMe != null && !aboutMe.isEmpty()){
            about_me_data_textview.setText("\""+aboutMe.trim()+"\"");
        }

        if(profession != null && !profession.isEmpty()){
            profession_data_textview.setText(bundle.getString("PROFESSION"));
        }

        if(location != null && !location.isEmpty()) {
            location_data_textview.setText(bundle.getString("LOCATION"));
        }

        if(education != null && !education.isEmpty()){
            education_data_textview.setText(bundle.getString("EDUCATION"));
        }

        profile_picture_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestStoragePermission();
            }
        });

        bio_edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserProfile.this, UserProfileEditDialog.class);
                        intent.putExtra("value", about_me_data_textview.getText().toString()
                                .replaceAll("\"", "").trim());
                        startActivityForResult(intent, 1);
                    }
                }
        );
        profession_edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserProfile.this, UserProfileEditDialog.class);
                        intent.putExtra("value", profession_data_textview.getText().toString());
                        startActivityForResult(intent, 2);
                    }
                }
        );
        education_edit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserProfile.this, UserProfileEditDialog.class);
                        intent.putExtra("value", education_data_textview.getText().toString());
                        startActivityForResult(intent, 3);
                    }
                }
        );
        location_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, UserProfileEditDialog.class);
                intent.putExtra("value", location_data_textview.getText().toString());
                startActivityForResult(intent, 4);
            }
        });

        fbPermissionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFbLinkGiven){
                    isFbLinkGiven = false;
                    fbPermissionIV.setImageResource(R.drawable.fbgrey);
                    saveSharedPreference(InfixdApp.STRING_PREFERENCE_FB_LINK, "empty");
                }
                else{
                    authenticateForFacebookLink();
                    isFbLinkGiven = true;
                }

            }
        });

        gpPermissionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGpLinkGiven){
                    isGpLinkGiven = false;
                    gpPermissionIV.setImageResource(R.drawable.gpgrey);
                    saveSharedPreference(InfixdApp.STRING_PREFERENCE_GP_LINK, "empty");
                }
                else{
                    authenticategoogleAccount();
                    isGpLinkGiven = true;
                }

            }
        });

        twitterPermissionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTwitterLinkGiven){
                    isTwitterLinkGiven = false;
                    twitterPermissionIV.setImageResource(R.drawable.tweetergrey);
                    saveSharedPreference(InfixdApp.STRING_PREFERENCE_TWITTER_LINK, "empty");
                }
                else{
                    authenticateTwitterAccount();
                    isTwitterLinkGiven = true;
                }
            }
        });

        instagramPermissionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInstaLinkGiven){
                    // Authenticate Instagram Profile
                }
            }
        });

        mobileNoPermissionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPhoneNoPermissionGiven){
                    isPhoneNoPermissionGiven = false;
                    mobileNoPermissionIV.setImageResource(R.drawable.call_icon_grey);
                    saveSharedPreference(InfixdApp.STRING_PREFERENCE_MOBILE_NUMBER_PREMISSION, "0");
                }
                else {
                    saveSharedPreference(InfixdApp.STRING_PREFERENCE_MOBILE_NUMBER_PREMISSION, "1");
                    mobileNoPermissionIV.setImageResource(R.drawable.call_icon);
                    isPhoneNoPermissionGiven = true;
                }
            }
        });

        fbPhotosCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateForFacebookPhotos();
            }
        });

    }

    private void authenticategoogleAccount() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();

        // Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(UserProfile.this)
                .enableAutoManage(UserProfile.this, mConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void authenticateForFacebookLink(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("public_profile");
        manager = LoginManager.getInstance();
        manager.logInWithReadPermissions(UserProfile.this, permissionNeeds);
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String fbLink = loginResult.getAccessToken().getUserId().toString();
                saveSharedPreference(InfixdApp.STRING_PREFERENCE_FB_LINK, fbLink);
                fbPermissionIV.setImageResource(R.drawable.fb);

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {

                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void authenticateForFacebookPhotos(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("user_photos");
        manager = LoginManager.getInstance();
        manager.logInWithReadPermissions(UserProfile.this, permissionNeeds);
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserFBPhotos(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {

                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void authenticateTwitterAccount(){
        // Configure Twitter SDK
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        Fabric.with(UserProfile.this, new TwitterCore(authConfig));

        twitterAuthClient = new TwitterAuthClient();
        TWITTER_SIGN_IN = twitterAuthClient.getRequestCode();
        twitterAuthClient.authorize(UserProfile.this, new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> result) {
                // Save Twitter Link to SharedPreference
                final TwitterSession sessionData = result.data;
                saveSharedPreference(InfixdApp.STRING_PREFERENCE_TWITTER_LINK, String.valueOf(sessionData.getUserName()));
                twitterPermissionIV.setImageResource(R.drawable.twt);

            }

            @Override
            public void failure(final TwitterException e) {
                Log.d(TAG, "facebook:onError", e);
                // Do something on fail
            }
        });
    }

    private void saveGpLink(GoogleSignInAccount acct){
        saveSharedPreference(InfixdApp.STRING_PREFERENCE_GP_LINK, acct.getId());
        gpPermissionIV.setImageResource(R.drawable.ggl);
    }

    private void openPhotoPicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Complete action using"), PICK_FROM_FILE);

      /*  // Some Issue with choosing camera have to do it as an improvement
        final String[] items = new String[] { "Take from camera", "Select from gallery" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserProfile.this,
                android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Select an Option");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent cameraIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File picFile = createTempFile();
                    tempPicURI = Uri.fromFile(picFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,tempPicURI);
                    startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image*//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Complete action using"), PICK_FROM_FILE);
                }
            }
        });
        dialog = builder.create();
        dialog.show();*/
    }

    private  File createTempFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Infixd/temp");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="temp"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }


    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                String value = data.getStringExtra("result").trim();
                if(value != null && !value.isEmpty()){
                    about_me_data_textview.setText("\""+value+"\"");
                    aboutMe = value;
                }
            }
        }
        else if(requestCode == 2){
            if(resultCode == Activity.RESULT_OK){
                String value = data.getStringExtra("result").trim();
                if(value != null && !value.isEmpty()){
                    profession_data_textview.setText(value);
                    profession = value;
                }

            }
        }
        else if(requestCode == 3){
            if(resultCode == Activity.RESULT_OK){
                String value = data.getStringExtra("result").trim();
                if(value != null && !value.isEmpty()){
                    education_data_textview.setText(value);
                    education = value;
                }

            }
        }
        else if(requestCode == 4){
            if(resultCode == Activity.RESULT_OK){
                String value = data.getStringExtra("result").trim();
                if(value != null && !value.isEmpty()){
                    location_data_textview.setText(value);
                    location = value;
                }

            }
        }
        else if(requestCode == PICK_FROM_CAMERA){
            if (resultCode == RESULT_OK && tempPicURI != null) {
                Intent intent = new Intent(this, CropProfilePictureActivity.class);
                intent.putExtra("imageUri", tempPicURI.toString());
                startActivityForResult(intent, CROP_IMAGE);
            }
            else{
                // handle user not selecting an image
            }

        }
        else if(requestCode == PICK_FROM_FILE){
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                Intent intent = new Intent(this, CropProfilePictureActivity.class);
                intent.putExtra("imageUri", selectedImage.toString());
                startActivityForResult(intent, CROP_IMAGE);
            }
            else{
                // handle user not selecting an image
            }
        }
        else if (requestCode == CROP_IMAGE && resultCode == Activity.RESULT_OK){
            String imageUri = data.getStringExtra("savedUri");
            savedImageUri = Uri.parse(imageUri);
            File f = new File(getRealPathFromURI(savedImageUri));
            Drawable d = Drawable.createFromPath(f.getAbsolutePath());
            profile_picture_imageview.setImageDrawable(d);
        }
        else if (requestCode == CROP_IMAGE && resultCode == Activity.RESULT_CANCELED){

        }
        else if(requestCode == TWITTER_SIGN_IN){
            // Pass the activity result to the Twitter login button.
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
        else if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                saveGpLink(account);
            }
        }
        else {
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void RequestStoragePermission(){
        if (ContextCompat.checkSelfPermission(UserProfile.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserProfile.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }
        else{
            openPhotoPicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openPhotoPicker();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String storageUrl = intent.getStringExtra(InfixdApp.EXTRA_STORAGE_URL);
                String downloadUrl = intent.getStringExtra(InfixdApp.EXTRA_DOWNLOAD_URL);
                saveSharedPreference(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL,downloadUrl);
                saveSharedPreference(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_STORAGE_URL,storageUrl);
                updateProfile();
                hideProgresbar();
                UserProfile.this.finish();
            }
        };
        //registering our receiver
        UserProfile.this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister our receiver
        UserProfile.this.unregisterReceiver(this.mReceiver);
    }

    public void updateProfile(){
        String profilePicUrl = getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL);
        String profilePicStorageUrl = getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_STORAGE_URL);
        String instagramLink = getPreferenceValue(InfixdApp.STRING_PREFERENCE_INSTAGRAM_LINK);
        String fbLink = getPreferenceValue(InfixdApp.STRING_PREFERENCE_FB_LINK);
        String TwitterLink = getPreferenceValue(InfixdApp.STRING_PREFERENCE_TWITTER_LINK);
        String googlePlusLink = getPreferenceValue(InfixdApp.STRING_PREFERENCE_GP_LINK);
        String mobileNumberPermission = getPreferenceValue(InfixdApp.STRING_PREFERENCE_MOBILE_NUMBER_PREMISSION);
        String friendshipMeterPermission = "1";

        UpdateUserProfileWrapper updateUserProfileWrapper = new UpdateUserProfileWrapper();
        updateUserProfileWrapper.setUserId(userId);
        updateUserProfileWrapper.setAboutMe(aboutMe);
        updateUserProfileWrapper.setFblink(fbLink);
        updateUserProfileWrapper.setGooglePlusLink(googlePlusLink);
        updateUserProfileWrapper.setInstagramLink(instagramLink);
        updateUserProfileWrapper.setTwitterLink(TwitterLink);
        updateUserProfileWrapper.setProfPicUrl(profilePicUrl);
        updateUserProfileWrapper.setProfPicStorageUrl(profilePicStorageUrl);
        updateUserProfileWrapper.setProfession(profession);
        updateUserProfileWrapper.setEducation(education);
        updateUserProfileWrapper.setCity(location);
        updateUserProfileWrapper.setPhoneNoPermission(mobileNumberPermission);
        updateUserProfileWrapper.setFriendshipMeterPermission(friendshipMeterPermission);
        updateUserProfileWrapper.setFbPhotos(fbPhotosURL);

        UpdateUserProfileBGT updateUserProfileBGT = new UpdateUserProfileBGT(UserProfile.this);
        updateUserProfileBGT.execute(updateUserProfileWrapper);
    }

    @Override
    public void onBackPressed() {
        if (savedImageUri != null) {
            showProgressBar();
            Intent intent = new Intent(UserProfile.this, InfixdUploadService.class);
            intent.setAction("action_upload");
            intent.putExtra("pc", savedImageUri);
            intent.putExtra(InfixdApp.STRING_PREFERENCE_USER_ID, userId);
            intent.putExtra("code", InfixdApp.ACTION_UPLOAD_PROFILE_PHOTO);
            intent.putExtra("category", "photos");
            UserProfile.this.startService(intent);
        } else {
            updateProfile();
            String profilePicImage = getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("profilePictureURL", profilePicImage);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    private void getUserFBPhotos(String accessToken){
        Call<GetFBPhotosResponse> call = graphAPIInterface.getFacebookPhotos(accessToken);
        call.enqueue(new retrofit2.Callback<GetFBPhotosResponse>() {
            @Override
            public void onResponse(Call<GetFBPhotosResponse> call, Response<GetFBPhotosResponse> response) {
                Log.d(TAG, "retrofit:onCancel");
                GetFBPhotosResponse result = response.body();
                if(result != null && result.getPhotos() != null){
                    Photos photos = result.getPhotos();
                    List<Datum> data = photos.getData();
                    for (Datum datum:data){
                        fbPhotosURL.add(datum.getImages().get(0).getSource());
                    }
                }
                fbPhotosCardView.setVisibility(View.GONE);
                userFbPhotosRV.setVisibility(View.VISIBLE);
                fbPhotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<GetFBPhotosResponse> call, Throwable t) {
                Log.d(TAG, "retrofit:onCancel");
            }
        });
    }

    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgresbar(){
        progressBar.setVisibility(View.GONE);
    }

}
