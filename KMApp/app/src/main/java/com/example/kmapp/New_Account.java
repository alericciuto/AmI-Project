package com.example.kmapp;

import android.app.Notification;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


public class New_Account extends AppCompatActivity {
    private EditText editName;
    private String name;
    private Button confirm_button;
    private ImageButton back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__account);
        editName = findViewById(R.id.input_name);
        confirm_button = findViewById(R.id.confirm_button);
        back_button = findViewById(R.id.back_button);

        confirm_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                name = editName.getText().toString().trim();
                if(name.equals("")){
                    Toast.makeText(getApplicationContext(),"Be careful! Some fields are empty.",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();
                    databaseAccess.insertRecord(name);
                    databaseAccess.close();
                    Toast.makeText(getApplicationContext(),"New Account saved",Toast.LENGTH_SHORT).show();
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openMainPage();
            }
    });
}

    private void openMainPage(){
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        this.finish();
    }
}
