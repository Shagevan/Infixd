/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.connect.infixd.mobile.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.DBModels.Contact;
import com.connect.infixd.mobile.DBModels.Group;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionDBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "searchSuggestiondb";

    // Table Names
    private static final String TABLE_SUGGESTION = "userContactsInfo";
    private static final String TABLE_GROUP_SUGGESTION = "userGroupsInfo";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NUMBER = "mobileNumber";
    private static final String GROUP_NAME = "groupName";
    private static final String GROUP_PHOTO_URL = "groupPhotoUrl";
    private static final String NO_OF_MEMBERS = "noOfMembers";
    private static final String POSITION = "position";
    private static final String PROF_PIC_URL = "profilePictureUrl";

    // userContactsInfo table create statement
    private static final String CREATE_TABLE_SUGGESTION = "CREATE TABLE "
            + TABLE_SUGGESTION + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_NUMBER + " INTEGER," + PROF_PIC_URL + " TEXT " +
            "" +
            ");";

    private static final String CREATE_TABLE_GROUPS = "CREATE TABLE "
            + TABLE_GROUP_SUGGESTION + "(" + GROUP_NAME + " TEXT PRIMARY KEY," + GROUP_PHOTO_URL + " TEXT,"
            + NO_OF_MEMBERS + " TEXT," + POSITION + " TEXT );";

    public SearchSuggestionDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //TABLE_SUGGESTION is Created
        sqLiteDatabase.execSQL(CREATE_TABLE_SUGGESTION);
        sqLiteDatabase.execSQL(CREATE_TABLE_GROUPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SUGGESTION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_SUGGESTION);

        // create new tables
        onCreate(sqLiteDatabase);

    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, contact.getID());
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_NUMBER, contact.getPhoneNumber()); // Contact Phone
        values.put(PROF_PIC_URL, contact.getProfilePicUrl()); // profile picture url

        // Inserting Row
        db.insert(TABLE_SUGGESTION, null, values);
        db.close(); // Closing database connection
    }

    // Adding a new group
    public void addGroup(Group group) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GROUP_NAME, group.getGroup_name());
        values.put(GROUP_PHOTO_URL, group.getGroup_photo_url());
        values.put(NO_OF_MEMBERS, group.getNoOfMembers());
        values.put(POSITION, group.getUserPosition());

        // Inserting Row
        db.insert(TABLE_GROUP_SUGGESTION, null, values);
        db.close(); // Closing database connection
    }

    // Adding List of contacts
    public void addContacts(List<Contact> contacts) {
        deleteAllContacts();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(int i=0;i<contacts.size();i++){
            values.put(KEY_ID, contacts.get(i).getID()); // Contact userId
            values.put(KEY_NAME, contacts.get(i).getName()); // Contact Name
            values.put(KEY_NUMBER, contacts.get(i).getPhoneNumber()); // Contact Phone
            values.put(PROF_PIC_URL, contacts.get(i).getProfilePicUrl()); // profile picture url
            db.insert(TABLE_SUGGESTION, null, values);
        }
        db.close(); // Closing database connection
    }

    // Adding List of groups
    public void addGroups (List<Group> groups) {
        deleteAllGroups();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(int i=0; i<groups.size();i++){
            values.put(GROUP_NAME, groups.get(i).getGroup_name());
            values.put(GROUP_PHOTO_URL, groups.get(i).getGroup_photo_url());
            values.put(NO_OF_MEMBERS, groups.get(i).getNoOfMembers());
            values.put(POSITION, groups.get(i).getUserPosition());
            db.insert(TABLE_GROUP_SUGGESTION, null, values);
        }
        db.close();
    }

    // Getting single contact
    public Contact getContact(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SUGGESTION, new String[] { KEY_ID,
                        KEY_NAME, KEY_NUMBER, PROF_PIC_URL }, KEY_ID + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(cursor.getString(0),
                cursor.getString(1), cursor.getString(2));
        contact.setProfilePicUrl(cursor.getString(3));
        // return contact
        return contact;
    }

    // Getting All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SUGGESTION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if(!cursor.getString(0).equals(InfixdApp.STRING_INFIXD_BOT)){
                    Contact contact = new Contact();
                    contact.setID(cursor.getString(0));
                    contact.setName(cursor.getString(1));
                    contact.setPhoneNumber(cursor.getString(2));
                    contact.setProfilePicUrl(cursor.getString(3));
                    contactList.add(contact);
                }
            } while (cursor.moveToNext());
        }
        db.close();
        return contactList;
    }

    // Getting All Groups
    public List<Group> getAllGroups () {
        List<Group> groupList = new ArrayList<Group>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_GROUP_SUGGESTION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Group group = new Group();
                group.setGroup_name(cursor.getString(0));
                group.setGroup_photo_url(cursor.getString(1));
                group.setNoOfMembers(cursor.getString(2));
                group.setUserPosition(cursor.getString(3));
                // Adding contact to list
                groupList.add(group);
            } while (cursor.moveToNext());
        }
        db.close();
        return groupList;
    }

    // Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_NUMBER, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_SUGGESTION, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUGGESTION, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }

    // Deleting all Contacts
    public void deleteAllContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_SUGGESTION);
        db.close();
    }

    // Deleting all Groups
    public void deleteAllGroups() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_GROUP_SUGGESTION);
        db.close();
    }


    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SUGGESTION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
