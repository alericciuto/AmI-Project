package com.example.kmapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_NUMBER = "com.example.application.example.EXTRA_NUMBER";

    private GridView gridView;
    private int image=R.drawable.user;      //volendo da rendere vettore
    private Button new_account;
    private ImageButton delete_account;
    public static NetworkTask networktask;
    private MediaPlayer mp;
    public static DatabaseAccess databaseAccess;
    public static TextToSpeech tts;
    public static int tts_init=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    Toast.makeText(getApplicationContext(), "Text To Speech Initialized", Toast.LENGTH_SHORT).show();
                    tts_init=1;
                }
            }
        });

        setContentView(R.layout.activity_main);
        new_account = findViewById(R.id.new_account);
        delete_account = findViewById( R.id.delete_button );

        gridView=findViewById(R.id.query_grid);

        databaseAccess=DatabaseAccess.getInstance(getApplicationContext());


        mp = MediaPlayer.create(this, R.raw.alarm_sleeping);
        networktask = new NetworkTask(mp); //New instance of NetworkTask
        networktask.execute();
        onResume();

        new_account.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openNewAccountPage();
            }
        });

        delete_account.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openDeleteAccountPage();
            }
        });

    }

    private void openAccountPage(int pos) {
        Intent intent = new Intent(this, Account.class);
        intent.putExtra( EXTRA_NUMBER, pos );
        startActivity(intent);
    }


    private void openNewAccountPage() {
        Intent intent = new Intent(this, New_Account.class);
        startActivity(intent);
    }

    private void openDeleteAccountPage() {
        Intent intent = new Intent(this, Delete_Account.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        networktask.cancel(true); //In case the task is currently running
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //networktask.SendDataToNetwork("start_server", "false");
    }

    @Override
    protected void onResume(){
        super.onResume();
        DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        final List<String> buffer=databaseAccess.getQuery();
        databaseAccess.close();

        final String[] vector= buffer.toArray(new String[0]);

        MainAdapter adapter = new MainAdapter(MainActivity.this, vector, image);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //da sostituire con collegamento a nuova pagina utente
                Toast.makeText(getApplicationContext(),"Switch to "+vector[position],Toast.LENGTH_SHORT).show();
                openAccountPage(position+1);
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

}