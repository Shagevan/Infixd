package com.connect.infixd.mobile.UserHome;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.connect.infixd.mobile.Animation.MyBounceInterpolator;
import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.BackgroundTasks.CheckFriendRelationBGT;
import com.connect.infixd.mobile.BackgroundTasks.GetUserDetailsBGT;
import com.connect.infixd.mobile.BackgroundTasks.LikeBGT;
import com.connect.infixd.mobile.BackgroundTasks.SharePostBGT;
import com.connect.infixd.mobile.Chat.ChatActivity;
import com.connect.infixd.mobile.POJOModels.NotificationRowData;
import com.connect.infixd.mobile.Post.ShareAlertDialog;
import com.connect.infixd.mobile.R;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.infixd.client.model.LikePostResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserHomePaginationAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private UserHomeActivity userHomeActivity;
    private ArrayList<NotificationRowData> notificationData;
    private static final String CREATED_BY = "CREATED_BY";
    private static final String ACTION_PASS_TO_CHAT = "action_pass_to_chat";
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private ArrayList<Boolean> clockClockValues = new ArrayList<>();
    private ArrayList<Boolean> isLikedValues = new ArrayList<>();
    private boolean isLoadingAdded = false;

    public UserHomePaginationAdapter(Context context) {
        this.ctx = context;
        notificationData = new ArrayList<NotificationRowData>();
        this.userHomeActivity = (UserHomeActivity) ctx;
    }

    public List<NotificationRowData> getCurrents() {
        return notificationData;
    }

    public void setCurrents(ArrayList<NotificationRowData> currents) {
        this.notificationData = currents;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.notification_post_row, parent, false);
        viewHolder = new PostNotificationViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case ITEM:

                PostNotificationViewHolder postNotificationViewHolder = (PostNotificationViewHolder) holder;
                clockClockValues.add(false);
                String caption = notificationData.get(position).getCaption();
                String imageUri = notificationData.get(position).getImageUri();
                String colorCode = notificationData.get(position).getTextBackgroundColor();
                String creatorName = notificationData.get(position).getCreatorName();
                String sharedUserName = notificationData.get(position).getSharedUserName();
                String sharedUserProfilePic = notificationData.get(position).getProfilePicUrl();
                String creatorProfilePic = notificationData.get(position).getCreatorProfilePic();
                String time = notificationData.get(position).getTime();
                String noOfLikes = notificationData.get(position).getNoOfLikes();
                String noOfShares = notificationData.get(position).getNoOfShares();
                String dateTimeHeader = "";
                boolean isLiked = notificationData.get(position).isLiked();
                isLikedValues.add(position,isLiked);

                if(notificationData.get(position).getType().equals(CREATED_BY)) dateTimeHeader = "Created";
                else dateTimeHeader = "Passed";

                if(noOfLikes == null || noOfLikes == "0"){
                    noOfLikes = "0";
                }

                if(noOfShares == null || noOfShares == "0"){
                    noOfShares = "0";
                }

                final Animation myAnim = AnimationUtils.loadAnimation(ctx, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);

                // when only image is coming
                if (caption.isEmpty() && (imageUri != null || !imageUri.isEmpty())) {
                    postNotificationViewHolder.only_image_layout.setVisibility(View.VISIBLE);
                    postNotificationViewHolder.only_text_layout.setVisibility(View.GONE);
                    postNotificationViewHolder.both_views.setVisibility(View.GONE);
                    postNotificationViewHolder.likes_text_view1.setText(noOfLikes);
                    postNotificationViewHolder.passes_text_view1.setText(noOfShares);
                    postNotificationViewHolder.showDateTimeHeaderTV1.setText(dateTimeHeader);
                    postNotificationViewHolder.showDateTimeContentTV1.setText(time);
                    if(isLiked) postNotificationViewHolder.likes_image_view1.setImageResource(R.drawable.like_icon_after_press);
                    else postNotificationViewHolder.likes_image_view1.setImageResource(R.drawable.like_icon_before_press);

                    Picasso.with(ctx)
                            .load(imageUri)
                            .into(postNotificationViewHolder.post_image_view);

                    if (notificationData.get(position).getType().equals(CREATED_BY)) {
                        postNotificationViewHolder.shared_user_name1.setText(creatorName);
                        postNotificationViewHolder.image_creator_image_view.setVisibility(View.GONE);
                        postNotificationViewHolder.share_in_between_image_view1.setVisibility(View.GONE);
                        if(creatorProfilePic != null && !creatorProfilePic.isEmpty()){
                            Picasso.with(ctx)
                                    .load(creatorProfilePic)
                                    .into(postNotificationViewHolder.shared_user_image_view1);
                        }
                    }

                    else {
                        postNotificationViewHolder.image_creator_image_view.setVisibility(View.VISIBLE);
                        postNotificationViewHolder.share_in_between_image_view1.setVisibility(View.VISIBLE);
                        postNotificationViewHolder.shared_user_name1.setText(sharedUserName);
                        if(sharedUserProfilePic != null && !sharedUserProfilePic.isEmpty()){
                            Picasso.with(ctx)
                                    .load(sharedUserProfilePic)
                                    .into(postNotificationViewHolder.shared_user_image_view1);
                        }

                        if(creatorProfilePic != null && !creatorProfilePic.isEmpty()){
                            Picasso.with(ctx)
                                    .load(creatorProfilePic)
                                    .into(postNotificationViewHolder.image_creator_image_view);
                        }

                    }

                    postNotificationViewHolder.likes_image_view1.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String userId = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
                                    String postId = notificationData.get(position).getPostId();
                                    if(!isLikedValues.get(position)){
                                        postNotificationViewHolder.likes_image_view1.startAnimation(myAnim);
                                        postNotificationViewHolder.likes_image_view1.setImageResource(R.drawable.like_icon_after_press);
                                        isLikedValues.add(position,true);
                                    }
                                    else{
                                        postNotificationViewHolder.likes_image_view1.startAnimation(myAnim);
                                        postNotificationViewHolder.likes_image_view1.setImageResource(R.drawable.like_icon_before_press);
                                        isLikedValues.add(position,false);
                                    }
                                    updateNumberOfLikes(postNotificationViewHolder.likes_image_view1,
                                            postNotificationViewHolder.likes_text_view1,userId,postId);
                                }
                            }
                    );

                    postNotificationViewHolder.showTimeIV1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!clockClockValues.get(position)){
                                // Prepare the View for the animation
                                postNotificationViewHolder.timeDateView1.setVisibility(View.VISIBLE);
                                postNotificationViewHolder.timeDateView1.setAlpha(0.0f);

                                // Start the animation
                                postNotificationViewHolder.timeDateView1.animate()
                                        .translationXBy(-5)
                                        .alpha(1.0f)
                                        .setListener(null);
                                clockClockValues.add(position,true);
                            }
                            else{
                                postNotificationViewHolder.timeDateView1.animate()
                                        .translationXBy(0)
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                postNotificationViewHolder.timeDateView1.setVisibility(View.GONE);
                                            }
                                        });
                                clockClockValues.add(position,false);
                            }

                        }
                    });
                }

                // when only text is coming
                else if ((imageUri == null || imageUri.isEmpty()) && !caption.isEmpty()){
                    postNotificationViewHolder.only_text_layout.setVisibility(View.VISIBLE);
                    postNotificationViewHolder.only_image_layout.setVisibility(View.GONE);
                    postNotificationViewHolder.both_views.setVisibility(View.GONE);
                    postNotificationViewHolder.caption_text_view.setText(caption);
                    postNotificationViewHolder.likes_text_view.setText(noOfLikes);
                    postNotificationViewHolder.passes_text_view.setText(noOfShares);
                    postNotificationViewHolder.showDateTimeHeaderTV.setText(dateTimeHeader);
                    postNotificationViewHolder.showDateTimeContentTV.setText(time);
                    if(isLiked) postNotificationViewHolder.likes_image_view.setImageResource(R.drawable.like_icon_after_press);
                    else postNotificationViewHolder.likes_image_view.setImageResource(R.drawable.like_icon_before_press);

                    if (notificationData.get(position).getType().equals(CREATED_BY)){
                        postNotificationViewHolder.shared_user_name.setText(creatorName);
                        postNotificationViewHolder.text_creator_image_view.setVisibility(View.GONE);
                        postNotificationViewHolder.share_in_between_image_view.setVisibility(View.GONE);

                        if(creatorProfilePic != null && !creatorProfilePic.isEmpty()){
                            Picasso.with(ctx)
                                    .load(creatorProfilePic)
                                    .into(postNotificationViewHolder.shared_user_image_view);
                        }

                    }

                    else {
                        postNotificationViewHolder.text_creator_image_view.setVisibility(View.VISIBLE);
                        postNotificationViewHolder.share_in_between_image_view.setVisibility(View.VISIBLE);
                        postNotificationViewHolder.shared_user_name.setText(sharedUserName);
                        if(sharedUserProfilePic != null && !sharedUserProfilePic.isEmpty()){
                            Picasso.with(ctx)
                                    .load(sharedUserProfilePic)
                                    .into(postNotificationViewHolder.shared_user_image_view);
                        }

                        if(creatorProfilePic != null && !creatorProfilePic.isEmpty()){
                            Picasso.with(ctx)
                                    .load(creatorProfilePic)
                                    .into(postNotificationViewHolder.text_creator_image_view);
                        }

                    }

                    switch (colorCode){
                        case "red" :
                            postNotificationViewHolder.caption_layout.setBackgroundResource(R.color.red_color);
                            postNotificationViewHolder.caption_text_view.setTextColor(ctx.getResources().getColor(R.color.white_color));
                            break;
                        case "blue" :
                            postNotificationViewHolder.caption_layout.setBackgroundResource(R.color.blue_color);
                            postNotificationViewHolder.caption_text_view.setTextColor(ctx.getResources().getColor(R.color.white_color));
                            break;
                        case "green" :
                            postNotificationViewHolder.caption_layout.setBackgroundResource(R.color.green_color);
                            postNotificationViewHolder.caption_text_view.setTextColor(ctx.getResources().getColor(R.color.white_color));
                            break;
                        case "yellow" :
                            postNotificationViewHolder.caption_layout.setBackgroundResource(R.color.yellow_color);
                            postNotificationViewHolder.caption_text_view.setTextColor(ctx.getResources().getColor(R.color.black_color));
                            break;
                        case "purple" :
                            postNotificationViewHolder.caption_layout.setBackgroundResource(R.color.purple_color);
                            postNotificationViewHolder.caption_text_view.setTextColor(ctx.getResources().getColor(R.color.white_color));
                            break;
                        case "maroon" :
                            postNotificationViewHolder.caption_layout.setBackgroundResource(R.color.maroon_color);
                            postNotificationViewHolder.caption_text_view.setTextColor(ctx.getResources().getColor(R.color.white_color));
                            break;
                        case "black" :
                            postNotificationViewHolder.caption_layout.setBackgroundResource(R.color.black_color);
                            postNotificationViewHolder.caption_text_view.setTextColor(ctx.getResources().getColor(R.color.white_color));
                            break;
                    }

                    postNotificationViewHolder.likes_image_view.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String userId = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
                                    String postId = notificationData.get(position).getPostId();
                                    if(!isLikedValues.get(position)){
                                        postNotificationViewHolder.likes_image_view.startAnimation(myAnim);
                                        postNotificationViewHolder.likes_image_view.setImageResource(R.drawable.like_icon_after_press);
                                        isLikedValues.add(position,true);
                                    }
                                    else{
                                        postNotificationViewHolder.likes_image_view.startAnimation(myAnim);
                                        postNotificationViewHolder.likes_image_view.setImageResource(R.drawable.like_icon_before_press);
                                        isLikedValues.add(position,false);
                                    }
                                    updateNumberOfLikes(postNotificationViewHolder.likes_image_view,
                                            postNotificationViewHolder.likes_text_view,userId,postId);
                                }
                            }
                    );

                    postNotificationViewHolder.showTimeIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!clockClockValues.get(position)){
                                // Prepare the View for the animation
                                postNotificationViewHolder.timeDateView.setVisibility(View.VISIBLE);
                                postNotificationViewHolder.timeDateView.setAlpha(0.0f);

                                // Start the animation
                                postNotificationViewHolder.timeDateView.animate()
                                        .translationXBy(-5)
                                        .alpha(1.0f)
                                        .setListener(null);
                                clockClockValues.add(position,true);
                            }
                            else{
                                postNotificationViewHolder.timeDateView.animate()
                                        .translationXBy(0)
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                postNotificationViewHolder.timeDateView.setVisibility(View.GONE);
                                            }
                                        });
                                clockClockValues.add(position,false);
                            }

                        }
                    });
                }

                // when text and image is coming
                else if (imageUri != null && !imageUri.isEmpty() && caption != null){
                    postNotificationViewHolder.both_views.setVisibility(View.VISIBLE);
                    postNotificationViewHolder.only_text_layout.setVisibility(View.GONE);
                    postNotificationViewHolder.only_image_layout.setVisibility(View.GONE);
                    postNotificationViewHolder.caption_text_view2.setText(caption);
                    postNotificationViewHolder.likes_text_view2.setText(noOfLikes);
                    postNotificationViewHolder.passes_text_view2.setText(noOfShares);
                    postNotificationViewHolder.showDateTimeHeaderTV2.setText(dateTimeHeader);
                    postNotificationViewHolder.showDateTimeContentTV2.setText(time);
                    if(isLiked) postNotificationViewHolder.likes_image_view2.setImageResource(R.drawable.like_icon_after_press);
                    else postNotificationViewHolder.likes_image_view2.setImageResource(R.drawable.like_icon_before_press);

                    Picasso.with(ctx)
                            .load(imageUri)
                            .into(postNotificationViewHolder.post_image_view2);

                    if (notificationData.get(position).getType().equals(CREATED_BY)){
                        postNotificationViewHolder.shared_user_name2.setText(creatorName);
                        postNotificationViewHolder.both_views_creator.setVisibility(View.GONE);
                        postNotificationViewHolder.share_in_between_image_view2.setVisibility(View.GONE);
                        if(creatorProfilePic != null && !creatorProfilePic.isEmpty()){
                            Picasso.with(ctx)
                                    .load(creatorProfilePic)
                                    .into(postNotificationViewHolder.shared_user_image_view2);
                        }
                    }

                    else {
                        postNotificationViewHolder.both_views_creator.setVisibility(View.VISIBLE);
                        postNotificationViewHolder.share_in_between_image_view2.setVisibility(View.VISIBLE);
                        postNotificationViewHolder.shared_user_name2.setText(sharedUserName);
                        if(sharedUserProfilePic != null && !sharedUserProfilePic.isEmpty()){
                            Picasso.with(ctx)
                                    .load(sharedUserProfilePic)
                                    .into(postNotificationViewHolder.shared_user_image_view2);
                        }

                        if(creatorProfilePic != null && !creatorProfilePic.isEmpty()){
                            Picasso.with(ctx)
                                    .load(creatorProfilePic)
                                    .into(postNotificationViewHolder.both_views_creator);
                        }
                    }

                    postNotificationViewHolder.likes_image_view2.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String userId = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
                                    String postId = notificationData.get(position).getPostId();
                                    if(!isLikedValues.get(position)){
                                        postNotificationViewHolder.likes_image_view2.startAnimation(myAnim);
                                        postNotificationViewHolder.likes_image_view2.setImageResource(R.drawable.like_icon_after_press);
                                        isLikedValues.add(position,true);
                                    }
                                    else{
                                        postNotificationViewHolder.likes_image_view2.startAnimation(myAnim);
                                        postNotificationViewHolder.likes_image_view2.setImageResource(R.drawable.like_icon_before_press);
                                        isLikedValues.add(position,false);
                                    }
                                    updateNumberOfLikes(postNotificationViewHolder.likes_image_view2,
                                            postNotificationViewHolder.likes_text_view2,userId,postId);
                                }
                            }
                    );

                    postNotificationViewHolder.showTimeIV2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!clockClockValues.get(position)){
                                // Prepare the View for the animation
                                postNotificationViewHolder.timeDateView2.setVisibility(View.VISIBLE);
                                postNotificationViewHolder.timeDateView2.setAlpha(0.0f);

                                // Start the animation
                                postNotificationViewHolder.timeDateView2.animate()
                                        .translationXBy(-5)
                                        .alpha(1.0f)
                                        .setListener(null);
                                clockClockValues.add(position,true);
                            }
                            else{
                                postNotificationViewHolder.timeDateView2.animate()
                                        .translationXBy(0)
                                        .alpha(0.0f)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                postNotificationViewHolder.timeDateView2.setVisibility(View.GONE);
                                            }
                                        });
                                clockClockValues.add(position,false);
                            }

                        }
                    });
                }

                postNotificationViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

                // Drag From Left
                postNotificationViewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left,
                        postNotificationViewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

                // Disable Drag From Right
                postNotificationViewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, null);
                postNotificationViewHolder.swipeLayout.setRightSwipeEnabled(false);

                // Handling different events when swiping
                postNotificationViewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                    @Override
                    public void onClose(SwipeLayout layout) {
                        //when the SurfaceView totally cover the BottomView.
                    }

                    @Override
                    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                        //you are swiping.
                    }

                    @Override
                    public void onStartOpen(SwipeLayout layout) {

                    }


                    @Override
                    public void onOpen(SwipeLayout layout) {
                        //when the BottomView totally show.
                    }

                    @Override
                    public void onStartClose(SwipeLayout layout) {

                    }

                    @Override
                    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                        //when user's hand released.
                    }
                });

                // mItemManger is member in RecyclerSwipeAdapter Class
                mItemManger.bindView(holder.itemView, position);

                break;
            case LOADING:
               //Do nothing
                break;
        }

    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public int getItemCount() {
        return notificationData == null ? 0 : notificationData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == notificationData.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /**
     * Helper Methods
     */

    private void updateNumberOfLikes(ImageView imageView, TextView view, String userId, String postId){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                LikeBGT obj = new LikeBGT(ctx){
                    @Override
                    protected void onPostExecute(LikePostResponse response) {
                        super.onPostExecute(response);
                        view.setText(response.getNoOfLikes());
                        /*if(response.getLikeStatus().equals("Like")){
                            imageView.setImageResource(R.drawable.like_icon_after_press);
                        }
                        else{
                            imageView.setImageResource(R.drawable.like_icon_before_press);
                        }*/
                    }
                };
                obj.execute(userId, postId);
            }
        });
    }

    public void add(NotificationRowData data) {
        notificationData.add(data);
        notifyItemInserted(notificationData.size() - 1);
    }

    public void addAll(List<NotificationRowData> ndList) {
        Log.d("", "loadCurrent Current Page: " + ndList.size());
        for (NotificationRowData data : ndList) {
            add(data);
        }
    }

    public void reSetClockClockValues() {
        this.clockClockValues = new ArrayList<>();
    }

    public void remove(NotificationRowData data) {
        int position = notificationData.indexOf(data);
        if (position > -1) {
            notificationData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new NotificationRowData());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = notificationData.size() - 1;
        NotificationRowData item = getItem(position);

        if (item != null) {
            notificationData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public NotificationRowData getItem(int position) {
        return notificationData.get(position);
    }

    public void navigateToProfile(String contactId){
        String userId = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
        if(contactId.equals(userId)){
            GetUserDetailsBGT getUserDetailsBGT = new GetUserDetailsBGT(ctx);
            getUserDetailsBGT.execute("userProfile", userId);
        }
        else{
            CheckFriendRelationBGT checkFriendRelationBGT = new CheckFriendRelationBGT(ctx);
            checkFriendRelationBGT.execute(userId, contactId);
        }
    }


    /**
     * Main list's content ViewHolder
     */

    public class PostNotificationViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout only_text_layout;
        public TextView caption_text_view;
        public ConstraintLayout caption_layout;
        public ImageView text_creator_image_view;
        public TextView shared_user_name;
        public ImageView shared_user_image_view;
        public TextView likes_text_view;
        public TextView passes_text_view;
        public ImageView showTimeIV;
        public ImageView share_in_between_image_view;
        public ImageView likes_image_view;
        public ConstraintLayout timeDateView;
        public TextView showDateTimeHeaderTV;
        public TextView showDateTimeContentTV;

        public ConstraintLayout only_image_layout;
        public ImageView post_image_view;
        public ImageView image_creator_image_view;
        public TextView shared_user_name1;
        public ImageView shared_user_image_view1;
        public TextView likes_text_view1;
        public TextView passes_text_view1;
        public ImageView showTimeIV1;
        public ImageView share_in_between_image_view1;
        public ImageView likes_image_view1;
        public ConstraintLayout timeDateView1;
        public TextView showDateTimeHeaderTV1;
        public TextView showDateTimeContentTV1;

        public ConstraintLayout both_views;
        public ImageView post_image_view2;
        public TextView caption_text_view2;
        public ImageView both_views_creator;
        public TextView shared_user_name2;
        public ImageView shared_user_image_view2;
        public TextView likes_text_view2;
        public TextView passes_text_view2;
        public ImageView showTimeIV2;
        public ImageView share_in_between_image_view2;
        public ImageView likes_image_view2;
        public ConstraintLayout timeDateView2;
        public TextView showDateTimeHeaderTV2;
        public TextView showDateTimeContentTV2;

        public SwipeLayout swipeLayout;
        public TextView tvEdit;
        public TextView tvShare;

        public PostNotificationViewHolder(View v) {
            super(v);

            only_text_layout = (LinearLayout) v.findViewById(R.id.only_text_layout);
            caption_layout = (ConstraintLayout) v.findViewById(R.id.caption_layout);
            caption_text_view = (TextView) v.findViewById(R.id.caption_text_view);
            text_creator_image_view = (ImageView) v.findViewById(R.id.text_creator_image_view);
            shared_user_name = (TextView) v.findViewById(R.id.shared_user_name);
            shared_user_image_view = (ImageView) v.findViewById(R.id.shared_user_image_view);
            likes_text_view = (TextView) v.findViewById(R.id.likes_text_view);
            passes_text_view = (TextView) v.findViewById(R.id.passes_text_view);
            showTimeIV = (ImageView) v.findViewById(R.id.showTimeIV);
            share_in_between_image_view = (ImageView) v.findViewById(R.id.share_in_between_IV);
            likes_image_view = (ImageView) v.findViewById(R.id.likes_image_view);
            timeDateView = (ConstraintLayout) v.findViewById(R.id.timeDateView);
            showDateTimeHeaderTV = (TextView) v.findViewById(R.id.showDateTimeHeaderTV);
            showDateTimeContentTV = (TextView) v.findViewById(R.id.showDateTimeContentTV);


            only_image_layout = (ConstraintLayout) v.findViewById(R.id.only_image_layout);
            post_image_view = (ImageView) v.findViewById(R.id.post_image_view);
            image_creator_image_view = (ImageView) v.findViewById(R.id.image_creator_image_view);
            shared_user_name1 = (TextView) v.findViewById(R.id.shared_user_name1);
            shared_user_image_view1 = (ImageView) v.findViewById(R.id.shared_user_image_view1);
            likes_text_view1 = (TextView) v.findViewById(R.id.likes_text_view1);
            passes_text_view1 = (TextView) v.findViewById(R.id.passes_text_view1);
            showTimeIV1 = (ImageView) v.findViewById(R.id.showTimeIV1);
            share_in_between_image_view1 = (ImageView) v.findViewById(R.id.share_in_between_IV1);
            likes_image_view1 = (ImageView) v.findViewById(R.id.likes_image_view1);
            timeDateView1 = (ConstraintLayout) v.findViewById(R.id.timeDateView1);
            showDateTimeHeaderTV1 = (TextView) v.findViewById(R.id.showDateTimeHeaderTV1);
            showDateTimeContentTV1 = (TextView) v.findViewById(R.id.showDateTimeContentTV1);


            both_views = (ConstraintLayout) v.findViewById(R.id.both_views);
            post_image_view2 = (ImageView) v.findViewById(R.id.post_image_view2);
            caption_text_view2 = (TextView) v.findViewById(R.id.caption_textview2);
            both_views_creator = (ImageView) v.findViewById(R.id.both_views_creator);
            shared_user_name2 = (TextView) v.findViewById(R.id.shared_user_name2);
            shared_user_image_view2 = (ImageView) v.findViewById(R.id.shared_user_image_view2);
            likes_text_view2 = (TextView) v.findViewById(R.id.likes_text_view2);
            passes_text_view2 = (TextView) v.findViewById(R.id.passes_text_view2);
            showTimeIV2 = (ImageView) v.findViewById(R.id.showTimeIV2);
            share_in_between_image_view2 = (ImageView) v.findViewById(R.id.share_in_between_IV2);
            likes_image_view2 = (ImageView) v.findViewById(R.id.likes_image_view2);
            timeDateView2 = (ConstraintLayout) v.findViewById(R.id.timeDateView2);
            showDateTimeHeaderTV2 = (TextView) v.findViewById(R.id.showDateTimeHeaderTV2);
            showDateTimeContentTV2 = (TextView) v.findViewById(R.id.showDateTimeContentTV2);


            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvEdit = (TextView) itemView.findViewById(R.id.tvEdit);
            tvShare = (TextView) itemView.findViewById(R.id.tvShare);


            shared_user_image_view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            if(notificationData.get(position).getType().equals(CREATED_BY)){
                                navigateToProfile(notificationData.get(position).getCreatorId());
                            }
                            else{
                                navigateToProfile(notificationData.get(position).getSharedUserId());
                            }
                        }
                    }
            );
            shared_user_image_view1.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            if(notificationData.get(position).getType().equals(CREATED_BY)){
                                navigateToProfile(notificationData.get(position).getCreatorId());
                            }
                            else{
                                navigateToProfile(notificationData.get(position).getSharedUserId());
                            }
                        }
                    }
            );
            shared_user_image_view2.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            if(notificationData.get(position).getType().equals(CREATED_BY)){
                                navigateToProfile(notificationData.get(position).getCreatorId());
                            }
                            else{
                                navigateToProfile(notificationData.get(position).getSharedUserId());
                            }
                        }
                    }
            );
            text_creator_image_view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            navigateToProfile(notificationData.get(position).getCreatorId());
                        }
                    }
            );
            image_creator_image_view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            navigateToProfile(notificationData.get(position).getCreatorId());
                        }
                    }
            );
            both_views_creator.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            navigateToProfile(notificationData.get(position).getCreatorId());
                        }
                    }
            );
            shared_user_name.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            if(notificationData.get(position).getType().equals(CREATED_BY)){
                                navigateToProfile(notificationData.get(position).getCreatorId());
                            }
                            else{
                                navigateToProfile(notificationData.get(position).getSharedUserId());
                            }
                        }
                    }
            );
            shared_user_name1.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            if(notificationData.get(position).getType().equals(CREATED_BY)){
                                navigateToProfile(notificationData.get(position).getCreatorId());
                            }
                            else{
                                navigateToProfile(notificationData.get(position).getSharedUserId());
                            }
                        }
                    }
            );
            shared_user_name2.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            if(notificationData.get(position).getType().equals(CREATED_BY)){
                                navigateToProfile(notificationData.get(position).getCreatorId());
                            }
                            else{
                                navigateToProfile(notificationData.get(position).getSharedUserId());
                            }
                        }
                    }
            );
            tvEdit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            String postType = notificationData.get(position).getType();
                            String friendId;
                            String friendName;
                            String friendProfPicURL;

                            if(postType.equals(CREATED_BY)){
                                friendId = notificationData.get(position).getCreatorId();
                                friendName = notificationData.get(position).getCreatorName();
                                friendProfPicURL = notificationData.get(position).getCreatorProfilePic();
                            }
                            else{
                                friendId = notificationData.get(position).getSharedUserId();
                                friendName = notificationData.get(position).getSharedUserName();
                                friendProfPicURL = notificationData.get(position).getProfilePicUrl();
                            }

                            Intent intent = new Intent(ctx, ChatActivity.class);
                            intent.setAction(ACTION_PASS_TO_CHAT);
                            intent.putExtra("friendId",friendId);
                            intent.putExtra("friendName", friendName);
                            intent.putExtra("friendProfPicURL", friendProfPicURL);
                            ctx.startActivity(intent);
                        }
                    }
            );
            tvShare.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            String userId = userHomeActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
                            String postId = notificationData.get(position).getPostId();
                            String caption = notificationData.get(position).getCaption();
                            String imageUri = notificationData.get(position).getImageUri();

                            SharePostBGT obj = new SharePostBGT(ctx);
                            obj.execute(userId, postId);

                            Intent intent = new Intent(ctx, ShareAlertDialog.class);
                            intent.putExtra("postId", postId);
                            intent.putExtra("userId", userId);
                            intent.putExtra("caption", caption);
                            intent.putExtra("imageUri", imageUri);
                            ctx.startActivity(intent);

                        }
                    }
            );
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}