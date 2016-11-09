package com.rfinnigan.popular_movies;


/**
 * Created by Ruairidh Finnigan on 09/11/2016.
 * basic class to hold info about Movie
 */

public class Movie {
    String title;
    String id;
    String poster;

    public Movie(String mTitle,String mId, String mPoster){
        this.title=mTitle;
        this.id=mId;
        this.poster=mPoster;
    }
}
