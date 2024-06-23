package com.example.game1;

public class Player {

    private static Player instance;
    private int image;
    private int position = GameManager.COLS / 2;


    private Player() {
        image = R.drawable.img_astronaut;
    }

    public static void init(){
        if (instance == null) {
            instance = new Player();
        }
    }

    public static Player getInstance() {
        return instance;
    }

    public int getImage() {
        return image;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int direction) {
        if (position == 0 && direction == -1) {
                position = 0;
        } else if (position == GameManager.COLS - 1 && direction == 1)
                position = GameManager.COLS - 1;
        else
                position += direction;
    }
}
