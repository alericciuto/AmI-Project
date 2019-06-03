package com.example.kmapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    private int PERMISSION_CODE = 1;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_account );

        networktask = ((MyApplication) this.getApplication()).getNetworktask();

        Intent intent = getIntent();
        String userName = Objects.requireNonNull(intent.getExtras()).getString( "USERNAME");
        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();
        user = databaseAccess.getUser(userName);

        conv_button = findViewById(R.id.conversation_button);
        back_button = findViewById(R.id.back_button);
        textView = findViewById( R.id.textView3 );
        detect_button = findViewById( R.id.button2);
        stop_button = findViewById( R.id.button3);

        back_button.setOnClickListener(v -> openMainPage());

        conv_button.setOnClickListener(v -> {
//            if(! user.is_configured()){
//                openInitialConfig();
//            }else openConversationActivity();
            openConversationActivity();
        });

        detect_button.setOnClickListener(v -> {
            if(! user.is_configured()){
                Toast.makeText( this, "Set Configuration first", Toast.LENGTH_SHORT ).show();
                return;
            }
            detect_button.setEnabled(false);
            stop_button.setEnabled(true);
            networktask.sendData( "start_server", "true" );
            Toast.makeText( this, "Starting detecting", Toast.LENGTH_SHORT ).show();
        } );
        stop_button.setOnClickListener(v -> {
            if( !detect_button.isEnabled() ) {
                networktask.sendData("start_server", "false");
                stop_button.setEnabled(false);
                detect_button.setEnabled(true);
            }else Toast.makeText( this, "No detection is running!", Toast.LENGTH_SHORT ).show();
        });
        stop_button.setEnabled(false);

        textView.setText(String.format("Attivato account di %s\nPreferenze = %s\nMAX EYELID = %.2f\nMIN EYELID = %.2f\nMAX PRESSURE = %d",
                                        user.getName(),
                                        user.getPreferences(),
                                        user.getMAX_EYELID(),
                                        user.getMIN_EYELID(),
                                        user.getMAX_PRESSURE()));

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
                    .setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions( Account.this, new String[] {Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE );
                            dialog.dismiss();
                        }
                    } )
                    .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    } )
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
        networktask.sendData( "start_server", "false" );
        this.onBackPressed();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
}
