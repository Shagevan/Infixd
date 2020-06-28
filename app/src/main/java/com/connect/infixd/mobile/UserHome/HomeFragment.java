/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.UserHome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.BackgroundTasks.GetCurrentFeedsBGT;
import com.connect.infixd.mobile.BackgroundTasks.NewPostBGT;
import com.connect.infixd.mobile.CustomViews.DividerItemDecoration;
import com.connect.infixd.mobile.POJOModels.NotificationRowData;
import com.connect.infixd.mobile.Post.NewPostActivity;
import com.connect.infixd.mobile.Post.ShareAlertDialog;
import com.connect.infixd.mobile.R;
import com.daimajia.swipe.util.Attributes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infixd.client.model.CreatePostResponse;
import com.infixd.client.model.CurrentResponse;
import com.infixd.client.model.GetCurrentFeedsResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private static final String TAG = "HomeFragment";
    private static final String FIRST_PAGE = "0";
    private static final int UPLOAD_CURRENT_PHOTO = 107;

    private RecyclerView home_recyclerview;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ConstraintLayout homePageEmptyView;
    private String userId;
    private FloatingActionButton fab;
    private UserHomeActivity userHomeActivity;
    private boolean isFirstLoading = true;
    private UserHomePaginationAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private Button inviteBtn;
    private Uri imageURL;
    private String caption;
    private String textBackgroundColor;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mCurrentPhotosStorageReference;
    private StorageReference photoRef;
    public ProgressBar progressBar;
    public ConstraintLayout ProgressbarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        userHomeActivity = (UserHomeActivity) getActivity();
        userId = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
        homePageEmptyView = (ConstraintLayout) v.findViewById(R.id.home_empty);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.home_fragment_swipe_refresh_layout);
        home_recyclerview = (RecyclerView) v.findViewById(R.id.home_recyclerview);
        inviteBtn =(Button) v.findViewById(R.id.invite_button);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar_home);
        ProgressbarView = (ConstraintLayout)v.findViewById(R.id.ProgressbarView);

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userHomeActivity.inviteFriends();
            }
        });

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewPostActivity.class);
                startActivityForResult(intent,UPLOAD_CURRENT_PHOTO);
            }
        });

        adapter = new UserHomePaginationAdapter(getContext());
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        home_recyclerview.addItemDecoration(new DividerItemDecoration());
        home_recyclerview.setLayoutManager(linearLayoutManager);
        home_recyclerview.setHasFixedSize(false);
        home_recyclerview.setItemAnimator(new DefaultItemAnimator());
        adapter.setMode(Attributes.Mode.Single);
        home_recyclerview.setAdapter(adapter);

        home_recyclerview.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 ||dy<0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadCurrents(String.valueOf(currentPage));
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        loadCurrents(FIRST_PAGE);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == UPLOAD_CURRENT_PHOTO
                && resultCode == Activity.RESULT_OK) {
            uploadCurrent(data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserHomeActivity){
            userHomeActivity= (UserHomeActivity) context;
        }
    }

    private void loadCurrents(String pageNumber) {
        Log.d(TAG, "loadCurrent Current Page: " + currentPage);
        new GetCurrentFeedsBGT(getContext()){
            @Override
            protected void onPostExecute(GetCurrentFeedsResponse getCurrentFeedsResponse) {
                super.onPostExecute(getCurrentFeedsResponse);
                if(getCurrentFeedsResponse != null && getCurrentFeedsResponse.getCurrentResponses() != null){
                    List<CurrentResponse> result = getCurrentFeedsResponse.getCurrentResponses();
                    ArrayList<NotificationRowData> currentsData = new ArrayList<NotificationRowData>();
                    for (CurrentResponse currentResponse : result) {
                        NotificationRowData obj = new NotificationRowData();
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(Long.valueOf(currentResponse.getTimeStamp()));
                        String dateTime = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
                        obj.setType(currentResponse.getType());
                        obj.setCreatorId(currentResponse.getCreatorId());
                        obj.setCreatorName(currentResponse.getCreatorName());
                        obj.setSharedUserId(currentResponse.getSharedUserId());
                        obj.setSharedUserName(currentResponse.getSharedUserName());
                        obj.setCaption(currentResponse.getCaption());
                        obj.setImageUri(currentResponse.getImageUri());
                        obj.setTextBackgroundColor(currentResponse.getTextBackgroundColor());
                        obj.setProfilePicUrl(currentResponse.getProfilePicUrl());
                        obj.setCreatorProfilePic(currentResponse.getCreatorProfilePic());
                        obj.setPostId(currentResponse.getPostId());
                        obj.setTime(dateTime);
                        obj.setNoOfLikes(currentResponse.getNoOfLikes());
                        obj.setNoOfShares(currentResponse.getNoOfShares());
                        obj.setLiked(currentResponse.isLiked());
                        currentsData.add(obj);
                    }

                    if(isFirstLoading){
                        isFirstLoading = false;
                        if(currentsData.size() == 0){
                            homePageEmptyView.setVisibility(View.VISIBLE);
                            home_recyclerview.setVisibility(View.GONE);
                        }
                        else{
                            adapter.addAll(currentsData);
                            if (currentsData.size()>0) adapter.addLoadingFooter();
                            else isLastPage = true;
                        }

                    }
                    else{
                        adapter.removeLoadingFooter();
                        isLoading = false;

                        adapter.addAll(currentsData);

                        if (currentsData.size()>0) adapter.addLoadingFooter();
                        else isLastPage = true;

                    }

                }
            }
        }.execute(userId,pageNumber);

    }

    private void refreshContent(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.reSetClockClockValues();
                isFirstLoading = true;
                currentPage = 0;
                isLastPage = false;
                loadCurrents(FIRST_PAGE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        },1000);
    }

    private void uploadCurrent(Intent intent){
        imageURL = intent.getParcelableExtra("imageURL");
        caption = intent.getStringExtra("caption");
        textBackgroundColor = intent.getStringExtra("textBackgroundColor");
        new UploadCurrent().execute();
    }


    private class UploadCurrent extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFirebaseStorage = FirebaseStorage.getInstance();
            mCurrentPhotosStorageReference = mFirebaseStorage.getReference().child("CURRENTS_PHOTO");
            photoRef = mCurrentPhotosStorageReference.child(imageURL.getLastPathSegment());
            ProgressbarView.setVisibility(View.VISIBLE);
            //progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(100);
        }

        @Override
        protected String doInBackground(String... params) {

            // Upload file to Firebase Storage
            photoRef.putFile(imageURL)
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            @SuppressWarnings("VisibleForTests")
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            new NewPostBGT(getActivity()){
                                @Override
                                protected void onPostExecute(CreatePostResponse response) {
                                    super.onPostExecute(response);
                                    Intent shareSocialIntent = new Intent(getContext(), ShareAlertDialog.class);
                                    shareSocialIntent.putExtra("caption", caption);
                                    shareSocialIntent.putExtra("imageUri", downloadUrl.toString());
                                    shareSocialIntent.putExtra("shareAlertDialogTitle", "Current has been posted !");
                                    startActivity(shareSocialIntent);
                                    ProgressbarView.setVisibility(View.GONE);
                                }
                            }.execute(userId, caption, downloadUrl.toString(), textBackgroundColor);

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Double completedTaskVal = (100.0 * taskSnapshot.getBytesTransferred())
                                    / taskSnapshot.getTotalByteCount();
                            publishProgress(Integer.toString(completedTaskVal.intValue()));
                        }
                    });

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            progressBar.setProgress(Integer.parseInt(progress[0]));
        }
    }

}
