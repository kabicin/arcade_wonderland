package com.example.kabicin.rhythmkk;

import android.net.Uri;

public class Song {
    /**
     * Name of the song
     */
    private String name;

    /**
     * BPM
     */
    private float bpm;

    /**
     * Starting delay offset
     */
    private int offset;


    /**
     * Song speed multiplier
     */
    private float mult;

    /**
     * Initialize the song
     *
     * @param name   of song
     * @param offset time at beginning
     * @param mult   speed
     */
    public Song(String name, int offset, float mult) {
        this.name = name;
        this.offset = offset;
        this.mult = mult;
    }


    private String formatName() {
        return this.name.toLowerCase().replace(" ", "_");
    }

    /**
     * Return the uniform resource identifier for the song name
     *
     * @return returns the uri for the song
     */
    public Uri getUri() {
        return Uri.parse("android.resource://com.example.kabicin.rhythmkk/raw/" + formatName());
    }

    /**
     * Return the uniform resource identifier for the song name
     *
     * @return returns the uri for the song
     */
    public Uri getMidiUri() {
        return Uri.parse("android.resource://com.example.kabicin.rhythmkk/raw/" + formatName() + "_midi");
    }


    /**
     * Set the current bpm
     *
     * @param bpm of song
     */
    public void setBpm(float bpm) {
        this.bpm = bpm;
    }

    /**
     * Set the starting offset
     *
     * @param offset of song
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Set song multiplier: constant factor of delay between notes
     *
     * @param mult of song
     */
    public void setMultiplier(float mult) {
        this.mult = mult;
    }

    /**
     * Get beats per minute
     *
     * @return bpm of song
     */
    public float getBpm() {
        return bpm;
    }

    /**
     * Get starting offset of song
     *
     * @return offset of song
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Get delay multiplier
     *
     * @return mult of song
     */
    public float getMultiplier() {
        return mult;
    }
}
