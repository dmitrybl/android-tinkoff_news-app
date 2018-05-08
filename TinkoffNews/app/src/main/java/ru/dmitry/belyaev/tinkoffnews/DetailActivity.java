package ru.dmitry.belyaev.tinkoffnews;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dmitrybelyaev on 08.05.2018.
 */

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<JSONObject> {

    private final int LOADER_ID = 1;

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefresh;
    private ViewGroup rootView;

    private TextView tvTitle;
    private TextView tvContent;

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        tvTitle = (TextView) findViewById(R.id.tvTitleDetail);
        tvContent = (TextView) findViewById(R.id.tvContentDetail);
        rootView = findViewById(R.id.rootViewDetail);
        toolbar = (Toolbar) findViewById(R.id.toolbarDetail);
        toolbar.setTitle("Tinkoff News");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh2);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
        load();
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
    public Loader<JSONObject> onCreateLoader(int i, Bundle bundle) {
        return new DownloadDetail(this, id, rootView);
    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject loaded_data) {
        Log.d("myLogs", "OK");
        tvTitle.setText("");
        tvContent.setText("");
        String text = "";
        String content = "";
        if (loaded_data != null) {
            try {
                text = loaded_data.getString("text");
                content = loaded_data.getString("content");
            }
             catch(JSONException e) {
                e.printStackTrace();
            }
        }
        tvTitle.setText(Html.fromHtml(text));
        tvContent.setText(Html.fromHtml(content));
        swipeRefresh.setRefreshing(false);
        getLoaderManager().destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
