/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.connect.infixd.mobile.R;

public class UserProfileEditDialog extends Activity {
    private EditText edit_data_edittext;
    private Button save_data_button;
    private Button cancel_edit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit_dialog);

        String value = getIntent().getStringExtra("value");
        edit_data_edittext = (EditText)findViewById(R.id.edit_data_edittext);
        save_data_button = (Button)findViewById(R.id.save_data_button);
        cancel_edit_button = (Button)findViewById(R.id.cancel_edit_button);

        if(value.equals(getString(R.string.my_profile_about_me))
                || value.equals(getString(R.string.my_profile_education))
                || value.equals(getString(R.string.my_profile_location))
                || value.equals(getString(R.string.my_profile_profession)) ){

            edit_data_edittext.setHint(value);
        }
        else{
            edit_data_edittext.setText(value);
        }

        save_data_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String changedData = edit_data_edittext.getText().toString();
                        Intent intent = new Intent();
                        intent.putExtra("result",changedData);
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    }
                }
        );
        cancel_edit_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );

    }
}
