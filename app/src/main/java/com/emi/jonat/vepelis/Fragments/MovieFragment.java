package com.emi.jonat.vepelis.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emi.jonat.vepelis.Adapters.NowPlayingAdapter;
import com.emi.jonat.vepelis.BuildConfig;
import com.emi.jonat.vepelis.Activities.DetailActivity;
import com.emi.jonat.vepelis.Model.Movie;
import com.emi.jonat.vepelis.Adapters.MovieAdapter;
import com.emi.jonat.vepelis.Services.MovieResponse;
import com.emi.jonat.vepelis.R;
import com.emi.jonat.vepelis.Services.ApiClient;
import com.emi.jonat.vepelis.Services.ApiInterface;
import com.emi.jonat.vepelis.Services.PaginationScrollListener;
import com.emi.jonat.vepelis.data.MovieContract;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jonat on 10/29/2017.
 */

public class MovieFragment extends Fragment {

    public final static String POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
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
    private static final int PAGE_START = 1;
    public static String TAG = MovieFragment.class.getSimpleName();
    public ProgressBar progressBar;
    ApiInterface apiService;
    LinearLayoutManager linearLayoutManager;
    Button btnRetry;
    private MovieAdapter mAdapter;
    private String mSortby = POPULAR;
    private ArrayList<Movie> mMovies;
    private RecyclerView recyclerView;
    private TextView EmptyState;
    private ArrayList<Movie> results;
    private MovieAdapter.Callbacks mCallbacks;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 30;
    private int currentPage = PAGE_START;
    public MovieFragment() {
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recycler_container, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.mrecyclerview);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        btnRetry = (Button) rootView.findViewById(R.id.error_btn_retry);
        mMovies = new ArrayList<Movie>();
        EmptyState = rootView.findViewById(R.id.empty_state);
        progressBar = rootView.findViewById(R.id.progress_bar);
        apiService = ApiClient.getClient().create(ApiInterface.class);
        recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage(mSortby);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
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

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortByMovies(mSortby);
            }
        });
        return rootView;
    }

    private void loadNextPage(String mSortby) {
        Log.d(TAG, "loadNextPage: " + currentPage);
        Call<MovieResponse> call = apiService.getMoviesPages(mSortby, currentPage, BuildConfig.MOVIE_API);
        call.enqueue(new Callback<MovieResponse>() {

            @Override
            public void onResponse(Call<MovieResponse> call, final Response<MovieResponse> response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            mAdapter.removeLoadingFooter();
                            isLoading = false;
                            Log.wtf(LOG_TAG, "response : " + response.body().getResults());
                            ArrayList<Movie> results = response.body().getResults();
                            mAdapter.addAll(results);
                            if (currentPage != TOTAL_PAGES) mAdapter.addLoadingFooter();
                            else isLastPage = true;
                            progressBar.setVisibility(View.GONE);
                            btnRetry.setVisibility(View.GONE);
                        }
                    }
                }).run();


            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // handle error
                t.printStackTrace();
                progressBar.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.VISIBLE);
                mAdapter.showRetry(true, fetchErrorMessage(t));
            }

        });
    }

    private String fetchErrorMessage(Throwable t) {

        String errorMsg = getResources().getString(R.string.error_msg_unknown);
        if (!isNetworkAvailable(getContext())) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (t instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }
        return errorMsg;
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

        Call<MovieResponse> call = apiService.getMoviesPages(mSortby, currentPage, BuildConfig.MOVIE_API);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, final Response<MovieResponse> response) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int statusCode = response.code();
                        if (response.isSuccessful()) {
                            mMovies = response.body().getResults();
                            Log.d(LOG_TAG, response.body().getResults().toString());
                            mAdapter = new MovieAdapter(mMovies, R.layout.content_container, getActivity(), mCallbacks);
                            recyclerView.setAdapter(mAdapter);
                            progressBar.setVisibility(View.GONE);
                            btnRetry.setVisibility(View.GONE);
                            if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter();
                            else isLastPage = true;
                        }
                    }
                }).run();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Log error here since request failed
                progressBar.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.VISIBLE);
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
            case POPULAR:
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
            case R.id.sort_by_most_popular:
                mSortby = POPULAR;
                SortByMovies(mSortby);
                item.setChecked(true);
                return true;
            case R.id.sort_by_top_rated:
                mSortby = TOP_RATED;
                SortByMovies(mSortby);
                item.setChecked(true);
                return true;
            case R.id.sort_by_favorites:
                mSortby = FAVORITE;
                SortByMovies(mSortby);
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SortByMovies(final String mSortBy) {
        if (isNetworkAvailable(getActivity())) {
            if (!mSortBy.contentEquals(FAVORITE)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ApiAccess(mSortBy);
                    }
                }).run();
            } else {
                if (isNetworkAvailable(getActivity())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new FetchFav(getActivity()).execute();
                            EmptyState.setVisibility(View.GONE);
                        }
                    }).run();
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

    public class FetchFav extends AsyncTask<String, Void, ArrayList<Movie>> {

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
        protected ArrayList<Movie> doInBackground(String... params) {
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
        protected void onPostExecute(ArrayList<Movie> movies) {
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

        private ArrayList<Movie> getFavMoviesFromCursor(Cursor cursor) {
            ArrayList<Movie> results = new ArrayList<>();
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
