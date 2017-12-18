package com.emi.jonat.vepelis.Activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.emi.jonat.vepelis.Fragments.DetailFragment;
import com.emi.jonat.vepelis.Model.Movie;
import com.emi.jonat.vepelis.Adapters.MovieAdapter;
import com.emi.jonat.vepelis.R;


public class DetailActivity extends AppCompatActivity implements MovieAdapter.Callbacks {

    public static final String Args = "movies_argument";
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
            args.putParcelable(DetailActivity.Args, items);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
