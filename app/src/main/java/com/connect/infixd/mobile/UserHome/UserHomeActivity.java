/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.UserHome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.BackgroundTasks.GetGroupsBGT;
import com.connect.infixd.mobile.BackgroundTasks.GetUserDetailsBGT;
import com.connect.infixd.mobile.BackgroundTasks.GetUserInfixdContactsBGT;
import com.connect.infixd.mobile.BackgroundTasks.UpdateFriendshipMeterBGT;
import com.connect.infixd.mobile.Chat.ChatActivity;
import com.connect.infixd.mobile.Contacts.ContactsFragment;
import com.connect.infixd.mobile.Currents.CurrentsFragment;
import com.connect.infixd.mobile.DBHelper.FriendshipMeterDBHelper;
import com.connect.infixd.mobile.DBHelper.SearchSuggestionDBHelper;
import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.DBModels.FriendshipMeterRow;
import com.connect.infixd.mobile.DBModels.Group;
import com.connect.infixd.mobile.Dialogs.DialogFactory;
import com.connect.infixd.mobile.Functions.BadgeDrawerArrowDrawable;
import com.connect.infixd.mobile.Interfaces.GetFriendsDetails;
import com.connect.infixd.mobile.Interfaces.GetGroups;
import com.connect.infixd.mobile.Interfaces.UpdateFriendShipMeterResult;
import com.connect.infixd.mobile.NearSearch.NearSearchActivity;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Requests.NotificationFragment;
import com.connect.infixd.mobile.Search.InfixdSearchActivity;
import com.connect.infixd.mobile.Search.SearchGroupActivity;
import com.connect.infixd.mobile.Services.RecentChatNotificationService;
import com.connect.infixd.mobile.User.UserProfile;
import com.connect.infixd.mobile.intentservices.FriendIntentService;
import com.connect.infixd.mobile.intentservices.InfixdIntentService;
import com.connect.infixd.mobile.intentservices.NotificationIntentService;
import com.hbb20.CountryCodePicker;
import com.infixd.client.model.GetUserGroupsResponse;
import com.infixd.client.model.UserGroupResponse;
import com.infixd.client.model.UserResponse;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.connect.infixd.mobile.Functions.Utils.setBadgeCount;
import static com.connect.infixd.mobile.R.id.nav_intro;
import static com.connect.infixd.mobile.R.id.nav_intro_req;

public class UserHomeActivity extends InfixdBaseActivity implements NavigationView
        .OnNavigationItemSelectedListener, GetFriendsDetails, GetGroups,
        UpdateFriendShipMeterResult,InfixdIntentService.Receiver {

    private View navHeader;
    private ImageView imgNavHeaderBg;
    private TextView txtName;
    private TextView introNotificationTV;
    private TextView introRequestNotificationTV;
    private TextView directFRNotificationTV;
    private String userId;
    private String fName;
    private String prof_pic_url;
    private boolean state;
    public static final String REQUESTING_DF = "REQUESTING_DF";
    public static final String REQUESTING_FI = "REQUESTING_FI";
    public static final String REQUESTING_INTRO = "REQUESTING_INTRO";
    private int NAVIGATION_SELECTION = 1;
    private static final int GO_TO_USER_PROFILE = 300;
    private Toolbar toolbar;
    private MKLoader loader;
    private InfixdIntentService.BroadcastReceiver mBroadCastReceiver;
    private ActionBarDrawerToggle toggle;
    private BadgeDrawerArrowDrawable badgeDrawable;
    private String notificationAction;
    private boolean isActionNotification = false;
    private DrawerLayout drawer;
    private Runnable mPendingRunnable;
    private Handler mHandler;
    private BroadcastReceiver rChatReceiver;
    private static final String NOTIFY_NEW_MESSAGE = "RecentChatNotificationService.NotifyNewMessage";
    private MenuItem chatMenu;
    private CountryCodePicker ccp;
    private EditText phone_no_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        mHandler = new Handler(Looper.getMainLooper());

        notificationAction = getIntent().getAction();
        if(notificationAction != null){
            isActionNotification = true;
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        loader = (MKLoader) findViewById(R.id.MkLoaderView);
        loader.setVisibility(View.VISIBLE);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mBroadCastReceiver = new InfixdIntentService.BroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NotificationIntentService.ACTION_NOTIFY_DIRECT_FRIEND_REQUEST);
        intentFilter.addAction(NotificationIntentService.ACTION_NOTIFY_INTRODUCTION_REQUEST);
        intentFilter.addAction(NotificationIntentService.ACTION_NOTIFY_FORWARD_INTRODUCTION_REQUEST);
        intentFilter.addAction(NotificationIntentService.ACTION_GET_NO_OF_REQUEST);
        intentFilter.addAction(FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_NUMBER_FAIL);
        intentFilter.addAction(FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_NUMBER_SUCCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReceiver, intentFilter);

        //[GET USERNAME,FIRSTNAME,PROFILE_PICTURE_URL FROM SHARED PREFERENCE]
        userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
        fName = getPreferenceValue(InfixdApp.STRING_PREFERENCE_FIRST_NAME);
        prof_pic_url = getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL);

        Intent intent = new Intent(this, RecentChatNotificationService.class);
        intent.putExtra("userId",userId);
        startService(intent);

        doUserContactsRequest();

    }

    /**
     * Upon completion calls getFriendsDetails.
     */
    private void doUserContactsRequest() {
        GetUserInfixdContactsBGT getUserInfixdContactsBGT = new GetUserInfixdContactsBGT(UserHomeActivity.this);
        getUserInfixdContactsBGT.execute(userId);
    }

    private void getNoOfRequests(){
        Intent intent = new Intent(this, NotificationIntentService.class);
        intent.setAction(NotificationIntentService.ACTION_GET_NO_OF_REQUEST);
        intent.putExtra(NotificationIntentService.DATA_USER_ID, userId);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_home_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_user:
                addFriendByMobileNumber();
                return true;

            case R.id.near_search:
                Intent nearSearch = new Intent(UserHomeActivity.this, NearSearchActivity.class);
                startActivity(nearSearch);
                return true;

            case R.id.chat_activity:
                unRegisterBroadcastReceiver();
                Intent chat = new Intent(UserHomeActivity.this, ChatActivity.class);
                chat.setAction("Fresh_Chat");
                startActivity(chat);
                return true;

            case R.id.action_search:
                if(NAVIGATION_SELECTION == 3){
                    Intent intent = new Intent(UserHomeActivity.this,SearchGroupActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(UserHomeActivity.this,InfixdSearchActivity.class);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(state){
            state = false;
            getMenuInflater().inflate(R.menu.search_toolbar, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.user_home_toolbar, menu);
            chatMenu = menu.findItem(R.id.chat_activity);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void displaySelectedScreen(int itemId) {
        drawer.closeDrawer(GravityCompat.START);
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_home:
                NAVIGATION_SELECTION = 1;
                mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, new HomeFragment());
                        ft.commit();
                        invalidateOptionsMenu();
                        toolbar.setTitle("Home");
                    }
                };
                break;
            case R.id.nav_contacts:
                NAVIGATION_SELECTION = 2;
                mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, new ContactsFragment());
                        ft.commit();
                        state = true;
                        invalidateOptionsMenu();
                        toolbar.setTitle("People");
                    }
                };
                break;
            case nav_intro_req:
                NAVIGATION_SELECTION = 4;
                mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", REQUESTING_INTRO);
                        NotificationFragment fragment = new NotificationFragment();
                        fragment.setArguments(bundle);
                        introRequestNotificationTV.setVisibility(View.GONE);
                        badgeDrawable.setEnabled(false);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.commit();
                        invalidateOptionsMenu();
                        toolbar.setTitle("Intro Requests");
                    }
                };
                break;
            case nav_intro:
                NAVIGATION_SELECTION = 5;
                mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", REQUESTING_FI);
                        NotificationFragment fragment = new NotificationFragment();
                        fragment.setArguments(bundle);
                        introNotificationTV.setVisibility(View.GONE);
                        badgeDrawable.setEnabled(false);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.commit();
                        invalidateOptionsMenu();
                        toolbar.setTitle("Introductions Requests");
                    }
                };
                break;
            case R.id.nav_direct_req:
                NAVIGATION_SELECTION = 6;
                mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", REQUESTING_DF);
                        NotificationFragment fragment = new NotificationFragment();
                        fragment.setArguments(bundle);
                        directFRNotificationTV.setVisibility(View.GONE);
                        badgeDrawable.setEnabled(false);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.commit();
                        invalidateOptionsMenu();
                        toolbar.setTitle("Direct Requests");
                    }
                };
                break;
            case R.id.nav_currents:
                mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, new CurrentsFragment());
                        ft.commit();
                        toolbar.setTitle("Currents");
                    }
                };
                break;

            case R.id.nav_search:
                mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(UserHomeActivity.this,
                                InfixdSearchActivity.class);
                        startActivity(intent);
                    }
                };
                break;
        }

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
            mPendingRunnable = null;
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        return true;
    }

    @Override
    public void getFriendsDetails(List<UserResponse> friendsDetails) {

        // [ START OF BLOCK SAVING USER INFIXD CONTACTS IN SQLTE DB]
        if(friendsDetails == null) {
            DialogFactory.getInstance().make(DialogFactory.CONNECTION_ERROR, findViewById(R.id.drawer_layout),
                    v -> {
                        doUserContactsRequest(); //again
                    }).show();
            return;
        }
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        for (UserResponse response : friendsDetails){
            Contact contact = new Contact();
            contact.setID(response.getUserId());
            contact.setName(response.getFirstName());
            contact.setPhoneNumber(response.getMobileNumber());
            contact.setProfilePicUrl(response.getProfPicUrl());
            contacts.add(contact);
        }
        SearchSuggestionDBHelper db = new SearchSuggestionDBHelper(getBaseContext());
        db.addContacts(contacts);
        // [ END OF BLOCK SAVING USER INFIXD CONTACTS IN SQLTE DB]

        GetGroupsBGT getUserInfixdGroups = new GetGroupsBGT(UserHomeActivity.this);
        getUserInfixdGroups.execute(userId);
    }

    @Override
    public void getGroups(GetUserGroupsResponse response) {

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

        // GET FRIENDSHIP METER VALUES AND UPDATE IT TO SERVER
        HashMap<String,String> count = new HashMap<String, String>();
        FriendshipMeterDBHelper dbobj = new FriendshipMeterDBHelper(UserHomeActivity.this);
        List<FriendshipMeterRow> rows = dbobj.getAllFMRows();

        if(rows.size() != 0){
            for (FriendshipMeterRow row: rows) {
                count.put(row.getFriendId(), String.valueOf(row.getMessageCount()));
            }
            UpdateFriendshipMeterBGT updateFriendshipMeterBGT = new UpdateFriendshipMeterBGT(UserHomeActivity.this,count);
            updateFriendshipMeterBGT.execute(userId);
        }
        else {
            setDisplay();
        }

    }

    public void setDisplay() {
        loader.setVisibility(View.GONE);
        getSupportActionBar().show();
        toolbar.setTitle("Home");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        badgeDrawable = new BadgeDrawerArrowDrawable(getSupportActionBar().getThemedContext());
        badgeDrawable.setBackgroundColor(getResources().getColor(R.color.yellow_color));
        badgeDrawable.setTextColor(getResources().getColor(R.color.black_color));
        badgeDrawable.setEnabled(false);

        toggle.setDrawerArrowDrawable(badgeDrawable);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        introNotificationTV =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(nav_intro));
        introNotificationTV.setGravity(Gravity.CENTER_VERTICAL);
        introNotificationTV.setTypeface(null, Typeface.BOLD);
        introNotificationTV.setTextColor(getResources().getColor(R.color.colorAccent));
        introNotificationTV.setVisibility(View.GONE);

        introRequestNotificationTV =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(nav_intro_req));
        introRequestNotificationTV.setGravity(Gravity.CENTER_VERTICAL);
        introRequestNotificationTV.setTypeface(null, Typeface.BOLD);
        introRequestNotificationTV.setTextColor(getResources().getColor(R.color.colorAccent));
        introRequestNotificationTV.setVisibility(View.GONE);

        directFRNotificationTV =(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_direct_req));
        directFRNotificationTV.setGravity(Gravity.CENTER_VERTICAL);
        directFRNotificationTV.setTypeface(null, Typeface.BOLD);
        directFRNotificationTV.setTextColor(getResources().getColor(R.color.colorAccent));
        directFRNotificationTV.setVisibility(View.GONE);


        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.nav_header_userName);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.nav_header_userImageView);
        loadNavHeader();
        getNoOfRequests();

        if(isActionNotification){
            switch (notificationAction) {
                case NotificationIntentService.ACTION_NOTIFY_DIRECT_FRIEND_REQUEST:
                    displaySelectedScreen(R.id.nav_direct_req);
                    break;
                case NotificationIntentService.ACTION_NOTIFY_INTRODUCTION_REQUEST:
                    displaySelectedScreen(nav_intro_req);
                    break;
                case NotificationIntentService.ACTION_NOTIFY_FORWARD_INTRODUCTION_REQUEST:
                    displaySelectedScreen(nav_intro);
                    break;
                case InfixdApp.ACTION_NOTIFY_SHARED_POST_CREATOR:
                    displaySelectedScreen(R.id.nav_currents);
                    break;
            }
        }
        else{
            displaySelectedScreen(R.id.nav_home);
        }


    }
    private void addFriendByMobileNumber(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(true);

        builder.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeypad();
                        String countryCode = ccp.getSelectedCountryCodeWithPlus();
                        String mobileNumber = phone_no_edit_text.getText().toString();
                        String mobileId = countryCode + mobileNumber;

                        if(mobileNumber.length() != 9 || !mobileNumber.startsWith("7")){
                            phone_no_edit_text.setError("Please check your mobile no again");
                        }
                        else{
                            String userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
                            Intent drIntent = new Intent(UserHomeActivity.this,
                                    FriendIntentService.class);
                            drIntent.setAction(FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_NUMBER);
                            drIntent.putExtra(FriendIntentService.DATA_SENDER_ID, userId);
                            drIntent.putExtra(FriendIntentService.DATA_TARGET_PHONE, mobileId);
                            startService(drIntent);
                            dialog.dismiss();
                        }
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

        android.app.AlertDialog dialog = builder.create();

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.activity_add_user_alert_dialog, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        ccp = (CountryCodePicker) dialogLayout.findViewById(R.id.ccp);
        phone_no_edit_text = (EditText) dialogLayout.findViewById(R.id.phone_no_edit_text);

        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary_color));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary_color));

    }

    public void hideKeypad(){
        try  {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    private void setNavHeaderImage(String profilePicURL){
        if(!profilePicURL.isEmpty()){
            Picasso.with(this)
                    .load(profilePicURL)
                    .into(imgNavHeaderBg);
        }
    }

    private void loadNavHeader() {
        txtName.setText(fName);
        setNavHeaderImage(prof_pic_url);
        imgNavHeaderBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgNavHeaderBg.setClickable(false);
                new GetUserDetailsBGT(UserHomeActivity.this){
                    @Override
                    protected void onPostExecute(UserResponse userResponse) {
                        Bundle bundle = new Bundle();
                        bundle.putString("USER_ID", userResponse.getUserId());
                        bundle.putString("FULL_NAME", userResponse.getName());
                        bundle.putString("FIRST_NAME", userResponse.getFirstName());
                        bundle.putString("LAST_NAME", userResponse.getLastName());
                        bundle.putString("MOBILE_NUMBER", userResponse.getMobileNumber());
                        bundle.putString("ABOUT_ME", userResponse.getAboutMe());
                        bundle.putString("EMAIL_ID", userResponse.getEmailAddress());
                        bundle.putString("FB_LINK", userResponse.getFblink());
                        bundle.putString("TWITTER_LINK", userResponse.getTwitterLink());
                        bundle.putString("GP_LINK", userResponse.getGooglePlusLink());
                        bundle.putString("LINKEDIN_LINK", userResponse.getLinkedInLink());
                        bundle.putString("INSTAGRAM_LINK", userResponse.getInstagramLink());
                        bundle.putString("PROF_PIC_URL", userResponse.getProfPicUrl());
                        bundle.putString("PROFESSION", userResponse.getProfession());
                        bundle.putString("EDUCATION", userResponse.getEducation());
                        bundle.putString("LOCATION", userResponse.getCity());
                        bundle.putString("MOBILE_NO_PERMISSION", userResponse.getPhoneNoPermission());
                        bundle.putString("FRIENDSHIP_METER_PERMISSION", userResponse.getFriendshipMeterPermission());
                        bundle.putStringArrayList("FB_PHOTOS", userResponse.getFbPhotos());

                        Intent intent = new Intent(UserHomeActivity.this, UserProfile.class);
                        intent.putExtras(bundle);
                        imgNavHeaderBg.setClickable(true);
                        startActivityForResult(intent,GO_TO_USER_PROFILE);
                    }
                }.execute("userProfile", userId);

            }
        });
    }

    private void registerBroadcastReceiver(){
        rChatReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(NOTIFY_NEW_MESSAGE)
                        && intent.getBooleanExtra("isNewMessage",false)){
                    LayerDrawable icon = (LayerDrawable) chatMenu.getIcon();
                    setBadgeCount(UserHomeActivity.this, icon, "New");
                }
            }
        };
        LocalBroadcastManager.getInstance(this).
                registerReceiver(rChatReceiver, new IntentFilter(NOTIFY_NEW_MESSAGE));
    }

    private void unRegisterBroadcastReceiver(){
        if(rChatReceiver != null){
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(rChatReceiver);
            } catch(IllegalArgumentException e) {

            }
        }
    }

    @Override
    public void getUpdateFriendshipMeterResult(String message) {
        setDisplay();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GO_TO_USER_PROFILE && resultCode == Activity.RESULT_OK){
            String profPicURL = data.getStringExtra("profilePictureURL");
            if(!profPicURL.isEmpty()){
                Picasso.with(this)
                        .load(profPicURL)
                        .into(imgNavHeaderBg);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String message;
            if(intent.getAction().equals(NotificationIntentService.ACTION_GET_NO_OF_REQUEST)){
                String noOfDF = intent.getStringExtra(NotificationIntentService.DATA_NUMBER_OF_DF_NOTIFICATIONS);
                String noOfIN = intent.getStringExtra(NotificationIntentService.DATA_NUMBER_OF_INTRO_NOTIFICATIONS);
                String noOfFI = intent.getStringExtra(NotificationIntentService.DATA_NUMBER_OF_FI_NOTIFICATIONS);

                if(noOfDF.isEmpty() && noOfIN.isEmpty() && noOfFI.isEmpty())badgeDrawable.setEnabled(false);
                else badgeDrawable.setEnabled(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    badgeDrawable.setText("New");
                }
                directFRNotificationTV.setVisibility(View.VISIBLE);
                directFRNotificationTV.setText(noOfDF);
                introNotificationTV.setVisibility(View.VISIBLE);
                introNotificationTV.setText(noOfFI);
                introRequestNotificationTV.setVisibility(View.VISIBLE);
                introRequestNotificationTV.setText(noOfIN);
            }
            else if(intent.getAction().equals(
                    FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_NUMBER_SUCCESS)
                    || intent.getAction().equals(
                    FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_NUMBER_FAIL)){
                message = intent.getStringExtra(FriendIntentService.DATA_MESSAGE);
                DialogFactory.getInstance().make(message,
                        findViewById(R.id.drawer_layout)).show();
            }
            else {
                String noOfNotifications = intent.getStringExtra(NotificationIntentService.DATA_NUMBER_OF_NOTIFICATIONS);
                badgeDrawable.setEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    badgeDrawable.setText("New");
                }
                switch (intent.getAction()) {
                    case NotificationIntentService.ACTION_NOTIFY_DIRECT_FRIEND_REQUEST:
                        directFRNotificationTV.setText(noOfNotifications);
                        break;
                    case NotificationIntentService.ACTION_NOTIFY_FORWARD_INTRODUCTION_REQUEST:
                        introNotificationTV.setText(noOfNotifications);
                        break;
                    case NotificationIntentService.ACTION_NOTIFY_INTRODUCTION_REQUEST:
                        introRequestNotificationTV.setText(noOfNotifications);
                        break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBroadCastReceiver.unregister(this);
        unRegisterBroadcastReceiver();
    }

}
