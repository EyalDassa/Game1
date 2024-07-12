package com.example.game1.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.game1.Game.GameManager;
import com.example.game1.R;
import com.example.game1.Utilities.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MenuActivity extends AppCompatActivity {

    private ExtendedFloatingActionButton menu_BTN_buttons;
    private ExtendedFloatingActionButton menu_BTN_sensors;
    private ExtendedFloatingActionButton menu_BTN_highScore;
    private double lat;
    private double lon;

    private GameManager gameManager;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViews();
        gameManager = GameManager.getInstance();
        loadScoresFromSP();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastKnownLocation();
        }

        initViews();
    }

    private void loadScoresFromSP() {
        gameManager.loadScoresFromSP();
    }

    private void findViews() {
        menu_BTN_buttons = findViewById(R.id.menu_BTN_buttons);
        menu_BTN_sensors = findViewById(R.id.menu_BTN_sensors);
        menu_BTN_highScore = findViewById(R.id.menu_BTN_highScore);
    }

    private void initViews() {
        Bundle bundle = new Bundle();
        menu_BTN_sensors.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            bundle.putDouble("LAT", lat);
            bundle.putDouble("LON", lon);
            bundle.putInt("MODE", Constants.GAME_MODE_SENSORS);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        menu_BTN_buttons.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            bundle.putDouble("LAT", lat);
            bundle.putDouble("LON", lon);
            bundle.putInt("MODE", Constants.GAME_MODE_BUTTONS);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        menu_BTN_highScore.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), HighScoreActivity.class);
            startActivity(intent);
        });
    }

    private void getLastKnownLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation();
            } else {
                finish();
            }
        }
    }
}
