package com.example.kmapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Account extends AppCompatActivity {

    private ImageButton back_button;
    private Button conv_button;
    private Button detect_button;
    private Button stop_button;
    private TextView textView;
    private FirebaseDatabase firebase;
    private DatabaseReference db_user;
    private NetworkTask networktask;
    private DatabaseAccess databaseAccess;
    private User user;
    private String userName;
    private MediaPlayer mp;

    private int PERMISSION_CODE = 1;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_account );

        ((MyApplication) this.getApplication()).setNetworkTask();
        networktask = ((MyApplication) this.getApplication()).getNetworktask();
        mp = MediaPlayer.create(getApplicationContext(), R.raw.alarm_sleeping);
        mp.setLooping(true);

        conv_button = findViewById(R.id.conversation_button);
        back_button = findViewById(R.id.back_button);
        textView = findViewById( R.id.textView3 );
        detect_button = findViewById( R.id.button2);
        stop_button = findViewById( R.id.button3);

        Intent intent = getIntent();
        userName = Objects.requireNonNull(intent.getExtras()).getString( "USERNAME");
        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();
        printUserInfo();

        back_button.setOnClickListener(v -> openMainPage());

        conv_button.setOnClickListener(v -> {
            if(! user.is_configured())
                openInitialConfig();
            else openConversationActivity();
        });

        detect_button.setOnClickListener(v -> {
            if(! user.is_configured()){
                runOnUiThread(() -> Toast.makeText( this, "Set Configuration first", Toast.LENGTH_SHORT ).show());
                return;
            }
            if(networktask.isConnected()) {
                runOnUiThread(() -> detect_button.setEnabled(false));
                runOnUiThread(() -> stop_button.setEnabled(true));
                networktask.sendData("MAX_EYELID", String.valueOf(user.getMAX_EYELID()));
                networktask.sendData("MIN_EYELID", String.valueOf(user.getMIN_EYELID()));
                networktask.sendData("start_server", "true");
                runOnUiThread(() -> Toast.makeText(this, "Starting detecting", Toast.LENGTH_SHORT).show());
                new Thread(this::detecting).start();
            }else runOnUiThread(()-> Toast.makeText( this, "No connection with th Server!", Toast.LENGTH_SHORT ).show());
        } );

        stop_button.setOnClickListener(v -> {
            if( !detect_button.isEnabled() ) {
                if(mp.isPlaying())
                    mp.stop();
                runOnUiThread(() -> stop_button.setEnabled(false));
                runOnUiThread(() -> detect_button.setEnabled(true));
                openMainPage();
            }else runOnUiThread(() -> Toast.makeText( this, "No detection is running!", Toast.LENGTH_SHORT ).show());
        });

        stop_button.setEnabled(false);


        firebase = FirebaseDatabase.getInstance();
        String buffer = databaseAccess.getPreferences(userName);
        db_user = firebase.getReference("userInfo");
        db_user.child("prefInfo").setValue(buffer);
    }

    private void openInitialConfig() {
        Intent initConf = new Intent( this, InitialConfiguration.class );
        initConf.putExtra( "USERNAME", user.getName() );
        startActivity( initConf );
    }

    protected void openConversationActivity(){
        if(ContextCompat.checkSelfPermission( Account.this, Manifest.permission.RECORD_AUDIO ) == PackageManager.PERMISSION_DENIED) {
            RequestRecordPermission();
        }
        if(ContextCompat.checkSelfPermission( Account.this, Manifest.permission.RECORD_AUDIO ) == PackageManager.PERMISSION_DENIED) {
            return;
        }
        if(networktask.isConnected())
            networktask.sendData( "start_server", "false" );
        Intent conv = new Intent( this, ConversationActivity.class );
        startActivity( conv );
        this.finish();
    }

    private void RequestRecordPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.RECORD_AUDIO )){

            new AlertDialog.Builder( this )
                    .setTitle( "Permission" )
                    .setMessage( "This permission is needed to start the conversation." )
                    .setPositiveButton( "Ok", (dialog, which) -> {
                        ActivityCompat.requestPermissions( Account.this, new String[] {Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE );
                        dialog.dismiss();
                    })
                    .setNegativeButton( "Cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText( this, "Permission GRANTED", Toast.LENGTH_SHORT ).show();
            } else {
                Toast.makeText( this, "Permission DENIED", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    private void openMainPage(){
        if(networktask.isConnected())
            networktask.sendData( "start_server", "false" );
        this.onBackPressed();
    }

    @Override
    protected void onResume(){
        super.onResume();
        printUserInfo();
    }

    @SuppressLint("DefaultLocale")
    private void printUserInfo(){
        user = databaseAccess.getUser(userName);
        textView.setText(String.format("Attivato account di %s\nPreferenze = %s\nMAX EYELID = %.2f\nMIN EYELID = %.2f",
                user.getName(),
                user.getPreferences(),
                user.getMAX_EYELID(),
                user.getMIN_EYELID())
        );
    }

    private void detecting(){
        // DA SCOMMENTARE QUANDO FINIAMO, SUONA DUE VOLTE PRIMA DI INIZIARE A PARLARE
        // LEVARE IL TASTO STOP SENNO NON FUNZIONA
//        for(int i =0; i <4 ; i++){
//            try {
//                JSONObject json = networktask.receiveData();
//                if(json.get("sound").equals("on"))
//                    mp.start();
//                else if (json.get("sound").equals("off"))
//                    mp.pause();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        openConversationActivity();
    }
}
