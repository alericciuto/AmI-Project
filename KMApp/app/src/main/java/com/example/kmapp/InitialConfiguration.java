package com.example.kmapp;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class InitialConfiguration extends AppCompatActivity {
    private Button start_button;
    private ImageButton back_button;
    private DatabaseAccess databaseAccess;
    private TextView text;
    private NetworkTask networktask;
    private MediaPlayer mp;
    private User user;
    private JSONObject json;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_intial_configuration );

        start_button = findViewById(R.id.button);
        text = findViewById(R.id.textView4);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();
        networktask = ((MyApplication) this.getApplication()).getNetworktask();
        mp = MediaPlayer.create(this, R.raw.beep);

        Intent intent = getIntent();
        String userName = Objects.requireNonNull(intent.getExtras()).getString( "USERNAME");
        user = databaseAccess.getUser(userName);

        start_button.setOnClickListener(v -> startConfig());
    }

    private void noConnection(){
        runOnUiThread(() ->  Toast.makeText(getApplicationContext(), "No connection with the Server!", Toast.LENGTH_SHORT).show());
        runOnUiThread(() -> start_button.setEnabled(true));
        runOnUiThread(() -> actionBar.setDisplayHomeAsUpEnabled(true));
    }

    private void startConfig(){
        new Thread(){
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        try{
                            runOnUiThread(() -> start_button.setEnabled(false));
                            runOnUiThread(() -> actionBar.setDisplayHomeAsUpEnabled(false));

                            if(! networktask.isConnected()) {
                                noConnection();
                                return;
                            }

                            runOnUiThread(() -> text.setTextColor(Color.RED));
                            runOnUiThread(() -> text.setText("You'll hear two beep now.\nDo what I tell you between them"));

                            wait(2000);

                            networktask.sendData("start_initial_configuration", "true");

                            runOnUiThread(() -> text.setText("Keep your eyes open"));

                            json = networktask.receiveData();
                            if(json.get("beep").equals("go")) {
                                mp.start();
                                runOnUiThread(() -> Toast.makeText( InitialConfiguration.this, "BEEP1", Toast.LENGTH_SHORT ).show());
                            }
                            json = networktask.receiveData();
                            user.setMAX_EYELID(Float.parseFloat( (String) json.get("MAX_EYELID")));
                            mp.start();
                            runOnUiThread(() -> Toast.makeText( InitialConfiguration.this, "BEEP2", Toast.LENGTH_SHORT ).show());

                            runOnUiThread(() -> text.setText("Keep your eyes close"));
                            json = networktask.receiveData();
                            if(json.get("beep").equals("go")) {
                                mp.start();
                                runOnUiThread(() -> Toast.makeText( InitialConfiguration.this, "BEEP3", Toast.LENGTH_SHORT ).show());
                            }
                            json = networktask.receiveData();
                            user.setMIN_EYELID(Float.parseFloat((String) json.get("MIN_EYELID")));
                            mp.start();
                            runOnUiThread(() -> Toast.makeText( InitialConfiguration.this, "BEEP4", Toast.LENGTH_SHORT ).show());

                            runOnUiThread(() -> text.setText("Thank you, now I'm ready to start!"));
                            databaseAccess.updateUser(user);

                            runOnUiThread(() -> actionBar.setDisplayHomeAsUpEnabled(true));

                            wait(3000);
                            runOnUiThread(() -> start_button.setEnabled(true));
                            runOnUiThread(InitialConfiguration.this::onSupportNavigateUp);

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent account = new Intent( this, Account.class );
        account.putExtra( "USERNAME", user.getName() );
        startActivity( account );
        this.finish();
        return true;
    }
}