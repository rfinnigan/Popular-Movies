package com.rfinnigan.popular_movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Ruairidh Finnigan on 10/11/2016.
 * Abstract class to fetch a JSON from TMDB
 * Any processing of JSON should be done in postExecute
 */

public abstract class TMDBFetchTask <Params,Progress, Result> extends AsyncTask <Params, Progress, String>{

    protected final String LOG_TAG = TMDBFetchTask.class.getSimpleName();

    @Override
    protected String doInBackground(Params... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        if (params.length == 0) {
            //if no parameters passed in dont do anything
            return null;
        }


        // Will contain the raw JSON response as a string.
        String jsonStr = null;





        //setURLVariables(params);

        try {
            URL url = buildUrl(params);

            //Log.v(LOG_TAG, "Using URL: " + url);


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
            jsonStr = buffer.toString();
            //Log.v(LOG_TAG, "retrieved JSON String: " + jsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the data, there's no point in attemping
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


            if (jsonStr != null) {
                return jsonStr;

            } else {
                Log.e(LOG_TAG, "failed to get JSON, is the API Key Correctly set?");
                return null;
            }


        }
    }

    protected String getTMDBApiKey(Context context) {
        //declare parameters of query getting API from shared prefs
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        //TODO before uploading to play store have this read from somewhere, not a user preference (particularly as this is now located in an abstract class)
        return sharedPref.getString(context.getString(R.string.pref_api_key), context.getString(R.string.pref_api_default));
    }
    //should be overwritten to set the appropriate variables given the inpur params
    //protected abstract void setURLVariables(String[] param);

    //should be overwritten to build URL from given params
    protected abstract URL buildUrl(Params[] param) throws MalformedURLException;
}
