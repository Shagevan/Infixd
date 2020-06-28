package com.connect.infixd.mobile.Introduction;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.BackgroundTasks.GetUserDetailsBGT;
import com.connect.infixd.mobile.R;
import com.infixd.client.model.ShortestPathNodeResponse;
import com.infixd.client.model.ShortestPathResponse;
import com.squareup.picasso.Picasso;

public class IntroductionPathRVAdapter extends RecyclerView.Adapter<IntroductionPathRVAdapter.ViewHolder> {

    private ShortestPathResponse shortestPath;
    private Context ctx;

    public IntroductionPathRVAdapter(Context context, ShortestPathResponse shortestPath) {
        this.shortestPath = shortestPath;
        this.ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fullName;
        public ImageView profilePicIV;

        public ViewHolder(View v) {
            super(v);
            fullName = (TextView) v.findViewById(R.id.introductionPathRowNameTV);
            profilePicIV = (ImageView) v.findViewById(R.id.introductionPathRowUserIV);
            GetUserDetailsBGT getUserDetailsBGT = new GetUserDetailsBGT(ctx);

            View introPathRow = v.findViewById(R.id.intro_path_row);
            introPathRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position == 0){
                        getUserDetailsBGT.execute("userProfile", shortestPath.getStartUserId());
                    }else if(position == 1){
                        getUserDetailsBGT.execute("userProfileFriend", shortestPath.getShortestPathNodes().get(1).getUserId());
                    }
                    else{
                        getUserDetailsBGT.execute("userProfileNotFriend", shortestPath.getShortestPathNodes().get(position).getUserId());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(shortestPath == null) {
            return 0;
        }
        return shortestPath.getShortestPathNodes().size();
    }

    @Override
    public IntroductionPathRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.introduction_path_row, parent, false);
        IntroductionPathRVAdapter.ViewHolder vh = new IntroductionPathRVAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(IntroductionPathRVAdapter.ViewHolder holder, int position) {
        ShortestPathNodeResponse user = shortestPath.getShortestPathNodes().get(position);
        final String userName = user.getName().replaceAll("\"", "");
        final String profilePicURL = user.getProfPicUrl().replaceAll("\"", "");
        holder.fullName.setText(userName);
        if(profilePicURL != null && !profilePicURL.isEmpty()){
            Picasso.with(ctx)
                    .load(profilePicURL)
                    .into(holder.profilePicIV);
        }
    }

}
