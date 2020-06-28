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

import com.connect.infixd.mobile.DBModels.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Postdb";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NEW_POST_DETAILS = "newPostDetails";

    private static final String KEY_ID = "id";
    private static final String TYPE = "type";
    private static final String BODY = "body";
    private static final String NAME_OF_CREATOR = "nameOfCreator";
    private static final String ID_OF_CREATOR = "idOfCreator";
    private static final String SHARED_USER_NAME = "sharedUserName";
    private static final String SHARED_USER_ID = "sharedUserId";
    private static final String CAPTION = "caption";
    private static final String IMAGE_URI = "imageUri";
    private static final String TEXT_BACKGROUND_COLOR = "textBackgroundColor";
    private static final String PROFILE_PICTURE_URL = "profilePicUrl";
    private static final String CREATOR_PROFILE_PICTURE = "creatorProfilePicture";
    private static final String POST_ID = "postId";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String NO_OF_LIKES = "noOfLikes";
    private static final String NO_OF_SHARES = "noOfShares";
    private static final String STATUS = "status";

    // notificationDetails table create statement
    private static final String CREATE_TABLE_POST_DETAILS = "CREATE TABLE "
            + TABLE_NEW_POST_DETAILS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + TYPE + " TEXT," + BODY + " TEXT,"
            + NAME_OF_CREATOR + " TEXT," + ID_OF_CREATOR + " TEXT," + SHARED_USER_NAME + " TEXT," + SHARED_USER_ID + " TEXT,"
            + CAPTION + " TEXT," + IMAGE_URI + " TEXT," + TEXT_BACKGROUND_COLOR + " TEXT," + PROFILE_PICTURE_URL + " TEXT,"
            + CREATOR_PROFILE_PICTURE + " TEXT," + POST_ID + " TEXT,"+ TIME + " TEXT,"+ DATE + " TEXT," + NO_OF_LIKES + " TEXT,"
            + NO_OF_SHARES + " TEXT," + STATUS + " TEXT DEFAULT 'NO RESPONSE' );";

    public PostDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_POST_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NEW_POST_DETAILS);
        onCreate(sqLiteDatabase);
    }

    // Adding new Notification
    public void addPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(KEY_ID, UUID.randomUUID().toString());
        values.put(TYPE, post.getType());
        values.put(NAME_OF_CREATOR, post.getNameOfCreator());
        values.put(ID_OF_CREATOR, post.getIdOfCreator());
        values.put(SHARED_USER_NAME, post.getSharedUserName());
        values.put(SHARED_USER_ID, post.getSharedUserId());
        values.put(CAPTION, post.getCaption());
        values.put(IMAGE_URI, post.getImageUri());
        values.put(TEXT_BACKGROUND_COLOR, post.getTextBackgroundColor());
        values.put(PROFILE_PICTURE_URL, post.getProfilePicUrl());
        values.put(CREATOR_PROFILE_PICTURE, post.getCreatorProfilePic());
        values.put(POST_ID, post.getPostId());
        values.put(TIME, post.getTime());
        values.put(DATE, post.getDate());
        values.put(NO_OF_LIKES, post.getNoOfLikes());
        values.put(NO_OF_SHARES, post.getNoOfShares());
        values.put(BODY, post.getBody());
        // Inserting Row
        db.insert(TABLE_NEW_POST_DETAILS, null, values);
        db.close();
    }

    // Getting All Post Notifications
    public List<Post> getAllNotifications() {
        List<Post> postNotifications = new ArrayList<Post>();
        String selectQuery = "SELECT * FROM " + TABLE_NEW_POST_DETAILS +" ORDER BY " + KEY_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Post post = new Post();
                post.set_id(cursor.getInt(0));
                post.setType(cursor.getString(1));
                post.setBody(cursor.getString(2));
                post.setNameOfCreator(cursor.getString(3));
                post.setIdOfCreator(cursor.getString(4));
                post.setSharedUserName(cursor.getString(5));
                post.setSharedUserId(cursor.getString(6));
                post.setCaption(cursor.getString(7));
                post.setImageUri(cursor.getString(8));
                post.setTextBackgroundColor(cursor.getString(9));
                post.setProfilePicUrl(cursor.getString(10));
                post.setCreatorProfilePic(cursor.getString(11));
                post.setPostId(cursor.getString(12));
                post.setTime(cursor.getString(13));
                post.setDate(cursor.getString(14));
                post.setNoOfLikes(cursor.getString(15));
                post.setNoOfShares(cursor.getString(16));
                post.setStatus(cursor.getString(17));
                // Adding Notification to list
                postNotifications.add(post);
            } while (cursor.moveToNext());
        }
        db.close();
        return postNotifications;
    }

    // Updating Post Notification Status
    public int updatePostStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATUS, status);
        // updating row
        return db.update(TABLE_NEW_POST_DETAILS, values, KEY_ID + " = " + id, null);
    }
}
