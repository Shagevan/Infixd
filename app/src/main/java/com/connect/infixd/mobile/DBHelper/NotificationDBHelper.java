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

import com.connect.infixd.mobile.DBModels.AddGroupNotification;
import com.connect.infixd.mobile.DBModels.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationDBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Notificationdb";

    // Table Names
    private static final String TABLE_NOTIFICATION_DETAILS = "notificationDetails";
    private static final String TABLE_ADD_GROUP_NOTIFICATION_DETAILS = "addGroupNotificationDetails";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String TYPE = "type";
    private static final String BODY = "body";
    private static final String SENDER_ID = "senderId";
    private static final String SENDER_NAME = "senderName";
    private static final String RECEIVER_ID = "receiverId";
    private static final String RECEIVER_NAME = "receiverName";
    private static final String THIRD_PARTY_ID = "thirdPartyId";
    private static final String THIRD_PARTY_NAME = "thirdPartyName";
    private static final String STATUS = "status";
    private static final String GROUP_NAME = "groupName";
    private static final String SENDER_PROFILE_PIC_URL = "senderProfPicURL";
    private static final String RECEIVER_PROFILE_PIC_URL = "receiverProfPicURL";
    private static final String THIRD_PARTY_PROFILE_PIC_URL = "thirdPartyProfPicURL";

    // notificationDetails table create statement
    private static final String CREATE_TABLE_NOTIFICATION_DETAILS = "CREATE TABLE "
            + TABLE_NOTIFICATION_DETAILS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + TYPE + " TEXT," + BODY + " TEXT,"
            + SENDER_ID + " TEXT," + SENDER_NAME + " TEXT," +  SENDER_PROFILE_PIC_URL + " TEXT," + RECEIVER_ID + " TEXT," + RECEIVER_NAME + " TEXT,"
            +  RECEIVER_PROFILE_PIC_URL + " TEXT," + THIRD_PARTY_ID + " TEXT," + THIRD_PARTY_NAME + " TEXT," +  THIRD_PARTY_PROFILE_PIC_URL + " TEXT," + STATUS + " TEXT DEFAULT 'NO RESPONSE' );";

    // addGroupNotificationDetails table create statement
    private static final String CREATE_TABLE_ADD_GROUP_NOTIFICATION_DETAILS = "CREATE TABLE "
            + TABLE_ADD_GROUP_NOTIFICATION_DETAILS + "(" + KEY_ID + " TEXT PRIMARY KEY," + TYPE + " TEXT," + BODY + " TEXT,"
            + GROUP_NAME + " TEXT," + SENDER_ID + " TEXT," + SENDER_NAME + " TEXT," + SENDER_PROFILE_PIC_URL + " TEXT,"
            + RECEIVER_ID + " TEXT," + RECEIVER_NAME + " TEXT," + RECEIVER_PROFILE_PIC_URL + " TEXT," + STATUS + " TEXT DEFAULT 'NO RESPONSE' );";

    public NotificationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //TABLE_SUGGESTION is Created
        sqLiteDatabase.execSQL(CREATE_TABLE_NOTIFICATION_DETAILS);
        sqLiteDatabase.execSQL(CREATE_TABLE_ADD_GROUP_NOTIFICATION_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_DETAILS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ADD_GROUP_NOTIFICATION_DETAILS);
        // create new tables
        onCreate(sqLiteDatabase);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations for Notification Table
     */

    // Adding new Notification
    public void addNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TYPE, notification.getType()); // Type of the Notification
        values.put(SENDER_ID, notification.getSenderId()); // Notification Sender's ID
        values.put(SENDER_NAME, notification.getSenderName()); // Notification Sender's Name
        values.put(SENDER_PROFILE_PIC_URL, notification.getSenderProfPicUrl()); // Notification Sender's ProfPicUrl
        values.put(RECEIVER_ID, notification.getReceiverId()); // Notification Receiver's ID
        values.put(RECEIVER_NAME, notification.getReceiverName()); // Notification Receiver's Name
        values.put(RECEIVER_PROFILE_PIC_URL, notification.getReceiverProfPicUrl()); // Notification Receiver's ProfPicUrl
        values.put(THIRD_PARTY_ID, notification.getThirdPartyId()); // ID of Third Party involved in the Notification
        values.put(THIRD_PARTY_NAME, notification.getThirdPartyName()); // Name of Third Party involved in the Notification
        values.put(THIRD_PARTY_PROFILE_PIC_URL, notification.getThirdPartyProfPicUrl()); // Notification Third Party ProfPicUrl
        values.put(BODY, notification.getBody()); // ID of Third Party involved in the Notification
        // Inserting Row
        db.insert(TABLE_NOTIFICATION_DETAILS, null, values);
        db.close(); // Closing database connection
    }

    // Adding new Group Notification
    public void addGroupNotification(AddGroupNotification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, UUID.randomUUID().toString()); // Unique Random Notification ID is created
        values.put(TYPE, notification.getType()); // Type of the Notification
        values.put(GROUP_NAME, notification.getGroupName()); // groupName of the Notification
        values.put(SENDER_ID, notification.getSenderId()); // Notification Sender's ID
        values.put(SENDER_NAME, notification.getSenderName()); // Notification Sender's Name
        values.put(SENDER_PROFILE_PIC_URL, notification.getSenderProfPicURL()); // Notification Sender's profilePicURL
        values.put(RECEIVER_ID, notification.getReceiverId()); // Notification Receiver's ID
        values.put(RECEIVER_NAME, notification.getReceiverName()); // Notification Receiver's Name
        values.put(RECEIVER_PROFILE_PIC_URL, notification.getReceiverProfPicURL()); // Notification Receiver's profilePicURL
        values.put(BODY, notification.getBody()); // ID of Third Party involved in the Notification
        // Inserting Row
        db.insert(TABLE_ADD_GROUP_NOTIFICATION_DETAILS, null, values);
        db.close(); // Closing database connection
    }

    // Adding List of Notifications
    public void addNotifications(List<Notification> notifications) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(int i=0;i<notifications.size();i++){
            values.put(TYPE, notifications.get(i).getType()); // Type of the Notification
            values.put(SENDER_ID, notifications.get(i).getSenderId()); // Notification Sender's ID
            values.put(SENDER_NAME, notifications.get(i).getSenderName()); // Notification Sender's Name
            values.put(SENDER_PROFILE_PIC_URL, notifications.get(i).getSenderProfPicUrl()); // Notification Sender's ProfPicUrl
            values.put(RECEIVER_ID, notifications.get(i).getReceiverId()); // Notification Receiver's ID
            values.put(RECEIVER_NAME, notifications.get(i).getReceiverName()); // Notification Receiver's Name
            values.put(RECEIVER_PROFILE_PIC_URL, notifications.get(i).getReceiverProfPicUrl()); // Notification Receiver's ProfPicUrl
            values.put(THIRD_PARTY_ID, notifications.get(i).getThirdPartyId()); // ID of Third Party involved in the Notification
            values.put(THIRD_PARTY_NAME, notifications.get(i).getThirdPartyName()); // Name of Third Party involved in the Notification
            values.put(THIRD_PARTY_PROFILE_PIC_URL, notifications.get(i).getThirdPartyProfPicUrl()); // Notification Third Party ProfPicUrl
            values.put(BODY, notifications.get(i).getBody()); // ID of Third Party involved in the Notification
        }
        // Inserting Row
        db.insert(TABLE_NOTIFICATION_DETAILS, null, values);
        db.close(); // Closing database connection
    }

    // Adding List of Group Notifications
    public void addGroupNotifications(List<AddGroupNotification> notifications) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(int i=0;i<notifications.size();i++){
            values.put(KEY_ID, UUID.randomUUID().toString()); // Unique Random Notification ID is created
            values.put(TYPE, notifications.get(i).getType()); // Type of the Notification
            values.put(GROUP_NAME, notifications.get(i).getGroupName()); // groupName of the Notification
            values.put(SENDER_ID, notifications.get(i).getSenderId()); // Notification Sender's ID
            values.put(SENDER_NAME, notifications.get(i).getSenderName()); // Notification Sender's Name
            values.put(SENDER_PROFILE_PIC_URL, notifications.get(i).getSenderProfPicURL()); // Notification Sender's pic
            values.put(RECEIVER_ID, notifications.get(i).getReceiverId()); // Notification Receiver's ID
            values.put(RECEIVER_NAME, notifications.get(i).getReceiverName()); // Notification Receiver's Name
            values.put(RECEIVER_PROFILE_PIC_URL, notifications.get(i).getReceiverProfPicURL()); // Notification Receiver's pic
            values.put(BODY, notifications.get(i).getBody()); // ID of Third Party involved in the Notification
        }
        // Inserting Row
        db.insert(TABLE_ADD_GROUP_NOTIFICATION_DETAILS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Notification
    public Notification getNotification(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTIFICATION_DETAILS, new String[] { KEY_ID, TYPE, BODY, SENDER_ID, SENDER_NAME,
                RECEIVER_ID, RECEIVER_NAME, THIRD_PARTY_ID, THIRD_PARTY_NAME, STATUS }, KEY_ID + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Notification notification = new Notification();
        notification.set_id(cursor.getInt(0));
        notification.setType(cursor.getString(1));
        notification.setBody(cursor.getString(2));
        notification.setSenderId(cursor.getString(3));
        notification.setSenderName(cursor.getString(4));
        notification.setSenderProfPicUrl(cursor.getString(5));
        notification.setReceiverId(cursor.getString(6));
        notification.setReceiverName(cursor.getString(7));
        notification.setReceiverProfPicUrl(cursor.getString(8));
        notification.setThirdPartyId(cursor.getString(9));
        notification.setThirdPartyName(cursor.getString(10));
        notification.setThirdPartyProfPicUrl(cursor.getString(11));
        notification.setStatus(cursor.getString(12));

        return notification;
    }

    // Getting All Notifications
    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<Notification>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATION_DETAILS + " WHERE " + STATUS + " = 'NO RESPONSE'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.set_id(cursor.getInt(0));
                notification.setType(cursor.getString(1));
                notification.setBody(cursor.getString(2));
                notification.setSenderId(cursor.getString(3));
                notification.setSenderName(cursor.getString(4));
                notification.setSenderProfPicUrl(cursor.getString(5));
                notification.setReceiverId(cursor.getString(6));
                notification.setReceiverName(cursor.getString(7));
                notification.setReceiverProfPicUrl(cursor.getString(8));
                notification.setThirdPartyId(cursor.getString(9));
                notification.setThirdPartyName(cursor.getString(10));
                notification.setThirdPartyProfPicUrl(cursor.getString(11));
                notification.setStatus(cursor.getString(12));
                // Adding Notification to list
                notifications.add(notification);
            } while (cursor.moveToNext());
        }
        db.close();
        return notifications;
    }

    // Getting All Group Notifications
    public List<AddGroupNotification> getAllGroupNotifications() {
        List<AddGroupNotification> notifications = new ArrayList<AddGroupNotification>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ADD_GROUP_NOTIFICATION_DETAILS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AddGroupNotification notification = new AddGroupNotification();
                notification.set_id(cursor.getString(0));
                notification.setType(cursor.getString(1));
                notification.setBody(cursor.getString(2));
                notification.setGroupName(cursor.getString(3));
                notification.setSenderId(cursor.getString(4));
                notification.setSenderName(cursor.getString(5));
                notification.setSenderProfPicURL(cursor.getString(6));
                notification.setReceiverId(cursor.getString(7));
                notification.setReceiverName(cursor.getString(8));
                notification.setReceiverProfPicURL(cursor.getString(9));
                notification.setStatus(cursor.getString(10));
                // Adding Notification to list
                notifications.add(notification);
            } while (cursor.moveToNext());
        }
        db.close();
        return notifications;
    }

    // Updating Notification Status
    public void updateNotificationStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS, status);
        // updating row
        db.update(TABLE_NOTIFICATION_DETAILS, values, KEY_ID + " = " + id, null);
    }

    // Updating Add Group Notification Status
    public int updateAddGroupNotificationStatus(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATUS, status);
        // updating row
        return db.update(TABLE_ADD_GROUP_NOTIFICATION_DETAILS, values, KEY_ID + " = ?",
                new String[] { id });
    }

    // Deleting single Notification
    public void deleteNotification(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATION_DETAILS, KEY_ID + " = ?",
                new String[] { id });
        db.close();
    }

    // Deleting single Add Group Notification
    public void deleteAddGroupNotification(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADD_GROUP_NOTIFICATION_DETAILS, KEY_ID + " = ?",
                new String[] { id });
        db.close();
    }

    // Getting Notifications Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NOTIFICATION_DETAILS;
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
