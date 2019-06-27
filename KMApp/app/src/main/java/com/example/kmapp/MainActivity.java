package com.example.kmapp;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kmapp.DatabaseInteraction.DatabaseAccess;
import com.example.kmapp.UserProfile.Account;
import com.example.kmapp.UserProfile.Delete_Account;
import com.example.kmapp.UserProfile.New_Account;
import com.example.kmapp.UserProfile.User;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private GridView gridView;
    private int image=R.drawable.user;
    private Button new_account;
    private ImageButton delete_account;
    private User[] users;
    private DatabaseAccess databaseAccess;
    private ProgressBar pb;
    private TextToSpeech tts;

    private static final int REQUEST_CODE = 121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new_account = findViewById(R.id.new_account);
        delete_account = findViewById( R.id.delete_button );
        pb = findViewById(R.id.progress_bar);
        gridView=findViewById(R.id.query_grid);

        ((MyApplication) this.getApplication()).setDatabase();
        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();

        pb.setVisibility(View.VISIBLE);
        new_account.setVisibility( View.GONE );
        gridView.setVisibility( View.GONE );
        delete_account.setVisibility( View.GONE );

        getSupportActionBar().hide();

        //fill the list view with the task list

        tts = new TextToSpeech(MainActivity.this, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage( Locale.US);
                Toast.makeText(getApplicationContext(), "Text To Speech Initialized", Toast.LENGTH_SHORT).show();
            }
            if(tts != null) {
                ((MyApplication) MainActivity.this.getApplication()).setTTS(tts);
                loadUsers();
                gridView.setVisibility( View.VISIBLE );
                delete_account.setVisibility( View.VISIBLE );
                new_account.setVisibility( View.VISIBLE);
                pb.setVisibility(View.GONE);
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(), "OPS, something goes wrong!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

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

        if (Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            String[] perms = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };


            if (!EasyPermissions.hasPermissions(this, perms)) {
                EasyPermissions.requestPermissions(this, "All permissions are required in oder to run this application", REQUEST_CODE, perms);
            }


        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.requestPermissions(this, "All permissions are required to run this application", requestCode, permissions);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText( this, "Permission GRANTED", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }

    }
}