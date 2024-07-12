package com.example.game1.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game1.Fragments.ListFragment;
import com.example.game1.Fragments.MapFragment;
import com.example.game1.Game.Score;
import com.example.game1.Interfaces.ScoreCallBack;
import com.example.game1.R;


public class HighScoreActivity extends AppCompatActivity {

    private ListFragment fragmentList;
    private MapFragment fragmentMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        fragmentList = new ListFragment();
        fragmentMap = new MapFragment();

        fragmentList.setListCallBack(showLocationCallBack);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.hs_LAY_top, fragmentList)
                .add(R.id.hs_LAY_bottom, fragmentMap)
                .commit();
    }

    private ScoreCallBack showLocationCallBack = new ScoreCallBack() {
        @Override
        public void showLocationInMap(Score score) {
            fragmentMap.setLocation(score);
        }
    };

}