package ru.dmitry.belyaev.tinkofftesttask;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;

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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by dmitrybelyaev on 04.05.2018.
 */

public class DownloadNews extends AsyncTask<Void, Void, Integer> {

    private final int NO_CONNECTION = 2;
    private final int DOWNLOAD_ERROR = 1;
    private final int DOWNLOAD_SUCCESS = 0;
    private final String LINK_URL = "https://api.tinkoff.ru/v1/news";
    private HttpURLConnection connection;
    private ArrayList<Note> data = new ArrayList<Note>();
    private Context context;
    private ViewGroup rootView;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;

    private final String PREFERENCES_NAME = "DataNews";
    private SharedPreferences sPref;


    public DownloadNews(Context context, ViewGroup rootView) {
        this.context = context;
        this.rootView = rootView;

        sPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh);
        swipeRefresh.setRefreshing(true);
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() &&
                cm.getActiveNetworkInfo().isConnected())) {
            return NO_CONNECTION;
        }
        try {
            URL url = new URL(LINK_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if(responseCode == connection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String resultJSON = buffer.toString();
                JSONObject jsonObject = new JSONObject(resultJSON);
                JSONArray jsonArray = jsonObject.getJSONArray("payload");
                Editor ed = sPref.edit();
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject note = (JSONObject) jsonArray.get(i);
                    String text = note.getString("text");
                    JSONObject pubDateObject = note.getJSONObject("publicationDate");
                    long milliseconds = pubDateObject.getLong("milliseconds");
                    Date date = new Date(milliseconds);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US);
                    String publicationDate = sdf.format(date);
                    int id = note.getInt("id");


                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("text", text);
                    map.put("publicationDate", publicationDate);
                    map.put("milliseconds", milliseconds + "");
                    JSONObject jsonObject2 = new JSONObject(map);

                    ed.putString(id  + "", jsonObject2.toString());
                    ed.apply();
                    Note noteObject = new Note(text, id, milliseconds, publicationDate);
                    data.add(noteObject);
                }
                Collections.sort(data, Note.getCompByTime());
                return DOWNLOAD_SUCCESS;
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
       return DOWNLOAD_ERROR;
    }

    @Override
    protected void onPostExecute(Integer status) {
        swipeRefresh.setRefreshing(false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbarMain);
        if (status == NO_CONNECTION) {
            toolbar.setTitle("No internet connection!");
            Snackbar.make(rootView, "No internet connection! Loading from local", Snackbar.LENGTH_SHORT).show();
            Map<String, ?> allPreferences = sPref.getAll();

            try {
                for (Map.Entry<String, ?> entry : allPreferences.entrySet()) {
                    JSONObject jsonObject = new JSONObject(entry.getValue().toString());
                    String text = jsonObject.getString("text");
                    long milliseconds = Long.parseLong(jsonObject.getString("milliseconds"));
                    String publicationDate = jsonObject.getString("publicationDate");
                    int id = Integer.parseInt(entry.getKey());
                    data.add(new Note(text, id, milliseconds, publicationDate));
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            Collections.sort(data, Note.getCompByTime());
            recyclerView.setAdapter(new RecyclerAdapter(data));
        }
        else if (status == DOWNLOAD_ERROR) {
            toolbar.setTitle("Error!");
            Snackbar.make(rootView, "An error occured!", Snackbar.LENGTH_SHORT).show();
        }
        else {
            toolbar.setTitle("Tinkoff News");
            recyclerView.setAdapter(new RecyclerAdapter(data));
        }
    }
}
