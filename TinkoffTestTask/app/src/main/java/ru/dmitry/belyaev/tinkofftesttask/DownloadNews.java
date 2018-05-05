package ru.dmitry.belyaev.tinkofftesttask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dmitrybelyaev on 04.05.2018.
 */

public class DownloadNews extends AsyncTask<Activity, Void, Integer> {

    private final int NO_CONNECTION = 2;
    private final int DOWNLOAD_ERROR = 1;
    private final int DOWNLOAD_SUCCESS = 0;
    private final String LINK_URL = "https://api.tinkoff.ru/v1/news";
    private HttpURLConnection connection;
    private ArrayList<Note> data = new ArrayList<Note>();
    private Context context;
    private LinearLayout linearLayout;


    public DownloadNews(Context context, LinearLayout linearLayout) {
        this.context = context;
        this.linearLayout = linearLayout;
    }

    @Override
    protected Integer doInBackground(Activity... params) {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager)params[0].getSystemService(Context.CONNECTIVITY_SERVICE);
            if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() &&
                    cm.getActiveNetworkInfo().isConnected())) {
                return NO_CONNECTION;
            }
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
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject note = (JSONObject) jsonArray.get(i);
                    String text = note.getString("text");
                    JSONObject pubDateObject = note.getJSONObject("publicationDate");
                    long milliseconds = pubDateObject.getLong("milliseconds");
                    Date date = new Date(milliseconds);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US);
                    String publicationDate = sdf.format(date);
                    int id = note.getInt("id");
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
        if (status == NO_CONNECTION) {
            Log.d("myLogs", "No connection!");
        } else if (status == DOWNLOAD_ERROR) {
            Log.d("myLogs", "Error loading data");
        } else {
            RecyclerView recyclerView = linearLayout.findViewById(R.id.recyclerview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(new RecyclerAdapter(data));
        }
    }
}
