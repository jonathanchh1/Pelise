package com.emi.jonat.vepelis.Activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.emi.jonat.vepelis.Adapters.TabAdapter;
import com.emi.jonat.vepelis.Fragments.MovieFragment;
import com.emi.jonat.vepelis.Fragments.NowPlayingFragment;
import com.emi.jonat.vepelis.R;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    final int[] TabsIcon = new int[]{
            R.drawable.ic_movie_filter_black_24dp,
            R.drawable.ic_local_movies_black_24dp,

    };
    TabAdapter mTabAdapter;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupWithViewPager(viewPager);

        //Set Tabs inside Toolbar
        TabLayout mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.setupWithViewPager(viewPager);

        //set icon tabs
        if (TabsIcon.length > 0) {
            mTabs.getTabAt(0).setIcon(TabsIcon[0]);
            mTabs.getTabAt(1).setIcon(TabsIcon[1]);

        }

    }

    private void setupWithViewPager(ViewPager viewPager) {
        mTabAdapter = new TabAdapter(getSupportFragmentManager());
        mTabAdapter.addFragment(new MovieFragment(), getString(R.string.Movies));
        mTabAdapter.addFragment(new NowPlayingFragment(), getString(R.string.nowplaying));
        viewPager.setAdapter(mTabAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


