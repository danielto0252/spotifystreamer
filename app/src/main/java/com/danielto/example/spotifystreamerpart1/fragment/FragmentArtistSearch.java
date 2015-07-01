package com.danielto.example.spotifystreamerpart1.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danielto.example.spotifystreamerpart1.R;
import com.danielto.example.spotifystreamerpart1.adapter.ArtistsListAdapter;
import com.danielto.example.spotifystreamerpart1.utils.Constants;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class FragmentArtistSearch extends Fragment {

    public static final String FRAGMENT_ARTIST_SEARCH = "FRAGMENT_ARTIST_SEARCH";

    private View view;
    private ProgressBar progress;
    private ListView artistsListView;
    private EditText searchBar;
    private ArtistsListAdapter.OnArtistClickListener artistClickListener;
    private String artistName;
    private SpotifyArtistSearchTask spotifySearch;

    public static FragmentArtistSearch newInstance() {
        FragmentArtistSearch fragment = new FragmentArtistSearch();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.artist_search, container, false);
        artistsListView = (ListView) view.findViewById(R.id.artists_listview);
        searchBar = (EditText) view.findViewById(R.id.search_bar);
        progress = (ProgressBar) view.findViewById(R.id.artist_search_progress);

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    progress.setVisibility(View.VISIBLE);
                    artistsListView.setVisibility(View.GONE);

                    spotifySearch = new SpotifyArtistSearchTask();
                    artistName = searchBar.getText().toString();

                    spotifySearch.execute(artistName);

                    //dismiss keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                    searchBar.setText("");
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState != null) {
            artistName = savedInstanceState.getString(Constants.ARTIST_NAME);
            if (!artistName.isEmpty() && !artistName.equals("")) {
                new SpotifyArtistSearchTask().execute(artistName);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ARTIST_NAME, artistName);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (spotifySearch != null) spotifySearch.cancel(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        artistClickListener = (ArtistsListAdapter.OnArtistClickListener) activity;
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
            if (getActivity() != null) {
                if (artists.size() > 0) {
                    artistsListView.setAdapter(new ArtistsListAdapter(getActivity().getApplicationContext(), artists, artistClickListener));
                    artistsListView.invalidateViews();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No artist found... Try new search", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
