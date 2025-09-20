package com.example.hangmangame;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {


    MaterialButton StartGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        StartGame = findViewById(R.id.StartGame);

        StartGame.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });



    }
}