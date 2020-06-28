/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.Interfaces.AddRemoveGroupResult;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Wrappers.GroupMemberResponseWrapper;
import com.infixd.client.model.AddAdminResponse;
import com.infixd.client.model.GroupMemberResponse;

import java.io.Serializable;
import java.util.List;

public class SearchGroupMembersActivity extends InfixdBaseActivity implements AddRemoveGroupResult {

    private AutoCompleteTextView textView;
    private ImageView backbtn;
    private ProgressBar searchProgressBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SearchGroupMembersAdapter adapterSuggest;
    private List<GroupMemberResponse> membersInfo;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group_members);

        GroupMemberResponseWrapper object = (GroupMemberResponseWrapper) getIntent().getSerializableExtra("membersInfo");
        groupName = getIntent().getStringExtra("groupName");
        membersInfo = object.getMembersInfo();

        backbtn = (ImageView) findViewById(R.id.searchBackbtn);
        searchProgressBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        searchProgressBar.setVisibility(View.GONE);
        textView = (AutoCompleteTextView) findViewById(R.id.searchAutoComptv);

        // SET RECYCLER VIEWER
        recyclerView = (RecyclerView) findViewById(R.id.searchGroupMembersRecyclerViewer);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        adapterSuggest = new SearchGroupMembersAdapter(SearchGroupMembersActivity.this,membersInfo,groupName);
        recyclerView.setAdapter(adapterSuggest);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapterSuggest.filterSearchResults(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void getAddRemoveGroupResult(AddAdminResponse response) {
        Intent i = getIntent();
        i.putExtra("admins", (Serializable) response.getAdmins());
        setResult(Activity.RESULT_OK, i);
        this.finish();
    }
}
