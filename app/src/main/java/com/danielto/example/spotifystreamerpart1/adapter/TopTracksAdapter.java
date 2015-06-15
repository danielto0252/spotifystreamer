package com.danielto.example.spotifystreamerpart1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielto.example.spotifystreamerpart1.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class TopTracksAdapter extends BaseAdapter {
    // ONLY DISPLAY TOP 10!

    private LayoutInflater inflater;
    private Context context;
    private List<Track> tracks;
    private OnTrackClickListener trackClickListener;

    public interface OnTrackClickListener {
        void onTrackClick(String trackName, String albumName, String thumbnailUrl, String previewUrl);
    }

    public TopTracksAdapter(Context context, List<Track> trackList, OnTrackClickListener trackClickListener) {
        this.context = context;
        this.tracks = (trackList.size() > 10 ? trackList.subList(0, 10) : trackList);
        this.inflater = LayoutInflater.from(context);
        this.trackClickListener = trackClickListener;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Track getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TopTrackViewHolder viewHolder;
        final Track track = getItem(position);
        final List<Image> albumImages = track.album.images;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.top_track_item, parent, false);

            viewHolder = new TopTrackViewHolder();
            viewHolder.albumImage = (ImageView) convertView.findViewById(R.id.track_album_image);
            viewHolder.trackName= (TextView) convertView.findViewById(R.id.track_title);
            viewHolder.trackAlbum = (TextView) convertView.findViewById(R.id.track_album);
            viewHolder.container = (ViewGroup) convertView.findViewById(R.id.top_track_view_container);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TopTrackViewHolder) convertView.getTag();
        }


        viewHolder.trackAlbum.setText(track.album.name);
        viewHolder.trackName.setText(track.name);
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackClickListener.onTrackClick(track.name, track.album.name, albumImages.get(0).url, track.preview_url);
            }
        });

        if (!albumImages.isEmpty()) {
            Picasso.with(context)
                    .load(albumImages.get(albumImages.size() - 2).url)
                    .into(viewHolder.albumImage);
        } else {
            // use random color for thumbnail
        }

        return convertView;
    }

    private static class TopTrackViewHolder {
        ImageView albumImage;
        TextView trackName;
        TextView trackAlbum;
        ViewGroup container;
    }
}
