package com.example.game1;

import java.util.ArrayList;

public class GameManager {

    private static GameManager instance;
    public static final int ROWS = 6;
    public static final int COLS = 3;
    public static final int BLANK = 0;
    //public static final int COIN = 1;
    public static final int OBSTACLE = 2;
    public static final int PLAYER = 3;
    private Player player;
    private int lives = 3;
    private int[][] map;;

    private GameManager() {
        Player.init();
        player = Player.getInstance();
        map = new int[ROWS][COLS];
    }

    public static void init(){
        if(instance == null)
            instance = new GameManager();
    }

    public static GameManager getInstance(){
        return instance;
    }

    public void spawnPlayer() {
        map[5][COLS / 2] = PLAYER;
    }

    public int spawnObstacle() {
        int col = (int) (Math.random() * COLS);
        map[0][col] = OBSTACLE;
        return col;
    }

    public int getPlayerPosition() {
        return player.getPosition();
    }

    public void movePlayer(int direction) {
        int position = getPlayerPosition();
        map[5][position] = BLANK;
        player.setPosition(direction);
        position = player.getPosition();
        map[5][position] = PLAYER;
    }

    public int mapPositionValue(int row, int col) {
        return map[row][col];
    }

    public void moveObject(int i, int j, int object) {
        map[i][j] = BLANK;
        map[i + 1][j] = object;
    }

    public int checkCollision() {
        int colType = BLANK; // no collision
        int playerCol = player.getPosition();
        if(map[ROWS - 2][playerCol] == OBSTACLE){
            lives--;
            colType = OBSTACLE;
        }
        return colType;
    }

    public int getLives() {
        return lives;
    }

    public void reset() {
        lives = 3;
    }

    public void cleanLastObjectRow() {
        for (int i = 0; i < COLS; i++) {
            map[ROWS - 2][i] = BLANK;
        }
    }
}
