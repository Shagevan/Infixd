/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.SignupProcess;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.BackgroundTasks.AddPhoneContactsBGT;
import com.connect.infixd.mobile.BackgroundTasks.UpdateUserProfileBGT;
import com.connect.infixd.mobile.Functions.CropProfilePictureActivity;
import com.connect.infixd.mobile.Interfaces.DirectToHome;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Services.InfixdUploadService;
import com.infixd.client.model.UpdateUserProfileWrapper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class SignupThirdStep extends Fragment {

    private DirectToHome directToHome;
    private Button importContactsBtn;
    private ImageView profPic;
    private TextView hello;
    private TextView skipStepThree;
    private String userId;
    private String firstName;
    private Uri selectedImage;
    private Uri tempPicURI;
    private BroadcastReceiver mReceiver;
    private BroadcastReceiver broadcastReceiver;
    public ProgressDialog mProgressDialog;
    private AlertDialog dialog;
    private SignupProcessActivity signupActivity;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final String TAG = "Check";
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 500;
    private static final int CROP_IMAGE = 300;
    private static final String SEND_USER_PROFILE_PIC = "user_profile_picture";
    private static final int STORAGE_PERMISSION_FB_PHOTO = 600;
    private boolean isFbPhotoAvailable;

    public void setSelectedImage(Uri selectedImage) {
        this.selectedImage = selectedImage;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup_third_step, container, false);

        signupActivity = ((SignupProcessActivity)getActivity());

        firstName = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_FIRST_NAME);
        userId = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        hello = (TextView)v.findViewById(R.id.hello);
        skipStepThree = (TextView)v.findViewById(R.id.thirdSkipTV);
        importContactsBtn = (Button) v.findViewById(R.id.requestBtn);
        profPic = (ImageView) v.findViewById(R.id.stepThreeProfPic);

        hello.setText("Hi " + firstName);

        profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestStoragePermission();
            }
        });

        importContactsBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                getPermissionToReadUserContacts();
            }
        });

        skipStepThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectedImage != null){
                    showProgressDialog();
                    Intent intent = new Intent(getActivity(),InfixdUploadService.class);
                    intent.setAction("action_upload");
                    intent.putExtra("pc",selectedImage);
                    intent.putExtra(InfixdApp.STRING_PREFERENCE_USER_ID,userId);
                    intent.putExtra("code",InfixdApp.ACTION_UPLOAD_PROFILE_PHOTO);
                    intent.putExtra("category","photos");
                    getActivity().startService(intent);
                }
                else{
                    if(profPic.getDrawable()!= null && isFbPhotoAvailable){
                        Bitmap bitmap = ((BitmapDrawable)profPic.getDrawable()).getBitmap();
                        requestStoragePermissionToStoreFBPhoto(bitmap);
                    }
                    else {
                        signupActivity.saveSharedPreference(
                                InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL,
                                InfixdApp.DEFAULT_PROFILE_PIC_URL);
                        showProgressDialog();
                        updateProfileInfo();
                    }
                }
            }
        });

        return v;
    }

    private void captureImageInitialization() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Complete action using"), PICK_FROM_FILE);

        /*Log.d(TAG,"captureImageInitialization called");
        final String[] items = new String[] { "Take from camera", "Select from gallery" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select an Option");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent cameraIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File picFile = createTempFile();
                    tempPicURI = Uri.fromFile(picFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,tempPicURI);
                    startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image*//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Complete action using"), PICK_FROM_FILE);
                }
            }
        });
        dialog = builder.create();
        dialog.show();*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"onActivityResult called -- Data : "+data);
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (resultCode == RESULT_OK && tempPicURI != null) {
                    Intent intent = new Intent(getContext(), CropProfilePictureActivity.class);
                    intent.putExtra("imageUri", tempPicURI.toString());
                    startActivityForResult(intent, CROP_IMAGE);
                }
                else{
                    // handle user not selecting an image
                }
                break;
            case PICK_FROM_FILE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    Intent intent = new Intent(getContext(), CropProfilePictureActivity.class);
                    intent.putExtra("imageUri", selectedImage.toString());
                    startActivityForResult(intent, CROP_IMAGE);
                }
                else{
                    // handle user not selecting an image
                }
                break;
            case CROP_IMAGE:
                if(resultCode == RESULT_OK){
                    String imageUri = data.getStringExtra("savedUri");
                    selectedImage = Uri.parse(imageUri);
                    File f = new File(getRealPathFromURI(selectedImage));
                    Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                    profPic.setImageDrawable(d);
                }
                else{

                }
                break;

        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = getContext().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    private File storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return null;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pictureFile;
    }

    /** Create a File for saving an image or video **/
    private  File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Infixd/Media/UserProfilePhoto");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="Infixd"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private  File createTempFile(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Infixd/temp");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="temp"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToReadUserContacts() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                READ_CONTACTS_PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_CONTACTS_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AddPhoneContactsBGT addPhoneContactsBGT = new AddPhoneContactsBGT(getContext());
                    addPhoneContactsBGT.execute(userId);
                } else {
                    directToHome.directToHome(true);
                }
            }
            break;

            case MY_PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImageInitialization();

                } else {

                }
            }
            break;

            case STORAGE_PERMISSION_FB_PHOTO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Bitmap bitmap = ((BitmapDrawable)profPic.getDrawable()).getBitmap();
                    storeFBPhoto(bitmap);
                    uploadProfilePhoto();

                } else {
                    showProgressDialog();
                    updateProfileInfo();
                }
            }
            break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        directToHome = (DirectToHome) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String storageUrl = intent.getStringExtra(InfixdApp.EXTRA_STORAGE_URL);
                String downloadUrl = intent.getStringExtra(InfixdApp.EXTRA_DOWNLOAD_URL);
                signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL,downloadUrl);
                signupActivity.saveSharedPreference(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_STORAGE_URL,storageUrl);
                updateProfileInfo();
            }
        };

        //registering our receiver
        getActivity().registerReceiver(mReceiver, intentFilter);

        //REGISTER BROADCAST RECEIVER FOR GETTING PRO_PIC_URL FROM SECOND STEP
        broadcastReceiver =  new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                String userProfilePicURL = b.getString("ProfilePicURL");

                if(userProfilePicURL != null){
                    if(!userProfilePicURL.isEmpty()){
                        isFbPhotoAvailable = true;
                        Picasso.with(getContext())
                                .load(userProfilePicURL)
                                .into(profPic);
                    }
                }

            }
        };
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(SEND_USER_PROFILE_PIC));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.broadcastReceiver);
        getActivity().unregisterReceiver(this.mReceiver);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void RequestStoragePermission(){
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }
        else{
            captureImageInitialization();
        }
    }

    public void requestStoragePermissionToStoreFBPhoto(Bitmap bitmap){
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_FB_PHOTO);
        }
        else{
            storeFBPhoto(bitmap);
            uploadProfilePhoto();
        }
    }

    public void storeFBPhoto(Bitmap bitmap){
        try {
            File file = getOutputMediaFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            setSelectedImage(Uri.fromFile(file));
            out.flush();
            out.close();
        } catch(Exception e){
            e.getMessage();
        }
    }

    public void uploadProfilePhoto(){
        showProgressDialog();
        Intent intent = new Intent(getActivity(),InfixdUploadService.class);
        intent.setAction("action_upload");
        intent.putExtra("pc",selectedImage);
        intent.putExtra(InfixdApp.STRING_PREFERENCE_USER_ID,userId);
        intent.putExtra("code",InfixdApp.ACTION_UPLOAD_PROFILE_PHOTO);
        intent.putExtra("category","photos");
        getActivity().startService(intent);
    }

    public void updateProfileInfo(){
        String storageUrl = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_STORAGE_URL);
        String profilePicUrl = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFILE_PIC_URL);
        String aboutMe = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_ABOUTME);
        String education = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_EDUCATION);
        String profession = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_PROFESSION);
        String location = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_LOCATION);
        String facebookID = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_FB_LINK);
        String googlePlusID = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_GP_LINK);
        String twitterID = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_TWITTER_LINK);
        String instagramLink = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_INSTAGRAM_LINK);
        String email = signupActivity.getPreferenceValue(InfixdApp.STRING_PREFERENCE_EMAIL);

        UpdateUserProfileWrapper updateUserProfileWrapper = new UpdateUserProfileWrapper();
        updateUserProfileWrapper.setUserId(userId);
        updateUserProfileWrapper.setAboutMe(aboutMe);
        updateUserProfileWrapper.setEducation(education);
        updateUserProfileWrapper.setCity(location);
        updateUserProfileWrapper.setProfession(profession);
        updateUserProfileWrapper.setFblink(facebookID);
        updateUserProfileWrapper.setGooglePlusLink(googlePlusID);
        updateUserProfileWrapper.setInstagramLink(instagramLink);
        updateUserProfileWrapper.setTwitterLink(twitterID);
        updateUserProfileWrapper.setEmailAddress(email);
        updateUserProfileWrapper.setProfPicUrl(profilePicUrl);
        updateUserProfileWrapper.setProfPicStorageUrl(storageUrl);

        new UpdateUserProfileBGT(getContext()){
            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                hideProgressDialog();
                directToHome.directToHome(true);
            }
        }.execute(updateUserProfileWrapper);
    }

}
