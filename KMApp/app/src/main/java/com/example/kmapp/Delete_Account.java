package com.example.kmapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

public class Delete_Account extends AppCompatActivity {

    private ImageButton back_button;
    private GridView gridView;
    private int image=R.drawable.userdel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_delete__account );
        back_button = findViewById( R.id.back_button );
        gridView=findViewById(R.id.delete_grid);

        back_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openMainPage();
            }
        });

        loadUsers();

    }

    private void openMainPage(){
        //Intent refresh = new Intent(this, MainActivity.class);
        //startActivity(refresh);
        this.finish();
    }

    private void loadUsers(){
        MainActivity.databaseAccess.open();
        final List<String> buffer=MainActivity.databaseAccess.getQuery();
        MainActivity.databaseAccess.close();

        final String[] vector= buffer.toArray(new String[0]);
        MainAdapter adapter = new MainAdapter(Delete_Account.this, vector, image);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"Deleting "+vector[position]+"...",Toast.LENGTH_SHORT).show(); //da sostituire con collegamento a nuova pagina utente
                DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();
                databaseAccess.deleteRecord(vector[position]);
                databaseAccess.close();
                loadUsers();
            }
        });
    }
}
