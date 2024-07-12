package com.example.game1.Utilities;

import android.app.Application;

import com.example.game1.Game.GameManager;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.init(this);
        MySignal.init(this);
        MSPV3.init(this);
        GameManager.init();
    }
}
