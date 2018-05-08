package ru.dmitry.belyaev.tinkoffnews;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dmitrybelyaev on 07.05.2018.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<Note> data;
    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(int position) {}
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView newsTextView;
        public TextView datetimeTextView;

        public MyViewHolder(View view, final OnClickListener listener) {
            super(view);
            newsTextView = view.findViewById(R.id.news);
            datetimeTextView = view.findViewById(R.id.datetime);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(getAdapterPosition());
                }
            });
        }

    }


    public RecyclerAdapter(ArrayList<Note> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView, listener);
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

    void setOnclickListener(OnClickListener listener) {
        this.listener = listener;
    }

    interface OnClickListener {
        void onClick(int position);
    }

}