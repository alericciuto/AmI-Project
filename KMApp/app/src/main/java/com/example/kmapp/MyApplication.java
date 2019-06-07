package com.example.kmapp;

import android.app.Application;
import android.speech.tts.TextToSpeech;


public class MyApplication extends Application {

    private DatabaseAccess databaseAccess;
    private NetworkTask networktask;

    private TextToSpeech tts;

    public void setDatabase(){
        this.databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
    }

    public void setNetworkTask(){
        this.networktask = new NetworkTask();
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

