package com.danielto.example.spotifystreamerpart1.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.danielto.example.spotifystreamerpart1.R;
import com.danielto.example.spotifystreamerpart1.fragment.FragmentMusicPlayer;
import com.danielto.example.spotifystreamerpart1.fragment.FragmentTopTracks;
import com.danielto.example.spotifystreamerpart1.utils.Constants;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

import static com.danielto.example.spotifystreamerpart1.adapter.TopTracksAdapter.OnTrackClickListener;
import static com.danielto.example.spotifystreamerpart1.fragment.FragmentMusicPlayer.OnNextTrackListener;
import static com.danielto.example.spotifystreamerpart1.fragment.FragmentMusicPlayer.OnPreviousTrackListener;
import static com.danielto.example.spotifystreamerpart1.fragment.FragmentMusicPlayer.newInstance;
import static com.danielto.example.spotifystreamerpart1.fragment.FragmentTopTracks.OnTrackListReceivedListener;
import static com.danielto.example.spotifystreamerpart1.fragment.FragmentTopTracks.newInstance;

public class ActivityMusicStreamer extends AppCompatActivity implements OnTrackClickListener, OnTrackListReceivedListener,
        OnNextTrackListener, OnPreviousTrackListener {

    private List<Track> tracks;
    private int trackPosition;
    private ProgressBar progress;
    private String artistId;
    private boolean isMusicPlayerShown;
    private FragmentMusicPlayer fragmentMusicPlayer;
    private FragmentTopTracks fragmentTopTracks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // we don't need this activity if we're in landscape!
            if (savedInstanceState != null) {
                Intent intent = new Intent();
                artistId = savedInstanceState.getString(Constants.ARTIST_ID);
                intent.putExtra(Constants.ARTIST_ID, artistId);
                setResult(RESULT_OK, intent);
            }
            finish();
            return;
        }

        setContentView(R.layout.activity_top_tracks);
        artistId = getIntent().getStringExtra(Constants.ARTIST_ID);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isMusicPlayerShown = false;

        fragmentTopTracks = newInstance(artistId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_top_tracks_container, fragmentTopTracks, Constants.FRAGMENT_TOP_TRACKS)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ARTIST_ID, artistId);
    }

    @Override
    public void onBackPressed() {
        if (isMusicPlayerShown) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_top_tracks_container, fragmentTopTracks, Constants.FRAGMENT_TOP_TRACKS)
                    .commit();
            isMusicPlayerShown = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && isMusicPlayerShown) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_top_tracks_container, fragmentTopTracks, Constants.FRAGMENT_TOP_TRACKS)
                    .commit();
            isMusicPlayerShown = false;
        } else {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onTrackClick(int position, String artistName, String trackName, String albumName, String thumbnailUrl, String previewUrl) {
        isMusicPlayerShown = true;
        trackPosition = position;
        fragmentMusicPlayer = newInstance(artistName, trackName, albumName, thumbnailUrl, previewUrl);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_top_tracks_container, fragmentMusicPlayer, Constants.FRAGMENT_TOP_TRACKS)
                .commit();
    }

    @Override
    public void updateTrackList(List<Track> trackList) {
        tracks = trackList;
    }

    @Override
    public void onNextTrackClicked() {
        if (trackPosition + 1 > tracks.size()) return;
        Track track = tracks.get(++trackPosition);
        if (fragmentMusicPlayer != null) {
            fragmentMusicPlayer.updatePlayerData(track.artists.get(0).name, track.name, track.album.name, track.album.images.get(0).url, track.preview_url);
            fragmentMusicPlayer.setPlayerData();
            fragmentMusicPlayer.setupPlayer();
        }
    }

    @Override
    public void onPreviousTrackClicked() {
        if (trackPosition - 1 < 0) return;
        Track track = tracks.get(--trackPosition);
        if (fragmentMusicPlayer != null) {
            fragmentMusicPlayer.updatePlayerData(track.artists.get(0).name, track.name, track.album.name, track.album.images.get(0).url, track.preview_url);
            fragmentMusicPlayer.setPlayerData();
            fragmentMusicPlayer.setupPlayer();
        }
    }
}
