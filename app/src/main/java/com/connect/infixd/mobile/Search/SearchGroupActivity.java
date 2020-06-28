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
import com.connect.infixd.mobile.BackgroundTasks.GetGroupSuggestionBGT;
import com.connect.infixd.mobile.R;
import com.infixd.client.model.Suggestions;

import java.util.ArrayList;
import java.util.List;

public class SearchGroupActivity extends InfixdBaseActivity {

    private AutoCompleteTextView textView;
    private ImageView backbtn;
    private ProgressBar searchProgressBar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SearchGroupAdapter adapterSuggest;
    private List<Suggestions> suggestions = new ArrayList<>();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);

        userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        backbtn = (ImageView) findViewById(R.id.searchBackbtn);
        searchProgressBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        textView = (AutoCompleteTextView) findViewById(R.id.searchAutoComptv);
        recyclerView = (RecyclerView) findViewById(R.id.searchGroupRecyclerViewer);
        searchProgressBar.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        adapterSuggest = new SearchGroupAdapter(SearchGroupActivity.this,userId,suggestions);
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
                if(charSequence.length()>= 3){
                    searchProgressBar.setVisibility(View.VISIBLE);
                    searchProgressBar.animate();
                    searchProgressBar.setEnabled(true);

                    new GetGroupSuggestionBGT(SearchGroupActivity.this){
                        @Override
                        protected void onPostExecute(List<Suggestions> results) {
                            super.onPostExecute(results);
                            suggestions.clear();
                            suggestions.addAll(results);
                            adapterSuggest.notifyDataSetChanged();
                            searchProgressBar.setEnabled(false);
                            searchProgressBar.setVisibility(View.GONE);
                        }
                    }.execute(charSequence.toString(),"20");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
