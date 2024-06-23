package com.example.game1;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {

    private AppCompatImageView[][] map;
    private AppCompatImageView[] game_IMG_hearts;
    private MaterialButton game_BTN_left;
    private MaterialButton game_BTN_right;
    private GameManager gameManager;
    private Player player;
    private int middle;
    private final int DELAY = 1000;
    private boolean toSpawn = true;
    private MySignal signal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        GameManager.init();
        gameManager = GameManager.getInstance();
        player = Player.getInstance();
        MySignal.init(this);
        signal = MySignal.getInstance();
        middle = GameManager.COLS / 2;
        spawnPlayer();

        game_BTN_left.setOnClickListener(v -> movePlayer(-1));
        game_BTN_right.setOnClickListener(v -> movePlayer(1));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    private void updateMap() {
        int object;

        checkCollision();
        cleanLastObjectRow();
        for (int i = GameManager.ROWS - 3; i >= 0 ; i--) {
            for (int j = GameManager.COLS - 1; j >= 0 ; j--) {
                object = gameManager.mapPositionValue(i, j);
                if (object != GameManager.BLANK){
                    moveObject(i, j,object);
                }
            }
        }
    }

    private void cleanLastObjectRow() {
        gameManager.cleanLastObjectRow();
        for (int i = 0; i < GameManager.COLS; i++) {
            map[GameManager.ROWS - 2][i].setImageResource(0);
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
        if(colType == GameManager.OBSTACLE)
        {
            playSound();
            updateLivesUI();
            signal.toast("ASTROID HIT!");
            signal.vibrate();
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
            map[GameManager.ROWS - 1][gameManager.getPlayerPosition()].startAnimation(animation);
            
        }
        if(gameManager.getLives() == 0){
            gameManager.reset(); // endless game
            updateLivesUI();
            signal.toast("Endless Game! Have Fun");
        }

    }

    private void moveObject(int i, int j, int object) {
        map[i][j].setImageResource(0);
        gameManager.moveObject(i, j,object);
        map[i+1][j].setImageResource(R.drawable.img_astroid);
    }

    private void spawnObstacle() {
        int col = gameManager.spawnObstacle();
        map[0][col].setImageResource(R.drawable.img_astroid);
    }

    private void spawnPlayer() {
        gameManager.spawnPlayer();
        map[GameManager.ROWS - 1][middle].setImageResource(player.getImage());
    }

    private void movePlayer(int direction) {
        signal.vibrateClick();
        map[GameManager.ROWS - 1][gameManager.getPlayerPosition()].setImageResource(0);
        gameManager.movePlayer(direction);
        map[GameManager.ROWS - 1][gameManager.getPlayerPosition()].setImageResource(player.getImage());
    }

    private void findViews() {

        map = new AppCompatImageView[GameManager.ROWS][GameManager.COLS];
        map[0][0] = findViewById(R.id.game_IMG_00);
        map[0][1] = findViewById(R.id.game_IMG_01);
        map[0][2] = findViewById(R.id.game_IMG_02);
        map[1][0] = findViewById(R.id.game_IMG_10);
        map[1][1] = findViewById(R.id.game_IMG_11);
        map[1][2] = findViewById(R.id.game_IMG_12);
        map[2][0] = findViewById(R.id.game_IMG_20);
        map[2][1] = findViewById(R.id.game_IMG_21);
        map[2][2] = findViewById(R.id.game_IMG_22);
        map[3][0] = findViewById(R.id.game_IMG_30);
        map[3][1] = findViewById(R.id.game_IMG_31);
        map[3][2] = findViewById(R.id.game_IMG_32);
        map[4][0] = findViewById(R.id.game_IMG_40);
        map[4][1] = findViewById(R.id.game_IMG_41);
        map[4][2] = findViewById(R.id.game_IMG_42);
        map[5][0] = findViewById(R.id.game_IMG_50);
        map[5][1] = findViewById(R.id.game_IMG_51);
        map[5][2] = findViewById(R.id.game_IMG_52);

        game_BTN_left = findViewById(R.id.game_BTN_left);
        game_BTN_right = findViewById(R.id.game_BTN_right);

        game_IMG_hearts = new AppCompatImageView[] {
                findViewById(R.id.game_IMG_heart1),
                findViewById(R.id.game_IMG_heart2),
                findViewById(R.id.game_IMG_heart3),
        };

    }

    private void tick() {
        updateMap();
        if(toSpawn)
            spawnObstacle();
        toSpawn = !toSpawn;
    }

    private final Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            handler.postDelayed(runnable, DELAY);
            tick();
        }
    };;

    private void stopTimer() {
        handler.removeCallbacks(runnable);
    }

    private void startTimer() {
        handler.postDelayed(runnable, DELAY);

    }


    private void playSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

