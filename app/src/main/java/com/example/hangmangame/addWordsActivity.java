package com.example.hangmangame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class addWordsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "hangman_prefs";
    public static final String KEY_WORDS = "words_list";

    RecyclerView recyclerView;
    EditText editWord;
    Toolbar toolbar;

    TextView tv_main_title;
    Button btnAdd;
    ImageButton btn_save;
    addWordsAdapter adapter;
    List<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_words);
        hideSystemUI();
        recyclerView = findViewById(R.id.rv_words_list);
        editWord = findViewById(R.id.et_word_input);
        btnAdd = findViewById(R.id.btn_confirm);
        btn_save = findViewById(R.id.btn_save);
        tv_main_title = findViewById(R.id.tv_main_title);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        words = loadWords();

        adapter = new addWordsAdapter(words);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (words.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
        Intent intent = getIntent();
        String add = intent.getStringExtra("add");
        if (add != null) {
            tv_main_title.setText(add);
        }

        btnAdd.setOnClickListener(v -> {
            // סגור את המקלדת אם היא פתוחה
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View view = getCurrentFocus();
            if (view == null) view = new View(this); // במקרה שאין פוקוס
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            // קוד קיים להוספת מילה
            String newWord = editWord.getText().toString().trim();
            if (!newWord.isEmpty()) {
                words.add(newWord);
                saveWords(words); // שמירה מיידית
                adapter.notifyItemInserted(words.size() - 1);

                editWord.setText("");
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.scrollToPosition(words.size() - 1);
            }
        });

        btn_save.setOnClickListener(v -> {
            Intent i = new Intent(this, GameActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void saveWords(List<String> words) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray array = new JSONArray();
        for (String s : words) {
            array.put(s);
        }
        editor.putString(KEY_WORDS, array.toString());
        editor.apply();
    }

    private List<String> loadWords() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_WORDS, null);
        List<String> list = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    list.add(array.getString(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_words, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_Add) {
            // יצירת AlertDialog לאישור מחיקה
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("אישור מחיקה")
                    .setMessage("אתה בטוח שברצונך למחוק את כל המילים?")
                    .setPositiveButton("כן", (dialog, which) -> {
                        // מחיקת כל המילים
                        words.clear();
                        adapter.notifyDataSetChanged();
                        saveWords(words);
                        recyclerView.setVisibility(View.GONE);
                    })
                    .setNegativeButton("לא", null) // סגירה ללא פעולה
                    .show();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
