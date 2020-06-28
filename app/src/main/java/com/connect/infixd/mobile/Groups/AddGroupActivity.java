/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Groups;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.BackgroundTasks.AddGroupBGT;
import com.connect.infixd.mobile.DBHelper.SearchSuggestionDBHelper;
import com.connect.infixd.mobile.DBModels.Group;
import com.connect.infixd.mobile.Functions.CropProfilePictureActivity;
import com.connect.infixd.mobile.Interfaces.AddGroupResult;
import com.connect.infixd.mobile.R;
import com.connect.infixd.mobile.Services.InfixdUploadService;
import com.infixd.client.model.GroupInfoResponse;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddGroupActivity extends InfixdBaseActivity implements AddGroupResult {

    private EditText group_name_editText;
    private EditText group_desc_editText;
    private EditText group_url_editText;
    private Button addGroupBtn;
    private ImageView group_pic_IV;
    private String userId;
    private String groupPicStorageUrl;
    private Uri savedImageUri;
    private Uri tempPicURI;
    private AlertDialog dialog;
    private BroadcastReceiver mReceiver;
    private MKLoader loader;
    private static final int PICK_FROM_CAMERA = 100;
    private static final int PICK_FROM_FILE = 200;
    private static final int CROP_IMAGE = 300;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        userId = getIntent().getStringExtra("userName");

        group_name_editText = (EditText) findViewById(R.id.AddGroupNameET );
        group_desc_editText = (EditText) findViewById(R.id.AddGroupDescET);
        group_url_editText = (EditText) findViewById(R.id.AddGroupExtUrlET);
        addGroupBtn = (Button) findViewById(R.id.createGroupbtn);
        group_pic_IV = (ImageView) findViewById(R.id.groupProfPicIV);
        addGroupBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(savedImageUri != null){
                            loader.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(AddGroupActivity.this,InfixdUploadService.class);
                            intent.setAction("action_upload");
                            intent.putExtra("pc",savedImageUri);
                            intent.putExtra("code",InfixdApp.ACTION_UPLOAD_GROUP_PHOTO);
                            intent.putExtra(InfixdApp.STRING_PREFERENCE_USER_ID,userId);
                            intent.putExtra("category","Group_Photos");
                            AddGroupActivity.this.startService(intent);
                        }
                        else{
                            updateGroupProfile();
                            finish();
                        }
                    }
                }
        );

        group_pic_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestStoragePermission();
            }
        });

    }

    @Override
    public void getGroups(List<GroupInfoResponse> groups) {
        // [ START OF BLOCK SAVING USER INFIXD GROUPS IN SQLTE DB]
        if (groups != null) {
            List<Group> userGroups = new ArrayList<Group>();
            for (int i=0;i<groups.size();i++) {
                Group group = new Group();
                group.setGroup_name(groups.get(i).getGroupName());
                group.setGroup_photo_url(groups.get(i).getGroupPhotoUri());
                group.setNoOfMembers(groups.get(i).getNoOfMembers());
                group.setUserPosition(groups.get(i).getUserPosition());
                userGroups.add(group);
            }
            SearchSuggestionDBHelper db = new SearchSuggestionDBHelper(getBaseContext());
            db.addGroups(userGroups);
        }
        // [ END OF BLOCK SAVING USER INFIXD GROUPS IN SQLTE DB]
        this.finish();
    }

    private void openPhotoPicker() {
        final String[] items = new String[] { "Take from camera", "Select from gallery" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddGroupActivity.this,
                android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupActivity.this);
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
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Complete action using"), PICK_FROM_FILE);
                }
            }
        });
        dialog = builder.create();
        dialog.show();
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

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public void RequestStoragePermission(){
        if (ContextCompat.checkSelfPermission(AddGroupActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddGroupActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }
        else{
            openPhotoPicker();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PICK_FROM_CAMERA){
            if (resultCode == RESULT_OK && tempPicURI != null) {
                Intent intent = new Intent(this, CropProfilePictureActivity.class);
                intent.putExtra("imageUri", tempPicURI.toString());
                startActivityForResult(intent, CROP_IMAGE);
            }
            else{
                // handle user not selecting an image
            }

        }
        else if(requestCode == PICK_FROM_FILE){
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                Intent intent = new Intent(this, CropProfilePictureActivity.class);
                intent.putExtra("imageUri", selectedImage.toString());
                startActivityForResult(intent, CROP_IMAGE);
            }
            else{
                // handle user not selecting an image
            }
        }
        else if (requestCode == CROP_IMAGE && resultCode == Activity.RESULT_OK){
            String imageUri = data.getStringExtra("savedUri");
            savedImageUri = Uri.parse(imageUri);
            File f = new File(getRealPathFromURI(savedImageUri));
            Drawable d = Drawable.createFromPath(f.getAbsolutePath());
            group_pic_IV.setImageDrawable(d);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openPhotoPicker();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                groupPicStorageUrl = intent.getStringExtra(InfixdApp.EXTRA_STORAGE_URL);
                updateGroupProfile();
                loader.setVisibility(View.GONE);
                AddGroupActivity.this.finish();
            }
        };
        //registering our receiver
        AddGroupActivity.this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister our receiver
        AddGroupActivity.this.unregisterReceiver(this.mReceiver);
    }

    public void updateGroupProfile(){
        String groupName = group_name_editText.getText().toString();
        String description = group_desc_editText.getText().toString();
        String externalUrl = group_url_editText.getText().toString();

        AddGroupBGT addGroupBGT = new AddGroupBGT(AddGroupActivity.this);
        addGroupBGT.execute(userId,groupName,groupPicStorageUrl,description,externalUrl);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
