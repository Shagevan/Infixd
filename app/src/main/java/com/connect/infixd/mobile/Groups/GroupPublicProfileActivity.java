/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Groups;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.BackgroundTasks.SendAddGroupNotificationBGT;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Wrappers.GetGroupInfoWrapper;
import com.infixd.client.model.GetGroupInfoResponse;

public class GroupPublicProfileActivity extends InfixdBaseActivity {

    private TextView groupNameTV;
    private TextView groupDescTV;
    private Toolbar toolbar;
    private Button joinBtn;
    private GetGroupInfoResponse response;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_public_profile);

        userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        GetGroupInfoWrapper object = (GetGroupInfoWrapper) getIntent().getSerializableExtra("response");
        response = object.getResponse();

        groupNameTV = (TextView) findViewById(R.id.groupPublicProfileGroupNameTV);
        groupDescTV = (TextView) findViewById(R.id.groupPublicProfileGroupDescTV);
        joinBtn = (Button) findViewById(R.id.groupPublicProfileButton);

        groupNameTV.setText(response.getName());
        groupDescTV.setText(response.getDescription());

        toolbar = (Toolbar) findViewById(R.id.AddGroupToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendAddGroupNotificationBGT obj = new SendAddGroupNotificationBGT(GroupPublicProfileActivity.this);
                obj.execute(userId,response.getName());
                joinBtn.setText("Request sent");
            }
        });
    }
}
