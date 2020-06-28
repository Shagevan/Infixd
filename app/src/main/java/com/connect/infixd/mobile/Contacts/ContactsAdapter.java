/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.BackgroundTasks.GetUserDetailsBGT;
import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{

    private List<Contact> contacts = new ArrayList<>();
    private Context ctx;

    public ContactsAdapter(Context context, List<Contact> contacts) {
        this.ctx = context;
        this.contacts = contacts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userName;
        public ImageView contactimageView;
        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.contactUserName);
            contactimageView = (ImageView) v.findViewById(R.id.contactimageView);
            View contactsRow = v.findViewById(R.id.contactsrowID);
            contactsRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    GetUserDetailsBGT getUserDetailsBGT = new GetUserDetailsBGT(ctx);
                    getUserDetailsBGT.execute("userProfileFriend",contacts.get(position).getID());
                }
            });
        }
    }


    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.userName.setText(contacts.get(position).getName());
        if(contacts.get(position).getProfilePicUrl() != null){
            if(!contacts.get(position).getProfilePicUrl().isEmpty() ){
                Picasso.with(ctx)
                        .load(contacts.get(position).getProfilePicUrl())
                        .into(holder.contactimageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
