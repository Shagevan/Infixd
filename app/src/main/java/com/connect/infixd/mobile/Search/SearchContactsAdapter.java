/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchContactsAdapter extends RecyclerView.Adapter<SearchContactsAdapter.ViewHolder> {
    private List<Contact> contacts;
    private List<Contact> filteredResults;
    private Context ctx;
    private Activity activity;

    public SearchContactsAdapter(Context context, List<Contact> contacts) {
        this.contacts = contacts;
        this.ctx = context;
        this.filteredResults = new ArrayList<Contact>();
        this.filteredResults.addAll(this.contacts);
        activity = (Activity) ctx;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userName;
        public ImageView profilePicIV;
        public LinearLayout contactRow;

        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.csContactName);
            profilePicIV = (ImageView) v.findViewById(R.id.csContactimageView);
            contactRow = (LinearLayout) v.findViewById(R.id.search_contact_row);
        }
    }

    @Override
    public SearchContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_search_row, parent, false);
        SearchContactsAdapter.ViewHolder vh = new SearchContactsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SearchContactsAdapter.ViewHolder holder, int position) {
        if(filteredResults.get(position).getProfilePicUrl() != null){
            if(!filteredResults.get(position).getProfilePicUrl().isEmpty()){
                Picasso.with(ctx)
                        .load(filteredResults.get(position).getProfilePicUrl())
                        .into(holder.profilePicIV);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //nougat or post nougat
            holder.userName.setText(Html.fromHtml(filteredResults.get(position).getName(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.userName.setText(Html.fromHtml(filteredResults.get(position).getName()));
        }

        holder.contactRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("friendDetail", filteredResults.get(position));
                activity.setResult(Activity.RESULT_OK,returnIntent);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredResults.size();
    }

    public void filterSearchResults(String query){
        // DO SEARCH ON SEPARATE THREAD
        new Thread(new Runnable() {
            @Override
            public void run() {

                filteredResults.clear();

                if(TextUtils.isEmpty(query)){
                    filteredResults.addAll(contacts);
                }
                else{
                    for (Contact row: contacts) {
                        if(row.getName().toLowerCase().contains(query.toLowerCase())){
                            filteredResults.add(row);
                        }
                    }
                }

                // Set on UI Thread
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }

}

