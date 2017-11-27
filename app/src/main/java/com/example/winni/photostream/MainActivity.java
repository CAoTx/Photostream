package com.example.winni.photostream;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.RequestType;
import hochschuledarmstadt.photostream_tools.adapter.BasePhotoAdapter;
import hochschuledarmstadt.photostream_tools.callback.OnCommentCountChangedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnRequestListener;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

public class MainActivity extends PhotoStreamActivity implements OnRequestListener, OnNewPhotoReceivedListener, OnPhotosReceivedListener {

    private static int columns = 3;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private Button loadMoreButton;
    private FloatingActionButton uploadButton;
    private int penis;

    // Key zum Zwischenspeichern der Fotos in onSaveInstance
    private static final String KEY_ADAPTER = "KEY_ADAPTER";

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {

        photoStreamClient.addOnRequestListener(this, RequestType.LOAD_PHOTOS);
        photoStreamClient.addOnPhotosReceivedListener(this);
        photoStreamClient.addOnNewPhotoReceivedListener(this);
        //photoStreamClient.addOnCommentCountChangedListener((OnCommentCountChangedListener) this);
        /**
         * Beim ersten Start der Activity soll die erste Seite aus dem Stream geladen werden
         */
        if (savedInstanceState == null){
            photoStreamClient.loadPhotos();
        }

    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        //Entfernen der Listener
        photoStreamClient.removeOnRequestListener(this);
        photoStreamClient.removeOnPhotosReceivedListener(this);
        //photoStreamClient.removeOnCommentCountChangedListener((OnCommentCountChangedListener) this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(MainActivity.class.getSimpleName(), "onCreate Main");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Photostream");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, columns));

        /*
        recyclerView.addOnScrollListener(new OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                int lastVisiblePos = ((GridLayoutManager) (recyclerView.getLayoutManager())).findLastVisibleItemPosition();
                int itemCount = recyclerView.getAdapter().getItemCount();
                Log.d(MainActivity.class.getSimpleName(), "Last Visible Position: " + lastVisiblePos);
                Log.d(MainActivity.class.getSimpleName(), "Anzahl items: " + itemCount);

            }
        }); */

        // Referenz auf Button setzen
        loadMoreButton = (Button) findViewById(R.id.loadmorebutton);

        // OnClickListener registrieren
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Button deaktivieren
                loadMoreButton.setEnabled(false);
                IPhotoStreamClient photoStreamClient = getPhotoStreamClient();
                // Wenn nicht bereits ein Request für Photos ausgeführt wird,
                if (!photoStreamClient.hasOpenRequestOfType(RequestType.LOAD_PHOTOS)) {
                    // dann nächste Seite aus dem Stream laden
                    photoStreamClient.loadMorePhotos();
                }
            }
        });

        Log.d(MainActivity.class.getSimpleName(),"onCreate: before fab init");

        uploadButton = (FloatingActionButton) findViewById(R.id.uploadButton);

        Log.d(MainActivity.class.getSimpleName(), "onCreate: after fab init");

        uploadButton.setOnClickListener(new View.OnClickListener(){
            public  void onClick(View view){
                Log.d(MainActivity.class.getSimpleName(), "onClick: happened");
            startActivity(new Intent(MainActivity.this, PhotoUpload.class));
            }

        });


        adapter = new PhotoAdapter();
        /* Nicht implementiert, da keine FullscreenActivity Class
        // OnItemClickListener für die ImageView mit der id "imageView" setzen.
        adapter.setOnItemClickListener(R.id.imageView, new BasePhotoAdapter.OnItemClickListener<PhotoAdapter.PhotoViewHolder>() {
            @Override
            public void onItemClicked(PhotoAdapter.PhotoViewHolder viewHolder, View v, Photo photo) {
                // Wenn auf die ImageView ein Klick ausgelöst wurde, dann die FullscreenActivity starten,
                // um das Photo im Vollbild anzuzeigen
                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                intent.putExtra(FullscreenActivity.KEY_PHOTO, photo);
                startActivity(intent);
            }
        });
        */

        // Als Letztes der RecyclerView den Adapter als Datenquelle zuweisen
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == R.id.action_refresh){
            final IPhotoStreamClient client = getPhotoStreamClient();
            // Wenn nicht bereits ein Request für Photos ausgeführt wird,
            if (!client.hasOpenRequestOfType(RequestType.LOAD_PHOTOS)){
                // dann die erste Seite aus dem Stream laden
                getPhotoStreamClient().loadPhotos();
            }
            // true zurückgeben, um dem System mitzuteilen, dass das Event verarbeitet wurde
            return true;
        }
// Wenn das Event nicht verarbeitet wurde, dann das Event weiterleiten
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestStarted() {
        //findViewById(R.id.progressCircle).setVisibility(View.VISIBLE);
        Log.d(MainActivity.class.getSimpleName(), "on request started");
    }

    @Override
    public void onRequestFinished() {
        Log.d(MainActivity.class.getSimpleName(), "on request finished");
        //findViewById(R.id.progressCircle).setVisibility(View.GONE);
    }

    @Override
    public void onNewPhotoReceived(Photo photo) {
        Log.d(MainActivity.class.getSimpleName(), "on new photo received");
        adapter.add(photo);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onPhotosReceived(PhotoQueryResult result) {
        Log.d(MainActivity.class.getSimpleName(), "many photos received");
        /* adapter.addAll(result.getPhotos());

        adapter.notifyDataSetChanged(); */

        List<Photo> photos = result.getPhotos();
        if (result.isFirstPage()){
            // Zum ersten Mal abgerufen oder Aktualisierung des Streams wurde explizit angefordet => Photos ersetzen
            adapter.set(photos);
        }else{
            // Photos an die Liste anhängen
            adapter.addAll(photos);
        }
        // Request ist beendet, also kann der Button wieder aktiviert werden
        loadMoreButton.setEnabled(true);
        // Den Button sichtbar machen, wenn weitere Seiten im Stream vorhanden sind, ansonsten ausblenden
        loadMoreButton.setVisibility(result.hasNextPage() ? Button.VISIBLE : Button.GONE);

    }

    @Override
    public void onReceivePhotosFailed(HttpError httpError) {
        Log.d(MainActivity.class.getSimpleName(), "http error");
        loadMoreButton.setEnabled(true);
    }

    @Override
    public void onNoNewPhotosAvailable() {
        Log.d(MainActivity.class.getSimpleName(), "no new photos available");
    }
}
