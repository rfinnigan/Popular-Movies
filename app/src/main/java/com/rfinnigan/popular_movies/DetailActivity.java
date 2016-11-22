/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rfinnigan.popular_movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    //simple method to set action bar title from fragments
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * A  fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {


        private final String LOG_TAG = DetailFragment.class.getSimpleName();
        private Movie mMovie;


        public DetailFragment() {
            setHasOptionsMenu(true);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


            //get the intent passed through to launch the activity
            Intent intent = getActivity().getIntent();

            //check the intent exists and has the correct extras
            if (intent != null && intent.hasExtra(MovieListFragment.EXTRA_MOVIE)) {

                //extract the Movie object from the intent
                mMovie = intent.getParcelableExtra(MovieListFragment.EXTRA_MOVIE);

                //set the texts in the appropriate textViews
                ((TextView) rootView.findViewById(R.id.text_detail_title)).setText(mMovie.getTitle());
                ((TextView) rootView.findViewById(R.id.text_detail_releasedate)).setText("Released: " + getFormattedDate(mMovie.getReleaseDay(), mMovie.getReleaseMonth(), mMovie.getReleaseYear()));
                ((TextView) rootView.findViewById(R.id.text_detail_rating)).setText("Rating: " + mMovie.getRating() + " (" + mMovie.getVote_count() + ")");
                ((TextView) rootView.findViewById(R.id.text_detail_synopsis)).setText(mMovie.getOverview());

                //set the poster image
                ImageView posterImage = (ImageView) rootView.findViewById(R.id.image_detail_poster);
                Picasso.with(rootView.getContext()).load(mMovie.getPosterUrl(3)).into(posterImage); //// TODO: 22/11/2016 have user choose poster size

                //set the action bar title of the Activity
                //takes form "Title (Year of Release)"
                String title = mMovie.getTitle() + " (" + mMovie.getReleaseYear() + ")";
                ((DetailActivity) getActivity()).setActionBarTitle(title);


            }

            return rootView;
        }

        private String getFormattedDate(String day, String month, String year) {
            int monthInt = Integer.parseInt(month);
            String monthAbreviation = getResources().getStringArray(R.array.months)[monthInt];
            return day + " - " + monthAbreviation + " - " + year;
        }


    }
}