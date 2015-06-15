package com.danielto.example.spotifystreamerpart1.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.danielto.example.spotifystreamerpart1.R;
import com.danielto.example.spotifystreamerpart1.adapter.TopTracksAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class ActivityTopTracks extends ActionBarActivity implements TopTracksAdapter.OnTrackClickListener{

    public static final String ARTIST_ID = "ARTIST_ID";
    private ListView toptracksListView;
    private ProgressBar progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);
        toptracksListView = (ListView) findViewById(R.id.top_tracks_listview);
        progress = (ProgressBar) findViewById(R.id.top_track_progress);
        String artistId = getIntent().getStringExtra(ARTIST_ID);

        ArtistTopTracksTask topTracksTask = new ArtistTopTracksTask();
        if (artistId == null || artistId.isEmpty() || artistId.equals("")) {
            // toast an error
            Toast.makeText(getApplicationContext(), "Top tracks not found...", Toast.LENGTH_SHORT).show();
        } else {
            progress.setVisibility(View.VISIBLE);
            topTracksTask.execute(artistId);
        }
    }

    @Override
    public void onTrackClick(String trackName, String albumName, String thumbnailUrl, String previewUrl) {
        // TODO send intent to stream item!
        Log.d("Daniel", "Trackname: " + trackName);
        Log.d("Daniel", "Album Name: " + albumName);
        Log.d("Daniel", "Thumbnail:  " + thumbnailUrl);
        Log.d("Daniel", "preview: " + previewUrl);
    }

    private class ArtistTopTracksTask extends AsyncTask<String, Void, Tracks> {

        @Override
        protected Tracks doInBackground(String... params) {
            String id = params[0];
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            Map<String, Object> urlParams = new HashMap<>();
            urlParams.put("country", "US");
            Tracks tracks = spotify.getArtistTopTrack(id, urlParams);
            int x = 2;
            return tracks;
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            progress.setVisibility(View.GONE);
            toptracksListView.setVisibility(View.VISIBLE);

            // set adapter
            List<Track> trackList = tracks.tracks;
            if (trackList.size() > 0) {
                toptracksListView.setAdapter(new TopTracksAdapter(getApplicationContext(), trackList, ActivityTopTracks.this));
            } else {
                Toast.makeText(getApplicationContext(), "Top tracks not found...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
