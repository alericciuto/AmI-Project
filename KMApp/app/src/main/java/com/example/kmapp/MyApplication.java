package com.example.kmapp;

import android.app.Application;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class MyApplication extends Application {

    private DatabaseAccess databaseAccess;
    private MediaPlayer mp;
    private NetworkTask networktask;

    private TextToSpeech tts;

    public void setDatabase(){
        this.databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
    }

    public void setNetworkTask(MediaPlayer mp){
        this.networktask = new NetworkTask(mp);
        this.networktask.execute();
    }

    public DatabaseAccess getDatabaseAccess() {
        return databaseAccess;
    }

    public NetworkTask getNetworktask() {
        return networktask;
    }

    public TextToSpeech getTTS(){
        return tts;
    }

    public void setTTS(TextToSpeech tts){
        this.tts = tts;
    }
}
