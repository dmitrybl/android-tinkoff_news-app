package ru.dmitry.belyaev.tinkofftesttask;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private final String PREFERENCES_NAME = "DataNews";
    private ArrayList<Note> data = new ArrayList<Note>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle("Tinkoff News");
        setSupportActionBar(toolbar);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(linearLayoutManager);

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


        SharedPreferences sPref = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Map<String, ?> allPreferences = sPref.getAll();
        if(allPreferences.size() > 0) {
            try {
                for (Map.Entry<String, ?> entry : allPreferences.entrySet()) {
                    JSONObject jsonObject = new JSONObject(entry.getValue().toString());
                    String text = jsonObject.getString("text");
                    long milliseconds = Long.parseLong(jsonObject.getString("milliseconds"));
                    String publicationDate = jsonObject.getString("publicationDate");
                    int id = Integer.parseInt(entry.getKey());
                    data.add(new Note(text, id, milliseconds, publicationDate));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Collections.sort(data, Note.getCompByTime());
            recyclerView.setAdapter(new RecyclerAdapter(this, data, swipeRefresh));
        }
        else {
            new DownloadNews(this, rootView).execute();
        }
    }
}
