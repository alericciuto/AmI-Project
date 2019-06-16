package com.example.kmapp;


import android.os.AsyncTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class NearbyPlaces extends AsyncTask<Void, Void, String> {
        private StringBuilder data;
        private StringBuilder dataParsed;
        private GpsTracker tracker;
        private Double latitude;
        private Double longitude;
        private boolean isDone;
        private String token;
        private int distance;
        private int numberOfResults;
        private String placeToFind;

    public NearbyPlaces(GpsTracker tracker) {
        this.tracker = tracker;
        this.latitude = tracker.getLatitude();
        this.longitude = tracker.getLongitude();
        this.token = "EAAEYNwZC2cb0BAGrZAacIcVZCwRHd1IYse0WZA5o3gBRR8xuuobajq5aSYdxTvTACGgZBoXfOqoeoJRTKRndEPAz4wYKYQ5QW24KMNwk47kn3yDTZBKUbAU8RLA5myTbJwcw8xMCJyOhwvDnL5eY59HUvDssk0dOvyRCKDSsI9zzdjpPOkaItSULe9q0Dq7P4ZCtRmzT2ovZCWj71o3sLczmBWDibhetONMZD";
        this.distance = 2000;
        this.dataParsed = new StringBuilder();
        this.data = new StringBuilder();
        this.numberOfResults = 3;
        this.placeToFind = "hotel";
    }

    @Override
        protected String doInBackground(Void... voids) {
        this.isDone = false;
            try {
                URL url = new URL("https://graph.facebook.com/v3.2/search?type=place&center="
                        + latitude.toString() + "," + longitude.toString() + "&distance="+this.distance +
                        "&q="+this.placeToFind+"&fields=name&limit="+ this.numberOfResults +"&access_token=" + this.token);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data.append(line);
                }

                JSONObject jo = new JSONObject(data.toString());
                JSONArray data = (JSONArray) jo.get("data");
                JSONObject singleData;
                int a= data.length();
                String name;
                dataParsed = dataParsed.append("You're attention span is decreasing really fast! I suggest you to rest. These are the hotel in a range of 2000 meters:");
                for (int j = 0; j < data.length(); j++) {
                    singleData = data.getJSONObject(j);
                    name = (String) singleData.get("name");
                    dataParsed = dataParsed.append("\n"+ String.valueOf(j+1) +". " + name);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.isDone = true;
            return dataParsed.toString();
    }


    public String getDataParsed() {
        return dataParsed.toString();
    }

    public boolean isDone() {
        return isDone;
    }

}
