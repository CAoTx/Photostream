package com.example.winni.photostream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import hochschuledarmstadt.photostream_tools.FullscreenPhotoActivity;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Created by stas on 27.11.17.
 */

public class FullscreenActivity extends FullscreenPhotoActivity
{
    public static String KEY_PHOTO = "sdfghjwedrt";
    public ImageView fullPhoto;
    private Photo photo;
    private Button deleteButton;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen);
        fullPhoto = (ImageView) findViewById(R.id.fullscreenview);
        photo = getIntent().getExtras().getParcelable(KEY_PHOTO);
        Bitmap loadedSlowBitmap = BitmapFactory.decodeFile(photo.getImageFilePath());
        fullPhoto.setImageBitmap(loadedSlowBitmap);

        deleteButton = (Button) findViewById(R.id.photo_delete_button);

    }

    @Override
    protected void onSystemUiVisible()
    {

    }

    @Override
    protected void onSystemUiHidden()
    {

    }

    @Override
    protected void onPhotoStreamServiceConnected(final IPhotoStreamClient photoStreamClient, Bundle savedInstanceState)
    {

        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                photoStreamClient.addOnPhotoDeletedListener(new OnPhotoDeletedListener()
                {
                    @Override
                    public void onPhotoDeleted(int photoId)
                    {
                        finish();
                    }

                    @Override
                    public void onPhotoDeleteFailed(int photoId, HttpError httpError)
                    {
                        Log.d("asdf", "nicht");
                    }
                });
                photoStreamClient.deletePhoto(photo.getId());
            }
        });

    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient)
    {

    }
}
