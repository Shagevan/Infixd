package com.connect.infixd.mobile.User;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.connect.infixd.mobile.Functions.ImageViewerActivity;
import com.connect.infixd.mobile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FBPhotosAdapter extends RecyclerView.Adapter<FBPhotosAdapter.FbPhotosViewHolder> {
    private ArrayList<String> fbPhotosURL;
    private Context context;
    public FBPhotosAdapter(Context context, ArrayList fbPhotosURL) {
        this.context = context;
        this.fbPhotosURL = fbPhotosURL;
    }

    public class FbPhotosViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public FbPhotosViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.fb_image_view);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra("PICTURE_URL", fbPhotosURL.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public FBPhotosAdapter.FbPhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fb_photo_row, parent, false);
        FbPhotosViewHolder vh = new FbPhotosViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FBPhotosAdapter.FbPhotosViewHolder holder, int position) {
        String photoURL = fbPhotosURL.get(position);
        if(photoURL != null && !photoURL.isEmpty()){
            Picasso.with(context)
                    .load(photoURL)
                    .resize(350,350)
                    .centerCrop()
                    .into(holder.image);
        }

    }

    @Override
    public int getItemCount() {
        return fbPhotosURL.size();
    }

}