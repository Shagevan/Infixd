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

import com.connect.infixd.mobile.DBModels.FriendshipMeterRow;

import java.util.ArrayList;
import java.util.List;

public class FriendshipMeterDBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "friendshipMeterdb";

    // Table Names
    private static final String TABLE_FRIENDSHIP_METER_INFO = "friendshipMeterInfo";

    // Common column names
    private static final String FRIEND_ID = "friendId";
    private static final String MESSAGE_COUNT = "messageCount";

    // friendshipMeterInfo table create statement
    private static final String CREATE_TABLE_FRIENDSHIP_METER_INFO = "CREATE TABLE "
            + TABLE_FRIENDSHIP_METER_INFO + "(" + FRIEND_ID + " TEXT PRIMARY KEY," + MESSAGE_COUNT + " INTEGER DEFAULT 0 );";


    public FriendshipMeterDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //TABLE_FRIENDSHIP_METER_INFO is Created
        sqLiteDatabase.execSQL(CREATE_TABLE_FRIENDSHIP_METER_INFO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDSHIP_METER_INFO);

        // create new tables
        onCreate(sqLiteDatabase);

    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new FriendshipMeterRow
    public void addNewFMRow(FriendshipMeterRow friendshipMeterRow) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FRIEND_ID, friendshipMeterRow.getFriendId());
        values.put(MESSAGE_COUNT, friendshipMeterRow.getMessageCount());

        // Inserting Row
        db.insert(TABLE_FRIENDSHIP_METER_INFO, null, values);
        db.close(); // Closing database connection
    }

    // Adding List of FriendshipMeterRow
    public void addFMRows(List<FriendshipMeterRow> friendshipMeterRows) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        for(int i=0;i<friendshipMeterRows.size();i++){
            values.put(FRIEND_ID, friendshipMeterRows.get(i).getFriendId());
            values.put(MESSAGE_COUNT, friendshipMeterRows.get(i).getMessageCount());
        }
        // Inserting Row
        db.insert(TABLE_FRIENDSHIP_METER_INFO, null, values);
        db.close(); // Closing database connection
    }

    // Getting single FriendshipMeterRow
    public FriendshipMeterRow getFMRow(String friendId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FRIENDSHIP_METER_INFO, new String[] { FRIEND_ID,
                        MESSAGE_COUNT}, FRIEND_ID + "=?",
                new String[] { friendId }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        FriendshipMeterRow friendshipMeterRow = new FriendshipMeterRow(cursor.getString(0), cursor.getInt(1));

        return friendshipMeterRow;
    }

    // Getting All FriendshipMeterRows
    public List<FriendshipMeterRow> getAllFMRows() {
        List<FriendshipMeterRow> friendshipMeterRows = new ArrayList<FriendshipMeterRow>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FRIENDSHIP_METER_INFO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FriendshipMeterRow friendshipMeterRow = new FriendshipMeterRow();
                friendshipMeterRow.setFriendId(cursor.getString(0));
                friendshipMeterRow.setMessageCount(cursor.getInt(1));
                friendshipMeterRows.add(friendshipMeterRow);
            } while (cursor.moveToNext());
        }
        db.close();
        return friendshipMeterRows;
    }

    //update Message Count
    public void updateMessageCount(String friendId){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FRIENDSHIP_METER_INFO, new String[] { FRIEND_ID,
                        MESSAGE_COUNT}, FRIEND_ID + "=?",
                new String[] { friendId }, null, null, null, null);

        if(!cursor.moveToFirst()){
            ContentValues values = new ContentValues();
            values.put(FRIEND_ID, friendId);
            values.put(MESSAGE_COUNT, 1);
            // Inserting Row
            db.insert(TABLE_FRIENDSHIP_METER_INFO, null, values);
            db.close();
        }

        else{
            cursor.moveToFirst();
            FriendshipMeterRow friendshipMeterRow = new FriendshipMeterRow(cursor.getString(0), cursor.getInt(1));
            int initialCount = friendshipMeterRow.getMessageCount();
            int updatedCount = initialCount + 1;

            ContentValues values = new ContentValues();
            values.put(FRIEND_ID, friendshipMeterRow.getFriendId());
            values.put(MESSAGE_COUNT,updatedCount);
            // updating row
            db.update(TABLE_FRIENDSHIP_METER_INFO, values, FRIEND_ID + " = ?",
                    new String[] { friendshipMeterRow.getFriendId() });
            db.close();
        }

    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
