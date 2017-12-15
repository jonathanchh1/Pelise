package com.emi.jonat.vepelis.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.emi.jonat.vepelis.Services.Bitmaps;
import com.emi.jonat.vepelis.Activities.DetailActivity;
import com.emi.jonat.vepelis.Model.Movie;
import com.emi.jonat.vepelis.R;
import com.emi.jonat.vepelis.Model.Review;
import com.emi.jonat.vepelis.Adapters.ReviewListAdapter;
import com.emi.jonat.vepelis.Model.Trailer;
import com.emi.jonat.vepelis.Adapters.TrailerListAdapter;
import com.emi.jonat.vepelis.Services.FetchReviewsTask;
import com.emi.jonat.vepelis.Services.FetchTrailersTask;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jonat on 12/9/2017.
 */
public class DetailFragment extends Fragment implements FetchTrailersTask.Listener, FetchReviewsTask.Listener,
        TrailerListAdapter.Callbacks, ReviewListAdapter.Callbacks {
    @SuppressWarnings("unused")

    public static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";
    static final String DETAIL_MOVIE = "DETAIL_MOVIE";
    Movie movie = new Movie();
    @BindView(R.id.movie_title)
    TextView mMovieTitleView;
    @BindView(R.id.movie_overview)
    TextView mMovieOverviewView;
    @BindView(R.id.movie_release_date)
    TextView mMovieReleaseDateView;
    @BindView(R.id.movie_vote_average)
    TextView mMovieRatingView;
    @BindView(R.id.movie_poster)
    ImageView mMoviePosterView;
    Unbinder unbinder;
    @BindView(R.id.trailer_list)
    RecyclerView mRecyclerViewForTrailers;
    @BindView(R.id.review_list)
    RecyclerView mRecyclerViewForReviews;
    @BindView(R.id.button_watch_trailer)
    Button mButtonWatchTrailer;
    Trailer trailer;
    @BindViews({R.id.rating_first_star, R.id.rating_second_star, R.id.rating_third_star,
            R.id.rating_fourth_star, R.id.rating_fifth_star})

    List<ImageView> ratingStarViews;
    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;
    private LayoutInflater mLayoutInflater;
    private Toast mToast;
    private ShareActionProvider mShareActionProvider;
    private View rootView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    private static String convertdouble(double number) {
        DecimalFormat formatter;
        if (number - (int) number > 0.0)
            formatter = new DecimalFormat("0.0"); // Here you can also deal with rounding if you wish..
        else
            formatter = new DecimalFormat("0");
        return formatter.format(number);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Activity activity = getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && activity instanceof DetailActivity) {
            appBarLayout.setTitle(movie.getOriginalTitle());
        }

        ImageView movieBackdrop = ((ImageView) activity.findViewById(R.id.movie_backdrop));
        if (movieBackdrop != null) {
            String poster_url = Bitmaps.buildBackdropUrl(movie.getPosterPath());
            Log.d(LOG_TAG, "poster_url" + poster_url);
            Picasso.with(getActivity()).load(poster_url).into(movieBackdrop);

        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Trailer> trailers = mTrailerListAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_TRAILERS, trailers);
        }

        ArrayList<Review> reviews = mReviewListAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_REVIEWS, reviews);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mLayoutInflater = inflater;

        Bundle arguments = getArguments();
        Intent intent = getActivity().getIntent();

        if (arguments != null || intent != null && intent.hasExtra(DetailActivity.Args)) {

            rootView = mLayoutInflater.inflate(R.layout.detail_fragment, container, false);
            if (arguments != null) {
                movie = getArguments().getParcelable(DetailActivity.Args);
            } else {
                movie = intent.getParcelableExtra(DetailActivity.Args);
            }

            ButterKnife.bind(this, rootView);


            DisplayInfo(rootView);
            updateRatingBar(rootView);


            //For horizontal list of trailers
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            mRecyclerViewForTrailers.setLayoutManager(layoutManager);
            mTrailerListAdapter = new TrailerListAdapter(new ArrayList<Trailer>(), this);
            mRecyclerViewForTrailers.setAdapter(mTrailerListAdapter);
            mRecyclerViewForTrailers.setNestedScrollingEnabled(false);

            LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false);

            mRecyclerViewForReviews.setLayoutManager(layoutManager1);

            //For vertical list of reviews
            mReviewListAdapter = new ReviewListAdapter(new ArrayList<Review>(), this);
            mRecyclerViewForReviews.setAdapter(mReviewListAdapter);


            // Fetch trailers only if savedInstanceState == null
            if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
                List<Trailer> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
                mTrailerListAdapter.add(trailers);
                mButtonWatchTrailer.setEnabled(true);
            } else {
                fetchTrailers(rootView);
            }

            // Fetch reviews only if savedInstanceState == null
            if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
                List<Review> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
                mReviewListAdapter.add(reviews);
            } else {
                fetchReviews(rootView);
            }

        }
        return rootView;

    }

    private void updateRatingBar(View view) {
        if (convertdouble(movie.getVoteAverage()) != null && !convertdouble(movie.getVoteAverage()).isEmpty()) {
            String userRatingStr = getResources().getString(R.string.vote_average,
                    convertdouble(movie.getVoteAverage()));
            mMovieRatingView.setText(userRatingStr);
            Log.d(LOG_TAG, convertdouble(movie.getVoteAverage()));
            float userRating = Float.valueOf(convertdouble(movie.getVoteAverage())) / 2;
            int integerPart = (int) userRating;

            // Fill stars
            for (int i = 0; i < integerPart + 3; i++) {
                ratingStarViews.get(i).setImageResource(R.drawable.ic_star_black_24dp);
            }

            // Fill half star
            if (Math.round(userRating) > integerPart) {
                ratingStarViews.get(integerPart).setImageResource(
                        R.drawable.ic_star_half_black_24dp);
            }

        } else {
            mMovieRatingView.setVisibility(View.GONE);
        }
    }


    private void DisplayInfo(View v) {

        if (movie != null) {
            String poster_url = Bitmaps.buildPosterUrl(movie.getPosterPath());
            //load poster with picasso
            Picasso.with(getActivity()).load(poster_url).into(mMoviePosterView);


            mMovieTitleView.setText(movie.getTitle());
            mMovieOverviewView.setText(movie.getOverview());
            mMovieReleaseDateView.setText(movie.getReleaseDate());

            mButtonWatchTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTrailerListAdapter.getItemCount() > 0) {
                        watch(mTrailerListAdapter.getTrailers().get(0), 0);
                    }
                }
            });

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (movie != null) {
            inflater.inflate(R.menu.main_detail, menu);
            Log.d(LOG_TAG, "detail Menu created");

            final MenuItem invite = menu.findItem(R.id.invite);
            MenuItem action_share = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);

        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.invite:


                return true;

            case R.id.action_share:
                //share movie trailer
                updateShareActionProvider(trailer);


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void watch(Trailer trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));

    }

    @Override
    public void onFetchFinished(ArrayList<Trailer> trailers) {
        mTrailerListAdapter.add(trailers);
        mButtonWatchTrailer.setEnabled(!trailers.isEmpty());

        if (mTrailerListAdapter.getItemCount() > 0) {
            if (trailer != null) {
                for (int i = 0; i < trailers.size(); i++) {
                    Trailer trailer = mTrailerListAdapter.getTrailers().get(i);
                    updateShareActionProvider(trailer);
                }
            }
        }
    }

    @Override
    public void read(Review review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getUrl())));
    }

    @Override
    public void onReviewsFetchFinished(ArrayList<Review> reviews) {
        mReviewListAdapter.add(reviews);
    }

    private void fetchReviews(View view) {
        FetchReviewsTask reviewtask = new FetchReviewsTask(this);
        reviewtask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movie.getId());
    }

    private void fetchTrailers(View view) {
        FetchTrailersTask trailertask = new FetchTrailersTask(this);
        trailertask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movie.getId());
    }

    private void updateShareActionProvider(Trailer trailer) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, movie.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, trailer.getName() + " : "
                + trailer.getTrailerUrl());
        mShareActionProvider.setShareIntent(sharingIntent);
    }

}

