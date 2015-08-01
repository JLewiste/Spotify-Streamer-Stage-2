package com.example.firdause.spotifystreamer.searchartist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.firdause.spotifystreamer.R;
import com.example.firdause.spotifystreamer.models.TopTracksParcelable;
import com.example.firdause.spotifystreamer.musicplayer.MusicPlayerActivityFragment;
import com.example.firdause.spotifystreamer.toptracks.TopTracksActivity;
import com.example.firdause.spotifystreamer.toptracks.TopTracksActivityFragment;

import java.util.ArrayList;


public class SearchMainActivity extends ActionBarActivity
        implements SearchMainActivityFragment.onSearchArtistSelectedListener
        , TopTracksActivityFragment.onTopTracksSelectedListener {

    //For debugging purpose
    private final String LOG_TAG = SearchMainActivity.class.getSimpleName();

    //Checks for Tablet UI
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_main);

        //Detects the presence of Tablet UI
        //If Tablet UI is present, boolean will be true and vice versa
        mTwoPane = getResources().getBoolean(R.bool.dual_pane);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemPicked(String idArtist, String nameArtist) {
        //If Tablet UI is present, utilize the tablet ui via fragments
        if (mTwoPane) {
            TopTracksActivityFragment topTracksActivityFragment =
                    TopTracksActivityFragment.newInstance(idArtist, nameArtist);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_container, topTracksActivityFragment)
                    .commit();
        }
        //If Tablet UI is absent, utilize the phone ui via activities
        else {
            //Start TopTracks Activity!
            Intent intent = new Intent(this, TopTracksActivity.class);
            intent.putExtra(TopTracksActivityFragment.ID_ARTIST, idArtist);
            intent.putExtra(TopTracksActivityFragment.NAME_ARTIST, nameArtist);

            //Start top tracks activity
            startActivity(intent);
        }
    }

    //Music player for Tablet UI
    @Override
    public void onItemPicked(ArrayList<TopTracksParcelable> topTracksParcelables, int position) {
        MusicPlayerActivityFragment musicPlayerActivityFragment = MusicPlayerActivityFragment
                .newInstance(topTracksParcelables, position);
        musicPlayerActivityFragment.show(getSupportFragmentManager(), "dialog");
    }
}
