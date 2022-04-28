package com.example.newsaggregator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;

public class Sources implements Serializable, Comparable<Sources> { //if we are going to pass this object over threads, we need to implement serializable
    //in order to be able to use Collection.sort() we have to implement comparable and the function to explain in case of sorting what need to
    //be compared to what
    private String id;
    private String name;
    private String category;
    private String language;
    private String country;



    Sources(String id, String name, String category, String language, String country) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.language = language;
        this.country = country;

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }



    @Override
    public int compareTo(Sources s) {
        return name.compareTo(s.getName());
    }

}



