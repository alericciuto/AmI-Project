package com.example.kmapp;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

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
    private Socket nsocket; //Network Socket
    private InputStream nis; //Network Input Stream
    private OutputStream nos; //Network Output Stream
    private JSONObject jsonSend = new JSONObject();
    private JSONObject jsonRecv = new JSONObject();
    private MediaPlayer mp;

    public NetworkTask(MediaPlayer mp){
        this.mp = mp;
        mp.setLooping(true);
    }


    @Override
    protected void onPreExecute() {
        Log.i("AsyncTask", "onPreExecute");
    }

    @Override
    protected Boolean doInBackground(Void... params) { //This runs on a different thread
        boolean result = false;
        try {
            Log.i("AsyncTask", "doInBackground: Creating socket");
            SocketAddress sockaddr = new InetSocketAddress("192.168.43.27", 5000);
            nsocket = new Socket();
            nsocket.connect(sockaddr, 0); //10 second connection timeout
            if (nsocket.isConnected()) {
                nis = nsocket.getInputStream();
                nos = nsocket.getOutputStream();
                Log.i("AsyncTask", "doInBackground: Socket created, streams assigned");
                Log.i("AsyncTask", "doInBackground: Waiting for initial data...");
                byte[] buffer = new byte[4096];
                int read = nis.read(buffer, 0, 4096); //This is blocking
                while(read != -1){
                    byte[] tempdata = new byte[read];
                    System.arraycopy(buffer, 0, tempdata, 0, read);
                    publishProgress(tempdata);
                    String data = new String(tempdata);
                    try {
                        jsonRecv = new JSONObject(data);
                        Log.i("AsyncTask", "doInBackground: Received : " + data);
                        System.out.println( jsonRecv);
                        if( jsonRecv.get("sound").equals("on") )
                            mp.start();
                        else if ( jsonRecv.get("sound").equals("off") )
                            mp.pause();
                    } catch(org.json.JSONException e){
                        Log.i("AsyncTask", "doInBackground: Length : " + data);
                    }
                    Log.i("AsyncTask", "doInBackground: Got some data");
                    read = nis.read(buffer, 0, 4096); //This is blocking
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("AsyncTask", "doInBackground: IOException");
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("AsyncTask", "doInBackground: Exception");
            result = true;
        } finally {
            try {
                nis.close();
                nos.close();
                nsocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("AsyncTask", "doInBackground: Finished");
        }
        return result;
    }

    public void SendDataToNetwork(final String name, final String value) {
        if (nsocket.isConnected()) {
            Log.i("AsyncTask","SendDataToNetwork: Writing received message to socket");
            new Thread(new Runnable() {
                public void run() {
                    try {
                        System.out.println("json : "+ jsonSend);
                        jsonSend.put(name, value);
                        System.out.println("json : "+ jsonSend);
                        OutputStreamWriter out = new OutputStreamWriter(nos, StandardCharsets.UTF_8);
                        Log.i("AsyncTask","SendDataToNetwork: Trying to send: " + jsonSend.toString().getBytes().length+jsonSend.toString());
                        out.write(jsonSend.toString().getBytes().length+"\n"+jsonSend.toString());
                        out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("AsyncTask","SendDataToNetwork: Message send failed. Caught an exception");
                    }
                }
            }).start();
        }
        else Log.i( "AsyncTask","SendDataToNetwork: Cannot send message. Socket is closed");
    }

    @Override
    protected void onProgressUpdate(byte[]... values) {
        if (values.length > 0) {
            Log.i("AsyncTask", "onProgressUpdate: " + values[0].length + " bytes received.");
        }
    }
    @Override
    protected void onCancelled() {
        Log.i("AsyncTask", "Cancelled.");
    }
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Log.i("AsyncTask", "onPostExecute: Completed with an Error.");
        } else {
            Log.i("AsyncTask", "onPostExecute: Completed.");
        }
    }
}
