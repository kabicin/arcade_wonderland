package com.example.kabicin.rhythmkk;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 * MyGLSurfaceView adapted from Android Docs
 */
public class MyGLSurfaceView extends GLSurfaceView {
    /**
     * Handle to OpenGL ES renderer
     */
    private MyGLRenderer renderer;

    /**
     * Score TextView for modifying score
     */
    private TextView scoreText;

    /**
     * SurfaceView's screen dimensions
     */
    private int height, width;

    /**
     * SongConductor that holds song and media player
     */
    private SongConductor songConductor;

    /**
     * Random number generator for note block automation
     */
    private Random rand;

    /**
     * Default constructor initializes View with a SongConductor obj
     *
     * @param context       of Activity
     * @param songConductor contains song info and media player
     */
    public MyGLSurfaceView(Context context, SongConductor songConductor, TextView currScoreText, String currentUser) {
        super(context);
        initSurfaceView(context, songConductor, currScoreText, currentUser);
    }

    /**
     * Backup constructor, won't let me compile w/o
     *
     * @param context       of Activity
     * @param songConductor song info and media player
     * @param attributeSet  mandatory attrib
     */
    public MyGLSurfaceView(Context context, SongConductor songConductor, TextView currScoreText, String currentUser,
                           AttributeSet attributeSet) {
        super(context, attributeSet);
        initSurfaceView(context, songConductor, currScoreText, currentUser);

    }


    /**
     * Initialize the surface view to adhere to both MyGLSurfaceView constructors
     *
     * @param context       of Activity
     * @param songConductor for song info and media player
     */
    private void initSurfaceView(Context context, SongConductor songConductor, TextView currScoreText,
                                 String currentUser) {
        // Create OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        this.scoreText = currScoreText;
        this.songConductor = songConductor;
        this.songConductor.playSong(getContext());
        rand = new Random();
        renderer = new MyGLRenderer(context, generateDelayTable(songConductor.getSong()), songConductor, currScoreText, currentUser);
        setRenderer(renderer);
        height = Resources.getSystem().getDisplayMetrics().heightPixels;
        width = Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * Generates a delay table of milliseconds to wait for the next note
     *
     * @param song provides delay offset and multiplier
     * @return long array of delays
     */
    public long[] generateDelayTable(Song song) {
        long[] delays = new long[0];

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(song.getMidiUri());
            MidiFile midiFile = new MidiFile(inputStream);
            TreeSet<MidiEvent> events = midiFile.getTracks().get(2).getEvents();
            MidiEvent prev = null;
            delays = new long[events.size() / 2 + 1];
            MidiTrack T = midiFile.getTracks().get(1);
            for (MidiEvent event : T.getEvents()) {
                if (event instanceof Tempo) {
                    Tempo tempo = (Tempo) event;
                    song.setBpm(tempo.getBpm());
                }
            }
            delays[0] = song.getOffset();
            int i = 1;
            for (MidiEvent event : events) {
                if (event instanceof NoteOn) {
                    Log.i("NoteOn", " " + event.getDelta() + " at " + ((NoteOn) event).getNoteValue());
                    if (prev == null) {
                        delays[i] = delays[i - 1] + Math.round((song.getMultiplier()) * (event.getDelta()));
                    } else {
                        delays[i] = delays[i - 1] + Math.round((song.getMultiplier()) * (event.getDelta() + prev.getDelta()));
                    }
                    i++;
                } else if (event instanceof NoteOff) {
                    Log.i("NoteOff", " " + event.getDelta() + " at " + (((NoteOff) event).getNoteValue()));
                    prev = event;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return delays;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int i = withinBounds(x, y);
                if (i >= 0) {
                    if (renderer.getHittable(i)) {
                        renderer.setHit(i);
                        // random number between 523 and 1022
                        updateScore(523 + rand.nextInt(500));
                        requestRender();
                    }
                }
        }

        return true;
    }

    /**
     * Sets the SongConductor of the GLSurfaceView
     *
     * @param songConductor to be set
     */
    public void setSongConductor(SongConductor songConductor) {
        this.songConductor = songConductor;
    }


    /**
     * Updates the score TextView to the newScore
     *
     * @param newScore to update
     */
    private void updateScore(int newScore) {
        int score = Integer.parseInt(scoreText.getText().toString()) + newScore;
        scoreText.setText(String.valueOf(score));
    }

    /**
     * Obtain a string version of the score
     *
     * @return the score obtained
     */
    public String getScore() {
        return scoreText.getText().toString();
    }

    /**
     * Check if android xy coordinates are within open gl bounds.
     *
     * @param x in android
     * @param y in android
     * @return value >= 0 if a square was hit, denoting the square at position i
     */
    private int withinBounds(float x, float y) {
        // derived formulas from android screen coordinates to opengl es scaling
        float scaleX = x / width - 0.5f;
        float scaleY = 1.0f - (2 * y) / height;

        // detect each square
        for (int i = 0; i < MyGLRenderer.NUM_SQUARES; i++) {
            float dx = MyGLRenderer.displacementx[i];
            float dy = MyGLRenderer.displacementy[i];
            if (scaleX < (0.15 - dx) && scaleX > (-0.15 - dx) && scaleY < (0.15 + dy) && scaleY > (-0.15 + dy)) {
                return i;
            }
        }
        return -1;
    }
}
