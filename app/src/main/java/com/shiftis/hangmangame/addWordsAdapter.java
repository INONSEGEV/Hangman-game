package com.shiftis.hangmangame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class addWordsAdapter extends RecyclerView.Adapter<addWordsAdapter.wordItem> {

    private List<String> dataList;

    public addWordsAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public addWordsAdapter.wordItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carrier_row, parent, false);
        return new addWordsAdapter.wordItem(view);
    }

    @Override
    public void onBindViewHolder(addWordsAdapter.wordItem holder, int position) {
        String word = dataList.get(position);
        holder.textView.setText(word);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class wordItem extends RecyclerView.ViewHolder {
        TextView textView;

        public wordItem(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_subtitle);
        }
    }
}
