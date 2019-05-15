package com.example.kmapp;

import android.content.Intent;
import android.icu.text.BreakIterator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class MainActivity extends AppCompatActivity {

    //public TextView result;
    private GridView gridView;
    private int image=R.drawable.user;      //volendo da rendere vettore
    private Button new_account;
    private ImageButton delete_account;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new_account = findViewById(R.id.new_account);
        delete_account = findViewById( R.id.delete_button );

        gridView=findViewById(R.id.query_grid);
        client = new OkHttpClient();

        DatabaseAccess databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        final List<String> buffer=databaseAccess.getQuery();
        databaseAccess.close();

        final String[] vector= buffer.toArray(new String[0]);

        MainAdapter adapter = new MainAdapter(MainActivity.this, vector, image);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"Switch to "+vector[position],Toast.LENGTH_SHORT).show(); //da sostituire con collegamento a nuova pagina utente
                start();
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

    }

    private void openNewAccountPage() {
        Intent intent = new Intent(this, New_Account.class);
        startActivity(intent);
        this.finish();
    }

    private void openDeleteAccountPage() {
        Intent intent = new Intent(this, Delete_Account.class);
        startActivity(intent);
        this.finish();
    }

    private final class EchoWebSocketListener extends WebSocketListener{
        private static final int NORMAL_CLOSURE_STATUS=1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            webSocket.send( "Hello!" );
            webSocket.close( NORMAL_CLOSURE_STATUS,  "Goodbye!");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text){
            output("Receiving: "+text);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason){
            webSocket.close( NORMAL_CLOSURE_STATUS, null );
            output("Closing: " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response){
            output("Error: "+t.getMessage());
        }

    }

    private void output(final String txt) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Send data: "+txt,Toast.LENGTH_SHORT).show();
            }
        } );
    }

    private void start(){
        Request request = new Request.Builder().url( "ws://192.168.43.58:39868").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket ws = client.newWebSocket( request, listener);

        client.dispatcher().executorService().shutdown();
    }
}