package com.example.winni.photostream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

import hochschuledarmstadt.photostream_tools.BitmapUtils;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.RequestType;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Created by winni on 22.11.17.
 */

public class PhotoUpload extends PhotoStreamActivity implements OnPhotoUploadListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;

    private ImageButton camButton, galButton;
    private Button confirmUpload;
    private EditText editText;
    private ImageView chosenPic;
    private Bitmap bitmap;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_upload);

        chosenPic = (ImageView) findViewById(R.id.chosenPic);

        galButton = (ImageButton) findViewById(R.id.galButton);
        galButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeGalleryIntent();
            }
        });

        confirmUpload = (Button) findViewById(R.id.confirmUpload);

        editText = (EditText) findViewById(R.id.photoDescription);

        camButton = (ImageButton) findViewById(R.id.camButton2);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        confirmUpload.setEnabled(false);
        initializeViews();

    };

    private void initializeViews() {
        confirmUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmUpload.setEnabled(false);
                IPhotoStreamClient photoStreamClient = getPhotoStreamClient();
                byte[] imageBytes = BitmapUtils.bitmapToBytes(bitmap);
                try {
                    String description = editText.getText().toString().trim();
                    photoStreamClient.uploadPhoto(imageBytes, description);
                } catch (IOException e) {
                    Log.e(PhotoUpload.class.getSimpleName(), "error while sending photo to server", e);
                    confirmUpload.setEnabled(true);
                } catch (JSONException e) {
                    Log.e(PhotoUpload.class.getSimpleName(), "error while encoding data to json", e);
                    confirmUpload.setEnabled(true);
                }
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchTakeGalleryIntent() {
        Intent chosePictureIntent = new Intent();
        chosePictureIntent.setType("image/*");
        chosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        if (chosePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chosePictureIntent, REQUEST_IMAGE_GALLERY);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            chosenPic.setImageBitmap(bitmap);
            confirmUpload.setEnabled(true);
        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK){
            Uri IMAGE_URI = data.getData();
            try{
                InputStream image_stream = getContentResolver().openInputStream(IMAGE_URI);
                bitmap = BitmapFactory.decodeStream(image_stream);
                chosenPic.setImageBitmap(bitmap);
                confirmUpload.setEnabled(true);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        BitmapUtils.recycleBitmap(bitmap);
        super.onDestroy();
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnPhotoUploadListener(this);
        if (savedInstanceState != null){
            boolean uploadRequestIsRunning = photoStreamClient.hasOpenRequestOfType(RequestType.UPLOAD_PHOTO);
            confirmUpload.setEnabled(!uploadRequestIsRunning);
        }
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        photoStreamClient.removeOnPhotoUploadListener(this);
    }

    @Override
    public void onPhotoUploaded(Photo photo) {
        Toast.makeText(this, "Photo Uploaded", Toast.LENGTH_LONG).show();
        confirmUpload.setEnabled(true);
        finish();
    }

    @Override
    public void onPhotoUploadFailed(HttpError httpError) {
        //Utils.showErrorInAlertDialog(this, "Photo Upload failed", httpError);
        confirmUpload.setEnabled(true);
    }


}
