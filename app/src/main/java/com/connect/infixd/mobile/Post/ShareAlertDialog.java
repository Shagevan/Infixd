/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Post;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;
import java.util.List;

public class ShareAlertDialog extends InfixdBaseActivity {
    private ImageView fb_image_view;
    private ImageView gg_image_view;
    private ImageView twitter_image_view;
    private ImageView linkedin_image_view;
    private TextView shareAlertTitleTV;
    private Button share_btn;
    private int fbIndex = 0;
    private int gpIndex = 0;
    private int liIndex = 0;
    private int twtIndex = 0;
    private CallbackManager callbackManager;
    private LoginManager manager;
    private String postId;
    private String userId;
    private String caption;
    private String imageUri;
    private String shareAlertDialogTitle;
    private static final String TAG = "FacebookLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_share_alert_dialog);

        postId = getIntent().getStringExtra("postId");
        userId = getIntent().getStringExtra("userId");
        caption = getIntent().getStringExtra("caption");
        imageUri = getIntent().getStringExtra("imageUri");
        shareAlertDialogTitle = getIntent().getStringExtra("shareAlertDialogTitle");


        fb_image_view = (ImageView) findViewById(R.id.fb_image_view);
        gg_image_view = (ImageView) findViewById(R.id.gg_image_view);
        twitter_image_view = (ImageView) findViewById(R.id.twitter_image_view);
        linkedin_image_view = (ImageView) findViewById(R.id.linkedin_image_view);
        shareAlertTitleTV = (TextView) findViewById(R.id.shareAlertTitleTV);
        share_btn = (Button) findViewById(R.id.share_btn);
        share_btn.setEnabled(false);

        if(shareAlertDialogTitle != null){
            shareAlertTitleTV.setText(shareAlertDialogTitle);
        }

        fb_image_view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ++fbIndex;
                        if(fbIndex%2 == 1){
                            fb_image_view.setImageResource(R.drawable.fb);
                            share_btn.setEnabled(true);
                        }
                        else {
                            fb_image_view.setImageResource(R.drawable.fbgrey);
                        }
                    }
                }
        );

        gg_image_view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ++gpIndex;
                        if(gpIndex%2 == 1){
                            gg_image_view.setImageResource(R.drawable.ggl);
                        }
                        else {
                            gg_image_view.setImageResource(R.drawable.gpgrey);
                        }

                    }
                }
        );

        twitter_image_view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ++twtIndex;
                        if(twtIndex%2 == 1){
                            twitter_image_view.setImageResource(R.drawable.twt);
                            share_btn.setEnabled(true);
                        }
                        else {
                            twitter_image_view.setImageResource(R.drawable.tweetergrey);
                        }

                    }
                }
        );

        share_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(fbIndex%2 != 0){
                            startLoginManager();
                        }
                    }
                }
        );
    }

    private void checkIndexces(){
        if (fbIndex%2 ==1 || gpIndex%2 ==1 || twtIndex%2 == 1 || liIndex%2 ==1){
            share_btn.setEnabled(true);
        }
        else {
            share_btn.setEnabled(false);
        }
    }

    private void startLoginManager(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Arrays.asList("publish_actions");
        manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(ShareAlertDialog.this, permissionNeeds);
        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                uploadFacebookLink(loginResult.getAccessToken());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFacebookLink(AccessToken token) {
        if (imageUri == null || imageUri.isEmpty()){
            Bundle params = new Bundle();
            params.putString("message", caption);
            //make the API call
            new GraphRequest(token, "/me/feed", params, HttpMethod.POST,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            Toast.makeText(ShareAlertDialog.this, "successfully posted", Toast.LENGTH_LONG).show();
                        }
                    }).executeAsync();
            ShareAlertDialog.this.finish();
        }
        else {
            if(caption == null || caption.isEmpty()){
                Bundle params = new Bundle();
                params.putString("url", imageUri);
                //make the API call
                new GraphRequest(token, "/me/photos", params, HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Toast.makeText(ShareAlertDialog.this, "successfully posted", Toast.LENGTH_LONG).show();
                            }
                        }).executeAsync();
                ShareAlertDialog.this.finish();
            }
            else {
                Bundle params = new Bundle();
                params.putString("caption", caption);
                params.putString("url", imageUri);
                //make the API call
                new GraphRequest(token, "/me/photos", params, HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Toast.makeText(ShareAlertDialog.this, "successfully posted", Toast.LENGTH_LONG).show();
                            }
                        }).executeAsync();
                ShareAlertDialog.this.finish();
            }

        }
    }

}
