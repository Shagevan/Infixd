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
import com.connect.infixd.mobile.BackgroundTasks.GetMembersInfoBGT;
import com.connect.infixd.mobile.Interfaces.GetMembersInfo;
import com.connect.infixd.mobile.R;
import com.infixd.client.model.GetGroupMemberInfoResponse;
import com.infixd.client.model.GroupMemberResponse;

import java.util.List;

public class GroupMembersInfoActivity extends InfixdBaseActivity implements GetMembersInfo {

    private Toolbar toolbar;
    private String userId;
    private String groupName;
    private RecyclerView recyclerView;
    private GroupMembersAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members_info);

        userId = getIntent().getStringExtra("userId");
        groupName = getIntent().getStringExtra("groupName");

        toolbar = (Toolbar) findViewById(R.id.group_members_info_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GetMembersInfoBGT obj = new GetMembersInfoBGT(GroupMembersInfoActivity.this);
        obj.execute(userId,groupName);
    }

    @Override
    public void getMembersInfo(GetGroupMemberInfoResponse response) {
        List<GroupMemberResponse> members = response.getOtherMembers();
        recyclerView = (RecyclerView) findViewById(R.id.group_members_info_recyclerViewer);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(GroupMembersInfoActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GroupMembersAdapter(GroupMembersInfoActivity.this, members);
        recyclerView.setAdapter(adapter);
    }
}
