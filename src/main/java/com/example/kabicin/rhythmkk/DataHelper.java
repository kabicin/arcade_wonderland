package com.example.kabicin.rhythmkk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "score_table";
    private static final String COL1 = "name";
    private static final String COL2 = "score";
    private static final String COL3 = "lishi_score";
    private SQLiteDatabase db;

    /**
     * Creates a DataHelper given a context
     *
     * @param context to run db on
     */
    public DataHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL1 + " TEXT, " +
                COL2 + " INTEGER, " +
                COL3 + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Determine if the user with current username exists in the database
     *
     * @param username to find
     * @return true if user is in database
     */
    public boolean userExists(String username) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 + "='" + username + "';";
        return db.rawQuery(query, null).getCount() != 0;
    }

    /**
     * Creates a user
     *
     * @param username to store
     */
    public boolean createUser(String username) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, username);
        contentValues.put(COL2, 0);
        contentValues.put(COL3, 0);
        return db.insert(TABLE_NAME, null, contentValues) != -1;
    }

    /**
     * Obtain the score that the user has won
     *
     * @param username of player
     * @return the score of player or 0 if the player does not exist
     */
    private int getScoreByUser(String username) {
        if (userExists(username)) {
            String query = "SELECT " + COL2 + " FROM " + TABLE_NAME + " WHERE " + COL1 + "='" + username + "';";
            Cursor cursor = db.rawQuery(query, null);
            String score = "";
            while (cursor.moveToNext()) {
                score = cursor.getString(0);
            }
            return Integer.valueOf(score);
        }
        return 0;
    }

    /**
     * Add a user to the database with a score attrib
     *
     * @param username of current player
     * @param score    achieved
     * @return true if entered into database
     */
    public boolean addData(String username, String score) {

        if (getScoreByUser(username) < Integer.valueOf(score)) {
            // if user already exists then update instead of insert
            if (userExists(username)) {
                updateUser(username, score);
                return true;
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(COL1, username);
            contentValues.put(COL2, score);
            contentValues.put(COL3, 0);
            return db.insert(TABLE_NAME, null, contentValues) != -1;
        }
        return false;
    }

    /**
     * Update the user of the given username with score
     */
    private void updateUser(String username, String score) {
        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 + "='" + score + "' WHERE " + COL1 + "='" + username + "';";
        db.execSQL(query);
    }

    /**
     * Obtain the cursor to all the data on file
     *
     * @return Cursor of data
     */
    public Cursor getData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Obtain lishi score from database
     *
     * @param username to find lishi score of
     */
    public int getLishiScore(String username) {
        String query = "SELECT " + COL3 + " FROM " + TABLE_NAME + " WHERE " + COL1 + "='" + username + "';";
        Cursor cursor = db.rawQuery(query, null);
        Log.i("lishiScore: ", "getting: " + cursor.getCount() + " and " + cursor.getColumnCount());
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                return Integer.valueOf(cursor.getString(0));
            }
        }
        return 0;
    }

    /**
     * Set the lishi score to score assuming the user exists
     *
     * @param username   to set lishi score
     * @param lishiScore to set
     */
    public void setLishiScore(String username, String lishiScore) {
        String query = "UPDATE " + TABLE_NAME + " SET " + COL3 + "='" + lishiScore + "' WHERE " + COL1 + "='" + username + "';";
        db.execSQL(query);
    }
}
