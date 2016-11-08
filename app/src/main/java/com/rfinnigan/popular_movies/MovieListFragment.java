package com.rfinnigan.popular_movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Ruairidh Finnigan on 07/11/2016.
 */

public class MovieListFragment extends Fragment {
    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private ArrayAdapter<String> mMovieAdapter;

    public MovieListFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //the arrayadapter will take data from a source
        //and use it to populate the ListView it's attached to
        mMovieAdapter = new ArrayAdapter(
                getActivity(), //the current context
                R.layout.list_item_movie, //the name of the layout ID
                R.id.list_item_movie_textview, // the ID of the textview to populate
                new ArrayList<String>()); //new empty arraylist


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //get a reference to the listView and attach this adapter to it
        ListView listView = (ListView) rootView.findViewById(R.id.listview_movies);
        listView.setAdapter(mMovieAdapter);


        return rootView;
    }

    private void updateMovies() {
        mMovieAdapter.clear();
        for (int i = 0; i < 10;i++) {
            mMovieAdapter.add("Movie "+ i);

        }
    }
}

