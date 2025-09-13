package com.example.hangmangame;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    Button btn_א, btn_ב, btn_ג, btn_ד, btn_ה, btn_ו, btn_ז,
            btn_ח, btn_ט, btn_י, btn_כ, btn_ל, btn_מ, btn_נ,
            btn_ס, btn_ע, btn_פ, btn_צ, btn_ק, btn_ר, btn_ש,
            btn_ת, btn_ך, btn_ם, btn_ן, btn_ף, btn_ץ;
    Button[] buttons;

    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, livesCount, scoreCount;

    int wrongCount = 0, score = 0;

    String[] words;
    String selectedWord;
    TextView[] textViews;
    HangmanStepDrawView hangmanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        hideSystemUI();


        // מאגר מילים
        words =new String[] {
                "מחשב", "תפוח", "ספר", "תכנות", "גיטרה", "חלון", "שולחן",
                "תמונה", "עכבר", "טלפון", "מקלדת", "ספריה", "כדור", "מכונית",
                "סוס", "בית", "דג", "תפוז", "חולצה", "שעון"
        };

        // בחירת מילה אקראית
        Random random = new Random();
        selectedWord = words[random.nextInt(words.length)];
        Log.d("DEBUG", "Selected Word: " + selectedWord);

        // TextViews להצגת המילה
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);
        tv7 = findViewById(R.id.tv7);
        tv8 = findViewById(R.id.tv8);
        tv9 = findViewById(R.id.tv9);
        livesCount = findViewById(R.id.livesCount);
        scoreCount = findViewById(R.id.scoreCount);

        scoreCount.setText(0 + "/" + selectedWord.length());

        textViews = new TextView[]{tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9};

        // הצגת קווים ראשוניים
        for (int i = 0; i < selectedWord.length(); i++) {
            textViews[i].setText("_");
        }

        // כפתורים
        btn_א = findViewById(R.id.btn_א);
        btn_ב = findViewById(R.id.btn_ב);
        btn_ג = findViewById(R.id.btn_ג);
        btn_ד = findViewById(R.id.btn_ד);
        btn_ה = findViewById(R.id.btn_ה);
        btn_ו = findViewById(R.id.btn_ו);
        btn_ז = findViewById(R.id.btn_ז);
        btn_ח = findViewById(R.id.btn_ח);
        btn_ט = findViewById(R.id.btn_ט);
        btn_י = findViewById(R.id.btn_י);
        btn_כ = findViewById(R.id.btn_כ);
        btn_ל = findViewById(R.id.btn_ל);
        btn_מ = findViewById(R.id.btn_מ);
        btn_נ = findViewById(R.id.btn_נ);
        btn_ס = findViewById(R.id.btn_ס);
        btn_ע = findViewById(R.id.btn_ע);
        btn_פ = findViewById(R.id.btn_פ);
        btn_צ = findViewById(R.id.btn_צ);
        btn_ק = findViewById(R.id.btn_ק);
        btn_ר = findViewById(R.id.btn_ר);
        btn_ש = findViewById(R.id.btn_ש);
        btn_ת = findViewById(R.id.btn_ת);
        btn_ך = findViewById(R.id.btn_ך);
        btn_ם = findViewById(R.id.btn_ם);
        btn_ן = findViewById(R.id.btn_ן);
        btn_ף = findViewById(R.id.btn_ף);
        btn_ץ = findViewById(R.id.btn_ץ);

        buttons = new Button[]{
                btn_א, btn_ב, btn_ג, btn_ד, btn_ה, btn_ו, btn_ז,
                btn_ח, btn_ט, btn_י, btn_כ, btn_ל, btn_מ, btn_נ,
                btn_ס, btn_ע, btn_פ, btn_צ, btn_ק, btn_ר, btn_ש,
                btn_ת, btn_ך, btn_ם, btn_ן, btn_ף, btn_ץ
        };


        // Hangman אנימציה
        hangmanView = findViewById(R.id.hangmanView);

        // Listener לכל הכפתורים
        View.OnClickListener letterClickListener = v -> {
            Button b = (Button) v;
            String letter = b.getText().toString();
            b.setEnabled(false);
            b.setVisibility(View.INVISIBLE);

            boolean updated = false;
            for (int i = 0; i < selectedWord.length(); i++) {
                if (String.valueOf(selectedWord.charAt(i)).equals(letter)) {
                    textViews[i].setText(letter);
                    score++;
                    updated = true;
                }
            }

            scoreCount.setText(score + "/" + selectedWord.length());

            if (updated) {
                checkWin(); // בדיקה אם ניצחת
            } else {
                wrongCount++; // טעות נוספת
                livesCount.setText(String.valueOf(hangmanView.getMaxSteps() - wrongCount));
                hangmanView.drawNextStep(); // מצייר את השלב הבא

                if (wrongCount >= hangmanView.getMaxSteps()) {
                    for (Button btn : buttons) {
                        btn.setEnabled(false);
                    }
                    showGameOver();
                }
            }
        };

        for (Button b : buttons) {
            b.setOnClickListener(letterClickListener);
        }
    }

    // בדיקה אם כל האותיות נחשפו
    private void checkWin() {
        boolean won = true;
        for (int i = 0; i < selectedWord.length(); i++) {
            if (textViews[i].getText().toString().equals("_")) {
                won = false;
                break;
            }
        }

        if (won) {
            Intent intent = new Intent(this, GgActivity.class);
            intent.putExtra("theWord", selectedWord);
            intent.putExtra("wrongCount", wrongCount);
            startActivity(intent);
            finish();
        }
    }

    private void showGameOver() {
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(this, GameOverActivity.class);
            intent.putExtra("theWord", selectedWord);
            startActivity(intent);
            finish();
        }, 1500); // 2000 מילישניות = 2 שניות

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
