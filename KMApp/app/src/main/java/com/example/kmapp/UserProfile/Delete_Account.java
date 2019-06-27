package com.example.kmapp.UserProfile;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.example.kmapp.DatabaseInteraction.DatabaseAccess;
import com.example.kmapp.MainAdapter;
import com.example.kmapp.MyApplication;
import com.example.kmapp.R;

import java.util.List;

public class Delete_Account extends AppCompatActivity {

    private GridView gridView;
    private int image= R.drawable.userdel;
    private DatabaseAccess databaseAccess;
    private String[] users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_delete_account);
        gridView=findViewById(R.id.delete_grid);

        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

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

    private void loadUsers(){
        final List<String> users_list=databaseAccess.getUserNames();

        users = users_list.toArray(new String[0]);
        MainAdapter adapter = new MainAdapter(Delete_Account.this, users, image);
        gridView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        this.finish();
        return true;
    }
}

