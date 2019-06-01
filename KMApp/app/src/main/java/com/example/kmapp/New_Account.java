package com.example.kmapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import java.util.List;
import java.util.stream.Collectors;


public class New_Account extends AppCompatActivity {
    private EditText editName;
    private String name;
    private Button confirm_button;
    private ImageButton back_button;

    private FirebaseDatabase firebase;
    private DatabaseReference db_categories;
    private DatabaseAccess databaseAccess;

    private ListView mListView;
    private int num_categories;
    private ArrayList<Categories> categories = new ArrayList<>( );
    private CategoriesAdapter adapter;
    private List<Integer> preferences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__account);

        editName = findViewById(R.id.input_name);
        confirm_button = findViewById(R.id.confirm_button);
        back_button = findViewById(R.id.back_button);
        mListView = findViewById( R.id.listView );


        firebase = FirebaseDatabase.getInstance();
        adapter = new CategoriesAdapter(getApplicationContext(), readCategories());
        mListView.setAdapter( adapter );

        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            CheckBox objCheckbox = (CheckBox) view.findViewById(R.id.checkbox);
            Categories cat = categories.get( position );
            if(cat.isChecked()) {
                cat.setChecked(false);
                preferences.remove(position);
                objCheckbox.setChecked(false);
            }
            else{
                cat.setChecked( true );
                preferences.add(position);
                objCheckbox.setChecked(true);
            }

            categories.set( position, cat );
        });
        confirm_button.setOnClickListener(v -> {
            name = editName.getText().toString().trim();
            if(name.equals(""))
                Toast.makeText(getApplicationContext(),"Username is necessary!",Toast.LENGTH_SHORT).show();
            else{
                if(preferences.isEmpty())
                    preferences.addAll(categories.stream().map(Categories::getId).collect(Collectors.toList()));
                databaseAccess.insertUser(new User(name, preferences));
                Toast.makeText(getApplicationContext(),"New Account saved",Toast.LENGTH_SHORT).show();

                Intent initConf = new Intent( New_Account.this, InitialConfiguration.class );
                initConf.putExtra( "USERNAME", name );
                startActivity( initConf );
                New_Account.this.finish();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                New_Account.super.onBackPressed();
            }
        });
    }

    public ArrayList<Categories> readCategories(){
        ArrayList<Categories> array = new ArrayList<>();
        db_categories = firebase.getReference("categoriesInfo");
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
                    array.add(cat);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return array;
    }


}
