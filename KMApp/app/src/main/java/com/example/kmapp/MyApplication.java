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

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(this, status -> {
            if(status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.US);
                Toast.makeText(getApplicationContext(), "Text To Speech Initialized", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setDatabase(){
        this.databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
    }

    public void setMediaPlayer(){
        this.mp = MediaPlayer.create(this, R.raw.alarm_sleeping);
    }

    public void setNetworkTask(){
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
}
