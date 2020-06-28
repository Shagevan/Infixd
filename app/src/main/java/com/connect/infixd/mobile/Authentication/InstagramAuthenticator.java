package com.connect.infixd.mobile.Authentication;


import android.content.Context;

import com.connect.infixd.mobile.BuildConfig;

public class InstagramAuthenticator {
    private static String clientId = BuildConfig.INSTAGRAM_CLIENT_ID;
    private static String redirURI = "https://infxd.lk";
    private static String instagramAuthURL = "https://api.instagram.com/oauth/authorize/?client_id="
            + clientId + "&redirect_uri=" + redirURI + "&response_type=code&display=touch";
    private Callback successListener;
    private Callback failureListener;

    public InstagramAuthenticator onSuccess(Callback listener) {
        successListener = listener;
        return this;
    }

    public InstagramAuthenticator onFailure(Callback listener) {
        failureListener = listener;
        return this;
    }

    public void show(Context context) {
        final InstagramDialog dialog = new InstagramDialog(context, instagramAuthURL, redirURI,
                new InstagramDialog.OAuthDialogListener() {
                    @Override
                    public void onComplete(String accessToken) {
                        if (successListener != null)
                            successListener.data(accessToken);
                    }

                    @Override
                    public void onError(String error) {
                        if (failureListener != null)
                            failureListener.data(error);
                    }
                });
        dialog.show();
    }

    public interface Callback {
        void data(String data);
    }
}
