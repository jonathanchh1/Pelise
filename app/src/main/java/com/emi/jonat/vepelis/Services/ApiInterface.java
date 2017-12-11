package com.emi.jonat.vepelis.Services;

import com.emi.jonat.vepelis.Model.Reviews;
import com.emi.jonat.vepelis.Model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jonat on 10/8/2017.
 */

public interface ApiInterface {

    @GET("movie/{sort_by}")
    Call<MovieResponse> getMoviesPages(@Path("sort_by") String mSortBy, @Query("page") int pageIndex, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<Trailers> findTrailersById(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<Reviews> findReviewsById(@Path("id") int movieId, @Query("api_key") String apiKey);


}