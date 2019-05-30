package com.example.kmapp;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class ObjectTTS extends Application {
    private static TextToSpeech tts;

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    Toast.makeText(getApplicationContext(), "Text To Speech Initialized", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public TextToSpeech getTTS(){
        return tts;
    }
}
