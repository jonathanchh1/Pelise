package com.emi.jonat.vepelis.Services;

import android.os.AsyncTask;
import android.util.Log;

import com.emi.jonat.vepelis.BuildConfig;
import com.emi.jonat.vepelis.Model.Trailer;
import com.emi.jonat.vepelis.Model.Trailers;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jonat on 10/5/2016.
 */
public class FetchTrailersTask extends AsyncTask<Integer, Void, ArrayList<Trailer>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private final Listener mListener;

    public FetchTrailersTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected ArrayList<Trailer> doInBackground(Integer... params) {
        // If there's no movie id, there's nothing to look up.
        if (params.length == 0) {
            return null;
        }
        int movieId = params[0];

        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
        Call<Trailers> call = service.findTrailersById(movieId,
                BuildConfig.MOVIE_API);
        try {
            Response<Trailers> response = call.execute();
            Trailers trailers = response.body();
            return trailers.getTrailers();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Trailer> trailers) {
        if (trailers != null) {
            mListener.onFetchFinished(trailers);
        } else {
            mListener.onFetchFinished(new ArrayList<Trailer>());
        }
    }

    /**
     * Interface definition for a callback to be invoked when trailers are loaded.
     */
    public interface Listener {
        void onFetchFinished(ArrayList<Trailer> trailers);
    }
}
