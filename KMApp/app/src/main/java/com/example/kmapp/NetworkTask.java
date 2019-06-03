package com.example.kmapp;

import android.media.MediaPlayer;
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
    private JSONObject jsonSend = new JSONObject();
    private JSONObject jsonRecv = new JSONObject();
    private MediaPlayer mp;

    private String HOST = "192.168.43.58";
    private int PORT = 5000;

    public NetworkTask(MediaPlayer mp){
        this.mp = mp;
        this.mp.setLooping(true);
    }


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
            socket.connect(sockaddr, 0); //10 second connection timeout
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
        if (socket.isConnected()) {
            Log.i("NetworkTask","SendDataToNetwork: Writing received message to socket");
            new Thread(() -> {
                try {
                    jsonSend.put(name, value);
                    OutputStreamWriter out = new OutputStreamWriter(nos, StandardCharsets.UTF_8);
                    Log.i("NetworkTask","SendDataToNetwork: Trying to send: " + jsonSend.toString().getBytes().length+jsonSend.toString());
                    out.write(jsonSend.toString().getBytes().length+"\n"+jsonSend.toString());
                    out.flush();
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.i("NetworkTask","SendDataToNetwork: Message send failed. Caught an exception");
                }
            }).start();
            return true;
        }
        Log.i( "NetworkTask","SendDataToNetwork: Cannot send message. Socket is closed");
        return false;
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public JSONObject receiveData(){
        new Thread(() -> {
            if( socket.isConnected() )
                try {

                    byte[] bytes = new byte[1];
                    StringBuilder data = new StringBuilder();
                    // inserisco in data la dimensione dei dati da ricevere
                    nis.read(bytes, 0, 1);
                    String buffer = new String(bytes);
                    while(! buffer.equals("\n")){
                        data.append(buffer);
                        nis.read(bytes, 0, 1);
                        buffer = new String(bytes);
                    }

                    int dim = Integer.parseInt(data.toString());
                    bytes = new byte[dim];
                    nis.read(bytes, 0, dim);
                    buffer = new String(bytes);
                    Log.i("NetworkTask", "doInBackground: Received : " + data);

                    jsonRecv = new JSONObject(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("NetworkTask", "receiveData: IOException");
                } catch (JSONException e){
                    e.printStackTrace();
                    Log.i("NetworkTask", "receiveData : json format not accepted ");
                }
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
        return jsonRecv;
    }

    /*private void analyzeReceivedData(){
        try {
            if( jsonRecv.get("sound").equals("on") )
                mp.start();
            else mp.pause();
            if ( jsonRecv.get("MAX_EYELID") != null )
                return;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("NetworkTask", "analyzeReceivedData : json format not accepted ");
        }
    }*/
}
