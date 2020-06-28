/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Search;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.connect.infixd.mobile.BackgroundTasks.CheckFriendRelationBGT;
import com.connect.infixd.mobile.R;
import com.infixd.client.model.Suggestions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class InfixdSearchAdapter extends RecyclerView.Adapter<InfixdSearchAdapter.ViewHolder> {

    private Context ctx;
    private String username;
    private List<Suggestions> suggestions = new ArrayList<>();

    public InfixdSearchAdapter(Context context, List<Suggestions> suggestions, String username) {
        this.suggestions = suggestions;
        this.username =username;
        this.ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userName;
        public LinearLayout contactRow;
        public ImageView contactIV;

        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.csContactName);
            contactRow = (LinearLayout) v.findViewById(R.id.search_contact_row);
            contactIV = (ImageView) v.findViewById(R.id.csContactimageView);
            contactRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String friendId = suggestions.get(position).getUser_id();

                    CheckFriendRelationBGT checkFriendRelationBGT = new CheckFriendRelationBGT(ctx);
                    checkFriendRelationBGT.execute(username,friendId);
                }
            });
        }
    }


    @Override
    public InfixdSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_search_row, parent, false);
        InfixdSearchAdapter.ViewHolder vh = new InfixdSearchAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(InfixdSearchAdapter.ViewHolder holder, int position) {
        String userName = suggestions.get(position).getName_suggestion();
        String profilePicURL = suggestions.get(position).getProfilePicURL();

        if (profilePicURL != null && !profilePicURL.isEmpty()) {
            Picasso.with(ctx)
                    .load(profilePicURL)
                    .into(holder.contactIV);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //post nougat
            holder.userName.setText(Html.fromHtml(userName, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.userName.setText(Html.fromHtml(userName));
        }
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }
}
