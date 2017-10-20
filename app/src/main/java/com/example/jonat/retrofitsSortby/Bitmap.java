package com.example.jonat.retrofitsSortby;

/**
 * Created by jonat on 10/8/2017.
 */

public class Bitmap {
    public static String buildPosterUrl(String PosterPath) {
        //use recommended w185 size for image
        return "http://image.tmdb.org/t/p/w185" + PosterPath;
    }
}
