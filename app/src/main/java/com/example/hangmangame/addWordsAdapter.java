package com.example.hangmangame;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;

import java.util.List;

public class addWordsAdapter extends RecyclerView.Adapter<addWordsAdapter.wordItem> {

    private List<String> dataList;
    private Context contextRef;

    public addWordsAdapter(Context context, List<String> dataList) {
        this.contextRef = context;
        this.dataList = dataList;
    }

    @Override
    public void onBindViewHolder(wordItem holder, int position) {
        String word = dataList.get(position);
        holder.textView.setText(word);

        // לחיצה ארוכה → תפתח BottomSheetMenu
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

        public wordItem(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_subtitle);
        }
    }

    public void removeWord(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size());
        saveWordsToPrefs(contextRef, dataList);
    }

    public void editWord(int position, String newWord) {
        dataList.set(position, newWord);
        notifyItemChanged(position);
        saveWordsToPrefs(contextRef, dataList);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
