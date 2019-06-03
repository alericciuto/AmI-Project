package com.example.kmapp;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_intial_configuration );

        start_button = findViewById(R.id.button);
        back_button = findViewById(R.id.back_button);
        text = findViewById(R.id.textView4);

        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();
        networktask = ((MyApplication) this.getApplication()).getNetworktask();
        mp = MediaPlayer.create(this, R.raw.beep);

        Intent intent = getIntent();
        String userName = Objects.requireNonNull(intent.getExtras()).getString( "USERNAME");
        user = databaseAccess.getUser(userName);

        start_button.setOnClickListener(v -> {
            try {

                start_button.setEnabled(false);
                back_button.setEnabled(false);

                text.setTextColor(Color.RED);
                text.setText("You'll hear two beep now.\nDo what I tell you between them");

                if(! networktask.sendData("start_initial_configuration", "true")) {
                    noConnection();
                    return;
                }

                text.setText("Keep your eyes open");

                json = networktask.receiveData();
                if(json.get("beep").equals("go"))   mp.start();
                json = networktask.receiveData();
                user.setMAX_EYELID(Float.parseFloat( (String) json.get("MAX_EYELID")));
                mp.start();

                text.setText("Keep your eyes close");

                json = networktask.receiveData();
                if(json.get("beep").equals("go"))   mp.start();
                json = networktask.receiveData();
                user.setMIN_EYELID(Float.parseFloat((String) json.get("MIN_EYELID")));
                mp.start();

                text.setText("Finally, tighten the steering wheel");

                /*json = networktask.receiveData();
                if(json.get("beep").equals("go"))   mp.start();
                json = networktask.receiveData();
                user.setMAX_PRESSURE(Integer.parseInt((String) json.get("MAX_PRESSURE")));
                mp.start();*/

                databaseAccess.insertUser(user);
                text.setText("Thank you, now I'm ready to start!");

                start_button.setEnabled(true);
                back_button.setEnabled(true);

                this.finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        back_button.setOnClickListener( v -> super.onBackPressed());
    }

    private void noConnection(){
        Toast.makeText(getApplicationContext(), "No connection with the Server!", Toast.LENGTH_SHORT).show();
        start_button.setEnabled(true);
        back_button.setEnabled(true);

    }
}
