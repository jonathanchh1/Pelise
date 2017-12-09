package com.emi.jonat.vepelis;

import android.content.Context;
import android.database.Cursor;

import com.emi.jonat.vepelis.data.MovieContract;

/**
 * Created by jonat on 10/16/2017.
 */

public class Query {
    //takes movie_id and tells whether or not that movie is favored
    public static int isFavorited(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", // selection
                new String[]{Integer.toString(id)},   // selectionArgs
                null    // sort order
        );
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }
}
