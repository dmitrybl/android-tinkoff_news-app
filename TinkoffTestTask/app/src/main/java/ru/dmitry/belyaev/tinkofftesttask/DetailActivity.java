package ru.dmitry.belyaev.tinkofftesttask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

/**
 * Created by dmitrybelyaev on 06.05.2018.
 */

public class DetailActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbarDetail);
        toolbar.setTitle("Tinkoff News");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent intent = getIntent();
        final int id = intent.getIntExtra("id", 0);

        final ViewGroup rootView = findViewById(R.id.rootViewDetail);
        SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh2);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Context context = getApplicationContext();
                new DetailNews(context, rootView).execute(id);
            }
        });
        new DetailNews(this, rootView).execute(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
