package com.example.game1.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.game1.Game.Score;
import com.example.game1.Interfaces.ScoreCallBack;
import com.example.game1.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>{

    private ArrayList<Score> highScores;
    private ScoreCallBack scoreCallBack;

    public ScoreAdapter(ArrayList<Score> highScores) {
        this.highScores = highScores;
    }

    public void setScoreCallBack(ScoreCallBack scoreCallBack) {
        this.scoreCallBack = scoreCallBack;
    }

    @NonNull
    @Override
    public ScoreAdapter.ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_list_item, parent, false);

        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreAdapter.ScoreViewHolder holder, int position) {
        Score score = highScores.get(position);
        if(score.getName() == null || score.getName().equals(""))
            return;
        switch (position){
            case(0):
                holder.score_LBL_name.setTextSize(50);
                holder.score_LBL_score.setTextSize(50);
                holder.score_LBL_name.setTextColor(0xFFFFCE3B);
                holder.score_LBL_name.setText(highScores.get(position).getName());
                holder.score_LBL_score.setText(String.valueOf(highScores.get(position).getScore()));
                break;
            case(1):
                holder.score_LBL_name.setTextSize(45);
                holder.score_LBL_score.setTextSize(45);
                holder.score_LBL_name.setTextColor(0xFFD5D5D5);
                holder.score_LBL_name.setText(highScores.get(position).getName());
                holder.score_LBL_score.setText(String.valueOf(highScores.get(position).getScore()));
                break;
            case(2):
                holder.score_LBL_name.setTextSize(40);
                holder.score_LBL_score.setTextSize(40);
                holder.score_LBL_name.setTextColor(0xFFD59940);
                holder.score_LBL_name.setText(highScores.get(position).getName());
                holder.score_LBL_score.setText(String.valueOf(highScores.get(position).getScore()));
                break;
        }
        holder.score_LBL_name.setText(highScores.get(position).getName());
        holder.score_LBL_score.setText(String.valueOf(highScores.get(position).getScore()));
    }

    @Override
    public int getItemCount() {
        return highScores == null ? 0 : highScores.size();
    }

    public class ScoreViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView score_LBL_name;
        private MaterialTextView score_LBL_score;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            score_LBL_name = itemView.findViewById(R.id.score_LBL_name);
            score_LBL_score = itemView.findViewById(R.id.score_LBL_score);
            score_LBL_name.setOnClickListener( v -> {
                scoreCallBack.showLocationInMap(highScores.get(getAdapterPosition()));
            });
        }
    }
}
