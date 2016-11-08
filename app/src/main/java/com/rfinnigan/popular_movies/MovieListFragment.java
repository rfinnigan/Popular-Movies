package com.rfinnigan.popular_movies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.rfinnigan.popular_movies.R.menu.movielistfragment;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(movielistfragment, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            updateMovies();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        mMovieAdapter.clear();
        for (int i = 0; i < 10; i++) {
            mMovieAdapter.add("Movie " + i);

        }

        FetchMoviesTask moviesTask = new FetchMoviesTask();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String apiUserKey = sharedPref.getString(getString(R.string.pref_api_key), getString(R.string.pref_api_default));


        moviesTask.execute(apiUserKey);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {


        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            if (params.length == 0) {
                //if no parameters passed in dont do anything
                return null;
            }


            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            //TODO delete this
            //full query
            //https://api.themoviedb.org/3/movie/popular?api_key=################&language=en-UK


            //declare parameters of query
            String apiKey = params[0]; //TODO before uploading to play store have this read from somewhere, not a user preference
            String language = "en-UK";

            //declare how the movies will be sorted
            //TODO allow using top_rated as sort path and error check
            String sorting = "popular";

            try {

                // Construct the URL for the theMovieDB query


                final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie";



                final String LANGUAGE_PARAM = "mode";
                final String APIKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL);
                builtUri = builtUri.buildUpon().appendPath(sorting)
                        .appendQueryParameter(APIKEY_PARAM, apiKey)
                        .appendQueryParameter(LANGUAGE_PARAM, language)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG,"Using URL: "+ url);


                // Create the request to TheMovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.d(LOG_TAG, "input stream is null");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");

                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.d(LOG_TAG, "stream was empty");
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG,"retrieved JSON String: "+ moviesJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
                /*String[] forecasts = new String[numDays];
                try {
                    forecasts = getWeatherDataFromJson(moviesJsonStr, numDays);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error ", e);
                }
                */

                //TODO make this actually return something
                return null;
            }
        }





    }
}

