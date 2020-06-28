/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.SignupProcess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.R;
import com.hbb20.CountryCodePicker;


public class RegisterPage extends Activity {
    private EditText etfname;
    private EditText etlname;
    private EditText etmobileNumber;
    private CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        etfname = (EditText) findViewById(R.id.fnameSignup);
        etlname = (EditText) findViewById(R.id.lnameSignup);
        etmobileNumber = (EditText) findViewById(R.id.mobileNumberSignup);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
    }

    private Boolean validationSuccess(){

         /*mobile no validation*/
        if(etmobileNumber.getText().toString().length() != 9
                ||!etmobileNumber.getText().toString().startsWith("7")){
            etmobileNumber.setError("Please check your mobile no again");
            return false;
        }

         /*first name validation*/
        if (etfname.getText().toString().trim().equals("")){
            etfname.setError("First name is required!");
            return false;
        }

         /*last name validation*/
        if (etlname.getText().toString().trim().equals("")) {
            etlname.setError("Last name is required!");
            return false;
        }

        return true;
    }

    public void onSignUpPressed(View view) {
        String fName = etfname.getText().toString();
        String lName = etlname.getText().toString();
        String countryCode = ccp.getSelectedCountryCodeWithPlus();
        String mobileNumber = countryCode + etmobileNumber.getText().toString();



        if(validationSuccess()) {
            String msg=getResources().getString(R.string.reg_popup1)+'\n'+'\n'+mobileNumber+'\n'+'\n'+getResources().getString(R.string.reg_popup2);
            String msgClickable = "Privacy Policy";

            SpannableString msgSpannable = new SpannableString(msg);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    String url = "http://www.infixd.com/privacy-policy";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            };

            msgSpannable.setSpan(clickableSpan, msg.indexOf(msgClickable),
                    msg.indexOf(msgClickable) + msgClickable.length(),
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage(msgSpannable);

            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent activityIntent = new Intent(RegisterPage.this, SignupProcessActivity.class);

                            activityIntent.putExtra(InfixdApp.STRING_PREFERENCE_MOBILE_NUMBER, mobileNumber);
                            activityIntent.putExtra(InfixdApp.STRING_PREFERENCE_FIRST_NAME, fName);
                            activityIntent.putExtra(InfixdApp.STRING_PREFERENCE_LAST_NAME, lName);

                            startActivity(activityIntent);
                            finish();
                        }
                    });
            builder.setNegativeButton("Edit Number",
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary_color));
            dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary_color));
            TextView privacy = (TextView) dialog.findViewById(android.R.id.message);
            privacy.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
