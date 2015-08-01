package com.example.firdause.spotifystreamer.musicplayer;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firdause.spotifystreamer.R;
import com.example.firdause.spotifystreamer.models.TopTracksParcelable;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MusicPlayerActivityFragment extends DialogFragment {
    //For debugging purpose
    private final String LOG_TAG = MusicPlayerActivityFragment.class.getSimpleName();

    public static final String TRACKS_KEY = "track_key";
    public static final String TRACK_NUMBER = "track_number";

    private MediaPlayer mediaPlayer;
    private ArrayList<TopTracksParcelable> trackList;
    private TopTracksParcelable tracks;
    private int trackNumber;
    private Handler mHandler = new Handler();

    //Detect MediaPlayer prepareAsync() state
    private boolean musicIsBuffering = false;

    String preview_url;

    TextView artistName;
    TextView albumName;
    TextView trackTitle;

    ImageView albumCover;

    SeekBar seekBar;

    TextView currentDuration;
    TextView totalDuration;

    ImageButton playPrevious;
    ImageButton playAndPause;
    ImageButton playNext;

    //Constructor
    public MusicPlayerActivityFragment() {
    }

    //New instance for MusicPlayerActivityFragment
    public static MusicPlayerActivityFragment newInstance(ArrayList<TopTracksParcelable> topTracksParcelables, int position) {
        MusicPlayerActivityFragment musicPlayerActivityFragment = new MusicPlayerActivityFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(TRACKS_KEY, topTracksParcelables);
        args.putInt(TRACK_NUMBER, position);
        musicPlayerActivityFragment.setArguments(args);
        return musicPlayerActivityFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music_player, container, false);

        //Create artist name, album name and track title objects
        artistName = (TextView) rootView.findViewById(R.id.mplayer_artist_name);
        albumName = (TextView) rootView.findViewById(R.id.mplayer_album_name);
        trackTitle = (TextView) rootView.findViewById(R.id.mplayer_music_title);

        //Create album cover object
        albumCover = (ImageView) rootView.findViewById(R.id.mplayer_album_artwork);

        //Create seek bar object
        seekBar = (SeekBar) rootView.findViewById(R.id.mplayer_seek_bar);

        //Create current and total duration objects
        currentDuration = (TextView) rootView.findViewById(R.id.mplayer_current_duration);
        totalDuration = (TextView) rootView.findViewById(R.id.mplayer_total_duration);

        //Create music player button objects
        playPrevious = (ImageButton) rootView.findViewById(R.id.mplayer_previous_button);
        playAndPause = (ImageButton) rootView.findViewById(R.id.mplayer_play_button);
        playNext = (ImageButton) rootView.findViewById(R.id.mplayer_next_button);

        Bundle args = getArguments();

        if (args != null) {
            trackList = args.getParcelableArrayList(TRACKS_KEY);
            trackNumber = args.getInt(TRACK_NUMBER, 0);

            if (trackList.get(trackNumber) != null) {
                tracks = trackList.get(trackNumber);
                preview_url = tracks.getPreviewUrl();
                displayTrackInfo();
            }
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Setup play previous song button
        playPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!musicIsBuffering) {
                    playAndPause.setTag("pause");
                    playAndPause.setImageResource(android.R.drawable.ic_media_pause);

                    mediaPlayer.stop();

                    if (trackNumber == 0) {
                        trackNumber = trackList.size() - 1;
                    } else {
                        trackNumber = --trackNumber;
                    }
                    preview_url = trackList.get(trackNumber).getPreviewUrl();
                    startMusicPlayer();
                }
                else {
                    Toast.makeText(getActivity(), R.string.music_buffering, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Setup play and pause song button
        playAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captureTag = (String) playAndPause.getTag();

                if (captureTag.equals("play")) {
                    playAndPause.setTag("pause");
                    playAndPause.setImageResource(android.R.drawable.ic_media_pause);
                    mediaPlayer.start();
                } else if (captureTag.equals("pause")) {
                    playAndPause.setTag("play");
                    playAndPause.setImageResource(android.R.drawable.ic_media_play);
                    mediaPlayer.pause();
                }
            }
        });

        //Setup play next song button
        playNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!musicIsBuffering) {
                    playAndPause.setTag("pause");
                    playAndPause.setImageResource(android.R.drawable.ic_media_pause);

                    mediaPlayer.stop();
                    if (trackNumber == trackList.size() - 1) {
                        trackNumber = 0;
                    } else {
                        trackNumber = ++trackNumber;
                    }

                    preview_url = trackList.get(trackNumber).getPreviewUrl();
                    startMusicPlayer();
                }
                else {
                    Toast.makeText(getActivity(), R.string.music_buffering, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        startMusicPlayer();

    }

    @Override
    public void onResume() {
        super.onResume();
        startMusicPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }


    //Display artist name, album name, track title and album cover on the music player UI
    public void displayTrackInfo() {
        artistName.setText(tracks.getArtist());
        albumName.setText(tracks.getAlbum());
        trackTitle.setText(tracks.getName());
        Picasso.with(getActivity()).load(tracks.getBigUrl()).into(albumCover);
    }


    public void startMusicPlayer() {
        tracks = trackList.get(trackNumber);

        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(preview_url);
            mediaPlayer.prepareAsync();
            musicIsBuffering = true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                long time = mediaPlayer.getDuration();
                int seconds = (int) Math.round(time / 1000);
                seekBar.setMax(seconds);
                totalDuration.setText(convertString(seconds));
                mp.start();
                displayTrackInfo();
                musicIsBuffering = false;
            }

        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int seekBarCursor = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(seekBarCursor);

                    currentDuration.setText(convertString(seekBarCursor));
                }
                mHandler.postDelayed(this, 1000);
            }
        });


    }


    public String convertString(int seconds) {
        if (seconds < 10) {
            return "0:0" + seconds;
        } else {
            return "0:" + seconds;
        }
    }

    //Setup the Dialog Fragment for Music Player
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("trackNumber", trackNumber);
    }
}
