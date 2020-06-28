/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Currents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.BackgroundTasks.GetLikedByBGT;
import com.connect.infixd.mobile.BackgroundTasks.GetPassedByBGT;
import com.connect.infixd.mobile.R;
import com.infixd.client.model.GetLikedByUsersResponse;
import com.infixd.client.model.GetPassedByUsersResponse;
import com.infixd.client.model.ReactedUserResponse;

import java.util.List;

public class ReactedUsersActivity extends InfixdBaseActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reacted_users);

        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");
        String type = intent.getStringExtra("type");

        userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        recyclerView = (RecyclerView) findViewById(R.id.reacted_users_recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        layoutManager = new LinearLayoutManager(ReactedUsersActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        if (type.equals("likedUsers")){
            GetLikedByBGT getLikedByBGT = new GetLikedByBGT(ReactedUsersActivity.this){
                @Override
                protected void onPostExecute(GetLikedByUsersResponse response) {
                    super.onPostExecute(response);
                    List<ReactedUserResponse> likedByUsers = response.getLikedByUsers();
                    for (ReactedUserResponse user: likedByUsers) {
                        userIds.add(user.getUserId());
                        names.add(user.getName());
                        profilePicUrls.add(user.getProfPicUrl());
                    }
                    adapter = new ReactedUsersAdapter(ReactedUsersActivity.this,
                            userIds, names, profilePicUrls,userId);
                    recyclerView.setAdapter(adapter);
                    hideProgresbar();
                }
            };
            getLikedByBGT.execute(postId);
        }
        else {
            GetPassedByBGT obj = new GetPassedByBGT(ReactedUsersActivity.this){
                @Override
                protected void onPostExecute(GetPassedByUsersResponse response) {
                    super.onPostExecute(response);
                    List<ReactedUserResponse> likedByUsers = response.getPassedByUsers();
                    for (ReactedUserResponse user: likedByUsers) {
                        userIds.add(user.getUserId());
                        names.add(user.getName());
                        profilePicUrls.add(user.getProfPicUrl());
                    }
                    adapter = new ReactedUsersAdapter(ReactedUsersActivity.this,
                            userIds, names, profilePicUrls,userId);
                    recyclerView.setAdapter(adapter);
                    hideProgresbar();
                }
            };
            obj.execute(postId);
        }
    }

    public void showProgressBar(){
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgresbar(){
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
