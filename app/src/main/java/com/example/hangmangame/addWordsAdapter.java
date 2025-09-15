package com.example.hangmangame;

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
    public wordItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_row, parent, false);
        return new wordItem(view);
    }

  public static class wordItem extends RecyclerView.ViewHolder
  {
      TextView textView;

      public wordItem(View itemView) {
          super(itemView);
          textView = itemView.findViewById(R.id.textView);
      }
  }
    @Override
    public void onBindViewHolder(wordItem holder, int position) {
        String item = dataList.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
