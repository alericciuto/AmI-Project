package com.example.kmapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    //public TextView result;
    private GridView gridView;
    private int image=R.drawable.user;      //volendo da rendere vettore
    private Button new_account;
    private ImageButton delete_account;
    private NetworkTask networktask;
    private MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new_account = findViewById(R.id.new_account);
        delete_account = findViewById( R.id.delete_button );

        gridView=findViewById(R.id.query_grid);

        DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        final List<String> buffer=databaseAccess.getQuery();
        databaseAccess.close();

        final String[] vector= buffer.toArray(new String[0]);

        MainAdapter adapter = new MainAdapter(MainActivity.this, vector, image);
        gridView.setAdapter(adapter);

        mp = MediaPlayer.create(this, R.raw.alarm_sleeping);
        networktask = new NetworkTask(mp); //New instance of NetworkTask
        networktask.execute();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //da sostituire con collegamento a nuova pagina utente
                Toast.makeText(getApplicationContext(),"Switch to "+vector[position],Toast.LENGTH_SHORT).show();
                networktask.SendDataToNetwork("start_server", "true");
            }
        });

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


    private void openNewAccountPage() {
        Intent intent = new Intent(this, New_Account.class);
        startActivity(intent);
        this.finish();
    }

    private void openDeleteAccountPage() {
        Intent intent = new Intent(this, Delete_Account.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        networktask.SendDataToNetwork("start_server", "false");
        networktask.cancel(true); //In case the task is currently running
        super.onDestroy();
    }
}