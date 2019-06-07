package com.example.kmapp;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

public class Delete_Account extends AppCompatActivity {

    private ImageButton back_button;
    private GridView gridView;
    private int image=R.drawable.userdel;
    private DatabaseAccess databaseAccess;
    private String[] users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_delete__account );
        back_button = findViewById( R.id.back_button );
        gridView=findViewById(R.id.delete_grid);

        back_button.setOnClickListener(v -> openMainPage());

        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();

        loadUsers();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Delete " + users[position] + "?");

            builder.setPositiveButton("YES", (dialog, which) -> {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Deleting "+users[position]+"...",Toast.LENGTH_SHORT).show());
                databaseAccess.deleteUser(users[position]);
                loadUsers();
                dialog.dismiss();
            });

            builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());

            AlertDialog alert = builder.create();
            alert.show();
        });

    }

    private void openMainPage(){
        this.finish();
    }

    private void loadUsers(){
        final List<String> users_list=databaseAccess.getUserNames();

        users = users_list.toArray(new String[0]);
        MainAdapter adapter = new MainAdapter(Delete_Account.this, users, image);
        gridView.setAdapter(adapter);
    }
}
