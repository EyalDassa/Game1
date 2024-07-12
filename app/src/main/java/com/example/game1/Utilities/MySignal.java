package com.example.game1.Utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import com.example.game1.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MySignal {

    private static MySignal instance;
    private static MediaPlayer hitSound;
    private static MediaPlayer plusLife;
    private Context context;

    private MySignal(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new MySignal(context.getApplicationContext());
            hitSound = MediaPlayer.create(context, R.raw.punch);
            plusLife = MediaPlayer.create(context, R.raw.plus_life);
        }
    }

    public static MySignal getInstance() {
        return instance;
    }

    public void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void openDialog(String title, String message, String button) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(button, null);
        builder.create().show();
    }

    public void vibrate() {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    public void vibrateClick() {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            // deprecated in API 26
            v.vibrate(20);
        }
    }

    public void playHit() {
        hitSound.start();
    }

    public void playStar() {
        plusLife.start();
    }

}
