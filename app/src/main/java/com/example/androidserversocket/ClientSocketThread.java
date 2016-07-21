package com.example.androidserversocket;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import cropimageview.ScrollTextViewClient;

/**
 * Created by multidots on 07-Jul-16.
 */
public class ClientSocketThread extends Thread {
    String dstAddress;
    int dstPort;

    public ClientSocketThread(String address, int port) {
        dstAddress = address;
        dstPort = port;
    }

    @Override
    public void run() {
        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            //Send the message to the server
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            String sendMessage = "true";
            bw.write(sendMessage);
            bw.flush();
            bw.close();
            os.flush();
            os.close();
            System.out.println("Message sent to the server : " + sendMessage);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    System.out.println("Started client Again...");
                    ClientText.startScrollAgain();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
