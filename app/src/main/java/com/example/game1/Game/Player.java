package com.example.game1.Game;

import android.widget.ImageView;

import com.example.game1.R;
import com.example.game1.Utilities.Constants;
import com.example.game1.Utilities.ImageLoader;

public class Player {

    private static Player instance;
    private int position;


    private Player() {
        position = Constants.MIDDLE;
    }

    public static void init() {
        if (instance == null) {
            instance = new Player();
        }
    }

    public static Player getInstance() {
        return instance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int direction) {
        if (position == 0 && direction == -1) {
            position = 0;
        } else if (position == Constants.COLS - 1 && direction == 1)
            position = Constants.COLS - 1;
        else
            position += direction;
    }

    public void resetPlayerPos() {
        position = Constants.MIDDLE;
    }
}
