package com.example.kmapp;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MusicPostConversation extends AppCompatActivity {

    private Button stopMusicButton;
    private MediaPlayer songPlayer;
    private AudioManager audio;
    private int actualVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_post_conversation);
        stopMusicButton = (Button) findViewById(R.id.stopMusicButton);
        audio = (AudioManager)getSystemService(AUDIO_SERVICE);
        actualVolume= audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, this.audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
        songPlayer = MediaPlayer.create(this, R.raw.song);
        songPlayer.start();
        songPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });
    }

    public void stopoMusicOnClick(final View view){
        songPlayer.stop();
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, actualVolume, AudioManager.FLAG_PLAY_SOUND);

        //CODICE PER TORNARE ALL'ATTIVITA' PRINCIPALE
        //MusicPostConversation.super.onBackPressed();
        this.finish();
    }
}
