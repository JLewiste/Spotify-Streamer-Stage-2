package com.example.firdause.spotifystreamer.musicplayer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.firdause.spotifystreamer.R;
import com.example.firdause.spotifystreamer.models.TopTracksParcelable;

import java.util.ArrayList;

public class MusicPlayerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Intent intent = getIntent();

        if (savedInstanceState == null && intent != null) {
            ArrayList<TopTracksParcelable> topTracksParcelables = intent
                    .getParcelableArrayListExtra(MusicPlayerActivityFragment.TRACKS_KEY);
            int position = intent.getIntExtra(MusicPlayerActivityFragment.TRACK_NUMBER, 0);

            MusicPlayerActivityFragment musicPlayerActivityFragment = MusicPlayerActivityFragment
                    .newInstance(topTracksParcelables, position);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.music_player_container, musicPlayerActivityFragment).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_tracks, menu);
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
}
