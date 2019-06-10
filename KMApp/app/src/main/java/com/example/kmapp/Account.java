package com.example.kmapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Account extends AppCompatActivity {

    private ListView pref_listview;
    private Button conv_button;
    private Button detect_button;
    private Button stop_button;
    private ImageButton edit_profile;
    private ProgressBar pg;
    private TextView username;
    private TextView min_eyelid;
    private TextView max_eyelid;
    private NetworkTask networktask;
    private DatabaseAccess databaseAccess;
    private User user;
    private String userName;
    private MediaPlayer mp;
    private ActionBar actionBar;

    private DatabaseReference db_categories;
    private FirebaseDatabase firebase;
    private DatabaseReference db_user;
    private ArrayList<String> categories;

    private int PERMISSION_CODE = 1;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_account );

        Intent intent = getIntent();
        userName = Objects.requireNonNull(intent.getExtras()).getString( "USERNAME");
        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();

        firebase = FirebaseDatabase.getInstance();
        String buffer = databaseAccess.getPreferences(userName);
        db_user = firebase.getReference("userInfo");
        db_user.child("prefInfo").setValue(buffer);
        user = databaseAccess.getUser(userName);

        mp = MediaPlayer.create(getApplicationContext(), R.raw.alarm_sleeping);
        mp.setLooping(true);

        conv_button = findViewById(R.id.conv_button);
        detect_button = findViewById( R.id.detect_button);
        stop_button = findViewById( R.id.stop_button);
        username = findViewById(R.id.username);
        max_eyelid = findViewById(R.id.max_eyelid);
        min_eyelid = findViewById(R.id.min_eyelid);
        pref_listview = findViewById(R.id.preferences);
        edit_profile = findViewById(R.id.edit_profile);
        pg = findViewById(R.id.progress_bar_account);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        edit_profile.setOnClickListener(v -> openEditProfile());

        conv_button.setOnClickListener(v -> openConversationActivity());

        detect_button.setOnClickListener(v -> {
            if(! user.is_configured()){
                runOnUiThread(() -> Toast.makeText( this, "Set Configuration first", Toast.LENGTH_SHORT ).show());
                openInitialConfig();
                return;
            }
            if(networktask.isConnected()) {
                runOnUiThread(() -> detect_button.setEnabled(false));
                runOnUiThread(() -> actionBar.setDisplayHomeAsUpEnabled(false));
                runOnUiThread(() -> stop_button.setEnabled(true));
                networktask.sendData("MAX_EYELID", String.valueOf(user.getMAX_EYELID()));
                networktask.sendData("MIN_EYELID", String.valueOf(user.getMIN_EYELID()));
                networktask.sendData("start_server", "true");
                runOnUiThread(() -> Toast.makeText(this, "Starting detecting", Toast.LENGTH_SHORT).show());
                new Thread(this::detecting).start();
            }else runOnUiThread(()-> Toast.makeText( this, "No connection with th Server!", Toast.LENGTH_SHORT ).show());
        } );

        stop_button.setOnClickListener(v -> {
            if( !detect_button.isEnabled() ) {
                if(mp.isPlaying())
                    mp.stop();
                runOnUiThread(() -> actionBar.setDisplayHomeAsUpEnabled(false));
                runOnUiThread(() -> detect_button.setEnabled(true));
                runOnUiThread(() -> stop_button.setEnabled(false));
//                if(networktask.isConnected()) {
//                    networktask.sendData("start_server", "false");
//                    ((MyApplication) this.getApplication()).setNetworkTask();
//                    networktask = ((MyApplication) this.getApplication()).getNetworktask();
//                }
            }else runOnUiThread(() -> Toast.makeText( this, "No detection is running!", Toast.LENGTH_SHORT ).show());
        });

        stop_button.setEnabled(false);
    }

    private void openEditProfile() {
        Intent edit = new Intent( this, New_Account.class );
        edit.putExtra( "USERNAME", user.getName() );
        startActivity( edit );
        this.finish();
    }

    private void openInitialConfig() {
        Intent initConf = new Intent( this, InitialConfiguration.class );
        initConf.putExtra( "USERNAME", user.getName() );
        startActivity( initConf );
        this.finish();
    }

    protected void openConversationActivity(){
        if(ContextCompat.checkSelfPermission( Account.this, Manifest.permission.RECORD_AUDIO ) == PackageManager.PERMISSION_DENIED) {
            RequestRecordPermission();
        }
        if(ContextCompat.checkSelfPermission( Account.this, Manifest.permission.RECORD_AUDIO ) == PackageManager.PERMISSION_DENIED) {
            return;
        }
        if(networktask.isConnected())
            networktask.sendData( "start_server", "false" );
        Intent conv = new Intent( this, ConversationActivity.class );
        startActivity( conv );
    }

    private void RequestRecordPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.RECORD_AUDIO )){

            new AlertDialog.Builder( this )
                    .setTitle( "Permission" )
                    .setMessage( "This permission is needed to start the conversation." )
                    .setPositiveButton( "Ok", (dialog, which) -> {
                        ActivityCompat.requestPermissions( Account.this, new String[] {Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE );
                        dialog.dismiss();
                    })
                    .setNegativeButton( "Cancel", (dialog, which) -> dialog.dismiss())
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

    @Override
    protected void onResume(){
        super.onResume();
        ((MyApplication) this.getApplication()).setNetworkTask();
        networktask = ((MyApplication) this.getApplication()).getNetworktask();
        getCategories(user.getPreferences());
    }

    public void getCategories(List<Integer> catId){
        categories = new ArrayList<>();
        db_categories = firebase.getReference("categoriesInfo");
        db_categories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int num_cat = catId.size();
                catId.sort(Integer::compareTo);
                for(int i=0; i<num_cat; i++){
                    categories.add(dataSnapshot.child("cat"+Integer.toString(catId.get(i))+"name").getValue(String.class));
                }
                printUserInfo();
                pg.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void printUserInfo(){
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this, R.layout.categories, R.id.textViewCat, categories);
        pref_listview.setAdapter(arrayAdapter);
        username.setText(user.getName());
        if(user.getMAX_EYELID() == -1)
            max_eyelid.setText("Not set yet");
        else max_eyelid.setText(String.format("%.2f", user.getMAX_EYELID()));
        if(user.getMIN_EYELID() == -1)
            min_eyelid.setText("Not set yet");
        else min_eyelid.setText(String.format("%.2f", user.getMIN_EYELID()));
    }

    @Override
    public boolean onSupportNavigateUp(){
        if(networktask.isConnected())
            networktask.sendData( "start_server", "false" );
        this.finish();
        return true;
    }

    private void detecting(){
         //DA SCOMMENTARE QUANDO FINIAMO, SUONA DUE VOLTE PRIMA DI INIZIARE A PARLARE
         //LEVARE IL TASTO STOP SENNO NON FUNZIONA
        for(int i =0; i <4 ; i++){
            try {
                JSONObject json = networktask.receiveData();
                if(json.get("sound").equals("on"))
                    mp.start();
                else if (json.get("sound").equals("off"))
                    mp.pause();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        openConversationActivity();
    }

}