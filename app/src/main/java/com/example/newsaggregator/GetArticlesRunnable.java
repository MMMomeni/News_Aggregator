package com.example.newsaggregator;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class GetArticlesRunnable implements Runnable {

    // The HTTP GET method is used to read (or retrieve) a representation of a resource.

    private static final String TAG = "GetArticlesTask";
    private static final String baseURL = "https://newsapi.org/v2/";
    private static final String endPoint = "top-headlines";
    private static final String apikey = "8edd78ec813f4487bce2a92062637bf7";

    private final MainActivity mainActivity;
    private final String sources;

    GetArticlesRunnable(MainActivity mainActivity, String sources) {
        this.mainActivity = mainActivity;
        this.sources = sources;

    }

    @Override
    public void run() {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = baseURL + endPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            Log.d(TAG, "run: Initial URL: " + urlString);

            buildURL.appendQueryParameter("sources", sources);
            buildURL.appendQueryParameter("apiKey", apikey);


            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent","");
            connection.connect();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();

            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }


            JSONObject firstObject = new JSONObject(result.toString());
            ThreadHandler(firstObject);


            //return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        //mainActivity.showResults("Error performing GET request");
    }

    private void ThreadHandler (JSONObject response){
        List<Articles> articleList = new ArrayList<>();

        try {
            //String check  = response.getString("totalResults");
            if (!response.getString("totalResults").equals("0")){
                JSONArray ja = response.getJSONArray("articles");

                //int arraySize = ja.length();

                for (int i = 0; i < ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);

                    articleList.add(new Articles(jo.getString("author"), jo.getString("title"),
                            jo.getString("description"), jo.getString("url"),
                            jo.getString("urlToImage"), jo.getString("publishedAt")));

                }


                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.articleThreadHandler(articleList);
                    }
                });
            }
            else{
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.articleThreadHandlerFAILED();
                    }
                });
            }


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}



