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


public class NearbyPlaces extends AsyncTask<Void, Void, Void> {
        private String lat = "";
        private String longt = "";
        private String data = "";
        private String dataParsed = "";
        private GpsTracker tracker;
        private Double latitude;
        private Double longitude;
        private boolean isDone;
        private String token;
        private int distance;

    public NearbyPlaces(GpsTracker tracker) {
        this.tracker = tracker;
        this.latitude = this.tracker.getLatitude();
        this.longitude = this.tracker.getLongitude();
        this.token="EAAEYNwZC2cb0BAETNk5iu2vExX7usAfGH6TZAv4rsE0ner27xrFupy9XyBe4Nv6qFPTlMIJZAZCEcP71ayR7dp7AZA5Xnuzxm72p6KEPeXbZAokY5yljxkQXQCn6MUsHrhoS90uvCZAJSNYL1E5epbjFmvWpZBNIey9ZBp9bK6OzqxcgyzfNZAx1ZCZBlCAv8NsFJyvWHRDZBjMmCgZAzZB6o6vqNaw0GvxfK7XOOQZD";
        this.distance = 2000;
    }

    @Override
        protected Void doInBackground(Void... voids) {
        this.isDone = false;
            try {
                URL url = new URL("https://graph.facebook.com/v3.2/search?type=place&center="
                        + latitude.toString() + "," + longitude.toString() + "&distance="+this.distance +
                        "&q=hotel&fields=name&limit=3&access_token=" + this.token);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                JSONObject jo = new JSONObject(data);
                JSONArray data = (JSONArray) jo.get("data");
                JSONObject singleData;
                int a= data.length();
                String name;
                dataParsed = "You're attention span is decreasing really fast! I suggest you to rest. These are the hotel in a range of 2000 meters:";
                for (int j = 0; j < data.length(); j++) {
                    singleData = data.getJSONObject(j);
                    name = (String) singleData.get("name");
                    dataParsed = dataParsed + "\n"+ String.valueOf(j+1) +". " + name;
                    //System.out.println(dataParsed);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Toast.makeText(getApplicationContext(),dataParsed, Toast.LENGTH_LONG).show();
            this.isDone = true;
        }

    public String getDataParsed() {
        return dataParsed;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
