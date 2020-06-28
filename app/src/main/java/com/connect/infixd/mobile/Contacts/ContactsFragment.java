/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Contacts;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.connect.infixd.mobile.DBHelper.SearchSuggestionDBHelper;
import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.UserHome.UserHomeActivity;

import java.util.List;

public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ConstraintLayout contactsDefault;
    private Button inviteBtn;
    private UserHomeActivity userHomeActivity;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contactView = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactsDefault = (ConstraintLayout) contactView.findViewById (R.id.contacts_default);
        recyclerView = (RecyclerView) contactView.findViewById(R.id.contacts_recycler_view);
        inviteBtn =(Button) contactView.findViewById(R.id.invite_button);
        userHomeActivity = (UserHomeActivity) getActivity();

        SearchSuggestionDBHelper db = new SearchSuggestionDBHelper(getContext());
        List<Contact> contacts = db.getAllContacts();

        if(contacts.size()>0){
            contactsDefault.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userHomeActivity.inviteFriends();
            }
        });

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ContactsAdapter(getContext(),contacts);
        recyclerView.setAdapter(adapter);


        return contactView;
    }

}
