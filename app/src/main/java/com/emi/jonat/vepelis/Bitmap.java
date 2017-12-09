package com.emi.jonat.vepelis;

/**
 * Created by jonat on 10/8/2017.
 */

public class Bitmap {
    public static String buildPosterUrl(String PosterPath) {
        //use recommended w185 size for image
        return "http://image.tmdb.org/t/p/w185" + PosterPath;
    }

    public static String buildBackdropUrl(String Backdrop) {
        return "https://image.tmdb.org/t/p/original" + Backdrop;
    }
}
