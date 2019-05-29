package com.example.kmapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Account extends AppCompatActivity {

    private ImageButton back_button;
    private Button conv_button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_account );

        MainActivity.networktask.SendDataToNetwork( "start_server", "true" );
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
    }

    protected void openConversationActivity(final View view){
        MainActivity.networktask.SendDataToNetwork( "start_server", "false" );
        Intent conv = new Intent(this, ConversationActivity.class);
        startActivity(conv);
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
