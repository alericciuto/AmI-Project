package com.example.kmapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class NetworkTask extends AsyncTask<Void, byte[], Boolean> {
    Socket nsocket; //Network Socket
    InputStream nis; //Network Input Stream
    OutputStream nos; //Network Output Stream

    @Override
    protected void onPreExecute() {
        Log.i("AsyncTask", "onPreExecute");
    }

    @Override
    protected Boolean doInBackground(Void... params) { //This runs on a different thread
        boolean result = false;
        try {
            Log.i("AsyncTask", "doInBackground: Creating socket");
            SocketAddress sockaddr = new InetSocketAddress("192.168.43.58", 5000);
            nsocket = new Socket();
            nsocket.connect(sockaddr, 0); //10 second connection timeout
            if (nsocket.isConnected()) {
                nis = nsocket.getInputStream();
                nos = nsocket.getOutputStream();
                Log.i("AsyncTask", "doInBackground: Socket created, streams assigned");
                Log.i("AsyncTask", "doInBackground: Waiting for inital data...");
                byte[] buffer = new byte[4096];
                int read = nis.read(buffer, 0, 4096); //This is blocking
                while(read != -1){
                    byte[] tempdata = new byte[read];
                    System.arraycopy(buffer, 0, tempdata, 0, read);
                    publishProgress(tempdata);
                    System.out.println(new String(tempdata) );
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

    public void SendDataToNetwork(final String cmd) {
        if (nsocket.isConnected()) {
            Log.i("AsyncTask","SendDataToNetwork: Writing received message to socket");
            new Thread(new Runnable() {
                public void run() {
                    try {
                        nos.write(cmd.getBytes());
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
