package com.example.kabicin.rhythmkk;

public class User {
    private String username;
    private String score;

    /**
     * Constructor for user class
     *
     * @param username
     * @param score
     */
    public User(String username, String score) {
        this.username = username;
        this.score = score;
    }

    /**
     * Username getter
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Score getter
     *
     * @return score
     */
    public String getScore() {
        return score;
    }

}
