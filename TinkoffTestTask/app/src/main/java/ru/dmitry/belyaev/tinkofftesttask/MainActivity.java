package ru.dmitry.belyaev.tinkofftesttask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public final String LINK_URL = "https://api.tinkoff.ru/v1/news";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new DownloadNews().execute(LINK_URL);
    }
}
