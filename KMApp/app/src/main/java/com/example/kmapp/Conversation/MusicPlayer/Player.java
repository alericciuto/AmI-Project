package com.example.kmapp.Conversation.MusicPlayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kmapp.R;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements View.OnClickListener{
    private Button play, pause, stop;
    private MediaPlayer mediaPlayer;
    private int pausePosition;
    private ArrayList<String> mySongs = new ArrayList<>( );
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_player );

        getMusic();

        play = findViewById( R.id.play );
        pause = findViewById( R.id.pause );
        stop = findViewById( R.id.stop );

        play.setOnClickListener( this::onClick );
        pause.setOnClickListener( this::onClick );
        stop.setOnClickListener( this::onClick );

        play.callOnClick();

    }

    private void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query( songUri, null, null, null, null );

        if(songCursor!=null && songCursor.moveToFirst()){
            int songLocation = songCursor.getColumnIndex( MediaStore.Audio.Media.DATA );
            do{
                String currentLocation = songCursor.getString( songLocation );
                mySongs.add(currentLocation);
            }while (songCursor.moveToNext());
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.play:
                if (mediaPlayer==null) {
                    starting(index);
                }
                else if(!mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo( pausePosition );
                    mediaPlayer.start();
                }
                break;
            case R.id.pause:
                if (mediaPlayer!=null) {
                    mediaPlayer.pause();
                    pausePosition=mediaPlayer.getCurrentPosition();
                }
                break;
            case R.id.stop:
                if(mediaPlayer!=null) {
                    mediaPlayer.stop();
                    mediaPlayer=null;
                    this.finish();
                }
                break;
        }
    }

    public void starting(int index){
        mediaPlayer = new MediaPlayer();
        try {
            Object[] s = mySongs.toArray();
            mediaPlayer.setDataSource(s[index].toString());
            mediaPlayer.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setOnCompletionListener( completeListener );
                    mp.start();
                }
            } );
            mediaPlayer.prepare();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    MediaPlayer.OnCompletionListener completeListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.release();
            if(index<mySongs.size()){
                index++;
                starting(index);
            }
            else{
                index=0;
                starting( index );
            }
        }
    };
}