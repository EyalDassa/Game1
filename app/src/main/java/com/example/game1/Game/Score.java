package com.example.game1.Game;

public class Score {
    private int score;
    private String name;
    private double locationX;
    private double locationY;

    public Score(int score, String name, double locationX, double locationY) {
        this.score = score;
        this.name = name;
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public double getLocationX() {
        return locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    @Override
    public String toString() {
        return name + " - " + score + "\n";
    }
}
