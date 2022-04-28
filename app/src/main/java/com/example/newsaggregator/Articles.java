package com.example.newsaggregator;

import org.json.JSONArray;

import java.io.Serializable;

public class Articles implements Serializable, Comparable<Articles> { //if we are going to pass this object over threads, we need to implement serializable
    //in order to be able to use Collection.sort() we have to implement comparable and the function to explain in case of sorting what need to
    //be compared to what
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;



    Articles(String author, String title, String description,
             String url, String urlToImage, String publishedAt) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;



    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }



    @Override
    public int compareTo(Articles s) {
        return author.compareTo(s.getAuthor());
    }
}


