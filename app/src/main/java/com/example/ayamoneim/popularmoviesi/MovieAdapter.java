package com.example.ayamoneim.popularmoviesi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ayamoneim on 25/06/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    private Context context;
    private LayoutInflater inflater;

    private ArrayList<Movie> movies;

    public MovieAdapter(Context context, ArrayList<Movie>movies) {
        super(context, R.layout.grid_item, movies);

        this.context = context;
        this.movies = movies;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
        }

        Picasso
                .with(context)
                .load(movies.get(position).getPosterPath())
                .fit()
                .into((ImageView) convertView);

        return convertView;
    }


    public Context getContext() {
        return context;
    }

    public void add(Movie object) {
        synchronized (Movie.class) { // Critical Section
            movies.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (Movie.class) { // Critical Section
            movies.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(ArrayList<Movie> data) {
        clear();
        for (Movie movie : data) {
            add(movie);
        }
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}