package com.danielto.example.spotifystreamerpart1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.danielto.example.spotifystreamerpart1.R;
import com.danielto.example.spotifystreamerpart1.adapter.ArtistsListAdapter;
import com.danielto.example.spotifystreamerpart1.adapter.TopTracksAdapter;
import com.danielto.example.spotifystreamerpart1.fragment.FragmentArtistSearch;
import com.danielto.example.spotifystreamerpart1.fragment.FragmentMusicPlayer;
import com.danielto.example.spotifystreamerpart1.fragment.FragmentTopTracks;
import com.danielto.example.spotifystreamerpart1.utils.Constants;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class ActivitySpotifySearch extends AppCompatActivity implements ArtistsListAdapter.OnArtistClickListener, TopTracksAdapter.OnTrackClickListener, FragmentTopTracks.OnTrackListReceivedListener,
        FragmentMusicPlayer.OnNextTrackListener, FragmentMusicPlayer.OnPreviousTrackListener  {

    public static final int REQUEST_ARTIST_ID = 1;

    private boolean twoPane;
    private List<Track> tracks;
    private String artistId;
    private FragmentArtistSearch fragmentArtistSearch;
    private int trackPosition;
    private FragmentMusicPlayer fragmentMusicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoPane = (findViewById(R.id.fragment_top_tracks_container) != null ? true: false);

        // always inflate this!
        if (savedInstanceState == null) {
            fragmentArtistSearch = FragmentArtistSearch.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_search_container, fragmentArtistSearch, FragmentArtistSearch.FRAGMENT_ARTIST_SEARCH)
                    .commit();
        } else if (savedInstanceState != null) {
            artistId = savedInstanceState.getString(Constants.ARTIST_ID);
            if (artistId != null && !artistId.isEmpty() && !twoPane) {
                Intent intent = new Intent(this, ActivityMusicStreamer.class);
                intent.putExtra(Constants.ARTIST_ID, artistId);
                startActivityForResult(intent, REQUEST_ARTIST_ID);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ARTIST_ID) {
            if (resultCode == RESULT_OK) {
                artistId = data.getStringExtra(Constants.ARTIST_ID);
                if (twoPane) {
                    FragmentTopTracks fragmentTopTracks = FragmentTopTracks.newInstance(artistId);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_top_tracks_container, fragmentTopTracks, Constants.FRAGMENT_TOP_TRACKS)
                            .commit();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ARTIST_ID, artistId);
    }

    @Override
    public void onArtistClick(String artistId) {
        this.artistId = artistId;
        if (twoPane) {
            // switch out the search with the top tracks
            FragmentTopTracks fragmentTopTracks = FragmentTopTracks.newInstance(artistId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_top_tracks_container, fragmentTopTracks, Constants.FRAGMENT_TOP_TRACKS)
                    .commit();
        } else {
            Intent intent = new Intent(this, ActivityMusicStreamer.class);
            intent.putExtra(Constants.ARTIST_ID, artistId);
            startActivityForResult(intent, REQUEST_ARTIST_ID);
        }
    }

    @Override
    public void onTrackClick(int position, String artistName, String trackName, String albumName, String thumbnailUrl, String previewUrl) {
        if (twoPane) {
            // launch a dialog fragment for the controls
            fragmentMusicPlayer = FragmentMusicPlayer.newInstance(artistName, trackName, albumName, thumbnailUrl, previewUrl);
            fragmentMusicPlayer.show(getSupportFragmentManager().beginTransaction(), Constants.FRAGMENT_MUSIC_DIALOG);
        }
    }

    @Override
    public void updateTrackList(List<Track> trackList) {
        this.tracks = trackList;
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
