package com.connect.infixd.mobile.Search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.connect.infixd.mobile.BackgroundTasks.GetUserDetailsBGT;
import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteContactsAdapter extends ArrayAdapter<Contact> {

    private final List<Contact> contacts;
    List<Contact> filteredContacts = new ArrayList<>();
    Context ctx;

    public AutoCompleteContactsAdapter(Context context, List<Contact> contacts) {
        super(context, 0, contacts);
        this.ctx = context;
        this.contacts = contacts;
    }

    @Override
    public int getCount() {
        return filteredContacts.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filteredContacts.clear();
                final FilterResults filteredResults = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    filteredContacts.addAll(contacts);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    // Your filtering logic goes in here
                    for (Contact contact : contacts) {
                        if(!contact.getID().equals("InfixD BotXXX-XXXXXXX")){
                            if (contact.getName().toLowerCase().contains(filterPattern)) {
                                filteredContacts.add(contact);
                            }
                        }

                    }
                }

                filteredResults.values = filteredContacts;
                filteredResults.count = filteredContacts.size();
                return filteredResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                filteredContacts = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }
        };

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item from filtered list.
        Contact contact = filteredContacts.get(position);

        // Inflate your custom row layout as usual.
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.contacts_search_row, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.csContactName);
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.csContactimageView);
        LinearLayout row = (LinearLayout) convertView.findViewById(R.id.search_contact_row);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetUserDetailsBGT getUserDetailsBGT = new GetUserDetailsBGT(ctx);
                getUserDetailsBGT.execute("userProfileFriend",contact.getID());
            }
        });

        tvName.setText(contact.getName());

        if(contact.getProfilePicUrl() != null){
            if(!contact.getProfilePicUrl().isEmpty()){
                Picasso.with(ctx)
                        .load(contact.getProfilePicUrl())
                        .into(ivIcon);
            }
        }
        return convertView;
    }
}
