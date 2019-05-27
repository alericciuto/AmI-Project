package com.example.kmapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Account extends AppCompatActivity {

    private ImageButton back_button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_account );

        Intent intent = getIntent();
        int id = intent.getIntExtra( MainActivity.EXTRA_NUMBER, 0);

        back_button = findViewById(R.id.back_button);
        textView = findViewById( R.id.textView3 );

        back_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openMainPage();
            }
        });

        DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        textView.setText( "Attivato account di " + databaseAccess.getUser(id) );
        databaseAccess.close();
    }

    private void openMainPage(){
        //Intent refresh = new Intent(this, MainActivity.class);
        //startActivity(refresh);
        this.finish();
    }
}
