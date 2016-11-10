package com.rfinnigan.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.rfinnigan.popular_movies.R.menu.movielistfragment;

/**
 * Created by Ruairidh Finnigan on 07/11/2016.
 */

public class MovieListFragment extends Fragment {
    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;

    private String sortMethod;

    public MovieListFragment() {
    }

    @Override
    public void onStart() {
        updateMovies();
        super.onStart();

    }

    @Override
    public void onResume() {
        updateMovies();
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        sortMethod = getString(R.string.sort_popular);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //the arrayadapter will take data from a source
        //and use it to populate the ListView it's attached to
        mMovieAdapter = new MovieAdapter(
                getActivity(), //the current context
                new ArrayList<Movie>()); //new empty arraylist


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //get a reference to the gridView and attach this adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                //declare the context and duration of the intent message
                Context context = getActivity();
                Movie movie = mMovieAdapter.getItem(i);

                //declare the intent to launch DetailActivity and start the activity
                Intent detailIntent = new Intent(context, DetailActivity.class);

                //add movie ID at position clicked
                // we can rebuild the Movie object from this in the detail activity
                // when we query TMDB for the extra info
                detailIntent.putExtra(Intent.EXTRA_TEXT, movie.getId());
                startActivity(detailIntent);
            }

        });

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
        } else if (id == R.id.action_sort_popular) {
            if (sortMethod != getString(R.string.sort_popular)) {
                item.setChecked(true);
                sortMethod = getString(R.string.sort_popular);
                updateMovies();

            }
        } else if (id == R.id.action_sort_toprated) {
            if (sortMethod != getString(R.string.sort_toprated)) {
                item.setChecked(true);
                sortMethod = getString(R.string.sort_toprated);
                updateMovies();

            }
        }


        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {

        if (!mMovieAdapter.isEmpty()) {
            mMovieAdapter.clear();
        }

        FetchMoviesTask moviesTask = new FetchMoviesTask();


        moviesTask.execute(sortMethod);

    }

    public class FetchMoviesTask extends TMDBFetchTask<String, Void, String> {


        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        //variables used to build URL
        private String language;
        private String page;//TODO allow changing the page
        private String sorting;

        @Override
        protected URL buildUrl(String[] param) throws MalformedURLException {

            // Construct the URL for the theMovieDB query
            setURLVariables(param);

            final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie";
            final String APIKEY_PARAM = "api_key";
            final String LANGUAGE_PARAM = "mode";
            final String PAGE_PARAM = "page";

            String apiKey = getTMDBApiKey(getActivity());

            Uri builtUri = Uri.parse(MOVIES_BASE_URL);
            builtUri = builtUri.buildUpon().appendPath(sorting)
                    .appendQueryParameter(APIKEY_PARAM, apiKey)
                    .appendQueryParameter(LANGUAGE_PARAM, language)
                    .appendQueryParameter(PAGE_PARAM, page)
                    .build();

            return new URL(builtUri.toString());
        }

        //overwritten method to set the variables for the URL
        //@Override
        protected void setURLVariables(String[] param) {

            language = "en-UK";

            //TODO allow changing the page
            page = "1";

            //declare how the movies will be sorted and check that it is a valid sort method
            sorting = getString(R.string.sort_popular);
            if (param[0].equals(getString(R.string.sort_toprated))) {
                sorting = param[0];
            } else if (!param.equals(getString(R.string.sort_popular))) {
                Log.d(LOG_TAG, "Unknown sorting method using " + sorting);
            }

        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                mMovieAdapter.clear();
                try {
                    Movie[] movies = getMoviesDataFromJson(result);
                    for (Movie movie : movies) {
                        //TODO more efficient to use addall for OS after Honeycomb

                        mMovieAdapter.add(movie);
                        //Log.v(LOG_TAG, "added result "+ movie.getTitle() + " to adapter");
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error ", e);
                }

                // New data is back from the server.  Hooray!
            } else if (result == null) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_no_data_retrieved), Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed
         */
        private Movie[] getMoviesDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "title";
            final String TMDB_POSTERPATH = "poster_path";
            final String TMDB_ID = "id";
            final String TMDB_OVERVIEW ="overview";
            final String TMDB_RATING = "vote_average";
            final String TMDB_VOTE_COUNT = "vote_count";
            final String TMDB_RELEASE_DATE = "release_date";


            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            /* TMDB returns Movies sorted either by rating or popularity depending on the query
            It returns 20 per page
            */

            Movie[] resultMovies = new Movie[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String title;
                String posterPath;
                String id;

                // Get the JSON object representing the Movie
                JSONObject movieDetails = moviesArray.getJSONObject(i);

                // get strings from object

                title = movieDetails.getString(TMDB_TITLE);
                posterPath = movieDetails.getString(TMDB_POSTERPATH).substring(1);//the API returns the poster path preceeded by a "/" we ignore this first character
                id = movieDetails.getString(TMDB_ID);


                resultMovies[i] = new Movie(title, id, posterPath);
                //Log.v(LOG_TAG, "Movie " + i + ": " + resultMovies[i].getTitle() + resultMovies[i].getPoster() );
            }


            return resultMovies;

        }


    }
}

