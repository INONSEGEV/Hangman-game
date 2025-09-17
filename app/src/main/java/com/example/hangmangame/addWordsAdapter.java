package com.example.hangmangame;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.List;

public class addWordsAdapter extends RecyclerView.Adapter<addWordsAdapter.wordItem> {

    private List<String> dataList;

    public addWordsAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public void onBindViewHolder(wordItem holder, int position) {
        String word = dataList.get(position);
        holder.textView.setText(word);

        holder.btnDelete.setOnClickListener(v -> {
            // מסיר מהרשימה
            dataList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, dataList.size());

            // שמירה מחודשת ל־SharedPreferences
            saveWordsToPrefs(holder.itemView.getContext(), dataList);
        });
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
        ImageButton btnDelete;

        public wordItem(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_subtitle);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
