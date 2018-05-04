package ru.dmitry.belyaev.tinkofftesttask;

import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dmitrybelyaev on 04.05.2018.
 */

public class DownloadNews extends AsyncTask<String, Void, Integer> {

    private final int DOWNLOAD_FAILED = 1;
    private final int DOWNLOAD_SUCCESS = 0;
    private HttpURLConnection connection;
    private ArrayList<Note> data = new ArrayList<Note>();

    @Override
    protected Integer doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
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
                Log.d("myLogs", data.size() + "");
                Collections.sort(data, Note.getCompByTime());
                for(int i = 0; i < data.size(); i++) {
                    Log.d("myLogs", data.get(i).getPublicationDate() + " " + data.get(i).getText());
                }

            }
        }
        catch(IOException e) {

        }
        catch(JSONException e) {

        }


       return  DOWNLOAD_FAILED;
    }


}
