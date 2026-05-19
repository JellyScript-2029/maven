package com.maven.model;

public class Player {

    private String Username;
    private String password;
    private int score;

    public Player(String Username, String password, int score) {
        this.Username = Username;
        this.password = password;
        this.score = score;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
