package com.danielto.example.spotifystreamerpart1.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.danielto.example.spotifystreamerpart1.R;
import com.danielto.example.spotifystreamerpart1.adapter.TopTracksAdapter;
import com.danielto.example.spotifystreamerpart1.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

import static com.danielto.example.spotifystreamerpart1.adapter.TopTracksAdapter.OnTrackClickListener;

public class FragmentTopTracks extends Fragment {

    private View view;
    private ListView toptracksListView;
    private ProgressBar progress;
    private String artistId;
    private OnTrackListReceivedListener trackListReceivedListener;
    private OnTrackClickListener trackClickListener;
    private ArtistTopTracksTask topTracksTask;

    public interface OnTrackListReceivedListener {
        void updateTrackList(List<Track> trackList);
    }

    public static FragmentTopTracks newInstance(String artistId) {
        Bundle b = new Bundle();
        b.putString(Constants.ARTIST_ID, artistId);
        FragmentTopTracks fragment = new FragmentTopTracks();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        trackListReceivedListener = (OnTrackListReceivedListener) activity;
        trackClickListener = (OnTrackClickListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.top_tracks, container, false);
        toptracksListView = (ListView) view.findViewById(R.id.top_tracks_listview);
        progress = (ProgressBar) view.findViewById(R.id.top_track_progress);

        if (savedInstanceState != null) {
            artistId = savedInstanceState.getString(Constants.ARTIST_ID);
        } else {
            artistId = getArguments().getString(Constants.ARTIST_ID);
        }

        topTracksTask = new ArtistTopTracksTask();
        if (artistId == null || artistId.isEmpty() || artistId.equals("")) {
            // toast an error
            Toast.makeText(getActivity().getApplicationContext(), "Top tracks not found...", Toast.LENGTH_SHORT).show();
        } else {
            progress.setVisibility(View.VISIBLE);
            topTracksTask.execute(artistId);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(artistId, Constants.ARTIST_ID);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (topTracksTask != null) topTracksTask.cancel(true);
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
            return tracks;
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            progress.setVisibility(View.GONE);
            toptracksListView.setVisibility(View.VISIBLE);

            // set adapter
            List<Track> trackList = tracks.tracks;
            if (getActivity() != null) {
                if (trackList.size() > 0) {
                    trackListReceivedListener.updateTrackList(trackList);
                    toptracksListView.setAdapter(new TopTracksAdapter(getActivity().getApplicationContext(), trackList, trackClickListener));
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Top tracks not found...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
