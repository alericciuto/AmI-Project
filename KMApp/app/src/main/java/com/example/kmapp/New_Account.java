package com.example.kmapp;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.CheckBox;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class New_Account extends AppCompatActivity {
    private EditText editName;
    private String name;
    private Button confirm_button;
    private ImageButton back_button;

    private FirebaseDatabase database;
    private DatabaseReference db_categories;

    private ListView mListView;
    private int num_categories;
    private ArrayList<Categories> categories = new ArrayList<>( );
    private ArrayList<String> array = new ArrayList<>( );
    private ArrayAdapter<String> adapter;
    private StringBuffer preferences = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__account);

        editName = findViewById(R.id.input_name);
        confirm_button = findViewById(R.id.confirm_button);
        back_button = findViewById(R.id.back_button);

        database = FirebaseDatabase.getInstance();
        mListView = findViewById( R.id.listView );

        readCategories();

        confirm_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                name = editName.getText().toString().trim();
                if(name.equals("")){
                    Toast.makeText(getApplicationContext(),"Be careful! Some fields are empty.",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    mListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Categories cat = categories.get( position );
                            if(cat.isChecked())
                                cat.setChecked( false );
                            else
                                cat.setChecked( true );

                            categories.set( position, cat );
                            preferences.append( position + " " );
                        }
                    } );
                    DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();
                    databaseAccess.insertRecord(name, preferences);
                    databaseAccess.close();
                    Toast.makeText(getApplicationContext(),"New Account saved",Toast.LENGTH_SHORT).show();

                    openMainPage();
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

    public void readCategories(){
        db_categories = database.getReference("categoriesInfo");
        db_categories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                num_categories = dataSnapshot.child("quantity").getValue(Integer.class);
                for(int i=0; i<num_categories; i++){
                    Categories cat = new Categories();
                    cat.setId(dataSnapshot.child("cat"+Integer.toString(i)+"id").getValue(Integer.class));
                    cat.setText(dataSnapshot.child("cat"+Integer.toString(i)+"name").getValue(String.class));
                    cat.setChecked(false);

                    categories.add(cat);
                    array.add(dataSnapshot.child("cat"+Integer.toString(i)+"name").getValue(String.class));
                }

                adapter = new ArrayAdapter<String>( getApplicationContext(), R.layout.row, R.id.checkBox, array);
                mListView.setAdapter( adapter );
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void openMainPage(){
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        this.finish();
    }


}
