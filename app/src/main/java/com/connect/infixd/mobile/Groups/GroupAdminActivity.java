/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Groups;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.BackgroundTasks.AddRemoveAdminBGT;
import com.connect.infixd.mobile.BackgroundTasks.AdminLeaveGroupBGT;
import com.connect.infixd.mobile.BackgroundTasks.LeaveGroupBGT;
import com.connect.infixd.mobile.BackgroundTasks.UpdateGroupInfoBGT;
import com.connect.infixd.mobile.DBHelper.NotificationDBHelper;
import com.connect.infixd.mobile.DBHelper.SearchSuggestionDBHelper;
import com.connect.infixd.mobile.DBModels.AddGroupNotification;
import com.connect.infixd.mobile.DBModels.Group;
import com.connect.infixd.mobile.Interfaces.AddGroupResult;
import com.connect.infixd.mobile.Interfaces.AddRemoveGroupResult;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Search.SearchGroupMembersActivity;
import com.connect.infixd.mobile.User.UserProfileEditDialog;
import com.connect.infixd.mobile.Wrappers.AddGroupNotificationWrapper;
import com.connect.infixd.mobile.Wrappers.GetGroupInfoWrapper;
import com.connect.infixd.mobile.Wrappers.GroupInfoResponseWrapper;
import com.connect.infixd.mobile.Wrappers.GroupMemberResponseWrapper;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.infixd.client.model.AddAdminResponse;
import com.infixd.client.model.GetGroupInfoResponse;
import com.infixd.client.model.GetUserGroupsResponse;
import com.infixd.client.model.GroupInfoResponse;
import com.infixd.client.model.GroupMemberResponse;
import com.infixd.client.model.UserGroupResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupAdminActivity extends InfixdBaseActivity implements AddRemoveGroupResult {

    private TextView groupNameTV;
    private TextView groupDescTV;
    private TextView noOfRequestTV;
    private TextView noOfMembersTV;
    private ImageView groupPicIV;
    private ImageView externalLinkPicIV;
    private ImageView leaveGroupIV;
    private ImageView addMemberPicIV;
    private ImageView firstAdminIV;
    private ImageView secondAdminIV;
    private ImageView thirdAdminIV;
    private TextView firstAdminTV;
    private TextView secondAdminTV;
    private TextView thirdAdminTV;
    private GetGroupInfoResponse response;
    private List<GroupMemberResponse> membersInfo;
    private List<GroupMemberResponse> administrators;
    private static final int UPDATE_GROUP_NAME_CODE = 1;
    private static final int UPDATE_GROUP_DESCRIPTION_CODE = 2;
    private static final int ADD_ADMIN_RESULT_CODE = 3;
    private static final String ADD_ADMIN = "add";
    private static final String REMOVE_ADMIN = "remove";
    private GroupMemberResponse user;
    private GroupMemberResponse secondAdmin;
    private GroupMemberResponse thirdAdmin;
    private boolean secondAdminIsAvilable = false;
    private boolean thirdAdminIsAvilable = false;
    private StorageReference mStorageRef;
    private Toolbar toolbar;
    private String userId;
    private String groupName;
    private String groupDesc;
    private String newgroupName;
    private String externalUrl;
    private String groupPicUrl;
    private AddGroupResult addGroupResult;
    private ArrayList<AddGroupNotification> notificationData;
    private boolean noOfRequestsClickable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetGroupInfoWrapper object = (GetGroupInfoWrapper) getIntent().getSerializableExtra("response");
        response = object.getResponse();
        notificationData = new ArrayList<>();
        fetchGroupRequestNotifications();
        setDisplay(response);
    }

    public void setDisplay(GetGroupInfoResponse response){

        setContentView(R.layout.activity_group_admin);
        groupNameTV = (TextView) findViewById(R.id.adminPageGroupNameTV);
        groupDescTV = (TextView) findViewById(R.id.adminPageGroupDescTV);
        noOfRequestTV = (TextView) findViewById(R.id.adminPageReqTV);
        noOfMembersTV = (TextView) findViewById(R.id.adminPageMembersTV);
        firstAdminTV = (TextView) findViewById(R.id.adminPageAdminOneTV);
        secondAdminTV = (TextView) findViewById(R.id.adminPageAdminTwoTV);
        thirdAdminTV = (TextView) findViewById(R.id.adminPageAdminThreeTV);
        groupPicIV = (ImageView) findViewById(R.id.adminPageGroupProfIV);
        externalLinkPicIV = (ImageView) findViewById(R.id.adminPageExtUrlIV);
        leaveGroupIV = (ImageView) findViewById(R.id.adminPageExitGroupIV);
        firstAdminIV = (ImageView) findViewById(R.id.adminPageAdminOneIV);
        secondAdminIV = (ImageView) findViewById(R.id.adminPageAdminTwoIV);
        thirdAdminIV = (ImageView) findViewById(R.id.adminPageAdminThreeIV);

        administrators = response.getAdmins();
        user = response.getUser();
        userId = user.getUserId();
        groupName = response.getName();
        newgroupName = groupName;
        groupDesc = response.getDescription();
        externalUrl = response.getExternalURL();
        groupPicUrl = response.getProfURL();
        membersInfo = response.getMembers();

        // Load the Profile Picture image fo user
        if(user.getProfPicUrl() != null){
            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getProfPicUrl());
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef)
                    .into(firstAdminIV);
        }

        refreshAdmins(administrators,user);

        firstAdminTV.setText(user.getFirstName() + " ( You ) ");
        groupNameTV.setText(response.getName());
        groupDescTV.setText(response.getDescription());
        noOfMembersTV.setText(response.getNoOfMembers() + " Members ");
        if(notificationData.size() == 0){
            noOfRequestTV.setText("No Requests");
            noOfRequestsClickable = false;
        }
        else if(notificationData.size() == 1){
            noOfRequestTV.setText("1 Request");
            noOfRequestsClickable = true;
        }
        else{
            noOfRequestTV.setText( notificationData.size() + " Requests ");
            noOfRequestsClickable = true;
        }
        noOfMembersTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupAdminActivity.this,GroupMembersActivity.class);
                GroupMemberResponseWrapper object = new GroupMemberResponseWrapper();
                object.setMembersInfo(membersInfo);
                intent.putExtra("membersInfo", (Serializable) object);
                startActivity(intent);
            }
        });

        noOfRequestTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noOfRequestsClickable){
                    Intent intent = new Intent(GroupAdminActivity.this,AdminRequestActivity.class);
                    AddGroupNotificationWrapper object = new AddGroupNotificationWrapper();
                    object.setNotificationData(notificationData);
                    intent.putExtra("notifications", (Serializable) object);
                    intent.putExtra("userId",userId);
                    intent.putExtra("groupName",groupName);
                    startActivity(intent);
                }
            }
        });

        groupNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupAdminActivity.this, UserProfileEditDialog.class);
                intent.putExtra("value",groupNameTV.getText().toString());
                startActivityForResult(intent,UPDATE_GROUP_NAME_CODE);
            }
        });

        groupDescTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupAdminActivity.this, UserProfileEditDialog.class);
                intent.putExtra("value",groupDescTV.getText().toString());
                startActivityForResult(intent,UPDATE_GROUP_DESCRIPTION_CODE);
            }
        });

        externalLinkPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditExtURLDialog();
            }
        });

        toolbar = (Toolbar) findViewById(R.id.AddGroupToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateGroupInfoBGT obj = new UpdateGroupInfoBGT(GroupAdminActivity.this);
                obj.execute(userId,groupName,newgroupName,groupDesc,externalUrl,groupPicUrl);
                finish();
            }
        });

        leaveGroupIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpToLeaveGroup();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == UPDATE_GROUP_NAME_CODE){
            if(resultCode == Activity.RESULT_OK){
                groupNameTV.setText(data.getStringExtra("result"));
                newgroupName = data.getStringExtra("result");
            }
        }
        else if(requestCode == UPDATE_GROUP_DESCRIPTION_CODE){
            if(resultCode == Activity.RESULT_OK){
                groupDescTV.setText(data.getStringExtra("result"));
                groupDesc = data.getStringExtra("result");
            }
        }
        else if(requestCode == ADD_ADMIN_RESULT_CODE){
            if(resultCode == Activity.RESULT_OK){
                refreshAdmins((List<GroupMemberResponse>) data.getSerializableExtra("admins"),user);
            }
        }
    }

    public void refreshAdmins(List<GroupMemberResponse> admins, GroupMemberResponse user){
        secondAdminIsAvilable = false;
        thirdAdminIsAvilable = false;
        secondAdmin = null;
        thirdAdmin = null;
        secondAdminIV.setImageResource(R.color.accent_color);
        thirdAdminIV.setImageResource(R.color.accent_color);

        for(int i=0; i<admins.size(); i++){
            if(admins.get(i).getUserId().equals(user.getUserId())){
                admins.remove(i);
            }
        }

        if(admins.size() == 1){
            secondAdminIsAvilable = true;
            thirdAdminIsAvilable = false;
            secondAdmin = admins.get(0);
            secondAdminTV.setVisibility(View.VISIBLE);
            thirdAdminTV.setVisibility(View.GONE);
            secondAdminTV.setText(secondAdmin.getFirstName());
            if(secondAdmin.getProfPicUrl() != null){
                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(secondAdmin.getProfPicUrl());
                Glide.with(getBaseContext())
                        .using(new FirebaseImageLoader())
                        .load(mStorageRef)
                        .into(secondAdminIV);
            }
            else {
                secondAdminIV.setImageResource(R.drawable.user_prof_pic);
            }

        }
        else if(admins.size() == 2){
            secondAdminIsAvilable = true;
            thirdAdminIsAvilable = true;
            secondAdmin = admins.get(0);
            thirdAdmin = admins.get(1);
            secondAdminTV.setVisibility(View.VISIBLE);
            thirdAdminTV.setVisibility(View.VISIBLE);
            secondAdminTV.setText(secondAdmin.getFirstName());
            thirdAdminTV.setText(thirdAdmin.getFirstName());
            if(secondAdmin.getProfPicUrl() != null){
                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(secondAdmin.getProfPicUrl());
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(mStorageRef)
                        .into(secondAdminIV);
            }
            else {
                secondAdminIV.setImageResource(R.drawable.user_prof_pic);
            }
            if(thirdAdmin.getProfPicUrl() != null){
                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(thirdAdmin.getProfPicUrl());
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(mStorageRef)
                        .into(thirdAdminIV);
            }
            else {
                thirdAdminIV.setImageResource(R.drawable.user_prof_pic);
            }

        }
        else{
            secondAdminIsAvilable = false;
            thirdAdminIsAvilable = false;
            secondAdminTV.setVisibility(View.GONE);
            thirdAdminTV.setVisibility(View.GONE);
        }

        secondAdminIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!secondAdminIsAvilable){
                    // ADD ADMIN CODE
                    Intent intent = new Intent(GroupAdminActivity.this,SearchGroupMembersActivity.class);
                    GroupMemberResponseWrapper object = new GroupMemberResponseWrapper();
                    object.setMembersInfo(removeUserFromMembers(membersInfo));
                    intent.putExtra("groupName",response.getName());
                    intent.putExtra("membersInfo", (Serializable) object);
                    startActivityForResult(intent,ADD_ADMIN_RESULT_CODE);
                }
            }
        });

        thirdAdminIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!thirdAdminIsAvilable){
                    // ADD ADMIN CODE
                    Intent intent = new Intent(GroupAdminActivity.this,SearchGroupMembersActivity.class);
                    GroupMemberResponseWrapper object = new GroupMemberResponseWrapper();
                    object.setMembersInfo(removeUserFromMembers(membersInfo));
                    intent.putExtra("groupName",response.getName());
                    intent.putExtra("membersInfo", (Serializable) object);
                    startActivityForResult(intent,ADD_ADMIN_RESULT_CODE);
                }
            }
        });

        secondAdminIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(secondAdminIsAvilable){
                    showPopUpToRemoveAdmin();
                }
                return true;
            }
        });

        thirdAdminIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               if(thirdAdminIsAvilable){
                   showPopUpToRemoveAdmin();
               }
               return true;
            }
        });

        secondAdminTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(secondAdminIsAvilable){
                    showPopUpToRemoveAdmin();
                }
                return true;
            }
        });

        thirdAdminTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(thirdAdminIsAvilable){
                    showPopUpToRemoveAdmin();
                }
                return true;
            }
        });
    }

    public List<GroupMemberResponse> removeUserFromMembers(List<GroupMemberResponse> membersInfo){
        List<GroupMemberResponse> refreshedMembers = new ArrayList<>();
        for(GroupMemberResponse obj:membersInfo){
            if(!obj.getUserId().equals(user.getUserId())){
                refreshedMembers.add(obj);
            }
        }
        return refreshedMembers;
    }

    @Override
    public void getAddRemoveGroupResult(AddAdminResponse response) {
        refreshAdmins(response.getAdmins(),user);
    }



    public void showPopUpToRemoveAdmin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you Sure do you want to remove this user from admin ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddRemoveAdminBGT obj = new AddRemoveAdminBGT(GroupAdminActivity.this);
                obj.execute(secondAdmin.getUserId(),response.getName(),"remove");
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

    public void showEditExtURLDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_box_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_box_editTV);
        edt.setText(externalUrl);

        dialogBuilder.setTitle("External URLex");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                externalUrl = edt.getText().toString();
                dialog.dismiss();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void showPopUpToLeaveGroup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(" You are About to Leave the group. are you sure you want to leave the group ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AdminLeaveGroupBGT(GroupAdminActivity.this){
                    @Override
                    protected void onPostExecute(GetUserGroupsResponse response) {
                        super.onPostExecute(response);
                        int code = response.getCode();
                        if(code == 0){
                            showPopUpToRemoveGroup();
                        }
                        else if (code == 1){
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
                }.execute(userId,groupName);
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

    public void showPopUpToRemoveGroup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are the only admin in this group. " +
                "if you leave this group new members will not be able to join. existing members will be able to view group" +
                "Are you sure do you want to proceed ? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new LeaveGroupBGT(GroupAdminActivity.this){
                    @Override
                    protected void onPostExecute(GetUserGroupsResponse getUserGroupsResponse) {
                        super.onPostExecute(getUserGroupsResponse);
                        if(getUserGroupsResponse.getCode() == 1){
                            List<UserGroupResponse> userGroups =  getUserGroupsResponse.getGroups();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UpdateGroupInfoBGT obj = new UpdateGroupInfoBGT(GroupAdminActivity.this);
        obj.execute(userId,groupName,newgroupName,groupDesc,externalUrl,groupPicUrl);
        finish();
    }

    public void finishActivityFroResult(List<GroupInfoResponse> groups){
        Intent i = getIntent();
        GroupInfoResponseWrapper object = new GroupInfoResponseWrapper();
        object.setGroups(groups);
        i.putExtra("groups", (Serializable) object);
        setResult(Activity.RESULT_OK, i);
        this.finish();
    }

    public void fetchGroupRequestNotifications(){
        NotificationDBHelper db = new NotificationDBHelper(GroupAdminActivity.this);
        List<AddGroupNotification> notifications = db.getAllGroupNotifications();
        for (AddGroupNotification notification:notifications) {
            if(notification.getGroupName().equals(response.getName()) && notification.getStatus().equals("NO RESPONSE") && notification.getType().equals(InfixdApp.ADD_GROUP_NOTIFICATION)){
                notificationData.add(notification);
            }
        }
    }
}
