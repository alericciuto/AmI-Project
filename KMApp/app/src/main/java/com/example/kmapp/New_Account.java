package com.example.kmapp;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.TreeMap;


public class New_Account extends AppCompatActivity {
    private EditText editName;
    private String name;
    private Button confirm_button;
    private ImageButton back_button;
    private TextView textView;

    private int num_categories;
    private Map<Integer, String> cat = new TreeMap<Integer, String>();
    private FirebaseDatabase database;
    private DatabaseReference db_categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_new__account);
        editName = findViewById(R.id.input_name);
        confirm_button = findViewById(R.id.confirm_button);
        back_button = findViewById(R.id.back_button);
        textView = findViewById(R.id.textViewCat);
        readCategories();
    }

    public void confirmOnClick(final View v) {
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
            openMainPage();
        }
    }
    public void backOnClick(final View v) {
        openMainPage();
    }

    public void readCategories(){ //FUNZIONE DI LETTURA DELLE CATEGORIE DAL DATABASE DI FIREBASE
        db_categories = database.getReference("categoriesInfo");
        db_categories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                num_categories = dataSnapshot.child("quantity").getValue(Integer.class);
                for(int i=0; i<num_categories; i++){
                    String name = dataSnapshot.child("cat"+Integer.toString(i)+"name").getValue(String.class);
                    int id = dataSnapshot.child("cat"+Integer.toString(i)+"id").getValue(Integer.class);
                    cat.put(id, name);
                }
                fillText(); //CHIAMATA ALLA FUNZIONE DI SETUP DEL TEXT VIEW
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void fillText() {//FUNZIONE DI SCRITTURA DEL TEXT VIEW
        StringBuilder x = new StringBuilder();
        for(int i=0; i<num_categories; i++){
            x.append(cat.get(i)+"\n");
        }
        textView.setText(x);
    }

    private void openMainPage(){
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        this.finish();
    }
}
