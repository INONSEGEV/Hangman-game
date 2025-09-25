package com.shiftis.hangmangame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

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
    ImageButton btnDeleteSelected;
    MaterialButton btn_save;
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
        btnDeleteSelected = findViewById(R.id.btn_image);
        btn_save = findViewById(R.id.btn_save);
        tv_main_title = findViewById(R.id.tv_main_title);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        words = loadWords();

        adapter = new addWordsAdapter(this, words);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

// InputFilter שמאפשר רק אותיות עבריות
        InputFilter hebrewFilter = (source, start, end, dest, dstart, dend) -> {
            StringBuilder filtered = new StringBuilder();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (c >= 'א' && c <= 'ת') {
                    filtered.append(c); // רק תווים חוקיים נשארים
                }
            }
            return filtered.toString();
        };

        editWord.setFilters(new InputFilter[]{hebrewFilter});



// מאזינים לשינויים בבחירת CheckBox
        adapter.setOnSelectionChangedListener(selectedCount -> {
            if (selectedCount > 0) {
                btnDeleteSelected.setVisibility(View.VISIBLE);
            } else {
                btnDeleteSelected.setVisibility(View.GONE);
            }
        });
        btnDeleteSelected.setVisibility(adapter.getSelectedCount() > 0 ? View.VISIBLE : View.GONE);


// סגור את הכפתור אם אין נבחרים בהתחלה
        btnDeleteSelected.setVisibility(View.GONE);

        updateRecyclerVisibility();

        Intent intent = getIntent();
        String add = intent.getStringExtra("add");
        if (add != null) {
            tv_main_title.setText(add);
        }

        btnAdd.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View view = getCurrentFocus();
            if (view == null) view = new View(this);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            String newWord = editWord.getText().toString().trim();

            if (newWord.isEmpty()) {
                Toast.makeText(this, "אנא הכנס מילה", Toast.LENGTH_SHORT).show();
                return;
            }

            if (words.contains(newWord)) {
                Toast.makeText(this, "המילה כבר קיימת ברשימה", Toast.LENGTH_SHORT).show();
                return;
            }

            words.add(newWord);
            saveWords(words);
            adapter.notifyItemInserted(words.size() - 1);

            editWord.setText("");
            updateRecyclerVisibility();
            recyclerView.scrollToPosition(words.size() - 1);

            adapter.setOnSelectionChangedListener(selectedCount -> {
                btnDeleteSelected.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE);
            });
        });





        btnDeleteSelected.setOnClickListener(v -> {
            adapter.removeSelected();
        });


        btn_save.setOnClickListener(v -> {
            if (!words.isEmpty()) { // בודק אם יש פריטים ברשימה
                Intent i = new Intent(this, GameActivity.class);
                startActivity(i);
                finish();
            } else {
                // אפשר להראות Toast או הודעה שהרשימה ריקה
                Toast.makeText(this, "לא ניתן להתחיל משחק ללא מילים שמורות", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateRecyclerVisibility() {
        recyclerView.setVisibility(words.isEmpty() ? View.GONE : View.VISIBLE);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteAllItem = menu.findItem(R.id.btn_Add);
        if (deleteAllItem != null) {
            deleteAllItem.setVisible(!words.isEmpty());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_Add) {
            // AlertDialog לאישור מחיקה
            new AlertDialog.Builder(this)
                    .setTitle("אישור מחיקה")
                    .setMessage("אתה בטוח שברצונך למחוק את כל המילים?")
                    .setPositiveButton("כן", (dialog, which) -> {
                        words.clear();
                        adapter.notifyDataSetChanged();
                        saveWords(words);
                        updateRecyclerVisibility();
                        invalidateOptionsMenu(); // עדכון MenuItem
                    })
                    .setNegativeButton("לא", null)
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
