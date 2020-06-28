package com.connect.infixd.mobile.UserHome;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.hbb20.CountryCodePicker;

public class AddUserAlertDialog extends InfixdBaseActivity {

    private CountryCodePicker ccp;
    private EditText phone_no_edit_text;
    private Button addBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_user_alert_dialog);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        phone_no_edit_text = (EditText) findViewById(R.id.phone_no_edit_text);
        addBtn = (Button) findViewById(R.id.add_button);
        cancelBtn = (Button) findViewById(R.id.cancel_button);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeypad();
                String countryCode = ccp.getSelectedCountryCodeWithPlus();
                String mobileNumber = phone_no_edit_text.getText().toString();
                String mobileId = countryCode + mobileNumber;

                if(mobileNumber.length() != 9 || !mobileNumber.startsWith("7")){
                    phone_no_edit_text.setError("Please check your mobile no again");
                }
                else{
                    String userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);
                    Intent drIntent = new Intent(AddUserAlertDialog.this, FriendIntentService.class);
                    drIntent.setAction(FriendIntentService.ACTION_SEND_DIRECT_REQUEST_BY_NUMBER);
                    drIntent.putExtra(FriendIntentService.DATA_SENDER_ID, userId);
                    drIntent.putExtra(FriendIntentService.DATA_TARGET_PHONE, mobileId);
                    startService(drIntent);
                    finish();
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
    }

    public void hideKeypad(){
        try  {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }
}
