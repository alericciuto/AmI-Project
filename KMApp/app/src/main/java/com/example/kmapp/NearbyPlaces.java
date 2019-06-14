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
        private String lat = "";
        private String longt = "";
        private String data = "";
        private String dataParsed = "";
        private GpsTracker tracker;
        private Double latitude;
        private Double longitude;
        private boolean isDone;

    public NearbyPlaces(GpsTracker tracker) {
        this.tracker = tracker;
        this.latitude = this.tracker.getLatitude();
        this.longitude = this.tracker.getLongitude();
    }

    @Override
        protected String doInBackground(Void... voids) {
        this.isDone = false;
            try {
                URL url = new URL("https://graph.facebook.com/v3.2/search?type=place&center="
                        + latitude.toString() + "," + longitude.toString() + "&distance=2000" +
                        "&q=hotel&fields=name&limit=3&access_token=EAAFtMJbfTIYBAAQwEh" +
                        "1wpBoElG3ithh37ZAD2JqDjjRDJfZBn0PwxudzRHvJnuIC5IwbkZCmGEQnPgNzGq1l7imZBB" +
                        "ZBzjG8PLEQOhDrIB0pZCxz1ZCy8l2kNBT8cHZCoCtSh3IabQRNl5NEG8gPdc75ecRCbzM87wxPIreb8" +
                        "DBWzRkepcb2PeWsadhmvYbuiNy5D2NX8crFZBgZDZD");

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
                int totRes = data.length();
                JSONObject singleData;
                String name;
                dataParsed = "You're attention span is decreasing really fast! I suggest you to rest. These are the hotel in a range of 2000 meters:";
                for (int j = 0; j < totRes; j++) {
                    singleData = data.getJSONObject(j);
                    name = (String) singleData.get("name");
                    dataParsed = dataParsed + "/n"+ j+1 +". " + name;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this.dataParsed;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
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
