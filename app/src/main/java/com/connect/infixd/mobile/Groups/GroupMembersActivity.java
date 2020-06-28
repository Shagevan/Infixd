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
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Wrappers.GroupMemberResponseWrapper;
import com.infixd.client.model.GroupMemberResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupMembersActivity extends InfixdBaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private GroupMembersAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<GroupMemberResponse> membersInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        GroupMemberResponseWrapper object = (GroupMemberResponseWrapper) getIntent().getSerializableExtra("membersInfo");
        membersInfo = object.getMembersInfo();

        Collections.sort(membersInfo, new Comparator<GroupMemberResponse>() {
            public int compare(GroupMemberResponse r1, GroupMemberResponse r2) {
                return Integer.getInteger(r2.getConnection()).compareTo(Integer.getInteger(r1.getConnection()));
            }
        });

        for(int i = 0; i < membersInfo.size(); i++){
            membersInfo.get(i).setRank(Integer.toString(i+1));
        }

        toolbar = (Toolbar) findViewById(R.id.groupMembersToolBar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.groupMembersRecyclerviewer);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(GroupMembersActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GroupMembersAdapter(GroupMembersActivity.this, membersInfo);
        recyclerView.setAdapter(adapter);
    }
}
