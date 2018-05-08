package ru.dmitry.belyaev.tinkoffnews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ViewGroup;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by dmitrybelyaev on 08.05.2018.
 */

public class DownloadDetail extends AsyncTaskLoader<JSONObject> {

    private int id;
    private final String LINK_URL =  "https://api.tinkoff.ru/v1/news_content?";
    private final String PREF_NAME = "DetailData";
    private final String JSON = "json";
    private Context context;
    private ViewGroup viewGroup;


    public DownloadDetail(Context context, int id, ViewGroup viewGroup) {
        super(context);
        this.id = id;
        this.context = context;
        this.viewGroup = viewGroup;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public JSONObject loadInBackground() {
        String resultJSON = null;
        Log.d("myLogs", "loadBack");

        JSONObject result = null;

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
            String link = LINK_URL + "id=" + id;
            URL url = new URL(link);
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

    private JSONObject parseJSON(String json) {
        JSONObject result = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject payloadObject = (JSONObject) jsonObject.get("payload");
            JSONObject titleObject = (JSONObject) payloadObject.get("title");
            String text = titleObject.getString("text");
            String content = payloadObject.getString("content");

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("text", text);
            map.put("content", content);
            result = new JSONObject(map);
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
                .putString(id + "", json)
                .apply();
    }

    private String restoreFromCache() {
        SharedPreferences preferences = getContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(id + "", null);
    }
}
