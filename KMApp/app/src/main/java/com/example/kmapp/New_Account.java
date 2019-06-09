package com.example.kmapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class New_Account extends AppCompatActivity {
    private EditText editName;
    private String name;
    private Button confirm_button;
    private ProgressBar pg;
    private TextView title;
    private User user;

    private FirebaseDatabase firebase;
    private DatabaseReference db_categories;
    private DatabaseAccess databaseAccess;
    private NetworkTask networktask;

    private ListView mListView;
    private int num_categories;
    private ArrayList<Categories> categories = new ArrayList<>( );
    private CategoriesAdapter adapter;
    private List<Integer> preferences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);

        editName = findViewById(R.id.input_name);
        confirm_button = findViewById(R.id.confirm_button);
        mListView = findViewById( R.id.listView );
        pg = findViewById(R.id.progress_bar_2);
        title = findViewById(R.id.textView);

        databaseAccess = ((MyApplication) this.getApplication()).getDatabaseAccess();

        Intent intent = getIntent();
        if(intent.getExtras() != null){
            name = Objects.requireNonNull(intent.getExtras()).getString( "USERNAME");
            user = databaseAccess.getUser(name);
            title.setText("Edit Profile:");
            editName.setText(name);
        }

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebase = FirebaseDatabase.getInstance();
        adapter = new CategoriesAdapter(getApplicationContext(), readCategories());
        mListView.setAdapter( adapter );


        mListView.setOnItemClickListener((parent, view, position, id) -> {
            CheckBox objCheckbox = view.findViewById(R.id.checkbox);
            Categories cat = categories.get( position );
            if(cat.isChecked()) {
                cat.setChecked(false);
                preferences.remove(cat.getId());
                objCheckbox.setChecked(false);
            }
            else{
                cat.setChecked( true );
                preferences.add(cat.getId());
                objCheckbox.setChecked(true);
            }

            categories.set( position, cat );
        });

        confirm_button.setOnClickListener(v -> {
            name = editName.getText().toString().trim();
            if(name.equals(""))
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Username is necessary!",Toast.LENGTH_SHORT).show());
            else{
                if(preferences.isEmpty())
                    preferences.addAll(categories.stream().map(Categories::getId).collect(Collectors.toList()));
                if(user != null){
                    user.setName(name);
                    user.setPreferences(preferences);
                    databaseAccess.updateUser(user);
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Profile Modified",Toast.LENGTH_SHORT).show());
                }
                else if(! databaseAccess.insertUser(new User(name, preferences))) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "User " + name + " already exist!", Toast.LENGTH_SHORT).show());
                    return;
                }else runOnUiThread(() -> Toast.makeText(getApplicationContext(),"New Account saved",Toast.LENGTH_SHORT).show());

                ((MyApplication) this.getApplication()).setNetworkTask();
                Intent initConf = new Intent( New_Account.this, InitialConfiguration.class );
                initConf.putExtra( "USERNAME", name );
                startActivity( initConf );
                New_Account.this.finish();
            }
        });

        pg.setVisibility(View.VISIBLE);

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
                    if( user != null){
                        if(user.getPreferences().contains(cat.getId())){
                            cat.setChecked(true);
                            preferences.add(cat.getId());
                        }
                    }
                    categories.add(cat);
                    array.add(cat);
                }
                pg.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return array;
    }

    @Override
    public boolean onSupportNavigateUp(){
        this.finish();
        return true;
    }



}
