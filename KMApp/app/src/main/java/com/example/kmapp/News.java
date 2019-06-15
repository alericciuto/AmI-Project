package com.example.kmapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class News extends AsyncTask<Void,Void,String> {
    private StringBuilder data;
    private StringBuilder dataParsed;
    private String news;
    private boolean ready = false;

    public News(StringBuilder data, StringBuilder dataParsed, String news) {
        this.data = data;
        this.dataParsed = dataParsed;
        this.news = news;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            while(news.compareTo("")==0){}
            URL url = new URL("https://newsapi.org/v2/everything?q=" + news.replace(" ", "%20") + "&sortBy=popularity&apiKey=3dc5347cbb25465885aab935b3aae6c0");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data.append(line);
            }

            JSONObject jo = new JSONObject(data.toString());

            JSONArray art = (JSONArray)jo.get("articles");
            int totRes = art.length();
            int num_article = (int)(Math.random()*totRes);
            JSONObject article = ((JSONArray) jo.get("articles")).getJSONObject(num_article);
            String description = (String)article.get("description");
            String j = (String)((JSONObject)article.get("source")).get("name");
            String[] des = description.split("\\. ");
            dataParsed.append( (String)article.get("title") + "\n" +
                    des[0] + "\n" +
                    "To continue go to: " + "\n" + (String)((JSONObject)article.get("source")).get("name") + "\n" +
                    "Do you want to do something else?");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.setReady( true );

        return dataParsed.toString();
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getDataParsed() {
        return dataParsed.toString();
    }
}