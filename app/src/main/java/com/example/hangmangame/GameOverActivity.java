package com.example.hangmangame;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class GameOverActivity extends AppCompatActivity {

    TextView trueCorrectWord,falseCorrectWord, attemptsCount;
    Button playAgain;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_over);
        hideSystemUI();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = MediaPlayer.create(this, R.raw.go);
        mediaPlayer.start();
        trueCorrectWord = findViewById(R.id.trueCorrectWord);
        falseCorrectWord=findViewById(R.id.falseCorrectWord);
        playAgain = findViewById(R.id.playAgainButton);
        attemptsCount=findViewById(R.id.attemptsCount);
        Intent intent = getIntent();
        ArrayList<String> trueWords = intent.getStringArrayListExtra("yesWords");
        if (!trueWords.isEmpty()) {
            trueCorrectWord.setText(String.join(", ", trueWords)); // מציג מופרד בפסיקים
        }
        ArrayList<String> falseWords = intent.getStringArrayListExtra("noWords");
        if (!falseWords.isEmpty()) {
            falseCorrectWord.setText(String.join(", ", falseWords)); // מציג מופרד בפסיקים
        }
        attemptsCount.setText(String.valueOf(intent.getIntExtra("wrongCount",0)));



        playAgain.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, GameActivity.class);
            startActivity(intent1);
            finish();
        });

    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}