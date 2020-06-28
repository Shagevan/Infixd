/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Currents;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CurrentsAdapter extends RecyclerView.Adapter<CurrentsAdapter.ViewHolder> {
    private String profilePicUrl;
    private String userName;
    private ArrayList<String> postIds = new ArrayList<String>();
    private ArrayList<String> captions = new ArrayList<String>();
    private ArrayList<String> imageUris = new ArrayList<String>();
    private ArrayList<String> textBackgroundColors = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();
    private ArrayList<String> times = new ArrayList<String>();
    private ArrayList<String> noOfLikes = new ArrayList<String>();
    private ArrayList<String> noOfShares = new ArrayList<String>();
    private Context ctx;
    public static final String LIKED_USERS = "likedUsers";
    public static final String PASSED_USERS = "passedUsers";

    public CurrentsAdapter(Context context, String profilePicUrl, String userName, ArrayList<String> postIds, ArrayList<String> captions,
                           ArrayList<String> imageUris, ArrayList<String> textBackgroundColors, ArrayList<String> dates, ArrayList<String> times,
                           ArrayList<String> noOfLikes, ArrayList<String> noOfShares) {
        this.profilePicUrl = profilePicUrl;
        this.userName = userName;
        this.postIds = postIds;
        this.captions = captions;
        this.imageUris = imageUris;
        this.textBackgroundColors = textBackgroundColors;
        this.dates = dates;
        this.times = times;
        this.noOfLikes = noOfLikes;
        this.noOfShares = noOfShares;
        this.ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ConstraintLayout only_text_layout;
        public ConstraintLayout caption_layout;
        public ConstraintLayout only_text_Likes_layout;
        public ConstraintLayout only_text_Passes_layout;
        public TextView caption_text_view;
        public TextView likes_text_view;
        public TextView passes_text_view;
        public TextView time_date_text_view;

        public ConstraintLayout only_image_layout;
        public ConstraintLayout only_image_Likes_layout;
        public ConstraintLayout only_image_Passes_layout;
        public ImageView post_image_view;
        public TextView likes_text_view1;
        public TextView passes_text_view1;
        public TextView time_date_text_view1;

        public ConstraintLayout both_views;
        public ConstraintLayout both_views_Likes_layout;
        public ConstraintLayout both_views_Passes_layout;
        public ImageView post_image_view2;
        public TextView caption_text_view2;
        public TextView likes_text_view2;
        public TextView passes_text_view2;
        public TextView time_date_text_view2;


        public ViewHolder(View v) {
            super(v);
            only_text_layout = (ConstraintLayout) v.findViewById(R.id.only_text_layout);
            caption_layout = (ConstraintLayout) v.findViewById(R.id.caption_layout);
            caption_text_view = (TextView) v.findViewById(R.id.caption_text_view);
            likes_text_view = (TextView) v.findViewById(R.id.likes_text_view);
            passes_text_view = (TextView) v.findViewById(R.id.passes_text_view);
            time_date_text_view = (TextView) v.findViewById(R.id.time_date_text_view);
            only_text_Likes_layout = (ConstraintLayout) v.findViewById(R.id.likes_layout);
            only_text_Passes_layout = (ConstraintLayout) v.findViewById(R.id.passes_layout);

            only_image_layout = (ConstraintLayout) v.findViewById(R.id.only_image_layout);
            post_image_view = (ImageView) v.findViewById(R.id.post_image_view);
            likes_text_view1 = (TextView) v.findViewById(R.id.likes_text_view1);
            passes_text_view1 = (TextView) v.findViewById(R.id.passes_text_view1);
            time_date_text_view1 = (TextView) v.findViewById(R.id.time_date_text_view1);
            only_image_Likes_layout = (ConstraintLayout) v.findViewById(R.id.likes_layout1);
            only_image_Passes_layout = (ConstraintLayout) v.findViewById(R.id.passes_layout1);

            both_views = (ConstraintLayout) v.findViewById(R.id.both_views);
            post_image_view2 = (ImageView) v.findViewById(R.id.post_image_view2);
            caption_text_view2 = (TextView) v.findViewById(R.id.caption_text_view2);
            likes_text_view2 = (TextView) v.findViewById(R.id.likes_text_view2);
            passes_text_view2 = (TextView) v.findViewById(R.id.passes_text_view2);
            time_date_text_view2 = (TextView) v.findViewById(R.id.time_date_text_view2);
            both_views_Likes_layout = (ConstraintLayout) v.findViewById(R.id.likes_layout2);
            both_views_Passes_layout = (ConstraintLayout) v.findViewById(R.id.passes_layout2);

            only_image_Likes_layout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openReactedUsersActivity(LIKED_USERS,getAdapterPosition());
                        }
                    }
            );

            only_image_Passes_layout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openReactedUsersActivity(PASSED_USERS,getAdapterPosition());
                        }
                    }
            );

            only_text_Likes_layout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openReactedUsersActivity(LIKED_USERS,getAdapterPosition());
                        }
                    }
            );

            only_text_Passes_layout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openReactedUsersActivity(PASSED_USERS,getAdapterPosition());
                        }
                    }
            );

            both_views_Likes_layout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openReactedUsersActivity(LIKED_USERS,getAdapterPosition());
                        }
                    }
            );

            both_views_Passes_layout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openReactedUsersActivity(PASSED_USERS,getAdapterPosition());
                        }
                    }
            );


        }
    }


    @Override
    public CurrentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.currents_row, parent, false);
        return new CurrentsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CurrentsAdapter.ViewHolder holder, int position) {

        String caption = captions.get(position);
        String imageUri = imageUris.get(position);
        String colorCode = textBackgroundColors.get(position);
        String time = times.get(position);
        String date = dates.get(position);
        String noOfLike = noOfLikes.get(position);
        String noOfShare = noOfShares.get(position);

        // when only image is coming
        if (caption.isEmpty() && (imageUri != null || !imageUri.isEmpty())){
            holder.only_image_layout.setVisibility(View.VISIBLE);
            holder.only_text_layout.setVisibility(View.GONE);
            holder.time_date_text_view1.setText(date+" "+time);
            holder.likes_text_view1.setText(noOfLike);
            holder.passes_text_view1.setText(noOfShare);

            Picasso.with(ctx)
                    .load(imageUri)
                    .into((holder.post_image_view));
        }

        // when only text is coming
        else if ((imageUri == null || imageUri.isEmpty()) && !caption.isEmpty()){
            switch (colorCode){
                case "red" :
                    holder.caption_layout.setBackgroundResource(R.color.red_color);
                    holder.caption_text_view.setTextColor(Color.WHITE);
                    break;
                case "blue" :
                    holder.caption_layout.setBackgroundResource(R.color.blue_color);
                    holder.caption_text_view.setTextColor(Color.WHITE);
                    break;
                case "green" :
                    holder.caption_layout.setBackgroundResource(R.color.green_color);
                    holder.caption_text_view.setTextColor(Color.WHITE);
                    break;
                case "yellow" :
                    holder.caption_layout.setBackgroundResource(R.color.yellow_color);
                    holder.caption_text_view.setTextColor(Color.BLACK);
                    break;
                case "purple" :
                    holder.caption_layout.setBackgroundResource(R.color.purple_color);
                    holder.caption_text_view.setTextColor(Color.WHITE);
                    break;
                case "maroon" :
                    holder.caption_layout.setBackgroundResource(R.color.maroon_color);
                    holder.caption_text_view.setTextColor(Color.WHITE);
                    break;
                case "black" :
                    holder.caption_layout.setBackgroundResource(R.color.black_color);
                    holder.caption_text_view.setTextColor(Color.WHITE);
                    break;
            }
            holder.caption_text_view.setText(caption);
            holder.time_date_text_view.setText(date+" "+time);
            holder.likes_text_view.setText(noOfLike);
            holder.passes_text_view.setText(noOfShare);

        }

        // when text and image is coming
        else if (imageUri != null && !imageUri.isEmpty() && caption != null){
            holder.both_views.setVisibility(View.VISIBLE);
            holder.only_text_layout.setVisibility(View.GONE);
            holder.caption_text_view2.setText(caption);
            holder.time_date_text_view2.setText(date+" "+time);
            holder.likes_text_view2.setText(noOfLike);
            holder.passes_text_view2.setText(noOfShare);

            Picasso.with(ctx)
                    .load(imageUri)
                    .into((holder.post_image_view2));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return postIds.size();
    }

    private void openReactedUsersActivity(String type, int position){
        String postId = postIds.get(position);
        Intent intent = new Intent(ctx, ReactedUsersActivity.class);
        intent.putExtra("postId", postId);
        intent.putExtra("type", type);
        ctx.startActivity(intent);
    }
}
