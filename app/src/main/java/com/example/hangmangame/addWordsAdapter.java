package com.example.hangmangame;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.OnSelectionChangedListener;

import org.json.JSONArray;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class addWordsAdapter extends RecyclerView.Adapter<addWordsAdapter.wordItem> {

    private List<String> dataList;
    private Context contextRef;
    private OnSelectionChangedListener selectionListener;

    private Set<String> selectedWords = new HashSet<>(); // שומר את הערכים המסומנים
    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }
    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }
    public addWordsAdapter(Context context, List<String> dataList) {
        this.contextRef = context;
        this.dataList = dataList;
    }

    @Override
    public void onBindViewHolder(wordItem holder, int position) {
        String word = dataList.get(position);
        holder.textView.setText(word);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedWords.contains(word));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedWords.add(word);
            } else {
                selectedWords.remove(word);
            }

            // עדכון הכפתור בהתאם לכמות הנבחרים
            if (selectionListener != null) {
                selectionListener.onSelectionChanged(selectedWords.size());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            showBottomSheetMenu(holder, position);
            return true;
        });
    }

    private void showBottomSheetMenu(wordItem holder, int position) {
        View sheetView = LayoutInflater.from(contextRef)
                .inflate(R.layout.bottom_sheet_menu, null);

        BottomSheetDialog dialog = new BottomSheetDialog(contextRef);
        dialog.setContentView(sheetView);

        // כפתור עריכה
        sheetView.findViewById(R.id.btnEdit).setOnClickListener(v -> {
            showEditDialog(position);
            dialog.dismiss();
        });

        // כפתור מחיקה
        sheetView.findViewById(R.id.btnDelete).setOnClickListener(v -> {
            removeWord(position);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showEditDialog(int position) {
        View dialogView = LayoutInflater.from(contextRef)
                .inflate(R.layout.dialog_edit_word, null);

        EditText input = dialogView.findViewById(R.id.editWordInput);
        input.setText(dataList.get(position));

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(contextRef)
                        .setView(dialogView)
                        .create();

        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String newWord = input.getText().toString().trim();
            if (!newWord.isEmpty()) {
                editWord(position, newWord);
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void saveWordsToPrefs(Context context, List<String> words) {
        SharedPreferences prefs = context.getSharedPreferences(addWordsActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray array = new JSONArray();
        for (String s : words) {
            array.put(s);
        }
        editor.putString(addWordsActivity.KEY_WORDS, array.toString());
        editor.apply();
    }

    @Override
    public wordItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_row, parent, false);
        return new wordItem(view);
    }

    public static class wordItem extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;

        public wordItem(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_subtitle);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public void removeWord(int position) {
        if (position >= 0 && position < dataList.size()) {
            String word = dataList.get(position);
            dataList.remove(position);
            selectedWords.remove(word); // להסיר מהסט של נבחרים

            notifyItemRemoved(position);
            saveWordsToPrefs(contextRef, dataList);

            // עדכון הכפתור דרך ה־callback
            if (selectionListener != null) {
                selectionListener.onSelectionChanged(selectedWords.size());
            }

            if (dataList.isEmpty()) {
                notifyDataSetChanged();
            }
        }
    }


    public void editWord(int position, String newWord) {
        if (position >= 0 && position < dataList.size()) {
            String oldWord = dataList.get(position);
            dataList.set(position, newWord);
            if (selectedWords.contains(oldWord)) {
                selectedWords.remove(oldWord);
                selectedWords.add(newWord);
            }
            notifyItemChanged(position);
            saveWordsToPrefs(contextRef, dataList);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void removeSelected() {
        if (selectedWords.isEmpty()) return;

        Iterator<String> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
            if (selectedWords.contains(word)) {
                iterator.remove();
            }
        }

        selectedWords.clear();
        saveWordsToPrefs(contextRef, dataList);
        notifyDataSetChanged();

        if (selectionListener != null) {
            selectionListener.onSelectionChanged(0);
        }
    }
}
