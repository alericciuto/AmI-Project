package com.example.kmapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class InitialConfiguration extends AppCompatActivity {
    private Button start_button;
    private DatabaseAccess databaseAccess;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_intial_configuration );

        start_button = findViewById(R.id.button);
        text = findViewById(R.id.textView4);

        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();

        Intent intent = getIntent();
        String userName = Objects.requireNonNull(intent.getExtras()).getString( "USERNAME");

        start_button.setOnClickListener(v -> {
            start_button.setEnabled(false);

            //send data to server

        });
    }
}
