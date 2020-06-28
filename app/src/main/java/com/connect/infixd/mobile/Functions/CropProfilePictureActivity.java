package com.connect.infixd.mobile.Functions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.connect.infixd.mobile.Application.InfixdBaseActivity;
import com.connect.infixd.mobile.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CropProfilePictureActivity extends InfixdBaseActivity {

    private CropImageView cropImageView;
    private ImageView cancelIV;
    private ImageView doneIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_profile_picture);

        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cancelIV = (ImageView) findViewById(R.id.cancelIV);
        doneIV = (ImageView) findViewById(R.id.doneIV);

        String selectedImageString = getIntent().getStringExtra("imageUri");
        Uri uri = Uri.parse(selectedImageString);
        cropImageView.setImageUriAsync(uri);

        doneIV.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap croppedBitmap = cropImageView.getCroppedImage();
                        File croppedImage = getOutputMediaFile();
                        try {
                            FileOutputStream fos = new FileOutputStream(croppedImage);
                            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                            fos.flush();
                            fos.close();
                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String saveUri = Uri.fromFile(croppedImage).toString();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("savedUri",saveUri);
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                }
        );

        cancelIV.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                }
        );
    }

    /** Create a File for saving an image **/
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
        String mImageName="user"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
}
