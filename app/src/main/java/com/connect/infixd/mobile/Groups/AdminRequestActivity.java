/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Groups;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.DBModels.AddGroupNotification;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Wrappers.AddGroupNotificationWrapper;

import java.util.ArrayList;

public class AdminRequestActivity extends InfixdBaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AdminRequestsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<AddGroupNotification> notificationData;
    private String userId;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_request);

        AddGroupNotificationWrapper object = (AddGroupNotificationWrapper) getIntent().getSerializableExtra("notifications");
        userId = getIntent().getStringExtra("userId");
        groupName = getIntent().getStringExtra("groupName");
        notificationData = object.getNotificationData();

        toolbar = (Toolbar) findViewById(R.id.adminRequestToolBar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.adminRequestRecyclerviewer);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(AdminRequestActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdminRequestsAdapter(AdminRequestActivity.this, userId,groupName,notificationData);
        recyclerView.setAdapter(adapter);
    }
}
