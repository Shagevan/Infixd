/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Currents;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.BackgroundTasks.GetCurrentsBGT;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.UserHome.UserHomeActivity;
import com.infixd.client.model.GetAllPostsResponse;
import com.infixd.client.model.PostResponse;

import java.util.List;

public class CurrentsFragment extends Fragment{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String userId;
    private UserHomeActivity userHomeActivity;
    private ConstraintLayout emptyCurrents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currentsView = inflater.inflate(R.layout.fragment_currents, container, false);

        userHomeActivity = (UserHomeActivity) getActivity();

        userId = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        recyclerView = (RecyclerView) currentsView.findViewById(R.id.currents_recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        emptyCurrents = (ConstraintLayout) currentsView.findViewById(R.id.currents_empty);

        loadMyCurrents();

        return currentsView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserHomeActivity){
            userHomeActivity= (UserHomeActivity) context;
        }
    }

    private void loadMyCurrents(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                GetCurrentsBGT obj = new GetCurrentsBGT(getContext()){
                    @Override
                    protected void onPostExecute(GetAllPostsResponse getAllPostsResponse) {
                        super.onPostExecute(getAllPostsResponse);
                        String profilePicUrl = getAllPostsResponse.getProfilePicUrl();
                        String userName = getAllPostsResponse.getUserName();
                        List<PostResponse> posts = getAllPostsResponse.getPosts();

                        if (posts.size() < 1) {
                            emptyCurrents.setVisibility(View.VISIBLE);
                        }
                        else{
                            for (PostResponse post : posts) {
                                postIds.add(post.getPostId());
                                captions.add(post.getCaption());
                                imageUris.add(post.getImageUri());
                                textBackgroudColors.add(post.getTextBackgroundColor());
                                dates.add(post.getDate());
                                times.add(post.getTime());
                                noOfLikes.add(post.getNoOfLikes());
                                noOfShares.add(post.getNoOfPasses());
                            }
                            adapter = new CurrentsAdapter(getContext(), profilePicUrl, userName, postIds, captions, imageUris,
                                    textBackgroudColors, dates, times, noOfLikes, noOfShares);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                };
                obj.execute(userId);
            }
        });
    }

}
