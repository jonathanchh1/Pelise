package com.emi.jonat.vepelis.Services;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.emi.jonat.vepelis.Fragments.DetailFragment;
import com.emi.jonat.vepelis.Model.Movie;
import com.emi.jonat.vepelis.Adapters.MovieAdapter;
import com.emi.jonat.vepelis.R;


public class DetailActivity extends AppCompatActivity implements MovieAdapter.Callbacks {

    public static final String Args = "movies_argument";
    Toolbar mToolbar;
    private FragmentManager fragmentManager = getFragmentManager();
    private boolean mTwoPane;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);


        if (findViewById(R.id.detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                fragmentManager.beginTransaction()
                        .add(R.id.detail_container, new DetailFragment(), Args)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    @Override
    public void onItemCompleted(Movie items, int position) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable("movies_details", items);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            fragmentManager.beginTransaction()
                    .replace(R.id.detail_container, fragment, Args)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.Args, items);
            startActivity(intent);
        }
    }
}
