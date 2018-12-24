package com.example.kabicin.rhythmkk;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * MyGLRenderer adapted from Android Docs
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {
    private ArrayList<Square> square;
    private Square[] shadowSquare;
    private Context context;
    public static final int NUM_SQUARES = 5;

    // initializing matrices
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    // time
    private long currentTime;
    private long deltaTime;
    private long lastFrameTime;

    // squares hittable flags
    private boolean[] hittable;
    private boolean[] hit;
    private boolean[] alreadyHit;
    // each square has a corresponding frame count
    private int[] frameCount;

    // universal wait index for the next note
    private int waitIndex;

    // universal songOver flag
    private boolean songOver;

    // song delay information to know when to hit next note
    private long[] songDelay;

    // moved flag when finished song
    private boolean moved;

    // parallel lists of displacements for the different squares on board
    public static final float[] displacementx = {0.0f, 0.4f, -0.4f, -0.4f, 0.4f};
    public static final float[] displacementy = {0.0f, 0.4f, 0.4f, -0.4f, -0.4f};

    private ArrayList<Integer> activeSquares;
    private SongConductor songConductor;
    private TextView scoreText;
    private String currentUser;

    /**
     * Initializes the GLES Renderer
     *
     * @param context       of Activity
     * @param songDelay     for animation signalling
     * @param songConductor for current position of song
     */
    public MyGLRenderer(Context context, long[] songDelay, SongConductor songConductor, TextView currScoreText,
                        String currentUser) {
        this.context = context;
        this.songDelay = songDelay;
        this.songConductor = songConductor;
        this.scoreText = currScoreText;
        this.currentUser = currentUser;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // clear screen to white
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // initialize each base square
        square = new ArrayList<>();
        for (int i = 0; i < NUM_SQUARES; i++) {
            square.add(new Square(new float[]{
                    -0.15f + displacementx[i], 0.15f + displacementy[i], 0.0f,   // top left
                    -0.15f + displacementx[i], -0.15f + displacementy[i], 0.0f,   // bottom left
                    0.15f + displacementx[i], -0.15f + displacementy[i], 0.0f,   // bottom right
                    0.15f + displacementx[i], 0.15f + displacementy[i], 0.0f    // top right
            }, new float[]{0.0f, 0.0f, 0.0f, 1.0f}));
        }
        // initialize the hit variables.
        hit = new boolean[NUM_SQUARES];
        alreadyHit = new boolean[NUM_SQUARES];
        hittable = new boolean[NUM_SQUARES];
        // initialize the frame counts
        // initialize the shadow squares
        frameCount = new int[NUM_SQUARES];
        shadowSquare = new Square[NUM_SQUARES];
        for (int i = 0; i < NUM_SQUARES; i++) {
            shadowSquare[i] = null;
            hit[i] = false;
            alreadyHit[i] = false;
            hittable[i] = false;
            frameCount[i] = 1;
        }
        // define the wait index for the time needed for the next note
        waitIndex = 0;
        // define when the song is over
        songOver = false;
        activeSquares = new ArrayList<>(Collections.singletonList(0));
        moved = false;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (songConductor.isSongPlaying()) {
            if (!songOver) {
                int pos = songConductor.getPosition();
                if (pos < 0) {
                    return;
                }
                currentTime = pos;
                deltaTime = currentTime - lastFrameTime;
//                Log.i("Song Conductor", "Current song position:" + pos);
                // start game with an array of delays between each note
                deploySquareShadow(songDelay, 5, pos);
            } else {
                // move back to scoreboard when song is over
                if (!moved) {
                    moved = true;
                    Intent intent = new Intent(context, GameActivity.class);
                    intent.putExtra("frag", 1);
                    intent.putExtra("username", currentUser);
                    intent.putExtra("score", scoreText.getText().toString());
                    context.startActivity(intent);
                }
            }
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        for (int i = 0; i < NUM_SQUARES; i++) {
            square.get(i).draw(mMVPMatrix);
            if (shadowSquare[i] != null) {
                shadowSquare[i].draw(mMVPMatrix);
            }
        }
    }

    /**
     * Determine if Square at postion i is hittable
     *
     * @param i Square position
     * @return true if Square is hittable
     */
    public boolean getHittable(int i) {
        return this.hittable[i];
    }

    /**
     * Sets a square at position i as hit
     *
     * @param i Square position
     */
    public void setHit(int i) {
        this.hit[i] = true;
    }

    /**
     * Tells the FSM to change the animation color of Square at position i
     *
     * @param i Square position
     */
    public void setHitColor(int i) {
        shadowSquare[i].setColor(new float[]{0.1f, 0.6f, 0.3f, 1.0f});
    }

    /**
     * Sets a block as hittable or unhittable
     *
     * @param i        Square position
     * @param hittable true or false
     */
    private void toggleHittable(int i, boolean hittable) {
        // If they are different, take new hittable value
        this.hittable[i] = hittable;
    }

    /**
     * Loads a shader
     *
     * @param type
     * @param shaderCode
     * @return integer value of loaded shader
     */
    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Intializes a square with OpenGL - cartesian based coordinates and color
     *
     * @param sideLength     of Square
     * @param displacement_x coord
     * @param displacement_y coord
     * @param color          array
     * @return Square object to be used in rendering
     */
    private Square createSquare(float sideLength, float displacement_x, float displacement_y, float[] color) {
        return new Square(new float[]{
                -sideLength + displacement_x, sideLength + displacement_y, 0.0f,   // top left
                -sideLength + displacement_x, -sideLength + displacement_y, 0.0f,   // bottom left
                sideLength + displacement_x, -sideLength + displacement_y, 0.0f,   // bottom right
                sideLength + displacement_x, sideLength + displacement_y, 0.0f    // top right
        }, color);
    }

    public void triggerSquare(int ind, int[] i, float[] color) {
        shadowSquare[ind] = createSquare((float) i[ind] / 200, displacementx[ind], displacementy[ind], color);
    }


    /**
     * Deploys a square shadow able to be hit.
     * Approximate 600ms delay.
     *
     * @param waitTimes   the list of wait times to consider
     * @param refreshRate the time in ms to refresh the screen
     */
    private void deploySquareShadow(long[] waitTimes, int refreshRate, int songPos) {
        if (deltaTime >= refreshRate) {
            if (waitIndex != 0) {
                Iterator<Integer> it = activeSquares.iterator();
                while (it.hasNext()) {
                    processSquare(it);
                }
            }
            lastFrameTime = songPos;
        }

        if (waitIndex < waitTimes.length && waitTimes[waitIndex] != 0 && songPos > waitTimes[waitIndex]) {
            Log.i("Wait Time", "Waited: waitTime: " + waitTimes[waitIndex]);
            Log.i("Note", "Note Played...........................................------====================: "
                    + waitIndex + "/" + waitTimes.length);
            waitIndex++;

            // add new square
            Random r = new Random();
            if (activeSquares.size() >= NUM_SQUARES) {
                // we have no choice but to pick some random one to overlap
                activeSquares.add(r.nextInt(5));
            } else {
                // we have a choice to pick some spot not already chosen
                int i = r.nextInt(5);
                while (activeSquares.contains(i)) {
                    i = r.nextInt(5);
                }
                activeSquares.add(i);
            }

        }
        // if the waitIndex list has been looped entirely, or the waitTime is giving a 0 value, and the activeSquares are finished
        else if ((waitIndex >= waitTimes.length && activeSquares.isEmpty() || waitTimes[waitIndex] == 0) && activeSquares.size() == 0) {
            songOver = true;
        }

    }

    /**
     * Processes a squares movement at position *it
     *
     * @param it Integer iterator denoting current square pos
     */
    private void processSquare(Iterator<Integer> it) {
        int currSquare = it.next();
        if (frameCount[currSquare] < 30) {
            triggerSquare(currSquare, frameCount, new float[]{0.4f, 0.4f, 0.4f, 1.0f});
            if (frameCount[currSquare] >= 26) {
                if (!alreadyHit[currSquare]) {
                    toggleHittable(currSquare, true);
                } else {
                    toggleHittable(currSquare, false);
                }
            } else {
                toggleHittable(currSquare, false);
            }
            frameCount[currSquare]++;
        } else if (frameCount[currSquare] <= 60) {
            if (!alreadyHit[currSquare]) {
                toggleHittable(currSquare, true);
                if (hit[currSquare]) {
                    setHitColor(currSquare);
                    alreadyHit[currSquare] = true;
                }
            } else {
                toggleHittable(currSquare, false);
            }
            frameCount[currSquare]++;
        } else {
            // reset currSquare values
            hit[currSquare] = false;
            shadowSquare[currSquare] = null;
            alreadyHit[currSquare] = false;
            toggleHittable(currSquare, false);
            frameCount[currSquare] = 0;
            // removes the current active square
            it.remove();
        }
    }
}
