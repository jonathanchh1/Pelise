package com.example.jonat.retrofitsSortby;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;


import com.example.jonat.retrofitsSortby.data.MovieContract;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    private final static String FAVORITE = "favorite";
    private MovieAdapter.Callbacks mCallbacks;
    private MovieAdapter mAdapter;
    private String mSortby = MOST_POPULAR;
    private List<Movie> mMovies;

    // TODO - insert your themoviedb.org API KEY here
    private final static String API_KEY = "e568b8a0746be29e194efdcf43151703";
    private RecyclerView recyclerView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_peoples);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCallback();
        SortByMovies(mSortby);


    }

    private void ApiAccess(String mSortby) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MovieResponse> call = apiService.getTopRatedMovies(mSortby, API_KEY);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                int statusCode = response.code();
                mMovies = response.body().getResults();
                recyclerView.setAdapter(new MovieAdapter(mMovies, R.layout.content_container, getApplicationContext(), mCallbacks));
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    private void SortByMovies(String mSortBy) {
        if (!mSortBy.contentEquals(FAVORITE)) {
            ApiAccess(mSortBy);
        } else {
            new FetchFav(getApplicationContext()).execute();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_list, menu);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_top_rated:
                mSortby = TOP_RATED;
                SortByMovies(mSortby);
                item.setChecked(true);
                break;
            case R.id.sort_by_most_popular:
                mSortby = MOST_POPULAR;
                SortByMovies(mSortby);
                item.setChecked(true);
                break;

            case R.id.sort_by_favorites:
                mSortby = FAVORITE;
                Log.d(TAG, "favorite pressed");
                SortByMovies(mSortby);
                item.setChecked(true);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
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
            } else{
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

    public void mCallback(){
        mCallbacks = new MovieAdapter.Callbacks() {
            @Override
            public void onItemCompleted(Movie movie, int position) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.Args, movie);
                startActivity(intent);


            }
        };

    }

}


