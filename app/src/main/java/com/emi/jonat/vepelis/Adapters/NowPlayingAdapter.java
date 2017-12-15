package com.emi.jonat.vepelis.Adapters;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.emi.jonat.vepelis.Model.Movie;
import com.emi.jonat.vepelis.R;
import com.emi.jonat.vepelis.Services.Bitmaps;
import com.emi.jonat.vepelis.data.MovieContract;
import com.emi.jonat.vepelis.data.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonat on 12/10/2017.
 */

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.MovieViewHolder> implements Filterable {
    private final Callbacks mCallbacks;
    private final Movie mMovie = new Movie();
    private ArrayList<Movie> itemsList = new ArrayList<>();
    private ArrayList<Movie> mListfilterable = new ArrayList<>();
    private int rowLayout;
    private Context context;
    private String errorMsg;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    public NowPlayingAdapter(ArrayList<Movie> movie, int rowLayout, Context context, Callbacks mCallbacks) {
        this.itemsList = movie;
        this.rowLayout = rowLayout;
        this.context = context;
        this.mListfilterable = movie;
        this.mCallbacks = mCallbacks;
    }

    public void setData(ArrayList<Movie> data) {
        remove();
        for (Movie movie : data) {
            add(movie);
        }
    }

    public void remove() {
        synchronized (mMovie) {
            mListfilterable.clear();
        }
        notifyDataSetChanged();
    }

    public void add(Movie movie) {
        synchronized (mMovie) {
            mListfilterable.add(movie);
        }

        notifyDataSetChanged();
    }



    /*--------------------------------------------------pagination----------------------------------------------*/

    public void addpages(Movie r) {
        mListfilterable.add(r);
        notifyItemInserted(mListfilterable.size() - 1);
    }

    public void addAll(ArrayList<Movie> moveResults) {
        for (Movie result : moveResults) {
            addpages(result);
        }
    }

    public void removes(Movie r) {
        int position = mListfilterable.indexOf(r);
        if (position > -1) {
            mListfilterable.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            removes(getItem(0));
        }
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        addpages(new Movie());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mListfilterable.size() - 1;
        Movie result = getItem(position);

        if (result != null) {
            mListfilterable.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Movie getItem(int position) {
        return mListfilterable.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(mListfilterable.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    /*----------------------------------------------------------end of pagination---------------------------------------------------*/
    @Override
    public NowPlayingAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new NowPlayingAdapter.MovieViewHolder(view);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(final NowPlayingAdapter.MovieViewHolder holder, final int position) {

        final Movie mItems = mListfilterable.get(position);
        holder.items = mItems;

        String poster = Bitmaps.buildPosterUrl(mItems.getPosterPath());


        if (!TextUtils.isEmpty(poster)) {
            Picasso.with(context).load(poster)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail,
                            new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (holder.thumbnail != null) {
                                        holder.thumbnail.setVisibility(View.VISIBLE);
                                    } else {
                                        holder.thumbnail.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onError() {
                                    holder.thumbnail.setVisibility(View.VISIBLE);
                                }
                            });


        }

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, mItems.getTitle());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, mItems.getOverview());
                context.startActivity(Intent.createChooser(sharingIntent, "sharing Option"));
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.onItemCompleted(mItems, holder.getAdapterPosition());
            }
        });

        holder.title.setText(mItems.getTitle());
        holder.description.setText(mItems.getOverview());

        //set  icon on toolbar for favorited items
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return Query.isFavorited(context, mItems.getId());
            }

            @Override
            protected void onPostExecute(Integer isFavored) {
                holder.FavoriteButton.setImageResource(isFavored == 1 ?
                        R.drawable.ic_favorite_black_24dp :
                        R.drawable.ic_favorite_border_black_24dp);
            }
        }.execute();

        holder.FavoriteButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                // check if movie is favored or not
                new AsyncTask<Void, Void, Integer>() {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        return Query.isFavorited(context, mItems.getId());
                    }

                    @Override
                    protected void onPostExecute(Integer isFavored) {
                        // if it is in favoritesc
                        if (isFavored == 1) {
                            // delete from favorites
                            new AsyncTask<Void, Void, Integer>() {
                                @Override
                                protected Integer doInBackground(Void... params) {
                                    return context.getContentResolver().delete(
                                            MovieContract.MovieEntry.CONTENT_URI,
                                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                            new String[]{Integer.toString(mItems.getId())}
                                    );
                                }

                                @Override
                                protected void onPostExecute(Integer rowsDeleted) {
                                    holder.FavoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);

                                }
                            }.execute();
                        }
                        // if it is not in favorites
                        else {
                            // add to favorites
                            new AsyncTask<Void, Void, Uri>() {
                                @Override
                                protected Uri doInBackground(Void... params) {
                                    ContentValues values = new ContentValues();

                                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mItems.getId());
                                    values.put(MovieContract.MovieEntry.COLUMN_TITLE, mItems.getTitle());
                                    values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mItems.getReleaseDate());
                                    values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mItems.getPosterPath());
                                    values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, mItems.getOriginalLanguage());
                                    values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mItems.getVoteAverage());
                                    values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mItems.getOverview());
                                    values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, mItems.getVoteCount());
                                    values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mItems.getOriginalTitle());
                                    values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, mItems.getBackdropPath());

                                    return context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
                                }

                                @Override
                                protected void onPostExecute(Uri returnUri) {
                                    holder.FavoriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                                }
                            }.execute();
                        }
                    }
                }.execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListfilterable == null ? 1 : mListfilterable.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mListfilterable = itemsList;
                } else {

                    ArrayList<Movie> filteredList = new ArrayList<>();

                    for (Movie movie : itemsList) {

                        if (movie.getTitle().toUpperCase().toLowerCase().contains(charString) ||
                                movie.getPosterPath().toUpperCase().toLowerCase().contains(charString) ||
                                movie.getOverview().toUpperCase().toLowerCase().contains(charString)) {

                            filteredList.add(movie);
                        }
                    }

                    mListfilterable = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListfilterable;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mListfilterable = (ArrayList<Movie>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }


    public interface Callbacks {
        void onItemCompleted(Movie items, int position);
    }


    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public Movie items;
        TextView title;
        ImageView thumbnail;
        TextView description;
        ImageButton FavoriteButton;
        ImageButton shareButton;
        View mView;


        public MovieViewHolder(View v) {
            super(v);
            mView = v;
            title = v.findViewById(R.id.title);
            thumbnail = v.findViewById(R.id.thumbnail);
            description = v.findViewById(R.id.sub_title);
            FavoriteButton = v.findViewById(R.id.favorite_button);
            shareButton = v.findViewById(R.id.share_button);

        }
    }
}

