package com.example.kmapp.Conversation.Meteo;

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

public class Meteo extends AsyncTask<Void,Void,String> {
    private StringBuilder data;
    private StringBuilder dataParsed;
    private String city;
    private boolean ready = false;

    public Meteo(StringBuilder data, StringBuilder dataParsed, String city) {
        this.data = data;
        this.dataParsed = dataParsed;
        this.city = city;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            while (city.compareTo( "" ) == 0) {
            }
            URL url = new URL( "http://api.openweathermap.org/data/2.5/weather?q=" + city.replace( " ", "%20" ) + "&APPID=5b223f593e42fa8af1b7229cdd7e3f23" );
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data.append( line );
            }

            JSONObject jo = new JSONObject( data.toString() );

            JSONObject tmp_main = ((JSONObject) jo.get( "main" ));
            Double temp = (Double) tmp_main.get( "temp" ) - 273.15;
            Double tempMin = (Double) tmp_main.get( "temp_min" ) - 273.15;
            Double tempMax = (Double) tmp_main.get( "temp_max" ) - 273.15;

            JSONObject tmp_weather = ((JSONArray) jo.get( "weather" )).getJSONObject( 0 );

            dataParsed.append( "In " + city + " the weather is characterized by " + tmp_weather.get( "description" ) + "\n" +
                    "Currently there are : " + String.format( "%.0f", temp ) + " grades" + "\n" +
                    "The minimum temperature is : " + String.format( "%.0f", tempMin ) + " grades" + "\n" +
                    "The maximum temperature is : " + String.format( "%.0f", tempMax ) + " grades" + "\n" +
                    "Do you want to do something else?" );

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

    public String getDataParsed() {
        return dataParsed.toString();
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

}