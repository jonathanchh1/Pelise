package com.emi.jonat.vepelis.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jonat on 10/16/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "favs.db";
    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 3;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create database table here
        final String SQL_CREATE_FAV_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ( " +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL " + ");";

        //gotta do logging
        Log.d(LOG_TAG, SQL_CREATE_FAV_TABLE);

        db.execSQL(SQL_CREATE_FAV_TABLE);

        Log.d(LOG_TAG, "all tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //this will be invoked when we will change DATABASE_VERSION aka schema of database.
        // if we upgrade schema user will lost his fav. collection
        //comment this out if you don't want this to happen
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}


