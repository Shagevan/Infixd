/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.SignupProcess;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Dialogs.DialogFactory;
import com.connect.infixd.mobile.Interfaces.DirectToHome;
import com.connect.infixd.mobile.Interfaces.NavigateFragment;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.intentservices.InfixdIntentService;
import com.connect.infixd.mobile.intentservices.UserIntentService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignupFirstStep extends Fragment implements InfixdIntentService.Receiver{

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private String code;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private View parentLayout;
    private Button varifyBtn;
    private EditText varificationCodeET;
    private TextView reSendCodeTV;
    private NavigateFragment navigateFragment;
    private DirectToHome directToHome;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private SweetAlertDialog pDialog;
    private static final String TAG = "PhoneAuthActivity";
    private InfixdIntentService.BroadcastReceiver mBroadCastReceiver;
    private SignupProcessActivity signupActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentLayout = inflater.inflate(R.layout.fragment_signup_first_step, container, false);
        signupActivity = ((SignupProcessActivity)getActivity());
        Bundle bundle = this.getArguments();
        firstName = bundle.getString(InfixdApp.STRING_PREFERENCE_FIRST_NAME);
        lastName = bundle.getString(InfixdApp.STRING_PREFERENCE_LAST_NAME);
        mobileNumber = bundle.getString(InfixdApp.STRING_PREFERENCE_MOBILE_NUMBER);

        varificationCodeET = (EditText) parentLayout.findViewById(R.id.et_verification_code);
        reSendCodeTV = (TextView) parentLayout.findViewById(R.id.reSendCodeTV);
        varifyBtn = (Button) parentLayout.findViewById(R.id.btn_verify);
        reSendCodeTV.setEnabled(false);
        reSendCodeTV.setText("");

        mBroadCastReceiver = new InfixdIntentService.BroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UserIntentService.ACTION_REGISTER_COMPLETE);
        intentFilter.addAction(UserIntentService.ACTION_REGISTER_NUMBEREXISTS);
        intentFilter.addAction(UserIntentService.ACTION_REGISTER_FAILED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadCastReceiver, intentFilter);

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                registerUser();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseNetworkException) {
                    DialogFactory.getInstance().make(DialogFactory.CONNECTION_ERROR, getView(),
                            view -> startPhoneNumberVerification(mobileNumber)).show();
                } else {
                    DialogFactory.getInstance().make(DialogFactory.REPORT_ERROR, getView(),
                            view -> {/*TODO: implement reporting */}).show();
                }

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                varifyBtn.setEnabled(true);
                new CountDownTimer(60000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        reSendCodeTV.setText(millisUntilFinished / 1000 + " s ");
                    }

                    public void onFinish() {
                        reSendCodeTV.setEnabled(true);
                        reSendCodeTV.setText("Resend code");
                    }
                }.start();
            }

        };

        varifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = varificationCodeET.getText().toString();
                pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Verifying...");
                pDialog.setCancelable(false);
                pDialog.show();
                verifyPhoneNumberWithCode(mVerificationId,code);
            }
        });

        reSendCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(mobileNumber,mResendToken);
            }
        });

        startPhoneNumberVerification(mobileNumber);
        return parentLayout;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),      // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),      // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            registerUser();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (pDialog != null) {
                                pDialog.dismiss();
                            }
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("verification failed")
                                        .setContentText("The verification code entered was invalid !")
                                        .show();
                            }
                            else{
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setContentText(" Try resend code !")
                                        .show();
                            }
                        }
                    }
                })
        .addOnFailureListener(getActivity(), e -> DialogFactory.getInstance().make(DialogFactory.REPORT_ERROR, getView(),
                                view -> { Log.e(TAG,"Firebase failure");}).show());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navigateFragment = (NavigateFragment) context;
        directToHome = (DirectToHome) context;
    }

    public void registerUser() {

        Intent serviceIntent = new Intent(getActivity(), UserIntentService.class);
        serviceIntent.setAction(UserIntentService.ACTION_DO_REGISTER);
        serviceIntent.putExtra(UserIntentService.DATA_FIRSTNAME, firstName);
        serviceIntent.putExtra(UserIntentService.DATA_LASTNAME, lastName);
        serviceIntent.putExtra(UserIntentService.DATA_MOBILENUMBER, mobileNumber);

        getActivity().startService(serviceIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            SharedPreferences.Editor editor;
            switch (intent.getAction()) {
                case UserIntentService.ACTION_REGISTER_COMPLETE:

                    String userName = intent.getStringExtra(UserIntentService.DATA_USERNAME);
                    String firstName = intent.getStringExtra(UserIntentService.DATA_FIRSTNAME);
                    String fullName = intent.getStringExtra(UserIntentService.DATA_FULLNAME);
                    String mobileNumber = intent.getStringExtra(UserIntentService.DATA_MOBILENUMBER);

                    //set first time preference flag
                    signupActivity.saveSharedPreference(InfixdApp.BOOLEAN_PREFERENCE_IS_FRESH_INSTALL, true);
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_USER_ID, userName);
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_FIRST_NAME, firstName);
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_FULL_NAME, fullName);
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_MOBILE_NUMBER, mobileNumber);
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_LOCATION_STATE, "1");

                    if (pDialog != null) {
                        pDialog.dismiss();
                    }

                    navigateFragment.doNavigation(1, true);

                    break;
                case UserIntentService.ACTION_REGISTER_NUMBEREXISTS:
                    //Toast.makeText(getActivity(), "Sorry mobile number already exist", Toast.LENGTH_LONG).show();
                    userName = intent.getStringExtra(UserIntentService.DATA_USERNAME);
                    firstName = intent.getStringExtra(UserIntentService.DATA_FIRSTNAME);
                    fullName = intent.getStringExtra(UserIntentService.DATA_FULLNAME);
                    mobileNumber = intent.getStringExtra(UserIntentService.DATA_MOBILENUMBER);
                    String profilePicURL = intent.getStringExtra(UserIntentService.DATA_PROFILE_PIC_URL);


                    //set first time preference flag
                    signupActivity.saveSharedPreference(InfixdApp.BOOLEAN_PREFERENCE_IS_FRESH_INSTALL, true);
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_USER_ID, userName);
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_FIRST_NAME, firstName);
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_FULL_NAME, fullName);
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_MOBILE_NUMBER, mobileNumber);
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_LOCATION_STATE, "1");
                    signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL,profilePicURL);

                    if (pDialog != null) {
                        pDialog.dismiss();
                    }

                    directToHome.directToHome(true);
                    break;
                case UserIntentService.ACTION_REGISTER_FAILED:
                    Snackbar mSnackbar = Snackbar.make(parentLayout, "Unable to connect", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", view -> {
                                Snackbar.make(parentLayout, "Retrying..", Snackbar.LENGTH_LONG).show();
                                registerUser();
                            });
                    mSnackbar.show();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBroadCastReceiver.unregister(getActivity());
    }

}
