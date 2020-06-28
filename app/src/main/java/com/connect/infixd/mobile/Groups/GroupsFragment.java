/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Groups;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.DBHelper.SearchSuggestionDBHelper;
import com.connect.infixd.mobile.DBModels.Group;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.UserHome.UserHomeActivity;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {

    private ArrayList<Group> groups = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GroupsAdapter groupsAdapter;
    private String userName;
    private UserHomeActivity userHomeActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewGroup = inflater.inflate(R.layout.fragment_groups, container, false);
        userHomeActivity = (UserHomeActivity) getActivity();

        userName = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        SearchSuggestionDBHelper db = new SearchSuggestionDBHelper(getContext());
        groups = (ArrayList<Group>) db.getAllGroups();

        FloatingActionButton fab = (FloatingActionButton) viewGroup.findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), AddGroupActivity.class);
                        intent.putExtra("userName", userName);
                        startActivityForResult(intent, 1);
                    }
                }
        );

        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.groups_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        groupsAdapter = new GroupsAdapter(getContext(),userName,groups);
        recyclerView.setAdapter(groupsAdapter);

        return viewGroup;

    }

    @Override
    public void onResume() {
        super.onResume();
        SearchSuggestionDBHelper db = new SearchSuggestionDBHelper(getContext());
        groups.clear();
        groups.addAll(db.getAllGroups());
        groupsAdapter.notifyDataSetChanged();
    }
}
