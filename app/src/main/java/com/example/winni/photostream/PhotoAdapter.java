package com.example.winni.photostream;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import hochschuledarmstadt.photostream_tools.adapter.BasePhotoAdapter;
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Created by winni on 13.11.17.
 */

public class PhotoAdapter extends BasePhotoAdapter<PhotoAdapter.PhotoViewHolder> {

    PhotoAdapter(){
        super();
    }

    @Override
    protected void onBitmapLoadedIntoImageView(ImageView imageView) {

    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        // Photo an der angegebenen Position referenzieren
        final Photo photo = getItemAtPosition(position);
        // Bitmap anhand des Photo Objekts laden und in der ImageView setzen
        loadBitmapIntoImageViewAsync(holder, holder.imageView, photo);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Objekt referenzieren für das Erzeugen des Layouts für ein Element in der angezeigten Liste
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Ein neues Layout für ein Element erzeugen
        View itemView = layoutInflater.inflate(R.layout.show_photos, parent, false);
        // ViewHolder erzeugen und dabei das erzeugte Layout übergeben
        return new PhotoViewHolder(itemView);
    }

    public class PhotoViewHolder extends  RecyclerView.ViewHolder {
        public final ImageView imageView;

        // itemView entspricht dem erzeugten Layout aus der onCreateViewHolder() Methode
        public PhotoViewHolder(View itemView) {
            super(itemView);
            // Views referenzieren, auf die man in der onBindViewHolder
            // Methode zugreifen möchte
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
