/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Application;

import android.app.Application;

import com.connect.infixd.mobile.Functions.FontsOverride;

public class InfixdApp extends Application {
    //This should be set to true if and only if the user has completed registration & phone verification.
    public static final String BOOLEAN_PREFERENCE_IS_FRESH_INSTALL = "BOOLEAN_PREFERENCE_IS_FRESH_INSTALL";
    public static final String STRING_PREFERENCE_LOCATION_STATE = "locationState";
    public static final String STRING_PREFERENCE_USER_ID = "userName"; //TODO: Refactor this to userId
    public static final String STRING_PREFERENCE_FIRST_NAME = "firstName";
    public static final String STRING_PREFERENCE_LAST_NAME = "lastName";
    public static final String STRING_PREFERENCE_FULL_NAME = "fullName";
    public static final String STRING_PREFERENCE_MOBILE_NUMBER = "mobileNumber";
    public static final String STRING_PREFERENCE_MOBILE_NUMBER_PREMISSION = "mobileNumberPermission";
    public static final String STRING_PREFERENCE_EMAIL = "email";
    public static final String STRING_PREFERENCE_PROFILE_PIC_URL = "profilePicURL";
    public static final String STRING_PREFERENCE_PROFILE_PIC_STORAGE_URL = "profilePicStorageURL";
    public static final String STRING_PREFERENCE_FB_LINK = "fbLink";
    public static final String STRING_PREFERENCE_GP_LINK = "gpLink";
    public static final String STRING_PREFERENCE_TWITTER_LINK = "twitterlink";
    public static final String STRING_PREFERENCE_INSTAGRAM_LINK = "instagramLink";
    public static final String STRING_PREFERENCE_EDUCATION = "education";
    public static final String STRING_PREFERENCE_PROFESSION = "profession";
    public static final String STRING_PREFERENCE_LOCATION = "location";
    public static final String STRING_PREFERENCE_ABOUTME = "aboutMe";
    public static final String STRING_INFIXD_BOT = "InfixD BotXXX-XXXXXXX";
    public static final String DEFAULT_PROFILE_PIC_URL = "https://firebasestorage.googleapis.com/v0/b/strapd-162605.appspot.com/o/InfixdDefaultProfilePhoto.png?alt=media&token=0271a8e4-83ae-4b7a-8ba5-4b17996c6739";
    public static final String IM_ON_INFIXD_IMAGE = "https://firebasestorage.googleapis.com/v0/b/strapd-162605.appspot.com/o/photos%2F0195f255-6d5b-4789-be72-5234b0066057?alt=media&token=ac044a3d-bd9e-44bd-b793-6e1d99414ddd";
    /**Chat-ChatActivity**/
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    /**Groups-AddGroupActivity**/
    public static final String ACTION_UPLOAD_GROUP_PHOTO = "action_upload_group_profile_photo";
    public static final String EXTRA_STORAGE_URL = "extra_storage_url";
    /**Groups-GroupAdminActivity**/
    public static final String ADD_GROUP_NOTIFICATION = "addGroupNotification";
    /**IntroductionNotifications-NotificationFragmenAdapter**/
    public static final String DIRECT_FRIEND_REQUEST = "directFriendRequest";
    /**Services-InfixdUploadService **/
    public static final String ACTION_UPLOAD = "action_upload";
    public static final String ACTION_UPLOAD_PROFILE_PHOTO = "action_upload_profile_photo";
    public static final String ACTION_UPLOAD_POST_IMAGE = "action_upload_post_image";
    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";
    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";
    /**Services-InfixdDownloadService **/
    public static final String DOWNLOAD_COMPLETED = "download_completed";
    public static final String DOWNLOAD_ERROR = "download_error";
    public static final String EXTRA_DOWNLOAD_PATH = "extra_download_path";
    public static final String EXTRA_BYTES_DOWNLOADED = "extra_bytes_downloaded";
    public static final String ACTION_DOWNLOAD = "action_download";
    /**Services-UploadProfilePictureService **/
    public static final String ACTION_UPLOAD_PROFILE_PICTURE = "action_upload_profile_picture";
    /**UserHome-HomeFragment **/
    public static final String NEW_POST_NOTIFICATION = "newPostNotification";
    public static final String SHARED_POST_NOTIFICATION = "sharedPostNotification";
    /**InfixdFirebaseMessagingService **/
    public static final String ACTION_NOTIFY_SHARED_POST_CREATOR = "notifySharedPostCreator";

    /**Profile Default Values **/
    public static final String MY_PORFILE_ABOUTME_DEF = "Tell something about you ...";
    public static final String MY_PORFILE_EDUCATION_DEF = "Where did you studied?";
    public static final String MY_PORFILE_PROFESSION_DEF = "What do you do?";
    public static final String MY_PORFILE_LOCATION_DEF = "Where do you live?";
    public static final String OTHER_PORFILE_ABOUTME_DEF = "not provided";
    public static final String OTHER_PORFILE_EDUCATION_DEF = "not provided";
    public static final String OTHER_PORFILE_PROFESSION_DEF = "not provided";
    public static final String OTHER_PORFILE_LOCATION_DEF = "not provided";

    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "MONOSPACE",
                "fonts/Nunito-SemiBold.ttf");
    }

}
