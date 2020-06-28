/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.SignupProcess;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.connect.infixd.mobile.Application.InfixdApp;

public class SignupProcessAdapter extends FragmentPagerAdapter {

    private String firstName;
    private String lastName;
    private String mobileNumber;

    public SignupProcessAdapter(FragmentManager fm, String firstName, String lastName, String mobileNumber){
        super(fm);
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;

        if(position == 0){
            fragment = new SignupFirstStep();
            Bundle bundle = new Bundle();
            bundle.putString(InfixdApp.STRING_PREFERENCE_FIRST_NAME, firstName);
            bundle.putString(InfixdApp.STRING_PREFERENCE_LAST_NAME, lastName);
            bundle.putString(InfixdApp.STRING_PREFERENCE_MOBILE_NUMBER, mobileNumber);
            fragment.setArguments(bundle);
        }
        if(position == 1){
            fragment = new SignupSecondStep();
        }
        if(position == 2){
            fragment = new SignupThirdStep();
        }

        return fragment;
    }

    @Override
    public int getCount()
    {
        return 3;
    }

}
