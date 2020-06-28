/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Groups;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.BackgroundTasks.LeaveGroupBGT;
import com.connect.infixd.mobile.DBHelper.SearchSuggestionDBHelper;
import com.connect.infixd.mobile.DBModels.Group;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Wrappers.GetGroupInfoWrapper;
import com.connect.infixd.mobile.Wrappers.GroupMemberResponseWrapper;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.infixd.client.model.GetGroupInfoResponse;
import com.infixd.client.model.GetUserGroupsResponse;
import com.infixd.client.model.GroupMemberResponse;
import com.infixd.client.model.UserGroupResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SubscribedMemberActivity extends InfixdBaseActivity {

    private TextView groupNameTV;
    private TextView groupDescTV;
    private TextView noOfMembersTV;
    private TextView userNameTV;
    private TextView userRankTV;
    private ImageView groupPicIV;
    private ImageView externalLinkPicIV;
    private ImageView leaveGroupIV;
    private ImageView userIV;
    private GetGroupInfoResponse response;
    private List<GroupMemberResponse> membersInfo;
    private Toolbar toolbar;
    private String userId;
    private String groupName;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetGroupInfoWrapper object = (GetGroupInfoWrapper) getIntent().getSerializableExtra("response");
        response = object.getResponse();
        userId = response.getUser().getUserId();
        groupName = response.getName();

        setDisplay(response);
    }

    public void setDisplay(GetGroupInfoResponse response){

        setContentView(R.layout.activity_subscribed_member);
        groupNameTV = (TextView) findViewById(R.id.subscribedGroupNameTV);
        groupDescTV = (TextView) findViewById(R.id.subscribedGroupDescTV);
        noOfMembersTV = (TextView) findViewById(R.id.subscribedGroupMembersTV);
        userNameTV = (TextView) findViewById(R.id.subscribedGroupUserNameTV);
        userRankTV = (TextView) findViewById(R.id.subscribedGroupUserRankTV);
        groupPicIV = (ImageView) findViewById(R.id.subscribedGroupPicIV);
        externalLinkPicIV = (ImageView) findViewById(R.id.subscribedGroupExtUrlIV);
        leaveGroupIV = (ImageView) findViewById(R.id.subscribedGroupLeaveGroupIV);
        userIV = (ImageView) findViewById(R.id.subscribedGroupUserIV);

        toolbar = (Toolbar) findViewById(R.id.subscribedGroupToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        groupNameTV.setText(response.getName());
        groupDescTV.setText(response.getDescription());
        noOfMembersTV.setText(response.getNoOfMembers() + " Members ");
        userNameTV.setText(response.getUser().getFirstName());
        userRankTV.setText(response.getUser().getConnection());

        // Load the Profile Picture image fo user
        if(response.getUser().getProfPicUrl() != null){
            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(response.getUser().getProfPicUrl());
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef)
                    .into(userIV);
        }

        membersInfo = response.getMembers();
        noOfMembersTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubscribedMemberActivity.this,GroupMembersActivity.class);
                GroupMemberResponseWrapper object = new GroupMemberResponseWrapper();
                object.setMembersInfo(membersInfo);
                intent.putExtra("membersInfo", (Serializable) object);
                startActivity(intent);
            }
        });

        leaveGroupIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpToLeaveGroup();
            }
        });

    }

    public void showPopUpToLeaveGroup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(" You are About to Leave the group. are you sure you want to leave the group ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new LeaveGroupBGT(SubscribedMemberActivity.this){
                    @Override
                    protected void onPostExecute(GetUserGroupsResponse response) {
                        super.onPostExecute(response);
                        int code = response.getCode();
                        if (code == 1){
                            List<UserGroupResponse> userGroups =  response.getGroups();
                            // [ START OF BLOCK SAVING USER INFIXD GROUPS IN SQLTE DB]
                            if (userGroups != null) {
                                List<Group> groups = new ArrayList<Group>();
                                for (int i=0;i<userGroups.size();i++) {
                                    Group group = new Group();
                                    group.setGroup_name(userGroups.get(i).getGroupName());
                                    group.setGroup_photo_url(userGroups.get(i).getGroupPhotoUri());
                                    group.setNoOfMembers(userGroups.get(i).getNoOfMembers());
                                    group.setUserPosition(userGroups.get(i).getUserPosition());
                                    groups.add(group);
                                }
                                SearchSuggestionDBHelper db = new SearchSuggestionDBHelper(getBaseContext());
                                db.addGroups(groups);
                            }
                            // [ END OF BLOCK SAVING USER INFIXD GROUPS IN SQLTE DB]
                            finish();
                        }
                    }
                }.execute(groupName,userId);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
