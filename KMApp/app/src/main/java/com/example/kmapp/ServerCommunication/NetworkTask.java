package com.example.kmapp.ServerCommunication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;


public class NetworkTask extends AsyncTask<Void, byte[], Boolean> {
    private Socket socket; //Network Socket
    private InputStream nis; //Network Input Stream
    private OutputStream nos; //Network Output Stream
    private JSONObject json;

    private String HOST = "192.168.0.159";
    private int PORT = 8563;


    @Override
    protected void onPreExecute() {
        Log.i("AsyncTask", "onPreExecute");
    }

    @Override
    protected Boolean doInBackground(Void... params) { //This runs on a different thread
        boolean result = false;
        try {
            Log.i("NetworkTask", "doInBackground: Creating socket");
            SocketAddress sockaddr = new InetSocketAddress( HOST, PORT );
            socket = new Socket();
            socket.connect(sockaddr, 2000); //10 second connection timeout
            if (socket.isConnected()) {
                nis = socket.getInputStream();
                nos = socket.getOutputStream();
                result = true;
                Log.i("NetworkTask", "doInBackground: Socket created, streams assigned");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("NetworkTask", "doInBackground: IOException");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("NetworkTask", "doInBackground: Exception");
        }
        Log.i("NetworkTask", "doInBackground: Finished");
        return result;
    }

    protected void connect(){
        new Thread( () -> {
            try {
                Log.i("NetworkTask", "doInBackground: Creating socket");
                SocketAddress sockaddr = new InetSocketAddress( HOST, PORT );
                socket = new Socket();
                socket.connect(sockaddr, 2); //10 second connection timeout
                if (socket.isConnected()) {
                    nis = socket.getInputStream();
                    nos = socket.getOutputStream();
                    Log.i("NetworkTask", "doInBackground: Socket created, streams assigned");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("NetworkTask", "doInBackground: IOException");
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("NetworkTask", "doInBackground: Exception");
            }
            Log.i("NetworkTask", "doInBackground: Finished");
            synchronized (this){
                notify();
            }
        }).start();
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected(){
        if(socket != null && socket.isConnected())
            return true;
        else return false;
    }

    @Override
    protected void onCancelled() {
        Log.i("NetworkTask", "Cancelled.");
    }
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Log.i("NetworkTask", "onPostExecute: Completed with an Error.");
        } else {
            Log.i("NetworkTask", "onPostExecute: Completed.");
        }
    }


    public boolean sendData(String name, String value) {
        //this.connect();
        new Thread(() -> {
            if (! socket.isConnected())
                this.connect();
            try {
                json = new JSONObject();
                json.put(name, value);
                OutputStreamWriter out = new OutputStreamWriter(nos, StandardCharsets.UTF_8);
                Log.i("NetworkTask","SendDataToNetwork: Trying to send: " + json.toString().getBytes().length + json.toString());
                out.write(json.toString().getBytes().length+"\n"+json.toString());
                out.flush();
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
                Log.i("NetworkTask","SendDataToNetwork: Message send failed. Caught an exception");
            }
            synchronized (this){
                notify();
            }
        }).start();
        //this.disconnect();
        try {
            synchronized (this){
                wait();
                Log.i( "NetworkTask","SendDataToNetwork: Message sent");
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i( "NetworkTask","SendDataToNetwork: Cannot send message. Socket is closed");
            return false;
        }
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public JSONObject receiveData(){
        new Thread(() -> {
            //this.connect();
            if( !socket.isConnected())
                this.connect();
            try {

                byte[] bytes = new byte[1];
                StringBuilder data = new StringBuilder();
                // inserisco in data la dimensione dei dati da ricevere
                nis.read(bytes, 0, 1);
                String buffer = new String(bytes);
                while (!buffer.equals("\n")) {
                    data.append(buffer);
                    nis.read(bytes, 0, 1);
                    buffer = new String(bytes);
                }

                int dim = Integer.parseInt(data.toString());
                bytes = new byte[dim];
                nis.read(bytes, 0, dim);
                buffer = new String(bytes);

                json = new JSONObject(buffer);
                Log.i("NetworkTask", "doInBackground: Received : " + data + " " + json);

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("NetworkTask", "receiveData: IOException");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("NetworkTask", "receiveData : json format not accepted ");
            }
            //this.disconnect();
            synchronized (this) {
                notify();
            }
        }).start();
        try {
            synchronized (this){
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return json;
    }
}