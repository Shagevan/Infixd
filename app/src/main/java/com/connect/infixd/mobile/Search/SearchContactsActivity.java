/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Search;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.DBHelper.SearchSuggestionDBHelper;
import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.R;

import java.util.List;

public class SearchContactsActivity extends InfixdBaseActivity {

    private AutoCompleteTextView textView;
    private ImageView backbtn;
    private ProgressBar searchProgressBar;
    private String userName;
    private List<Contact> contacts;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SearchContactsAdapter adapterSuggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        backbtn = (ImageView) findViewById(R.id.searchBackbtn);
        searchProgressBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        searchProgressBar.setVisibility(View.GONE);
        textView = (AutoCompleteTextView) findViewById(R.id.searchAutoComptv);

        userName = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        // GET ALL CONTACTS FROM SQLITE DB
        SearchSuggestionDBHelper db = new SearchSuggestionDBHelper(getBaseContext());
        contacts = db.getAllContacts();

        // SET RECYCLER VIEWER
        recyclerView = (RecyclerView) findViewById(R.id.searchRecyclerViewer);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        adapterSuggest = new SearchContactsAdapter(SearchContactsActivity.this,contacts);
        recyclerView.setAdapter(adapterSuggest);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapterSuggest.filterSearchResults(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

}
