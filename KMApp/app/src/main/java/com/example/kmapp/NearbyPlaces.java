package com.example.kmapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class NearbyPlaces extends AppCompatActivity {
    private String lat = "";
    private String longt = "";

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchLocationData x = new fetchLocationData();
        x.execute();
    }

    public class fetchLocationData extends AsyncTask<Void, Void, Void> {
        String data = "";
        String dataParsed = "";
        GpsTracker tracker = new GpsTracker(getApplicationContext());
        Double latitude = tracker.getLatitude();
        Double longitude = tracker.getLongitude();

        @Override
        protected Void doInBackground(Void... voids) {
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
                    dataParsed = dataParsed + "/n"+ j +". " + name;
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
            //handleExit(dataParsed);
        }
    }
}
