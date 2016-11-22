package com.rfinnigan.popular_movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Ruairidh Finnigan on 09/11/2016.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context The current context. Used to inflate the layout file.
     * @param movies  A List of Movie objects to display in a list
     */
    public MovieAdapter(Context context, ArrayList<Movie> movies) {

        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.

        super(context, 0, movies);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        Movie movie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView posterImage = (ImageView) convertView.findViewById(R.id.list_item_movie_imageView);


        String url = movie.getPosterUrl(3);//// TODO: 22/11/2016 enable user choice of poster size

        //Log.v(LOG_TAG, "Using Poster URL: " + url);


        posterImage.setAdjustViewBounds(true);

        Picasso.with(getContext()).load(url).into(posterImage);


        return convertView;

    }
}

