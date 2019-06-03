package com.example.kmapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private GridView gridView;
    private int image=R.drawable.user;      //volendo da rendere vettore
    private Button new_account;
    private ImageButton delete_account;
    private User[] users;
    private DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        new_account = findViewById(R.id.new_account);
        delete_account = findViewById( R.id.delete_button );

        gridView=findViewById(R.id.query_grid);

        databaseAccess=DatabaseAccess.getInstance(getApplicationContext());

        ((MyApplication) this.getApplication()).setDatabase();
        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();

        loadUsers();

        ((MyApplication) this.getApplication())
                .setNetworkTask(MediaPlayer.create(this, R.raw.alarm_sleeping));

        new_account.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openNewAccountPage();
            }
        });

        delete_account.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openDeleteAccountPage();
            }
        });

    }

    private void loadUsers() {
        List<User> users_list=databaseAccess.getAllUsers();
        if(users_list == null)
            return;

        users = users_list.toArray(new User[0]);

        MainAdapter adapter = new MainAdapter(MainActivity.this,
                                                users_list.stream().map(User::getName).toArray(String[]::new),
                                                image);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(getApplicationContext(),"Switch to " + users[position].getName(), Toast.LENGTH_SHORT).show();
            openAccountPage(users[position].getName());
        });
    }

    private void openAccountPage(String user) {
        Intent intent = new Intent(this, Account.class);
        intent.putExtra( "USERNAME", user );
        startActivity(intent);
    }


    private void openNewAccountPage() {
        Intent intent = new Intent(this, New_Account.class);
        startActivity(intent);
    }

    private void openDeleteAccountPage() {
        Intent intent = new Intent(this, Delete_Account.class);
        startActivity(intent);
    }


    @Override
    protected void onResume(){
        super.onResume();
        loadUsers();
    }

}