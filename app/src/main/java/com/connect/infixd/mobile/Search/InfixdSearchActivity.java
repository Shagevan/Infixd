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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.BackgroundTasks.GetSuggestionBGT;
import com.connect.infixd.mobile.DBHelper.SearchSuggestionDBHelper;
import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.R;
import com.infixd.client.model.Suggestions;

import java.util.ArrayList;
import java.util.List;

public class InfixdSearchActivity extends InfixdBaseActivity {

    private AutoCompleteTextView textView;
    private ImageView backbtn;
    private ProgressBar searchProgressBar;
    private String userName;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private InfixdSearchAdapter adapterSuggest;
    private List<Suggestions> suggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        userName = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
        adapterSuggest = new InfixdSearchAdapter(InfixdSearchActivity.this,suggestions,userName);
        backbtn = (ImageView) findViewById(R.id.searchBackbtn);
        searchProgressBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        textView = (AutoCompleteTextView) findViewById(R.id.searchAutoComptv);
        recyclerView = (RecyclerView) findViewById(R.id.searchRecyclerViewer);
        searchProgressBar.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterSuggest);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SearchSuggestionDBHelper db = new SearchSuggestionDBHelper(getBaseContext());
        List<Contact> contacts = db.getAllContacts();

        AutoCompleteContactsAdapter adapter = new AutoCompleteContactsAdapter(InfixdSearchActivity.this,contacts);
        textView.setAdapter(adapter);
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                textView.dismissDropDown();
            }
        });

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length()>= 3){
                    textView.dismissDropDown();
                    searchProgressBar.setVisibility(View.VISIBLE);
                    searchProgressBar.animate();
                    searchProgressBar.setEnabled(true);
                    new GetSuggestionBGT(InfixdSearchActivity.this){
                        @Override
                        protected void onPostExecute(List<Suggestions> resultVal) {
                            super.onPostExecute(resultVal);
                            suggestions.clear();
                            suggestions.addAll(resultVal);
                            adapterSuggest.notifyDataSetChanged();
                            searchProgressBar.setEnabled(false);
                            searchProgressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }.execute(userName,charSequence.toString(),"5");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}
