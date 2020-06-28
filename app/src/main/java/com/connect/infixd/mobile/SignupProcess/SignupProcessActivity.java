/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.SignupProcess;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.badoualy.stepperindicator.StepperIndicator;
import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.CustomViews.CustomViewPager;
import com.connect.infixd.mobile.Interfaces.DirectToHome;
import com.connect.infixd.mobile.Interfaces.NavigateFragment;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.UserHome.UserHomeActivity;
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

public class SignupProcessActivity extends InfixdBaseActivity implements NavigateFragment,DirectToHome {

    private CustomViewPager customViewPager;
    private SignupProcessAdapter signupProcessAdapter;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private StepperIndicator stepperIndicator;
    private String tag_fragment_second_step;
    private CallbackManager callbackManager;
    private LoginManager manager;
    private static final String TAG = "SignupProcessActivity";

    public String getTag_fragment_second_step() {
        return tag_fragment_second_step;
    }

    public void setTag_fragment_second_step(String tag_fragment_second_step) {
        this.tag_fragment_second_step = tag_fragment_second_step;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_process);

        Bundle bundle = getIntent().getExtras();
        firstName = bundle.getString(InfixdApp.STRING_PREFERENCE_FIRST_NAME);
        lastName = bundle.getString(InfixdApp.STRING_PREFERENCE_LAST_NAME);
        mobileNumber = bundle.getString(InfixdApp.STRING_PREFERENCE_MOBILE_NUMBER);

        customViewPager = (CustomViewPager) findViewById(R.id.signupViewPager);
        customViewPager.setSwipeLocked(true);
        stepperIndicator = (StepperIndicator) findViewById(R.id.stepper_indicator);

        signupProcessAdapter= new SignupProcessAdapter(getSupportFragmentManager(),firstName,lastName,mobileNumber);
        customViewPager.setAdapter(signupProcessAdapter);
        stepperIndicator.setViewPager(customViewPager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager != null){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        String secondFragmentTag = getTag_fragment_second_step();
        SignupSecondStep signupSecondStep = (SignupSecondStep) getSupportFragmentManager().findFragmentByTag(secondFragmentTag);
        signupSecondStep.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void doNavigation(int code, boolean value) {
        if(value == true){
            switch (code){
                case 1:
                    customViewPager.setCurrentItem(1);
                    break;
                case 2:
                    customViewPager.setCurrentItem(2);
                    break;
            }
        }
    }

    @Override
    public void directToHome(boolean value) {
        postImOnInfixdtoFB();
    }

    private void postImOnInfixdtoFB(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(getResources().getString(R.string.im_on_infixd_msg));

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        callbackManager = CallbackManager.Factory.create();
                        List<String> permissionNeeds = Arrays.asList("publish_actions");
                        manager = LoginManager.getInstance();
                        manager.logInWithPublishPermissions(SignupProcessActivity.this,
                                permissionNeeds);
                        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                dialog.dismiss();
                                uploadFacebookLink(loginResult.getAccessToken());
                                Intent intent = new Intent(SignupProcessActivity.this,
                                        UserHomeActivity.class);
                                startActivity(intent);
                                finish();
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
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(SignupProcessActivity.this,
                                UserHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        android.app.AlertDialog dialog = builder.create();

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.im_on_infixd_layout, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        // Must call show() prior to fetching text view
        TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary_color));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary_color));

    }

    private void uploadFacebookLink(AccessToken token) {
        Bundle params = new Bundle();
        params.putString("caption", "http://infixd.com");
        params.putString("url", InfixdApp.IM_ON_INFIXD_IMAGE);
        //make the API call
        new GraphRequest(token, "/me/photos", params, HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                    }
                }).executeAsync();
    }

}
