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

public class GetSourcesRunnable implements Runnable {

    // The HTTP GET method is used to read (or retrieve) a representation of a resource.

    private static final String TAG = "GetSourcesTask";
    private static final String baseURL = "https://newsapi.org/v2/";
    private static final String endPoint = "sources";
    private static final String apikey = "8edd78ec813f4487bce2a92062637bf7";

    private final MainActivity mainActivity;

    GetSourcesRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

    }


    @Override
    public void run() {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            String urlString = baseURL + endPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            Log.d(TAG, "run: Initial URL: " + urlString);

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


            return;
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
        List<Sources> sourcesList = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<String> languages = new ArrayList<>();
        List<String> countries = new ArrayList<>();

        try {
            JSONArray ja = response.getJSONArray("sources");
            //int arraySize = ja.length();

            for (int i = 0; i < ja.length(); i++){
                JSONObject jo = ja.getJSONObject(i);

                sourcesList.add(new Sources(jo.getString("id"), jo.getString("name"),
                        jo.getString("category"), jo.getString("language"),
                        jo.getString("country")));

                if (!categories.contains(jo.getString("category"))){
                    categories.add(jo.getString("category"));
                }

                if (!languages.contains(jo.getString("language"))){
                    languages.add(jo.getString("language"));
                }

                if (!countries.contains(jo.getString("country"))){
                    countries.add(jo.getString("country"));
                }

            }



            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        mainActivity.sourceThreadHandler(sourcesList, categories,
                                languages, countries);
                }
            });



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


