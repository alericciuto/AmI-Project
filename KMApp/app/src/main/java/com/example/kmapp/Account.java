package com.example.kmapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Account extends AppCompatActivity {

    private ImageButton back_button;
    private Button conv_button;
    private TextView textView;
    private FirebaseDatabase database;
    private DatabaseReference db_user;

    private int PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_account );

<<<<<<< HEAD
        onResume();
=======
>>>>>>> 5a99b005dd5fa64ba53e69a2c00ff42dc31989a4
        Intent intent = getIntent();
        int id = intent.getIntExtra( MainActivity.EXTRA_NUMBER, 0);
        conv_button = findViewById(R.id.conversation_button);

        back_button = findViewById(R.id.back_button);
        textView = findViewById( R.id.textView3 );

        back_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openMainPage();
            }
        });

        MainActivity.databaseAccess.open();
        textView.setText( "Attivato account di " + MainActivity.databaseAccess.getUser(id) );
        MainActivity.databaseAccess.close();

        database = FirebaseDatabase.getInstance();
        MainActivity.databaseAccess.open();
        StringBuffer buffer = MainActivity.databaseAccess.getPref( id );
        MainActivity.databaseAccess.close();
        db_user = database.getReference("userInfo");
        db_user.child("prefInfo").setValue(buffer.toString());

    }

    protected void openConversationActivity(final View view){
        while(ContextCompat.checkSelfPermission( Account.this, Manifest.permission.RECORD_AUDIO ) == PackageManager.PERMISSION_DENIED) {
            RequestRecordPermission();
        }
//        if(MainActivity.tts_init==1){
            MainActivity.networktask.SendDataToNetwork( "start_server", "false" );
            Intent conv = new Intent( this, ConversationActivity.class );
            startActivity( conv );
//        }
//        else{
//            Toast.makeText(this, "Conversation not avaiable yet!", Toast.LENGTH_SHORT).show();
//        }
    }

    private void RequestRecordPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.RECORD_AUDIO )){

            new AlertDialog.Builder( this )
                    .setTitle( "Permission needed" )
                    .setMessage( "This permission is needed to start the conversation." )
                    .setPositiveButton( "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions( Account.this, new String[] {Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE );
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
        MainActivity.networktask.SendDataToNetwork( "start_server", "false" );
        this.finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
        MainActivity.networktask.SendDataToNetwork( "start_server", "true" );
    }
}
