package ru.dmitry.belyaev.tinkoffnews;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Note>> {

    private final int LOADER_ID = 0;

    private ArrayList<Note> data;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ViewGroup rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        rootView = findViewById(R.id.linLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Tinkoff News");
        setSupportActionBar(toolbar);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });

        data = new ArrayList<>();
        load();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerAdapter adapter = new RecyclerAdapter(data);
        adapter.setOnclickListener(new RecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", data.get(position).getId());
                startActivity(intent);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    public void load() {
        swipeRefresh.setRefreshing(true);
        Bundle args = new Bundle();
        getLoaderManager().initLoader(LOADER_ID, args, this);
    }

    public void reload() {
        swipeRefresh.setRefreshing(true);
        Bundle args = new Bundle();
        getLoaderManager().restartLoader(LOADER_ID, args, this);
    }

    @Override
    public Loader<ArrayList<Note>> onCreateLoader(int i, Bundle bundle) {
        return new DownloadNews(this, rootView);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Note>> loader, ArrayList<Note> notes) {
        data.clear();
        if (notes != null) {
            data.addAll(notes);
        }
        recyclerView.getAdapter().notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
        getLoaderManager().destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Note>> loader) {

    }
}
