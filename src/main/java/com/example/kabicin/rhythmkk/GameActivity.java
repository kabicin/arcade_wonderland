package com.example.kabicin.rhythmkk;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;


public class GameActivity extends AppCompatActivity {
    /**
     * The number of pages on the view pager
     */
    private static final int NUM_PAGES = 2;

    /**
     * The handle to the view pager
     */
    private ViewPager viewPager;

    /**
     * The pager adapter that binds to the view pager
     */
    private PagerAdapter pagerAdapter;

    /**
     * Current username
     */
    private String currentUser;

    /**
     * Handle to database
     */
    protected DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Disable screen orientation changes
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        // Initialize Database
        dataHelper = new DataHelper(this);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize ViewPager
        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new ScreenPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // set current fragment to scoreboard if coming from a recent game.
        if (getIntent() != null) {
            Intent intent = getIntent();
            // No matter where you are coming from, obtain the username
            currentUser = intent.getExtras().getString("username");
            TextView lishiScoreTextView = findViewById(R.id.lishi_score);
            lishiScoreTextView.setText(String.valueOf(dataHelper.getLishiScore(currentUser)));

            // If I am coming from a finished game the fragment I land on should be specified
            if (intent.hasExtra("frag")) {
                int frag = intent.getExtras().getInt("frag");
                String score = intent.getExtras().getString("score");
                boolean added = dataHelper.addData(currentUser, score);
                // Value of lishiscore from current User
                int lishiScore = Integer.valueOf(String.valueOf(dataHelper.getLishiScore(currentUser)));
                if (added) {
                    lishiScore += 10;
                } else {
                    lishiScore += 1;
                }
                dataHelper.setLishiScore(currentUser, String.valueOf(lishiScore));
                lishiScoreTextView.setText(String.valueOf(lishiScore));
                viewPager.setCurrentItem(frag);
            }

        }
    }

    /**
     * Obtains the current user
     *
     * @return user
     */
    public String getUsername() {
        return currentUser;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_toolbar, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }


    /**
     * A simple pager adapter that represents 2 ScreenPageFragment objects, in
     * sequence.
     */
    private class ScreenPagerAdapter extends FragmentStatePagerAdapter {
        ScreenPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new PlayScreenFragment();
            }
            return new ScoreboardFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


}
