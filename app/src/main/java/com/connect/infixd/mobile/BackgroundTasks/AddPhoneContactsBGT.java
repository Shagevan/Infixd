/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.BackgroundTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.connect.infixd.mobile.Interfaces.DirectToHome;
import com.infixd.client.InfixdClient;
import com.infixd.client.model.AddContactResponse;

import java.util.ArrayList;

public class AddPhoneContactsBGT extends AsyncTask<String, String, ArrayList<String>> {

    private Context ctx;
    private Activity activity;
    private String uname;
    private ArrayList<String> contactNumbers = new ArrayList<String>();
    private ProgressDialog progressDialog;
    private DirectToHome directToHome;

    public AddPhoneContactsBGT(Context ctx) {
        this.ctx = ctx;
        activity = (Activity) ctx;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("Imporing Contacts...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        uname = strings[0];
        int count = 0;
        Cursor cursor = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null, null);
        if(cursor != null && cursor.getCount()>0) {
            while(cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                String pno = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if( Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER))) > 1){
                    Cursor phoneNumbers = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.NUMBER +" = ?",new String[]{ id }, null);
                    while(phoneNumbers.moveToNext()){
                        String phone = phoneNumbers.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactNumbers.add(phone);
                    }
                    phoneNumbers.close();
                }
                contactNumbers.add(pno);
                count++;
            }
            cursor.close();
        }

        InfixdClient infixdClient = new InfixdClient();
        AddContactResponse addContactResponse = infixdClient.addContacts(uname,contactNumbers);
        ArrayList<String> addedContacts = (ArrayList<String>) addContactResponse.getAddedContacts();
        return addedContacts;
    }

    @Override
    protected void onPostExecute(ArrayList<String> addedContacts) {
        progressDialog.dismiss();
        directToHome = (DirectToHome) ctx;
        directToHome.directToHome(true);
    }

}
