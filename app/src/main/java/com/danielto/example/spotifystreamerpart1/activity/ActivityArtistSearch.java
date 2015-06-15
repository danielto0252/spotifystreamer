package com.danielto.example.spotifystreamerpart1.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danielto.example.spotifystreamerpart1.R;
import com.danielto.example.spotifystreamerpart1.adapter.ArtistsListAdapter;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


public class ActivityArtistSearch extends ActionBarActivity implements ArtistsListAdapter.OnArtistClickListener {

    private ListView artistsListView;
    private EditText searchBar;
    private Button searchBtn;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        artistsListView = (ListView) findViewById(R.id.artists_listview);
        searchBar = (EditText) findViewById(R.id.search_bar);
        progress = (ProgressBar) findViewById(R.id.artist_search_progress);

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    progress.setVisibility(View.VISIBLE);
                    artistsListView.setVisibility(View.GONE);

                    SpotifyArtistSearchTask spotifySearch = new SpotifyArtistSearchTask();
                    spotifySearch.execute(searchBar.getText().toString());

                    //dismiss keyboard
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                    searchBar.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onArtistClick(String artistId) {
        Intent intent = new Intent(this, ActivityTopTracks.class);
        intent.putExtra(ActivityTopTracks.ARTIST_ID, artistId);
        startActivity(intent);
    }

    private class SpotifyArtistSearchTask extends AsyncTask<String, Void, ArtistsPager> {

        @Override
        protected ArtistsPager doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            ArtistsPager artistsPager = spotify.searchArtists(params[0]);
            return artistsPager;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            artistsListView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);

            List<Artist> artists = artistsPager.artists.items;
            if (artists.size() > 0) {
                artistsListView.setAdapter(new ArtistsListAdapter(getApplicationContext(), artists, ActivityArtistSearch.this));
            } else {
                Toast.makeText(getApplicationContext(), "No artist found... Try new search", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
