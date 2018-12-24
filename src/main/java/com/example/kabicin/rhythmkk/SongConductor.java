package com.example.kabicin.rhythmkk;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;


import java.io.IOException;

public class SongConductor {

    /**
     * Song data for bpm, delay table, name
     */
    private Song song;

    /**
     * MediaPlayer to control mp3/wav files
     */
    private MediaPlayer mediaPlayer;

    /**
     * Single SongConductor instance
     */
    private static final SongConductor SINGLETON = new SongConductor();

    /**
     * Remove initialization of this class to implement Singleton design pattern
     */
    private SongConductor() {
    }

    /**
     * Returns the one and only instance of SongConductor
     *
     * @return songConductor singleton
     */
    public static SongConductor getInstance() {
        return SINGLETON;
    }

    /**
     * Set the current song to song
     *
     * @param song
     */
    public void setSong(Song song) {
        this.song = song;
    }

    /**
     * Plays the song with given song path
     *
     * @param context the context to play the song in
     */
    public void playSong(Context context) {
        try {
            // initialize a new media player
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, this.song.getUri());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Determine if song is currently playing
     *
     * @return true if song is playing
     */
    public boolean isSongPlaying() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.isPlaying();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Stop the song and release the media player
     */
    public void stopSong() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get the position of the song
     *
     * @return the position
     */
    public int getPosition() {
        if (mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        }
        return -1;
    }

    /**
     * Get the current song
     *
     * @return the song
     */
    public Song getSong() {
        return this.song;
    }
}
