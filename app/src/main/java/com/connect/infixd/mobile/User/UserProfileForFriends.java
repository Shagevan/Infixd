/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.User;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserProfileForFriends extends InfixdBaseActivity {

    private TextView userName_textview;
    private TextView profession_textview;
    private TextView about_me_textview;
    private TextView education_textview;
    private TextView location_textview;
    private ImageView fbIV;
    private ImageView gpIV;
    private ImageView twitterIV;
    private ImageView instagramIV;
    private ImageView mobileNoIV;
    private String mobile_number;
    private String userId;
    private String profession;
    private String about_me;
    private String education;
    private String location;
    private ImageView profilePicForFrndIV;
    private String fullName;
    private String profilePicURL;
    private String phoneNoPermission;
    private String fbLink;
    private String gpLink;
    private String twitterLink;
    private String instagramLink;
    private boolean isFbLinkGiven;
    private boolean isGpLinkGiven;
    private boolean isTwitterLinkGiven;
    private boolean isInstaLinkGiven;
    private boolean isPhoneNoPermissionGiven;
    private RecyclerView userFbPhotosRV;
    private GridLayoutManager gridLayoutManager;
    private FBPhotosAdapter fbPhotosAdapter;
    private ArrayList<String> fbPhotosURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_for_friends);

        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("USER_ID");
        mobile_number = bundle.getString("MOBILE_NUMBER");
        profession = bundle.getString("PROFESSION");
        about_me = bundle.getString("ABOUT_ME");
        fullName = bundle.getString("FULL_NAME");
        education = bundle.getString("EDUCATION");
        location = bundle.getString("LOCATION");
        profilePicURL = bundle.getString("PROF_PIC_URL");
        phoneNoPermission = bundle.getString("MOBILE_NO_PERMISSION");
        fbLink = bundle.getString("FB_LINK");
        gpLink = bundle.getString("GP_LINK");
        twitterLink = bundle.getString("TWITTER_LINK");
        instagramLink = bundle.getString("INSTAGRAM_LINK");

        if(bundle.getStringArrayList("FB_PHOTOS") != null) fbPhotosURL = bundle.getStringArrayList("FB_PHOTOS");
        else fbPhotosURL = new ArrayList<>();

        userFbPhotosRV = (RecyclerView) findViewById(R.id.my_recycler_view);
        userFbPhotosRV.setFocusable(false);
        // set a GridLayoutManager with 3 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2, LinearLayoutManager.HORIZONTAL,false);
        userFbPhotosRV.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        fbPhotosAdapter = new FBPhotosAdapter(UserProfileForFriends.this, fbPhotosURL);
        userFbPhotosRV.setAdapter(fbPhotosAdapter); // set the Adapter to RecyclerView

        userName_textview = (TextView) findViewById(R.id.fullNameUserProfileTV);
        profession_textview = (TextView) findViewById(R.id.profession_data_textview);
        about_me_textview = (TextView) findViewById(R.id.about_me_data_textview);
        education_textview = (TextView) findViewById(R.id.education_data_textview);
        location_textview = (TextView) findViewById(R.id.location_data_textview);

        profilePicForFrndIV = (ImageView) findViewById(R.id.ProfilePicForNtFrndIV);
        fbIV = (ImageView) findViewById(R.id.facebook_imageview);
        gpIV = (ImageView) findViewById(R.id.google_plus);
        twitterIV = (ImageView) findViewById(R.id.twitter_imageview);
        instagramIV = (ImageView) findViewById(R.id.insta_imageview);
        mobileNoIV = (ImageView) findViewById(R.id.callIcon);

        userName_textview.setText(fullName);

        if(profilePicURL != null && !profilePicURL.isEmpty()){
            Picasso.with(this)
                    .load(profilePicURL)
                    .into(profilePicForFrndIV);
        }

        if(phoneNoPermission != null && phoneNoPermission.equals("1")){
            isPhoneNoPermissionGiven = true;
            mobileNoIV.setImageResource(R.drawable.call_icon_green);
        }

        if(fbLink != null && !fbLink.isEmpty()){
            isFbLinkGiven = true;
            fbIV.setImageResource(R.drawable.fb);
        }

        if(gpLink != null && !gpLink.isEmpty()){
            isGpLinkGiven = true;
            gpIV.setImageResource(R.drawable.ggl);
        }

        if(twitterLink != null && !twitterLink.isEmpty()){
            isTwitterLinkGiven = true;
            twitterIV.setImageResource(R.drawable.twt);
        }

        if(instagramLink != null && !instagramLink.isEmpty()){
            isInstaLinkGiven = true;
            instagramIV.setImageResource(R.drawable.instagram);
        }

        if(about_me != null && !about_me.isEmpty()){
            about_me_textview.setText("\""+about_me.trim()+"\"");
        }

        if(profession != null && !profession.isEmpty()){
            profession_textview.setText(profession);
        }

        if(location != null && !location.isEmpty()){
            location_textview.setText(location);
        }

        if(education != null && !education.isEmpty()){
            education_textview.setText(education);
        }

        fbIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFbLinkGiven){
                    openFbAccount(fbLink);
                }

            }
        });

        gpIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGpLinkGiven){
                    openGPlusAccount(gpLink);
                }

            }
        });

        twitterIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTwitterLinkGiven){
                    openTwitterAccount(twitterLink);
                }
            }
        });

        instagramIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInstaLinkGiven){
                    // Open Instagram profile
                }
            }
        });

        mobileNoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPhoneNoPermissionGiven){
                    openDialerPad(mobile_number);
                }
            }
        });

    }

    private void openDialerPad(String mobileNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:"+mobileNumber));
        startActivity(intent);
    }

    private void openTwitterAccount(String twitterName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("twitter://user?screen_name=" + twitterName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/#!/" + twitterName)));
        }
    }

    private void openGPlusAccount(String profileId) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", profileId);
            startActivity(intent);
        } catch(ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/"+profileId+"/posts")));
        }
    }

    private void openFbAccount(String facebookId){
        String url = "https://www.facebook.com/"+facebookId;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href="+url));
            startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
