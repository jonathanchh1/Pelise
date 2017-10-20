package com.example.jonat.retrofitsSortby;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jonat on 10/8/2017.
 */

public interface ApiInterface {
    @GET("movie/{sort_by}")
    Call<MovieResponse> getTopRatedMovies( @Path("sort_by") String mSortBy, @Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<MovieResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);


}