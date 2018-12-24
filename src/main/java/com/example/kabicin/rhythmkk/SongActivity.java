package com.example.kabicin.rhythmkk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class SongActivity extends AppCompatActivity {
    private MyGLSurfaceView surfaceView;
    private TextView currPlayingText;
    private TextView songNameText;
    private TextView currTimeText;
    private TextView currScoreText;
    private LinearLayout currPlayingLayout;
    private SongConductor songConductor;
    private String songName;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = songConductor.getPosition();
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
//            Log.i("TIMEEEE", String.format("%d:%02d", minutes, seconds));
            currTimeText.setText(String.format(Locale.getDefault(), "%d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get previous activity data
        Intent intent = getIntent();
        String currentUser = intent.getStringExtra("username");
        songName = intent.getStringExtra("name");
        //String description = intent.getStringExtra("description");

        // Define all TextView and LinearLayout instances
        currPlayingText = new TextView(this);
        songNameText = new TextView(this);
        currTimeText = new TextView(this);
        currScoreText = new TextView(this);
        currPlayingLayout = new LinearLayout(this);

        // Initialize the Text Views and load the Linear Layout
        initializeCurrPlayingText();
        initializeSongNameText();
        initializeCurrTimeText();
        initializeCurrScoreText();
        initializeCurrPlayingLayout();

        songConductor = SongConductor.getInstance();
        // stops a potential previous song which was playing.
        songConductor.stopSong();
        songConductor.setSong(new Song(songName, Integer.valueOf(intent.getStringExtra("offset")),
                Float.valueOf(intent.getStringExtra("multiplier"))));
        surfaceView = new MyGLSurfaceView(this, songConductor, currScoreText, currentUser);
        surfaceView.setSongConductor(songConductor);
        setContentView(surfaceView);

        // Add the linear layout to a view group.
        addContentView(currPlayingLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        timerRunnable.run();
        //startConductor();

    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    /**
     * Initializes the current time text
     */
    private void initializeCurrTimeText() {
        currTimeText.setTextColor(Color.BLACK);
        currTimeText.setGravity(Gravity.CENTER);
    }

    /**
     * Initialize the currently playing text
     */
    private void initializeCurrPlayingText() {
        currPlayingText.setText(getString(R.string.currently_playing_header));
        currPlayingText.setTextColor(Color.BLACK);
        currPlayingText.setGravity(Gravity.CENTER);
    }

    /**
     * Initialize the song name text
     */
    private void initializeSongNameText() {
        songNameText.setText(songName);
        songNameText.setTextColor(Color.BLACK);
        songNameText.setGravity(Gravity.CENTER);
    }

    /**
     * Initialize the current score text
     */
    private void initializeCurrScoreText() {
        currScoreText.setText("0");
        currScoreText.setTextColor(Color.BLACK);
        currScoreText.setGravity(Gravity.CENTER);
    }

    /**
     * Initialize the LinearLayout to hold all currently playing data
     */
    private void initializeCurrPlayingLayout() {
        currPlayingLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        currPlayingLayout.setOrientation(LinearLayout.VERTICAL);
        // Add the views to the layout
        currPlayingLayout.addView(currPlayingText);
        currPlayingLayout.addView(songNameText);
        currPlayingLayout.addView(currTimeText);
        currPlayingLayout.addView(currScoreText);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        songConductor.stopSong();
    }

}
