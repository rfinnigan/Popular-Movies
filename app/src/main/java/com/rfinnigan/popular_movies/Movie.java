package com.rfinnigan.popular_movies;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ruairidh Finnigan on 09/11/2016.
 * Basic class to hold info about Movie
 */

public class Movie implements Parcelable {
    private String title;
    private String id;
    private String poster;
    private String releaseDate;
    private String overview;
    private String rating;
    private String vote_count;

    private final String POSTERS_BASE_URL = "http://image.tmdb.org/t/p/";

    private final String[] POSTER_SIZES = {"w92", "w154", "w185", "w342", "w500", "w780"};

    public Movie(String title, String id, String poster, String releaseDate, String overview, String rating, String vote_count) {
        this.title = title;
        this.id = id;
        this.poster = poster;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.rating = rating;
        this.vote_count = vote_count;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(id);
        out.writeString(poster);
        out.writeString(releaseDate);
        out.writeString(overview);
        out.writeString(rating);
        out.writeString(vote_count);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        title = in.readString();
        id = in.readString();
        poster = in.readString();
        releaseDate = in.readString();
        overview = in.readString();
        rating = in.readString();
        vote_count = in.readString();
    }


    //Getter Methods
    public String getFullReleaseDate() {
        return releaseDate;
    }

    public String getReleaseYear() {
        return releaseDate.substring(0, 4);
    }

    public String getReleaseDay() {
        return releaseDate.substring(8);
    }

    public String getReleaseMonth() {
        return releaseDate.substring(5, 7);


    }


    public String getOverview() {
        return overview;
    }

    public String getRating() {
        return rating;
    }

    public String getVote_count() {
        return vote_count;
    }

    public String getId() {

        return id;
    }

    public String getPoster() {

        return poster;
    }

    public String getTitle() {
        return title;
    }

    /*Method to return the full Poster URL
    @param size int between 0 & 5 specifying size of poster image
     */
    public String getPosterUrl(int size) {

        // check if size is within the allowed range
        if (size < 0 || size > POSTER_SIZES.length) {
            size = 3;
        }
        String sizePath = POSTER_SIZES[size]; //TODO enable user choice of size and passing size as string


        Uri builtUri = Uri.parse(POSTERS_BASE_URL);
        builtUri = builtUri.buildUpon().appendPath(sizePath)
                .appendPath(poster)
                .build();


        String url = builtUri.toString();

        return url;
    }
}
