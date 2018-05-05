package ru.dmitry.belyaev.tinkofftesttask;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Tinkoff News");
        setSupportActionBar(toolbar);
        final ViewGroup rootView = findViewById(R.id.linLayout);
        SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Context context = getApplicationContext();
                new DownloadNews(context, rootView).execute();
            }
        });
        new DownloadNews(this, rootView).execute();
    }
}
