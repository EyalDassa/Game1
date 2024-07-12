package com.example.game1.Game;

import com.example.game1.Utilities.Constants;

import android.util.Log;

import com.example.game1.Utilities.MSPV3;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class GameManager {

    private static GameManager instance;
    private Player player;
    private MSPV3 mspv3;
    private int lives = 3;
    private int score = 0;
    private int[][] map;
    ;
    private ArrayList<Score> highScores;
    private Gson gson;

    private GameManager() {
        Player.init();
        gson = new Gson();
        highScores = new ArrayList<>();
        player = Player.getInstance();
        mspv3 = MSPV3.getInstance();
        map = new int[Constants.ROWS][Constants.COLS];
    }

    public static void init() {
        if (instance == null)
            instance = new GameManager();
    }

    public static GameManager getInstance() {
        return instance;
    }

    public void spawnPlayer() {
        map[Constants.ROWS - 1][Constants.COLS / 2] = Constants.PLAYER;
    }

    public int spawnObstacle() {
        int col = (int) (Math.random() * Constants.COLS);
        map[0][col] = Constants.OBSTACLE;
        return col;
    }

    public int spawnStar() {
        int col = (int) (Math.random() * Constants.COLS);
        map[0][col] = Constants.STAR;
        return col;
    }

    public int getPlayerPosition() {
        return player.getPosition();
    }

    public void movePlayer(int direction) {
        int position = getPlayerPosition();
        map[Constants.ROWS - 1][position] = Constants.BLANK;
        player.setPosition(direction);
        position = player.getPosition();
        map[Constants.ROWS - 1][position] = Constants.PLAYER;
    }

    public int mapPositionValue(int row, int col) {
        return map[row][col];
    }

    public void moveObject(int i, int j, int object) {
        map[i][j] = Constants.BLANK;
        map[i + 1][j] = object;
    }

    public int checkCollision() {
        int colType = Constants.BLANK; // no collision
        int playerCol = player.getPosition();
        if (map[Constants.ROWS - 2][playerCol] == Constants.OBSTACLE) {
            lives--;
            colType = Constants.OBSTACLE;
        } else if (map[Constants.ROWS - 2][playerCol] == Constants.STAR) {
            lives++;
            colType = Constants.STAR;
        }
        return colType;
    }

    public int getLives() {
        return lives;
    }

    public void resetLives() {
        lives = 3;
    }

    public void cleanLastObjectRow() {
        for (int i = 0; i < Constants.COLS; i++) {
            map[Constants.ROWS - 2][i] = Constants.BLANK;
        }
    }

    public void cleanSingle(int i, int j) {
        map[i][j] = Constants.BLANK;
    }

    public int getScore() {
        return score;
    }

    public void updateScore() {
        score++;
    }


    public void loadScoresFromSP() {
        highScores.clear();
        String m = mspv3.readString("highScores", "");
        Log.d("highScoresLoad", m);
        highScores = gson.fromJson(mspv3.readString("highScores", "[]"), new TypeToken<ArrayList<Score>>() {
        }.getType());
    }


    public ArrayList<Score> getHighScores() {
        return highScores;
    }

    public void saveGameScore(String name, double lat, double lon) {
        Score newScore = new Score(score, name, lat, lon);
        highScores.add(newScore);
        highScores.sort((s1, s2) -> s2.getScore() - s1.getScore());
        String highScoresString = gson.toJson(highScores);
        mspv3.saveString("highScores", highScoresString);
        restartGameState();
    }

    public void restartGameState() {
        resetLives();
        score = 0;
        player.resetPlayerPos();
        for (int i = 0; i < Constants.ROWS; i++) {
            for (int j = 0; j < Constants.COLS; j++) {
                map[i][j] = Constants.BLANK;
            }
        }
    }
}
