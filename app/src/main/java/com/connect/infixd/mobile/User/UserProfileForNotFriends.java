/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.User;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.BackgroundTasks.GetIntroductionPathBGT;
import com.connect.infixd.mobile.R;
import com.squareup.picasso.Picasso;

public class UserProfileForNotFriends extends InfixdBaseActivity {

    private TextView profession_textview;
    private TextView userName_textview;
    private TextView about_me_textview;
    private TextView education_textview;
    private TextView location_textview;
    private Button get_introduced_btn;
    private ImageView profilePicForNtFrndIV;
    private String userId;
    private String profession;
    private String about_me;
    private String education;
    private String location;
    private String fullName;
    private String profilePicURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_for_not_friends);

        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("USER_ID");
        profession = bundle.getString("PROFESSION");
        about_me = bundle.getString("ABOUT_ME");
        fullName = bundle.getString("FULL_NAME");
        education = bundle.getString("EDUCATION");
        location = bundle.getString("LOCATION");
        profilePicURL = bundle.getString("PROF_PIC_URL");

        userName_textview = (TextView) findViewById(R.id.fullNameUserProfileTV);
        profession_textview = (TextView) findViewById(R.id.profession_data_textview);
        about_me_textview = (TextView) findViewById(R.id.about_me_data_textview);
        education_textview = (TextView) findViewById(R.id.education_data_textview);
        location_textview = (TextView) findViewById(R.id.location_data_textview);
        get_introduced_btn = (Button) findViewById(R.id.get_infixd);
        profilePicForNtFrndIV =  (ImageView) findViewById(R.id.ProfilePicForNtFrndIV);

        userName_textview.setText(fullName);

        if(profilePicURL != null && !profilePicURL.isEmpty()){
            Picasso.with(this)
                    .load(profilePicURL)
                    .into(profilePicForNtFrndIV);
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

        if(education != null && !location.isEmpty()){
            education_textview.setText(education);
        }

        get_introduced_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senderId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
                GetIntroductionPathBGT obj = new GetIntroductionPathBGT(UserProfileForNotFriends.this);
                obj.execute(senderId,userId,fullName);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
