/* Copyright (C) Infixd Pvt.Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.connect.infixd.mobile.Post;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.connect.infixd.mobile.Application.InfixdApp;
import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.BackgroundTasks.NewPostBGT;
import com.connect.infixd.mobile.Functions.CropImageActivity;
import com.connect.infixd.mobile.R;
import com.infixd.client.model.CreatePostResponse;

import java.io.File;

public class NewPostActivity extends InfixdBaseActivity {
    private EditText new_post_edit_text;
    private EditText new_post_edit_text2;
    private ImageView upload_photo_image_view1;
    private ImageView upload_image_image_view2;
    private ImageView pick_color_image_view;
    private ImageView uploaded_photo_image_view;
    private FloatingActionButton fabPost1;
    private FloatingActionButton fabPost2;
    private ProgressBar progressBar;
    private ConstraintLayout first_constraint_layout;
    private ConstraintLayout second_constraint_layout;
    private Uri savedImageUri;
    private String userId;
    private String caption = "";
    private String textBackgroundColor;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 500;
    private static final int PICK_FROM_GALARY = 1;
    private static final int CROP_IMAGE = 2;
    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";
    private static final String YELLOW = "yellow";
    private static final String MAROON = "maroon";
    private static final String BLACK = "black";
    private int[] androidColors;
    private int colorChangeClickCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        userId = getPreferenceValue(InfixdApp.STRING_PREFERENCE_USER_ID);

        progressBar = (ProgressBar)findViewById(R.id.progress_dialog);

        //first constraint layout
        first_constraint_layout = (ConstraintLayout) findViewById(R.id.first_constraint_layout);
        new_post_edit_text = (EditText) findViewById(R.id.new_post_edit_text);
        pick_color_image_view = (ImageView) findViewById(R.id.pick_color_image_view);
        upload_photo_image_view1 = (ImageView) findViewById(R.id.upload_photo_image_view1);
        fabPost1 = (FloatingActionButton) findViewById(R.id.fabPost1);
        new_post_edit_text.setSelection(0);

        //second constraint layout
        second_constraint_layout = (ConstraintLayout) findViewById(R.id.second_constraint_layout);
        fabPost2 = (FloatingActionButton) findViewById(R.id.fabPost2);
        new_post_edit_text2 = (EditText) findViewById(R.id.new_post_edit_text2);
        upload_image_image_view2 = (ImageView) findViewById(R.id.upload_image_image_view2);
        uploaded_photo_image_view = (ImageView) findViewById(R.id.uploaded_photo_image_view);
        androidColors = getResources().getIntArray(R.array.newCurrentsColors);

        first_constraint_layout.setBackgroundColor(androidColors[colorChangeClickCount]);
        textBackgroundColor = GREEN;
        fabPost1.setEnabled(false);
        new_post_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                charSequence.length();
                if (charSequence.toString().trim().length() > 0 ){
                    fabPost1.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pick_color_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(colorChangeClickCount >= 6){
                    colorChangeClickCount = 0;
                    setBackgroundColor(colorChangeClickCount);
                }
                else{
                    colorChangeClickCount++;
                    setBackgroundColor(colorChangeClickCount);
                }

            }
        });

        upload_photo_image_view1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RequestStoragePermission();
                    }
                }
        );

        fabPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                fabPost1.setEnabled(false);
                caption = new_post_edit_text.getText().toString();
                if (!caption.isEmpty() && (savedImageUri == null || savedImageUri.toString().isEmpty())){
                    String imageUri = "";
                    new NewPostBGT(NewPostActivity.this){
                        @Override
                        protected void onPostExecute(CreatePostResponse response) {
                            super.onPostExecute(response);
                            hideProgresbar();
                            Intent shareSocialIntent = new Intent(NewPostActivity.this, ShareAlertDialog.class);
                            shareSocialIntent.putExtra("caption", caption);
                            shareSocialIntent.putExtra("imageUri", imageUri);
                            shareSocialIntent.putExtra("shareAlertDialogTitle", "Current has been posted !");
                            startActivity(shareSocialIntent);
                            finish();
                        }
                    }.execute(userId, caption, imageUri, textBackgroundColor);
                }
            }
        });

        //Second layout click listeners
        upload_image_image_view2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RequestStoragePermission();
                    }
                }
        );

        fabPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabPost2.setEnabled(false);
                caption = new_post_edit_text.getText().toString();
                if (savedImageUri != null || !savedImageUri.toString().isEmpty()){
                    caption = new_post_edit_text2.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("imageURL",savedImageUri);
                    intent.putExtra("caption",caption);
                    intent.putExtra("textBackgroundColor",textBackgroundColor);
                    setResult(Activity.RESULT_OK,intent);
                    finish();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_GALARY && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            Intent intent = new Intent(this, CropImageActivity.class);
            intent.putExtra("imageUri", selectedImage.toString());
            startActivityForResult(intent, CROP_IMAGE);
        }
        else if (requestCode == PICK_FROM_GALARY && resultCode == RESULT_CANCELED){

        }
        else if (requestCode == CROP_IMAGE && resultCode == Activity.RESULT_OK){
            String imageUri = data.getStringExtra("savedUri");
            savedImageUri = Uri.parse(imageUri);
            first_constraint_layout.setVisibility(View.GONE);
            second_constraint_layout.setVisibility(View.VISIBLE);
            new_post_edit_text2.setText(new_post_edit_text.getText().toString());

            File f = new File(getRealPathFromURI(savedImageUri));
            Drawable d = Drawable.createFromPath(f.getAbsolutePath());
            uploaded_photo_image_view.setImageDrawable(d);

            uploaded_photo_image_view.setImageURI(savedImageUri);
        }
        else if (requestCode == CROP_IMAGE && resultCode == Activity.RESULT_CANCELED){

        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void setBackgroundColor(int colorCode){
        first_constraint_layout.setBackgroundColor(androidColors[colorCode]);
        switch (colorCode){
            case 0:
                textBackgroundColor = GREEN;
                break;
            case 1:
                textBackgroundColor = YELLOW;
                new_post_edit_text.setHintTextColor(getResources().getColor(R.color.black_color));
                new_post_edit_text.setTextColor(getResources().getColor(R.color.black_color));
                break;
            case 2:
                textBackgroundColor = RED;
                break;
            case 3:
                textBackgroundColor = BLUE;
                break;
            case 4:
                textBackgroundColor = MAROON;
                break;
            case 5:
                textBackgroundColor = BLACK;
                break;
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Complete action using"), PICK_FROM_GALARY);
    }

    public void RequestStoragePermission(){
        if (ContextCompat.checkSelfPermission(NewPostActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NewPostActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }
        else{
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
    }

    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgresbar(){
        progressBar.setVisibility(View.GONE);
    }

}
