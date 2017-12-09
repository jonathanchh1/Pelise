package com.emi.jonat.vepelis;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jonat on 10/5/2016.
 */
public class FetchReviewsTask extends AsyncTask<Integer, Void, ArrayList<Review>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private final Listener mListener;

    public FetchReviewsTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected ArrayList<Review> doInBackground(Integer... params) {
        // If there's no movie id, there's nothing to look up.
        if (params.length == 0) {
            return null;
        }
        int movieId = params[0];

        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
        Call<Reviews> call = service.findReviewsById(movieId,
                BuildConfig.MOVIE_API);
        try {
            Response<Reviews> response = call.execute();
            Reviews reviews = response.body();
            return reviews.getReviews();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Review> reviews) {
        if (reviews != null) {
            mListener.onReviewsFetchFinished(reviews);
        } else {
            mListener.onReviewsFetchFinished(new ArrayList<Review>());
        }
    }

    /**
     * Interface definition for a callback to be invoked when reviews are loaded.
     */
    interface Listener {
        void onReviewsFetchFinished(ArrayList<Review> reviews);
    }
}