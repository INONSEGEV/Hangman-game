package com.example.hangmangame;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class GameActivity extends AppCompatActivity {

    Button btn_א, btn_ב, btn_ג, btn_ד, btn_ה, btn_ו, btn_ז,
            btn_ח, btn_ט, btn_י, btn_כ, btn_ל, btn_מ, btn_נ,
            btn_ס, btn_ע, btn_פ, btn_צ, btn_ק, btn_ר, btn_ש,
            btn_ת, btn_ך, btn_ם, btn_ן, btn_ף, btn_ץ;
    Button[] buttons;

    TextView livesCount, scoreCount;

    int wrongCount = 0, score = 0,wrongCountAll=0,scoreAll=0;
    int wordIndex = 0;

    ArrayList<String> words,yesWords,noWords;
    String selectedWord;
    TextView[] textViews;
    HangmanStepDrawView hangmanView;
    FlexboxLayout lettersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        hideSystemUI();

        // מאגר מילים
        words = new ArrayList<>(Arrays.asList(
                "מחשב", "תפוח"
        ));
        yesWords=new ArrayList<>();
        noWords=new ArrayList<>();

        // מצביעים ל־UI
        livesCount = findViewById(R.id.livesCount);
        scoreCount = findViewById(R.id.scoreCount);
        lettersContainer = findViewById(R.id.lettersContainer);
        hangmanView = findViewById(R.id.hangmanView);

        // כפתורים
        initButtons();

        startNextWord();
    }

    private void initButtons() {
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
                checkWin();
            } else {
                wrongCount++;
                livesCount.setText(String.valueOf(hangmanView.getMaxSteps() - wrongCount));
                hangmanView.drawNextStep();

                if (wrongCount >= hangmanView.getMaxSteps()) {
                    for (Button btn : buttons) btn.setEnabled(false);
                    showGameOver();
                }
            }
        };

        for (Button b : buttons) b.setOnClickListener(letterClickListener);
    }

    private void startNextWord() {
        if (words.isEmpty()) {
            Toast.makeText(this, "סיימת את כל המילים!", Toast.LENGTH_SHORT).show();
            if (scoreAll>wrongCountAll) {
                Intent intent = new Intent(this, GgActivity.class);
                intent.putExtra("ScoreCount", scoreAll);
                intent.putExtra("noWords",noWords);
                intent.putExtra("yesWords",yesWords);
                startActivity(intent);
            }
            else
            {
                Intent intent =new Intent(this, GameOverActivity.class);
                intent.putExtra("wrongCount",wrongCountAll);
                intent.putExtra("noWords",noWords);
                intent.putExtra("yesWords",yesWords);
                startActivity(intent);
            }
            finish();
        }

        // איפוס ערכים
        wrongCount = 0;
        score = 0;
        hangmanView.reset();
        // איפוס הכפתורים
        for (Button b : buttons) {
            b.setEnabled(true);
            b.setVisibility(View.VISIBLE);
        }

        // בוחרים מילה חדשה
        Random random = new Random();
        int a = random.nextInt(words.size());
        selectedWord = words.get(a);
        words.remove(a);
        Log.d("DEBUG",selectedWord);

        // נקה TextViews ישנים
        lettersContainer.removeAllViews();
        textViews = new TextView[selectedWord.length()];

        // הוסף TextViews חדשים
        for (int i = 0; i < selectedWord.length(); i++) {
            TextView tv = createLetterView("_");
            lettersContainer.addView(tv);
            textViews[i] = tv;
        }

        // עדכון ה־score/lives
        scoreCount.setText(score + "/" + selectedWord.length());
        livesCount.setText(String.valueOf(10));
    }

    // יצירת TextView עם סטייל
    private TextView createLetterView(String text) {
        TextView tv = new TextView(this);

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()
                )
        );

        int margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()
        );
        params.setMargins(margin, margin, margin, margin);
        tv.setLayoutParams(params);

        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tv.setText(text);

        return tv;
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
            Toast.makeText(this, "נחשפת המילה: " + selectedWord, Toast.LENGTH_SHORT).show();
            scoreAll++;
            yesWords.add(selectedWord);
            startNextWord();
        }
    }

    private void showGameOver() {
        Toast.makeText(this, "המילה הייתה: " + selectedWord, Toast.LENGTH_SHORT).show();
        wrongCountAll++;
        noWords.add(selectedWord);
        startNextWord();
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
