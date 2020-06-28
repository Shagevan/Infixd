/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.NearSearch;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.Dialogs.DialogFactory;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.intentservices.FriendIntentService;
import com.connect.infixd.mobile.intentservices.InfixdIntentService;
import com.connect.infixd.mobile.intentservices.UserIntentService;

import java.util.ArrayList;
import java.util.List;

import static com.connect.infixd.mobile.intentservices.UserIntentService.ACTION_GET_NEARBY_USERS;
import static com.connect.infixd.mobile.intentservices.UserIntentService.ACTION_NO_NEARBY_USERS;
import static com.connect.infixd.mobile.intentservices.UserIntentService.DATA_NEAR_USERS;

public class NearSearchActivity extends InfixdBaseActivity implements InfixdIntentService.Receiver,
        LocationListener, CompoundButton.OnCheckedChangeListener {

    private Toolbar near_search_toolbar;
    private TextView message_text_view;
    private Switch toolbar_switch;
    private RecyclerView near_by_users_recyclerViewer;
    private List<com.infixd.client.model.Location> userData;
    private LocationManager locationManager;
    private UsersRecyclerViewAdapter adapter;
    private String locationState;
    private String userId;
    private static final long MIN_DISTANCE_CHANGE = 0;
    private static final long MIN_TIME_CHANGE = 0;
    private int MY_PERMISSIONS_REQUEST_LOACTION = 500;
    private static final String TAG = "NearSearchActivity";
    private InfixdIntentService.BroadcastReceiver mBroadCastReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_search);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        userData = new ArrayList<>();

        locationState = getPreferenceValue(InfixdApp.STRING_PREFERENCE_LOCATION_STATE);
        userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        near_search_toolbar = (Toolbar) findViewById(R.id.near_search_toolbar);
        message_text_view = (TextView) findViewById(R.id.message_text_view);
        near_by_users_recyclerViewer = (RecyclerView) findViewById(R.id.near_by_users_recyclerViewer);
        near_by_users_recyclerViewer.setLayoutManager(new LinearLayoutManager(NearSearchActivity.this));
        adapter = new UsersRecyclerViewAdapter(this,userData);
        near_by_users_recyclerViewer.setAdapter(adapter);

        ViewCompat.setNestedScrollingEnabled(near_by_users_recyclerViewer, true);
        setSupportActionBar(near_search_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        near_search_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        near_search_toolbar.setTitle("Near You");
        near_search_toolbar.setTitleTextColor(Color.WHITE);
        near_search_toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_ID_FAIL);
        intentFilter.addAction(FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_ID_SUCCESS);
        intentFilter.addAction(ACTION_GET_NEARBY_USERS);
        intentFilter.addAction(ACTION_NO_NEARBY_USERS);
        intentFilter.addAction(UserIntentService.ACTION_GET_NEARBY_USERS_FAILED);
        intentFilter.addAction(UserIntentService.ACTION_HIDE_USER_LOCATION);
        intentFilter.addAction(UserIntentService.ACTION_HIDE_USER_LOCATION_FAILED);
        mBroadCastReciever = new InfixdIntentService.BroadcastReceiver(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReciever, intentFilter);
    }

    public void hideKeypad(){
        try  {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userinfo_toolbar_menu, menu);
        MenuItem switchItem = menu.findItem(R.id.toolbar_switch);
        View view = MenuItemCompat.getActionView(switchItem);
        toolbar_switch = (Switch) view.findViewById(R.id.switchForToolbar);
        toolbar_switch.setOnCheckedChangeListener(this);
        if (locationState.equals("1")) {
            near_by_users_recyclerViewer.setVisibility(View.VISIBLE);
            message_text_view.setVisibility(View.GONE);
            toolbar_switch.setChecked(true);
        }
        else {
            near_by_users_recyclerViewer.setVisibility(View.GONE);
            message_text_view.setVisibility(View.VISIBLE);
            message_text_view.setText("Turn on location to see nearby people");
            toolbar_switch.setChecked(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toolbar_switch != null && toolbar_switch.isChecked()){
            RequestLocationPermission();
        }
        else if(toolbar_switch != null && !toolbar_switch.isChecked()){
            near_by_users_recyclerViewer.setVisibility(View.GONE);
            message_text_view.setVisibility(View.VISIBLE);
            message_text_view.setText("Turn on location to see near by people");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribeToLocationUpdates();
    }

    public void checkForGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Ask the user to enable GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Manager");
            builder.setMessage("Please switch on the Location Service");
            builder.setPositiveButton("Go To Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    toolbar_switch.setChecked(false);
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        } else {
            // take the location location
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                Location GPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location serviceProviderLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (GPSLocation != null){
                    //trigger a location changed event using GPSLocation information
                    this.onLocationChanged(GPSLocation);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_CHANGE, MIN_DISTANCE_CHANGE, this);
                }
                else {
                    if(serviceProviderLocation != null){
                        this.onLocationChanged(serviceProviderLocation);
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_CHANGE, MIN_DISTANCE_CHANGE, NearSearchActivity.this);

                }
            }

        }

    }

    public void RequestLocationPermission(){
        if (ContextCompat.checkSelfPermission(NearSearchActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NearSearchActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOACTION);
        }
        else{
            checkForGPS();
        }
    }

    private void unsubscribeToLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOACTION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkForGPS();
        }
        else if(requestCode == MY_PERMISSIONS_REQUEST_LOACTION
                && grantResults[0] != PackageManager.PERMISSION_GRANTED){
            toolbar_switch.setChecked(false);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message;
        if (intent != null) switch (intent.getAction()) {
            case FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_ID_SUCCESS:
                message = intent.getStringExtra(FriendIntentService.DATA_MESSAGE);
                DialogFactory.getInstance().make(message,
                        findViewById(R.id.activity_near_search)).show();
                break;
            case FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_ID_FAIL:
                message = intent.getStringExtra(FriendIntentService.DATA_MESSAGE);
                DialogFactory.getInstance().make(message,
                        findViewById(R.id.activity_near_search)).show();
                break;
            case ACTION_GET_NEARBY_USERS:
                List<com.infixd.client.model.Location> nearByUsers = (List<com.infixd.client.model.Location>)
                        intent.getSerializableExtra(DATA_NEAR_USERS);
                message_text_view.setVisibility(View.GONE);
                userData.clear();
                userData.addAll(nearByUsers);
                adapter.notifyDataSetChanged();
                near_by_users_recyclerViewer.setVisibility(View.VISIBLE);
                break;
            case ACTION_NO_NEARBY_USERS:
                near_by_users_recyclerViewer.setVisibility(View.GONE);
                message_text_view.setVisibility(View.VISIBLE);
                message_text_view.setText("No nearby people");
                //DialogFactory.getInstance().make("No Nearby Users", findViewById(R.id.activity_near_search)).show();
                break;
            case UserIntentService.ACTION_GET_NEARBY_USERS_FAILED:
                near_by_users_recyclerViewer.setVisibility(View.GONE);
                message_text_view.setVisibility(View.VISIBLE);
                message_text_view.setText("No nearby people");
                //DialogFactory.getInstance().make("Server Error", findViewById(R.id.activity_near_search)).show();
                break;
            case UserIntentService.ACTION_HIDE_USER_LOCATION:
                near_by_users_recyclerViewer.setVisibility(View.GONE);
                message_text_view.setVisibility(View.VISIBLE);
                message_text_view.setText("Turn on location to see nearby people");
                break;
            case UserIntentService.ACTION_HIDE_USER_LOCATION_FAILED:
                near_by_users_recyclerViewer.setVisibility(View.GONE);
                message_text_view.setVisibility(View.VISIBLE);
                message_text_view.setText("Turn on location to see nearby people");
                DialogFactory.getInstance().make("Server Error", findViewById(R.id.activity_near_search)).show();
                break;

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Intent gnUserIntent = new Intent(NearSearchActivity.this, UserIntentService.class);
        gnUserIntent.setAction(UserIntentService.ACTION_GET_NEARBY_USERS);
        gnUserIntent.putExtra(UserIntentService.DATA_USERID, userId);
        gnUserIntent.putExtra(UserIntentService.DATA_LATITUDE, Double.toString(latitude));
        gnUserIntent.putExtra(UserIntentService.DATA_LONGITUDE, Double.toString(longitude));
        startService(gnUserIntent);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            RequestLocationPermission();
            saveSharedPreference(InfixdApp.STRING_PREFERENCE_LOCATION_STATE, "1");
        }
        else {
            saveSharedPreference(InfixdApp.STRING_PREFERENCE_LOCATION_STATE, "0");

            Intent hideLocationIntent = new Intent(NearSearchActivity.this, UserIntentService.class);
            hideLocationIntent.setAction(UserIntentService.ACTION_HIDE_USER_LOCATION);
            hideLocationIntent.putExtra(UserIntentService.DATA_USERID, userId);
            startService(hideLocationIntent);

            near_by_users_recyclerViewer.setVisibility(View.GONE);
            message_text_view.setVisibility(View.VISIBLE);
            message_text_view.setText("Turn on location to see nearby people");

            if (ActivityCompat.checkSelfPermission(NearSearchActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                unsubscribeToLocationUpdates();
            }

        }
    }
}
