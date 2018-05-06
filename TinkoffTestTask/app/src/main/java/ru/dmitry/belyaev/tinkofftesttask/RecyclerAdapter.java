package ru.dmitry.belyaev.tinkofftesttask;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by dmitrybelyaev on 04.05.2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<Note> data;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView newsTextView;
        public TextView datetimeTextView;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            newsTextView = view.findViewById(R.id.news);
            datetimeTextView = view.findViewById(R.id.datetime);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("id", data.get(getLayoutPosition()).getId());
            context.startActivity(intent);
        }
    }


    public RecyclerAdapter(Context context, ArrayList<Note> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.newsTextView.setText(Html.fromHtml(data.get(position).getText()));
        holder.datetimeTextView.setText(Html.fromHtml(data.get(position).getPublicationDate()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
