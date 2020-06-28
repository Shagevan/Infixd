/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.NearSearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.BackgroundTasks.GetUserDetailsBGT;
import com.connect.infixd.mobile.BackgroundTasks.SendDirectFriendRequestByIDBGT;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.intentservices.FriendIntentService;
import com.infixd.client.model.Location;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Card;

import java.util.ArrayList;
import java.util.List;


public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {
    private List<Location> userData = new ArrayList<>();
    private Context context;
    private NearSearchActivity nearSearchActivity;

    public UsersRecyclerViewAdapter(Context context, List<Location> userData) {
        this.context = context;
        this.userData = userData;
        this.nearSearchActivity = (NearSearchActivity) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_pic_image_view;
        ImageView add_friend_button;
        TextView user_name_textview;
        TextView user_profession_textview;
        TextView user_education_textview;
        CardView neaSearchRowCard;

        public ViewHolder(View itemView) {
            super(itemView);
            profile_pic_image_view = (ImageView) itemView.findViewById(R.id.nearSearchRowUserIV);
            add_friend_button = (ImageView) itemView.findViewById(R.id.nearSearchRowAddIV);
            user_name_textview = (TextView) itemView.findViewById(R.id.nearSearchRowNameTV);
            user_profession_textview = (TextView) itemView.findViewById(R.id.nearSearchRowProfessionTV);
            user_education_textview = (TextView) itemView.findViewById(R.id.nearSearchRowEducationTV);
            neaSearchRowCard = (CardView) itemView.findViewById(R.id.neaSearchRowCard);

            neaSearchRowCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String userId = userData.get(position).getUserId();
                    GetUserDetailsBGT getUserDetailsBGT = new GetUserDetailsBGT(nearSearchActivity);

                    if(userData.get(position).isIsFriend()){
                        getUserDetailsBGT.execute("userProfileFriend", userId);
                    }
                    else {
                        getUserDetailsBGT.execute("userProfileNotFriend", userId);
                    }
                }
            });

            add_friend_button.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            String senderId = nearSearchActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
                            String targetId = userData.get(position).getUserId();

                            Intent drIntent = new Intent(nearSearchActivity, FriendIntentService.class);
                            drIntent.setAction(FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_ID);
                            drIntent.putExtra(FriendIntentService.DATA_SENDER_ID, senderId);
                            drIntent.putExtra(FriendIntentService.DATA_TARGET_ID, targetId);

                            nearSearchActivity.startService(drIntent);

                            //SendDirectFriendRequestByIDBGT obj = new SendDirectFriendRequestByIDBGT(context);
                            //obj.execute(senderId, targetId);
                        }
                    }
            );
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.near_search_user_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.user_name_textview.setText(userData.get(position).getFullName());
        holder.user_profession_textview.setText(userData.get(position).getProfession());
        holder.user_education_textview.setText(userData.get(position).getEducation());

        if(userData.get(position).isIsFriend()){
            holder.add_friend_button.setVisibility(View.GONE);
        }

        if(userData.get(position).getProfilePicLink() != null
                && !userData.get(position).getProfilePicLink().isEmpty()){
            Picasso.with(context)
                    .load(userData.get(position).getProfilePicLink())
                    .into(holder.profile_pic_image_view);
        }
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

}
