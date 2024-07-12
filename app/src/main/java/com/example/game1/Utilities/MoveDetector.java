package com.example.game1.Utilities;

import com.example.game1.Interfaces.MoveCallBack;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MoveDetector {

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    private long timestamp = 0l;

    private MoveCallBack moveCallback;

    public MoveDetector(Context context, MoveCallBack moveCallback) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.moveCallback = moveCallback;
        initEventListener();
    }

    private void initEventListener() {
        this.sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                calculateMove(x, y);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // pass
            }
        };
    }

    private void calculateMove(float x, float y) {
        if (System.currentTimeMillis() - timestamp > 300) {
            timestamp = System.currentTimeMillis();
            if (x > 2) {
                if (moveCallback != null) {
                    moveCallback.moveLeft();
                }
            } else if (x < -2) {
                if (moveCallback != null) {
                    moveCallback.moveRight();
                }
            }
            if (y > 2) {
                if (moveCallback != null) {
                    moveCallback.speedLow();
                }
            } else if (y < -2) {
                if (moveCallback != null) {
                    moveCallback.speedHigh();
                }
            }

        }
    }


    public void start() {
        sensorManager.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    public void stop() {
        sensorManager.unregisterListener(
                sensorEventListener,
                sensor
        );
    }
}