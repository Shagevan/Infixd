/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.SignupProcess;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Interfaces.NavigateFragment;
import com.connect.infixd.mobile.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

public class SignupSecondStep extends Fragment {

    public ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;
    private LoginButton facebookLoginButton;
    private TwitterLoginButton twitterLoginButton;
    private String profPicLink = "";
    private NavigateFragment navigateFragment;
    private TextView skip;
    private CardView fbCard;
    private CardView twitterCard;
    private CardView gplusCard;
    private ImageView fbTick;
    private ImageView gTick;
    private ImageView twtTick;
    private SignupProcessActivity signupActivity;
    private static final String SEND_USER_PROFILE_PIC = "user_profile_picture";
    private static final String TAG = "FacebookLogin";
    private static final int RC_SIGN_IN = 9001;
    private String fbPermissions[];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String myTag = getTag();
        ((SignupProcessActivity)getActivity()).setTag_fragment_second_step(myTag);

        signupActivity = ((SignupProcessActivity)getActivity());

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();

        // Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, mConnectionFailedListener /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getContext());

        // Configure Twitter SDK
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        Fabric.with(getActivity(), new TwitterCore(authConfig));

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize auth_state_listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

        // Initialize connection_failed_listener
        mConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                // An unresolvable error has occurred and Google APIs (including Sign-In) will not
                // be available.
                Log.d(TAG, "onConnectionFailed:" + connectionResult);
                Toast.makeText(getContext(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup_second_step, container, false);
        skip = (TextView) v.findViewById(R.id.skipStepTwo);
        fbCard=(CardView) v.findViewById(R.id.cardView1);
        twitterCard=(CardView) v.findViewById(R.id.cardView3);
        gplusCard=(CardView) v.findViewById(R.id.cardView2);

        fbTick=(ImageView) v.findViewById(R.id.fbTick);
        gTick=(ImageView) v.findViewById(R.id.googleTick);
        twtTick=(ImageView) v.findViewById(R.id.twitterTick);

        fbPermissions = new String[]{"public_profile"};
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) v.findViewById(R.id.button_facebook_login);
        facebookLoginButton.setFragment(this);
        facebookLoginButton.setReadPermissions(Arrays.asList(fbPermissions));
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
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

        // [START initialize_twitter_login]
        twitterLoginButton = (TwitterLoginButton) v.findViewById(R.id.button_twitter_login);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);

            }
        });
        // [END initialize_twitter_login]

        fbCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                facebookLoginButton.performClick();
            }
        });

        twitterCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                twitterLoginButton.performClick();
            }
        });

        gplusCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                googleSignIn();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateFragment.doNavigation(2,true);
            }
        });

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // [START on_activity_result]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
            }
        }
        else if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE){
            // Pass the activity result to the Twitter login button.
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
        else{
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    // [END on_activity_result]


    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //fb.setImageResource(R.drawable.newfb);
                            fbTick.setVisibility(View.VISIBLE);
                            getFacebookDetails(token);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }
    // [END auth_with_facebook]

    private void getFacebookDetails(AccessToken token){

        GraphRequest request = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if(response.getJSONObject() != null){
                            try {
                                saveFacebookDetails(response.getJSONObject());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                hideProgressDialog();
                            }
                        }
                    }});

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,picture.type(large),location ,education,work");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void saveFacebookDetails(JSONObject jsonObject) throws JSONException{

        JSONObject pictureObj = jsonObject.getJSONObject("picture");
        //JSONObject locationObj = jsonObject.getJSONObject("location");
        //JSONArray educationArray = jsonObject.getJSONArray("education");
        //JSONArray workArray = jsonObject.getJSONArray("work");

        if(jsonObject.getString("id") != null){
            String fbLink = jsonObject.getString("id").toString();
            signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_FB_LINK, fbLink);
        }

        if(pictureObj != null) {
            profPicLink = pictureObj.getJSONObject("data").getString("url");
            signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL, profPicLink);
        }

        /*if(locationObj != null) {
            String location = locationObj.getString("name");
            signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_LOCATION, location);
        }

        if(educationArray != null && educationArray.getJSONObject(educationArray.length()-1).getJSONObject("school") != null) {
            String education = educationArray.getJSONObject(educationArray.length()-1).getJSONObject("school").getString("name");
            signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_EDUCATION, education);
        }

        if(workArray != null && workArray.getJSONObject(workArray.length()-1).getJSONObject("position") != null ) {
            String work = workArray.getJSONObject(workArray.length()-1).getJSONObject("position").getString("name");
            signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_PROFESSION, work);
        }
*/
        if(profPicLink != null && !profPicLink.isEmpty()){
            // Send profile picture link to third step
            Intent i = new Intent(SEND_USER_PROFILE_PIC);
            i.putExtra("ProfilePicURL",profPicLink);
            getActivity().sendBroadcast(i);
        }
        skip.setText("Save");
        hideProgressDialog();
    }

    // [START auth_with_twitter]
    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);
        showProgressDialog();
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_TWITTER_LINK, String.valueOf(session.getUserName()));
                            //twitter.setImageResource(R.drawable.newtwitter);
                            twtTick.setVisibility(View.VISIBLE);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }
    // [END auth_with_twitter]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_GP_LINK, acct.getId());
                            //gp.setImageResource(R.drawable.newgp);
                            gTick.setVisibility(View.VISIBLE);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }
    // [END auth_with_google]

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navigateFragment = (NavigateFragment) context;
    }

}
