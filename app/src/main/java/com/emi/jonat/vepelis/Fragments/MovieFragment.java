package com.emi.jonat.vepelis.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emi.jonat.vepelis.BuildConfig;
import com.emi.jonat.vepelis.Services.DetailActivity;
import com.emi.jonat.vepelis.Model.Movie;
import com.emi.jonat.vepelis.Adapters.MovieAdapter;
import com.emi.jonat.vepelis.Services.MovieResponse;
import com.emi.jonat.vepelis.R;
import com.emi.jonat.vepelis.Services.ApiClient;
import com.emi.jonat.vepelis.Services.ApiInterface;
import com.emi.jonat.vepelis.data.MovieContract;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jonat on 10/29/2017.
 */

public class MovieFragment extends Fragment {

    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    private final static String FAVORITE = "favorite";
    private static final String STORED_KEY = "choice";
    private static final String MOVIES_DATA_KEY = "movies";
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE
    };
    public static String TAG = MovieFragment.class.getSimpleName();
    public ProgressBar progressBar;
    private MovieAdapter.Callbacks mCallbacks;
    private MovieAdapter mAdapter;
    private String mSortby = MOST_POPULAR;
    private ArrayList<Movie> mMovies;
    private RecyclerView recyclerView;
    private TextView EmptyState;

    public MovieFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recycler_container, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.mrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        EmptyState = rootView.findViewById(R.id.empty_state);
        progressBar = rootView.findViewById(R.id.progress_bar);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STORED_KEY)) {
                mSortby = savedInstanceState.getString(STORED_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_DATA_KEY)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES_DATA_KEY);
            } else {
                SortByMovies(mSortby);
            }
        }

        SortByMovies(mSortby);
        mCallback();
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mSortby.contentEquals(FAVORITE)) {
            outState.putString(STORED_KEY, mSortby);
        }
        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIES_DATA_KEY, mMovies);
        }
        super.onSaveInstanceState(outState);
    }

    private void ApiAccess(String mSortby) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MovieResponse> call = apiService.getTopRatedMovies(mSortby, BuildConfig.MOVIE_API);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                int statusCode = response.code();
                mMovies = response.body().getResults();
                recyclerView.setAdapter(mAdapter = new MovieAdapter(mMovies, R.layout.content_container, getActivity(), mCallbacks));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.VISIBLE);
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_list, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        switch (mSortby) {
            case MOST_POPULAR:
                menu.findItem(R.id.sort_by_most_popular).setChecked(true);
                break;
            case TOP_RATED:
                menu.findItem(R.id.sort_by_top_rated).setChecked(true);
                break;
            case FAVORITE:
                menu.findItem(R.id.sort_by_favorites).setChecked(true);
                break;
        }

    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isNetworkAvailable(getActivity())) {
                    mAdapter.getFilter().filter(newText);
                } else {
                    EmptyState.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_top_rated:
                mSortby = TOP_RATED;
                SortByMovies(mSortby);
                item.setChecked(true);
                return true;
            case R.id.sort_by_most_popular:
                mSortby = MOST_POPULAR;
                SortByMovies(mSortby);
                item.setChecked(true);
                return true;
            case R.id.sort_by_favorites:
                mSortby = FAVORITE;
                Log.d(TAG, "favorite pressed");
                SortByMovies(mSortby);
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SortByMovies(String mSortBy) {
        if (isNetworkAvailable(getActivity())) {
            if (!mSortBy.contentEquals(FAVORITE)) {
                ApiAccess(mSortBy);
            } else {
                if (isNetworkAvailable(getActivity())) {
                    new FetchFav(getActivity()).execute();
                    EmptyState.setVisibility(View.GONE);
                } else {
                    EmptyState.setVisibility(View.VISIBLE);
                }
            }
        } else {
            EmptyState.setVisibility(View.VISIBLE);
        }
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null) {
            Toast.makeText(getActivity(), "there's no network connection", Toast.LENGTH_LONG).show();
        }

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();


    }


    public void mCallback() {
        mCallbacks = new MovieAdapter.Callbacks() {
            @Override
            public void onItemCompleted(Movie movie, int position) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(DetailActivity.Args, movie);
                startActivity(intent);
            }
        };

    }

    public class FetchFav extends AsyncTask<String, Void, List<Movie>> {

        private Context mContext;

        //constructor
        public FetchFav(Context context) {
            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );

            return getFavMoviesFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            //we got Fav movies so let's show them
            if (movies != null) {
                if (mAdapter != null) {
                    mAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);

                Log.d(TAG, "Favorites :" + mMovies.toString());
            } else {
                Log.d(TAG, getString(R.string.nofav));
            }
        }

        private List<Movie> getFavMoviesFromCursor(Cursor cursor) {
            List<Movie> results = new ArrayList<>();
            //if we have data in database for Fav. movies.
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }
    }

}
