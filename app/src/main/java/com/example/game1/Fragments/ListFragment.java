package com.example.game1.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.game1.Adapters.ScoreAdapter;
import com.example.game1.Game.GameManager;
import com.example.game1.Interfaces.ScoreCallBack;
import com.example.game1.R;

public class ListFragment extends Fragment {

    private RecyclerView list_LST_scores;
    private GameManager gameManager;

    private ScoreCallBack scoreCallBack;

    public void setListCallBack(ScoreCallBack scoreCallBack) {
        this.scoreCallBack = scoreCallBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        gameManager = GameManager.getInstance();


        list_LST_scores = view.findViewById(R.id.list_LST_scores);

        ScoreAdapter scoreAdapter = new ScoreAdapter(gameManager.getHighScores());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list_LST_scores.setLayoutManager(linearLayoutManager);
        list_LST_scores.setAdapter(scoreAdapter);
        scoreAdapter.setScoreCallBack((score) -> {
            if (scoreCallBack != null) {
                scoreCallBack.showLocationInMap(score);

            }
        });


        return view;
    }

}
