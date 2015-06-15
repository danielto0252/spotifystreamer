package com.danielto.example.spotifystreamerpart1.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielto.example.spotifystreamerpart1.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

public class ArtistsListAdapter extends BaseAdapter {


    private OnArtistClickListener artistClickListener;
    private LayoutInflater inflater;
    private Context context;
    private List<Artist> artists;

    public interface OnArtistClickListener {
        void onArtistClick(String artistId);
    }

    public ArtistsListAdapter(Context context, List<Artist> artists, OnArtistClickListener artistClickListener) {
        this.context = context;
        this.artists = artists;
        this.inflater = LayoutInflater.from(context);
        this.artistClickListener = artistClickListener;
    }

    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Artist getItem(int position) {
        return artists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArtistViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.artist_item, parent, false);

            viewHolder = new ArtistViewHolder();
            viewHolder.artistImage = (ImageView) convertView.findViewById(R.id.artist_image);
            viewHolder.artistName = (TextView) convertView.findViewById(R.id.artist_name);
            viewHolder.container = (ViewGroup) convertView.findViewById(R.id.artist_item_view_container);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ArtistViewHolder) convertView.getTag();
        }

        final Artist artist = getItem(position);
        viewHolder.artistName.setText(artist.name);
        List<Image> albumImages = artist.images;
        viewHolder.artistImage.setImageDrawable(null);

        // need external click listener to call the activity artst search
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artistClickListener.onArtistClick(artist.id);
            }
        });

        if (!albumImages.isEmpty()) {
            Picasso.with(context)
                    .load(albumImages.get(albumImages.size() - 2).url)
                    .into(viewHolder.artistImage);
        } else {
            Drawable placeholder = context.getResources().getDrawable(R.drawable.empty_placeholder);
            viewHolder.artistImage.setBackgroundColor(getRandomColor());
        }

        return convertView;
    }

    private int getRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

        return Color.rgb(r, g, b);
    }

    private static class ArtistViewHolder {
        ViewGroup container;
        ImageView artistImage;
        TextView artistName;
        View placeholder;
    }
}
