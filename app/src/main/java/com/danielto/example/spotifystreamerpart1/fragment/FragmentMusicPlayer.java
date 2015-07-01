package com.danielto.example.spotifystreamerpart1.fragment;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.danielto.example.spotifystreamerpart1.R;
import com.danielto.example.spotifystreamerpart1.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class FragmentMusicPlayer extends DialogFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private String artistName;
    private String trackName;
    private String albumName;
    private String thumbnailUrl;
    private String previewUrl;

    private TextView trackAristTextView;
    private TextView trackAlbumTextView;
    private ImageView trackArtImageView;
    private TextView trackNameTextView;
    private TextView totalTimeTextView;
    private TextView elapsedTimeTextView;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private ImageButton skipBackButton;
    private ImageButton skipForwardButton;
    private ImageButton playPauseButton;
    private OnNextTrackListener nextTrackListener;
    private OnPreviousTrackListener previousTrackListener;
    private View view;
    private Handler handler;
    private Runnable runnable;

    public interface OnNextTrackListener {
        void onNextTrackClicked();
    }

    public interface OnPreviousTrackListener {
        void onPreviousTrackClicked();
    }

    public static FragmentMusicPlayer newInstance(String artistName, String trackName, String albumName, String thumbnailUrl, String previewUrl) {
        FragmentMusicPlayer fragment = new FragmentMusicPlayer();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARTIST_NAME, artistName);
        bundle.putString(Constants.TRACK_NAME, trackName);
        bundle.putString(Constants.ALBUM_NAME, albumName);
        bundle.putString(Constants.THUMBNAIL_URL, thumbnailUrl);
        bundle.putString(Constants.PREVIEW_URL, previewUrl);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (getShowsDialog()) getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        if (savedInstanceState == null) {
            artistName = bundle.getString(Constants.ARTIST_NAME);
            trackName = bundle.getString(Constants.TRACK_NAME);
            albumName = bundle.getString(Constants.ALBUM_NAME);
            thumbnailUrl = bundle.getString(Constants.THUMBNAIL_URL);
            previewUrl = bundle.getString(Constants.PREVIEW_URL);
        } else {
            artistName = savedInstanceState.getString(Constants.ARTIST_NAME);
            trackName = savedInstanceState.getString(Constants.TRACK_NAME);
            albumName = savedInstanceState.getString(Constants.ALBUM_NAME);
            thumbnailUrl = savedInstanceState.getString(Constants.THUMBNAIL_URL);
            previewUrl = savedInstanceState.getString(Constants.PREVIEW_URL);
        }

        view = inflater.inflate(R.layout.music_player, container, false);
        trackAristTextView = (TextView) view.findViewById(R.id.player_track_artist_text);
        trackAlbumTextView = (TextView) view.findViewById(R.id.player_track_album_text);
        trackArtImageView = (ImageView) view.findViewById(R.id.player_track_art);
        trackNameTextView = (TextView) view.findViewById(R.id.player_track_name);
        elapsedTimeTextView = (TextView) view.findViewById(R.id.elapsed_time);
        totalTimeTextView = (TextView) view.findViewById(R.id.total_time);
        skipBackButton = (ImageButton) view.findViewById(R.id.skip_back_button);
        playPauseButton = (ImageButton) view.findViewById(R.id.play_pause_button);
        skipForwardButton = (ImageButton) view.findViewById(R.id.skip_forward_button);
        seekBar = (SeekBar) view.findViewById(R.id.player_seekbar);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setPlayerData();
        setupPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        resetPlayer();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        nextTrackListener = (OnNextTrackListener) activity;
        previousTrackListener = (OnPreviousTrackListener) activity;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (getActivity() != null) {
            playPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            mp.start();
            updateProgressBar();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
    }

    public void updatePlayerData(String artistName, String trackName, String albumName, String thumbnailUrl, String previewUrl) {
        this.artistName = artistName;
        this.trackName = trackName;
        this.albumName = albumName;
        this.thumbnailUrl = thumbnailUrl;
        this.previewUrl = previewUrl;

        resetPlayer();
    }

    public void setPlayerData() {
        trackAristTextView.setText(artistName);
        trackNameTextView.setText(trackName);
        trackAlbumTextView.setText(albumName);

        if (thumbnailUrl != null && !thumbnailUrl.equals("")) {
            Picasso.with(getActivity().getApplicationContext())
                    .load(thumbnailUrl)
                    .into(trackArtImageView);
        }

        seekBar.setMax(30000);
        elapsedTimeTextView.setText("0:00");
        skipForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTrackListener.onNextTrackClicked();
            }
        });

        skipBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousTrackListener.onPreviousTrackClicked();
            }
        });
    }

    public void setupPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(previewUrl);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(), "Could not find song file", Toast.LENGTH_SHORT).show();
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        totalTimeTextView.setText("0:30");
    }


    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
            } else {
                mediaPlayer.start();
                updateProgressBar();
                playPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            }
        }
    }

    private void updateProgressBar() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = (mediaPlayer.getCurrentPosition() / 1000) + 1;
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    elapsedTimeTextView.setText("0:" + (mCurrentPosition < 10 ? "0" : "") + mCurrentPosition);
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.post(runnable);
    }

    private void resetPlayer() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
