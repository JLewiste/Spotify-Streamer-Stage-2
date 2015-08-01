package com.example.firdause.spotifystreamer.toptracks;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.firdause.spotifystreamer.R;
import com.example.firdause.spotifystreamer.models.TopTracksParcelable;
import com.example.firdause.spotifystreamer.musicplayer.MusicPlayerActivity;
import com.example.firdause.spotifystreamer.musicplayer.MusicPlayerActivityFragment;

import java.util.ArrayList;


public class TopTracksActivity extends ActionBarActivity implements
        TopTracksActivityFragment.onTopTracksSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        Intent intent = getIntent();

        if (intent != null) {
            String nameArtist = intent.getStringExtra(TopTracksActivityFragment.NAME_ARTIST);

            //Display artist name below the action bar
            getSupportActionBar().setSubtitle(nameArtist);
        }

        if (intent != null && savedInstanceState == null) {
            String idArtist = intent.getStringExtra(TopTracksActivityFragment.ID_ARTIST);
            String nameArtist = intent.getStringExtra(TopTracksActivityFragment.NAME_ARTIST);

            TopTracksActivityFragment topTracksActivityFragment =
                    TopTracksActivityFragment.newInstance(idArtist, nameArtist);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, topTracksActivityFragment)
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemPicked(ArrayList<TopTracksParcelable> topTracksParcelables, int position) {
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        intent.putParcelableArrayListExtra(MusicPlayerActivityFragment.TRACKS_KEY, topTracksParcelables);
        intent.putExtra(MusicPlayerActivityFragment.TRACK_NUMBER, position);
        startActivity(intent);
    }
}
