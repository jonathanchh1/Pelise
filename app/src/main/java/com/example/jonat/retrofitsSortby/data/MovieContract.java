package com.example.jonat.retrofitsSortby.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jonat on 10/16/2017.
 */

public class MovieContract {
    //it should be unique in system,we use package name because it is unique
    public static final String CONTENT_AUTHORITY = "com.example.jonat.retrofitsSortby.data";

    //base URI for content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //URI end points for Content provider
    public static final String PATH_FAV = "movies";


    //for favorites
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV).build();

        //these are MIME types ,not really but they are similar to MIME types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;

        // Table name
        public static final String TABLE_NAME = "movies";
        //TMDB Movie id ; we will need this reviews and Trailer

        public static final String COLUMN_MOVIE_ID = "id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";




        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_RELEASE_DATE = 3;
    public static final int COL_POSTER_PATH = 4;
    public static final int COL_VOTE_AVERAGE = 5;
    public static final int COL_OVERVIEW = 6;
    public static final int COL_BACKDROP = 7;
    public static final int COL_ORIGIN = 8;
    public static final int COL_VOTE_COUNT = 9;
    public static final int COL_ORIGINAL_LANGUAGE = 10;
}