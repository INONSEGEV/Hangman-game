package com.example.hangmangame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class addWordsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "hangman_prefs";
    private static final String KEY_WORDS = "words_list";

    RecyclerView recyclerView;
    EditText editWord;
    Button btnAdd;
    ImageButton btn_save;
    addWordsAdapter adapter;
    List<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_words);

        recyclerView = findViewById(R.id.rv_words_list);
        editWord = findViewById(R.id.et_word_input);
        btnAdd = findViewById(R.id.btn_confirm);
        btn_save=findViewById(R.id.btn_save);
        words = loadWords();

        adapter = new addWordsAdapter(words);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        // אם יש מילים שמורות – להציג אותן מייד
        if (words.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }

        btnAdd.setOnClickListener(v -> {
            String newWord = editWord.getText().toString().trim();
            if (!newWord.isEmpty()) {
                words.add(newWord);
                saveWords(words); // שמירה מיידית ב־SharedPreferences
                adapter.notifyItemInserted(words.size() - 1);

                editWord.setText("");

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.scrollToPosition(words.size() - 1);
            }
        });
        btn_save.setOnClickListener(v -> {
            Intent i=new Intent(this, GameActivity.class);
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
        Log.d("DEBUG",json);
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
}
