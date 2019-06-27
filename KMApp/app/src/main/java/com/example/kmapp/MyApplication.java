package com.example.kmapp;

import android.app.Application;
import android.speech.tts.TextToSpeech;

import com.example.kmapp.DatabaseInteraction.DatabaseAccess;
import com.example.kmapp.ServerCommunication.NetworkTask;


public class MyApplication extends Application {

    private DatabaseAccess databaseAccess;
    private NetworkTask networktask;
    private boolean restartDetect = false;
    private boolean startLocation = false;
    private boolean musicBack = false;

    public boolean getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(boolean startLocation) {
        this.startLocation = startLocation;
    }



    public boolean getRestartDetect() {
        return restartDetect;
    }

    public void setRestartDetect(boolean restartDetect) {
        this.restartDetect = restartDetect;
    }

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


    public boolean getMusicBack() {
        return musicBack;
    }

    public void setMusicBack(boolean musicBack) {
        this.musicBack=musicBack;
    }
}

