package ru.dmitry.belyaev.tinkofftesttask;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dmitrybelyaev on 04.05.2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<Note> data;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView newsTextView;
        public TextView datetimeTextView;

        public MyViewHolder(View view) {
            super(view);
            newsTextView = view.findViewById(R.id.news);
            datetimeTextView = view.findViewById(R.id.datetime);
        }
    }


    public RecyclerAdapter(ArrayList<Note> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.newsTextView.setText(data.get(position).getText());
        holder.datetimeTextView.setText(data.get(position).getPublicationDate());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
