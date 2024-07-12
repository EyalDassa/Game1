package com.example.game1.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.game1.Game.GameManager;
import com.example.game1.Interfaces.MoveCallBack;
import com.example.game1.Utilities.Constants;
import com.example.game1.Utilities.ImageLoader;
import com.example.game1.Utilities.MSPV3;
import com.example.game1.Utilities.MoveDetector;
import com.example.game1.Utilities.MySignal;
import com.example.game1.Game.Player;
import com.example.game1.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    private AppCompatImageView[][] map;
    private AppCompatImageView[] game_IMG_hearts;
    private MaterialButton game_BTN_left;
    private MaterialButton game_BTN_right;
    private MaterialTextView game_LBL_score;
    private GameManager gameManager;
    private Player player;
    private int speed = Constants.SPEED_REG;
    private boolean toSpawn = true;
    private MySignal signal;
    private MoveDetector moveDetector;
    private int mode;
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        gameManager = GameManager.getInstance();

        player = Player.getInstance();
        signal = MySignal.getInstance();
        restartGameState();
        spawnPlayer();
        mode = getIntent().getExtras().getInt("MODE");
        lat = getIntent().getExtras().getDouble("LAT");
        lon = getIntent().getExtras().getDouble("LON");
        initViews();


    }

    private void initViews() {
        new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        switch (mode) {
            case (Constants.GAME_MODE_BUTTONS):
                game_BTN_left.setOnClickListener(v -> movePlayer(-1));
                game_BTN_right.setOnClickListener(v -> movePlayer(1));
                break;

            case (Constants.GAME_MODE_SENSORS):
                game_BTN_left.setVisibility(View.INVISIBLE);
                game_BTN_right.setVisibility(View.INVISIBLE);
                game_BTN_left.setActivated(false);
                game_BTN_right.setActivated(false);
                initMoveDetector();
                break;
        }

    }

    private void initMoveDetector() {
        moveDetector = new MoveDetector(this,
                new MoveCallBack() {

                    @Override
                    public void moveRight() {
                        movePlayer(1);
                    }

                    @Override
                    public void moveLeft() {
                        movePlayer(-1);
                    }

                    @Override

                    public void speedHigh() {
                        speed = Constants.SPEED_FAST;
                    }

                    @Override
                    public void speedLow() {
                        speed = Constants.SPEED_REG;
                    }
                });
    }

    private void restartGameState() {
        gameManager.restartGameState();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
        if (mode == Constants.GAME_MODE_SENSORS)
            moveDetector.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
        if (mode == Constants.GAME_MODE_SENSORS)
            moveDetector.stop();

    }

    private void updateMap() {
        int object;

        checkCollision();
        updateScore();
        cleanLastObjectRow();
        for (int i = Constants.ROWS - 3; i >= 0; i--) {
            for (int j = Constants.COLS - 1; j >= 0; j--) {
                object = gameManager.mapPositionValue(i, j);
                if (object == Constants.STAR && gameManager.getLives() == 3)
                    cleanSingle(i, j);
                else if (object != Constants.BLANK) {
                    moveObject(i, j, object);
                }
            }
        }
    }

    private void loseGame() {
        stopTimer();
        if (mode == Constants.GAME_MODE_SENSORS)
            moveDetector.stop();
        saveGameScore();
    }

    private void saveGameScore() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("SAVE YOUR SCORE");
        alert.setMessage("Enter your name:");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("SAVE", (dialog, whichButton) -> {
            String name = input.getText().toString();
            gameManager.saveGameScore(name, lat, lon);
            finish();
        });

        alert.setNegativeButton("CANCEL", (dialog, which) -> {
            finish();
        });

        alert.show();
    }

    private void updateScore() {
        gameManager.updateScore();
        game_LBL_score.setText(String.valueOf(gameManager.getScore()));
    }

    private void cleanSingle(int i, int j) {
        gameManager.cleanSingle(i, j);
        map[i][j].setImageResource(0);
    }

    private void cleanLastObjectRow() {
        gameManager.cleanLastObjectRow();
        for (int i = 0; i < Constants.COLS; i++) {
            map[Constants.ROWS - 2][i].setImageResource(0);
        }
    }

    private void updateLivesUI() {
        int len = game_IMG_hearts.length;

        for (int i = 0; i < len; i++) {
            game_IMG_hearts[i].setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < len - gameManager.getLives(); i++) {
            game_IMG_hearts[len - i - 1].setVisibility(View.INVISIBLE);
        }
    }

    private void checkCollision() {
        int colType = gameManager.checkCollision();
        if (colType == Constants.OBSTACLE) {
            signal.playHit();
            updateLivesUI();
            signal.toast("ASTROID HIT!");
            signal.vibrate();
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
            map[Constants.ROWS - 1][gameManager.getPlayerPosition()].startAnimation(animation);

        } else if (colType == Constants.STAR) {
            signal.playStar();
            updateLivesUI();
            signal.toast("STAR!");
            signal.vibrate();
        }
        if (gameManager.getLives() == 0) {
            loseGame();
        }

    }

    private void moveObject(int i, int j, int object) {
        map[i][j].setImageResource(0);
        gameManager.moveObject(i, j, object);
        if (object == Constants.STAR)
        ImageLoader.getInstance().load(R.drawable.img_star, map[i + 1][j]);
        else
            ImageLoader.getInstance().load(R.drawable.img_astroid, map[i + 1][j]);
    }

    private void spawnObstacle() {
        int col = gameManager.spawnObstacle();
        ImageLoader.getInstance().load(R.drawable.img_astroid, map[0][col]);
    }

    private void spawnStar() {
        int col = gameManager.spawnStar();
        ImageLoader.getInstance().load(R.drawable.img_star, map[0][col]);
    }


    private void spawnObject() {
        int num = (int) (Math.random() * 10);
        if (num >= 7 && gameManager.getLives() < 3)
            spawnStar();
        else
            spawnObstacle();
    }

    private void spawnPlayer() {
        gameManager.spawnPlayer();
        AppCompatImageView img = map[Constants.ROWS - 1][Constants.MIDDLE];
        ImageLoader.getInstance().load(R.drawable.img_astronaut, img);
    }

    private void movePlayer(int direction) {
        signal.vibrateClick();
        map[Constants.ROWS - 1][gameManager.getPlayerPosition()].setImageResource(0);
        gameManager.movePlayer(direction);
        AppCompatImageView img = map[Constants.ROWS - 1][gameManager.getPlayerPosition()];
        ImageLoader.getInstance().load(R.drawable.img_astronaut, img);
    }

    /**
     * to change the size of the matrix, change the ROWS, COLS constants at Constnats.java
     * modify the activity_main.xml file to have the right number of images
     * then find the correct views inside this function
     */
    private void findViews() {

        map = new AppCompatImageView[Constants.ROWS][Constants.COLS];
        map[0][0] = findViewById(R.id.game_IMG_00);
        map[0][1] = findViewById(R.id.game_IMG_01);
        map[0][2] = findViewById(R.id.game_IMG_02);
        map[0][3] = findViewById(R.id.game_IMG_03);
        map[0][4] = findViewById(R.id.game_IMG_04);
        map[1][0] = findViewById(R.id.game_IMG_10);
        map[1][1] = findViewById(R.id.game_IMG_11);
        map[1][2] = findViewById(R.id.game_IMG_12);
        map[1][3] = findViewById(R.id.game_IMG_13);
        map[1][4] = findViewById(R.id.game_IMG_14);
        map[2][0] = findViewById(R.id.game_IMG_20);
        map[2][1] = findViewById(R.id.game_IMG_21);
        map[2][2] = findViewById(R.id.game_IMG_22);
        map[2][3] = findViewById(R.id.game_IMG_23);
        map[2][4] = findViewById(R.id.game_IMG_24);
        map[3][0] = findViewById(R.id.game_IMG_30);
        map[3][1] = findViewById(R.id.game_IMG_31);
        map[3][2] = findViewById(R.id.game_IMG_32);
        map[3][3] = findViewById(R.id.game_IMG_33);
        map[3][4] = findViewById(R.id.game_IMG_34);
        map[4][0] = findViewById(R.id.game_IMG_40);
        map[4][1] = findViewById(R.id.game_IMG_41);
        map[4][2] = findViewById(R.id.game_IMG_42);
        map[4][3] = findViewById(R.id.game_IMG_43);
        map[4][4] = findViewById(R.id.game_IMG_44);
        map[5][0] = findViewById(R.id.game_IMG_50);
        map[5][1] = findViewById(R.id.game_IMG_51);
        map[5][2] = findViewById(R.id.game_IMG_52);
        map[5][3] = findViewById(R.id.game_IMG_53);
        map[5][4] = findViewById(R.id.game_IMG_54);
        map[6][0] = findViewById(R.id.game_IMG_60);
        map[6][1] = findViewById(R.id.game_IMG_61);
        map[6][2] = findViewById(R.id.game_IMG_62);
        map[6][3] = findViewById(R.id.game_IMG_63);
        map[6][4] = findViewById(R.id.game_IMG_64);
        map[7][0] = findViewById(R.id.game_IMG_70);
        map[7][1] = findViewById(R.id.game_IMG_71);
        map[7][2] = findViewById(R.id.game_IMG_72);
        map[7][3] = findViewById(R.id.game_IMG_73);
        map[7][4] = findViewById(R.id.game_IMG_74);
        map[8][0] = findViewById(R.id.game_IMG_80);
        map[8][1] = findViewById(R.id.game_IMG_81);
        map[8][2] = findViewById(R.id.game_IMG_82);
        map[8][3] = findViewById(R.id.game_IMG_83);
        map[8][4] = findViewById(R.id.game_IMG_84);


        game_BTN_left = findViewById(R.id.game_BTN_left);
        game_BTN_right = findViewById(R.id.game_BTN_right);

        game_LBL_score = findViewById(R.id.game_LBL_score);

        game_IMG_hearts = new AppCompatImageView[]{
                findViewById(R.id.game_IMG_heart1),
                findViewById(R.id.game_IMG_heart2),
                findViewById(R.id.game_IMG_heart3),
        };

    }

    private void tick() {
        updateMap();
        if (toSpawn)
            spawnObject();
        toSpawn = !toSpawn;
    }

    private final Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            handler.postDelayed(runnable, speed);
            tick();
        }
    };
    ;

    private void stopTimer() {
        handler.removeCallbacks(runnable);
    }

    private void startTimer() {
        handler.postDelayed(runnable, speed);

    }

}

