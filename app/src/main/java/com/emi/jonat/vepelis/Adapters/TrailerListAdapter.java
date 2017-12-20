package com.emi.jonat.vepelis.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emi.jonat.vepelis.R;
import com.emi.jonat.vepelis.Model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jonat on 10/5/2016.
 */
public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private final static String LOG_TAG = TrailerListAdapter.class.getSimpleName();
    private final Callbacks mCallbacks;
    private ArrayList<Trailer> mTrailers;

    public TrailerListAdapter(ArrayList<Trailer> trailers, Callbacks callbacks) {
        mTrailers = trailers;
        mCallbacks = callbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Trailer trailer = mTrailers.get(position);
        final Context context = holder.mView.getContext();

        float paddingLeft = 0;
        if (position == 0) {
            paddingLeft = context.getResources().getDimension(R.dimen.detail_horizontal_padding);
        }

        float paddingRight = 0;
        if (position + 1 != getItemCount()) {
            paddingRight = context.getResources().getDimension(R.dimen.detail_horizontal_padding) / 2;
        }

        holder.mView.setPadding((int) paddingLeft, 0, (int) paddingRight, 0);

        holder.mTrailer = trailer;

        String videoUrl = "http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";
        Log.i(LOG_TAG, "thumbnailUrl -> " + videoUrl);

        Picasso.with(context)
                .load(videoUrl)
                .config(Bitmap.Config.RGB_565)
                .into(holder.mThumbnailView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.watch(trailer, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public void add(List<Trailer> trailers) {
        mTrailers.clear();
        mTrailers.addAll(trailers);
        notifyDataSetChanged();
    }

    public void setmTrailers(ArrayList<Trailer> mTrailers) {
        this.mTrailers = mTrailers;
    }

    public ArrayList<Trailer> getTrailers() {
        return mTrailers;
    }

    public interface Callbacks {
        void watch(Trailer trailer, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Trailer mTrailer;
        ImageView mThumbnailView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mThumbnailView = (ImageView) view.findViewById(R.id.trailer_thumbnail);
        }
    }
}

