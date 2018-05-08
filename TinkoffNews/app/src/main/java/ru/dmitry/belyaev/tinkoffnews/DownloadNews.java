package ru.dmitry.belyaev.tinkoffnews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dmitrybelyaev on 07.05.2018.
 */

public class DownloadNews extends AsyncTaskLoader<ArrayList<Note>> {

    private final String LINK_URL = "https://api.tinkoff.ru/v1/news";
    private final String PREF_NAME = "NewsData";
    private final String JSON = "json";

    private Context context;
    private ViewGroup viewGroup;


    public DownloadNews(Context context, ViewGroup viewGroup) {
        super(context);
        this.context = context;
        this.viewGroup = viewGroup;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Note> loadInBackground() {
        String resultJSON = null;
        ArrayList<Note> result = new ArrayList<>();
        Log.d("myLogs", "load_background");

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() &&
                cm.getActiveNetworkInfo().isConnected())) {
            resultJSON = restoreFromCache();
            Snackbar.make(viewGroup, "No internet connection! Loading from local",
                    Snackbar.LENGTH_SHORT).show();
            if(resultJSON == null) {
                return null;
            }
            else {
                result = parseJSON(resultJSON);
                return result;
            }
        }

        try {
            URL url = new URL(LINK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            resultJSON = buffer.toString();
            saveInCache(resultJSON);

        }
        catch(IOException e) {
            e.printStackTrace();
        }
        result = parseJSON(resultJSON);
        return result;
    }

    private ArrayList<Note> parseJSON(String json) {
        ArrayList<Note> result = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("payload");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject note = (JSONObject) jsonArray.get(i);
                String text = note.getString("text");
                JSONObject pubDateObject = note.getJSONObject("publicationDate");
                long milliseconds = pubDateObject.getLong("milliseconds");
                Date date = new Date(milliseconds);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US);
                String publicationDate = sdf.format(date);
                int id = note.getInt("id");
                Note noteObject = new Note(text, id, milliseconds, publicationDate);
                result.add(noteObject);
            }
            Collections.sort(result, Note.getCompByTime());
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void saveInCache(String json) {
        SharedPreferences preferences = getContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putString(JSON, json)
                .apply();
    }

    private String restoreFromCache() {
        SharedPreferences preferences = getContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(JSON, null);
    }
}
