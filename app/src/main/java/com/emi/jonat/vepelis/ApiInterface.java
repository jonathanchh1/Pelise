package com.emi.jonat.vepelis;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jonat on 10/8/2017.
 */

public interface ApiInterface {
    @GET("movie/{sort_by}")
    Call<MovieResponse> getTopRatedMovies(@Path("sort_by") String mSortBy, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<Trailers> findTrailersById(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<Reviews> findReviewsById(@Path("id") int movieId, @Query("api_key") String apiKey);


}