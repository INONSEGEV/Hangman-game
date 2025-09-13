package com.example.hangmangame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

public class GgActivity extends AppCompatActivity {

    TextView correctWord, attemptsCount;
    Button playAgainButton, shareButton;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gg);
        hideSystemUI();
        mediaPlayer = MediaPlayer.create(this, R.raw.gg);
        mediaPlayer.start();

        correctWord = findViewById(R.id.correctWord);
        attemptsCount = findViewById(R.id.attemptsCount);
        playAgainButton = findViewById(R.id.playAgainButton);
        shareButton = findViewById(R.id.shareButton); // כפתור השיתוף

        Intent intent = getIntent();
        correctWord.setText(intent.getStringExtra("theWord"));
        attemptsCount.setText(String.valueOf(intent.getIntExtra("wrongCount",0)));

        playAgainButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, GameActivity.class);
            startActivity(intent1);
            finish();
        });

        // --- הוספה של שיתוף מסך ---
        shareButton.setOnClickListener(v -> {
            Bitmap screenshot = takeScreenshot(findViewById(android.R.id.content));
            Uri uri = saveScreenshot(screenshot);
            if (uri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "שתף את המסך"));
            } else {
                Toast.makeText(this, "לא ניתן לשמור את התמונה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // פונקציה לצילום המסך
    private Bitmap takeScreenshot(android.view.View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // פונקציה לשמירת צילום המסך
    private Uri saveScreenshot(Bitmap bitmap) {
        try {
            File file = new File(getCacheDir(), "screenshot.png");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
