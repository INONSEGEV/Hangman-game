package com.example.hangmangame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameOverActivity extends AppCompatActivity {

    TextView correctWordTEXT;
    Button playAgain;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_over);
        mediaPlayer = MediaPlayer.create(this, R.raw.go);
        mediaPlayer.start();
        correctWordTEXT = findViewById(R.id.correctWord);
        playAgain = findViewById(R.id.playAgainButton);
        Intent intent = getIntent();
        correctWordTEXT.setText(intent.getStringExtra("theWord"));

        playAgain.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, GameActivity.class);
            startActivity(intent1);
            finish();
        });

    }


        // יוצרים Runnable שיבצע מעבר לאחר 2 שניות
//        new android.os.Handler().postDelayed(() -> {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish(); // סוגר את הדף הנוכחי
//        }, 2000); // 2000 מילישניות = 2 שניות
}