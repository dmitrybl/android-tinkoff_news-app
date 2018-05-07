package ru.dmitry.belyaev.tinkofftesttask;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.TextView;

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
 * Created by dmitrybelyaev on 06.05.2018.
 */

public class DetailNews extends AsyncTask<Integer, Void, Integer> {

    private final int NO_CONNECTION = 2;
    private final int DOWNLOAD_ERROR = 1;
    private final int DOWNLOAD_SUCCESS = 0;
    private final String LINK_URL =  "https://api.tinkoff.ru/v1/news_content?";
    private HttpURLConnection connection;
    private Context context;
    private ViewGroup rootView;
    private SwipeRefreshLayout swipeRefresh;

    private final String PREFERENCES_NAME = "DataDetail";
    private SharedPreferences sPref;

    private String text;
    private String content;

    private int ID;


    public DetailNews(Context context, ViewGroup rootView) {
        this.context = context;
        this.rootView = rootView;

        sPref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        ID = params[0];
        swipeRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh2);
        swipeRefresh.setRefreshing(true);
        String jsonString = sPref.getString(ID + "", "null");

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() &&
                cm.getActiveNetworkInfo().isConnected())) {
            return NO_CONNECTION;
        }

        try {
            String link = LINK_URL + "id=" + ID;
            URL url = new URL(link);
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
                JSONObject payloadObject = (JSONObject) jsonObject.get("payload");
                JSONObject titleObject = (JSONObject) payloadObject.get("title");
                text = titleObject.getString("text");

                content = payloadObject.getString("content");

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("text", text);
                map.put("content", content);
                JSONObject jsonObject2 = new JSONObject(map);

                Editor ed = sPref.edit();
                ed.putString(ID  + "", jsonObject2.toString());
                ed.apply();
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
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbarDetail);
        TextView tvTitleDetail = (TextView) rootView.findViewById(R.id.tvTitleDetail);
        TextView tvContentDetail = (TextView) rootView.findViewById(R.id.tvContentDetail);
        if (status == NO_CONNECTION) {
            toolbar.setTitle("No internet connection!");
            Snackbar.make(rootView, "No internet connection! Loading from local", Snackbar.LENGTH_SHORT).show();
            String jsonString = sPref.getString(ID + "", "null");
            String t = "";
            String cont = "";
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                t = jsonObject.getString("text");
                cont  = jsonObject.getString("content");
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            tvTitleDetail.setText(Html.fromHtml(t));
            tvContentDetail.setText(Html.fromHtml(cont));
        }
        else if (status == DOWNLOAD_ERROR) {
            toolbar.setTitle("Error!");
            Snackbar.make(rootView, "An error occured!", Snackbar.LENGTH_SHORT).show();
        }
        else {
            toolbar.setTitle("Tinkoff News");
            tvTitleDetail.setText(Html.fromHtml(text));
            tvContentDetail.setText(Html.fromHtml(content));
        }
    }
}
