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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
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
    private boolean allSelected = false;


    RecyclerView recyclerView;
    EditText editWord;
    Toolbar toolbar;
    TextView tv_main_title;
    Button btnAdd;
    ImageButton btnDeleteSelected;
    MaterialButton btn_save,btnSelectAll,btnAutoDraw;
    addCarrierAdapter adapter;
    List<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_words);
        hideSystemUI();
        listenForWordChanges();
        recyclerView = findViewById(R.id.rv_words_list);
        editWord = findViewById(R.id.et_word_input);
        btnAdd = findViewById(R.id.btn_confirm);
        btnDeleteSelected = findViewById(R.id.btn_image);
        btn_save = findViewById(R.id.btn_save);
        tv_main_title = findViewById(R.id.tv_main_title);
        btnSelectAll = findViewById(R.id.btn_select_all);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateSelectAllButton();

        words = loadWords();


        adapter = new addCarrierAdapter(this, words);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        btnAutoDraw = findViewById(R.id.btn_auto_draw);

        btnAutoDraw.setOnClickListener(v -> generateRandomWords());

        btnSelectAll.setOnClickListener(v -> {
            if (adapter.getItemCount() == 0) {
                return; // אין מילים — אל תעשה כלום
            }

            adapter.toggleSelectAll();

            if (adapter.getSelectedCount() == adapter.getItemCount()) {
                btnSelectAll.setText("בטל בחירה");
            } else {
                btnSelectAll.setText("בחר הכל");
            }
        });


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
                btnSelectAll.setText("בחר הכל");

            }

            if (selectedCount == adapter.getItemCount()) {
                btnSelectAll.setText("בטל בחירה");
            } else {
                btnSelectAll.setText("בחר הכל");
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
            saveWordsToFirestore(words); // שמירה ב־Firebase
            adapter.notifyItemInserted(words.size() - 1);
            btnSelectAll.setText("בחר הכל");
            btnSelectAll.setVisibility(View.VISIBLE);
            editWord.setText("");
            updateRecyclerVisibility();
            recyclerView.scrollToPosition(words.size() - 1);
            adapter.setOnSelectionChangedListener(selectedCount -> {
                btnDeleteSelected.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE);
                if (selectedCount == adapter.getItemCount()) {
                    btnSelectAll.setText("בטל בחירה");
                } else {
                    btnSelectAll.setText("בחר הכל");
                }
                if (words.isEmpty()) { // אם הרשימה ריקה
                    btnSelectAll.setText("בחר הכל");
                    btnSelectAll.setVisibility(View.GONE);
                }
                else {
                    btnSelectAll.setVisibility(View.VISIBLE);
                }
            });


        });

        btnDeleteSelected.setOnClickListener(v -> {
            adapter.removeSelected();
            adapter.clearSelection(); // איפוס מצב בחירה
            updateRecyclerVisibility();
            updateSelectAllButton();
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
    private void updateSelectAllButton() {
        if (words.isEmpty()) {
            btnSelectAll.setVisibility(View.GONE);
        } else {
            btnSelectAll.setVisibility(View.VISIBLE);
            btnSelectAll.setText("בחר הכל");
        }
    }


    private void updateRecyclerVisibility() {
        if (words.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            btnSelectAll.setVisibility(View.GONE);// אם אין מילים — הכפתור נעלם
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            btnSelectAll.setVisibility(View.VISIBLE); // אם יש מילים — הכפתור מופיע
        }
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
        if (item.getItemId() == R.id.action_logout) {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
            clearWords();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void generateRandomWords() {
        // רשימה לדוגמה של מילים אוטומטיות
        String[] randomWords = {"תפוח", "מחשב", "ספר", "כדור", "גיטרה", "דגל", "מים", "חג", "פרח", "שמיים","יונתן","אגאי"};

        boolean added = false;

        for (String word : randomWords) {
            if (!words.contains(word)) { // למנוע כפילויות
                words.add(word);
                adapter.notifyItemInserted(words.size() - 1);
                added = true;
            }
        }

        if (added) {
            saveWords(words); // שמירה ב־SharedPreferences
            adapter.notifyDataSetChanged();
            updateRecyclerVisibility();
            updateSelectAllButton();
            recyclerView.scrollToPosition(words.size() - 1);

            // איפוס מצב הבחירה
            adapter.clearSelection();

            Toast.makeText(this, "רשימת מילים אוטומטית נוספה", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "כל המילים כבר קיימות ברשימה", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveWordsToFirestore(List<String> words) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("words", words);

        db.collection("users").document(uid)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "רשימת מילים נשמרה ב־Firebase", Toast.LENGTH_SHORT).show();
                    saveWords(words); // שומר ב־SharedPreferences את הרשימה המעודכנת
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בשמירה ב־Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void listenForWordChanges() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(uid)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "שגיאה: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        List<String> firestoreWords = (List<String>) snapshot.get("words");
                        if (firestoreWords != null) {
                            words.clear();
                            words.addAll(firestoreWords);

                            // שמירה מקומית ב־SharedPreferences
                            saveWords(words);

                            adapter.notifyDataSetChanged();
                            updateRecyclerVisibility();
                            updateSelectAllButton();
                        }
                    }
                });
    }

    private void clearWords() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_WORDS); // מוחק את המפתח והרשימה
        editor.apply();
    }





}
