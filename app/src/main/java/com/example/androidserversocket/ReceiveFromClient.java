package com.example.androidserversocket;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by multidots on 20-Jul-16.
 */
public class ReceiveFromClient extends Thread {
    private ServerSocket socketServer = null;
    private Socket socket = null;

    @Override
    public void run() {
        super.run();
        try {
            socketServer = new ServerSocket(9000);
            System.out.println("OUTER-----------");
            while (true) {
                System.out.println("Control in while..");
                socket = socketServer.accept();
                System.out.println("AFTER while..");
                InputStream inputStream = socket.getInputStream();
                System.out.println("INput stream--------");
                InputStreamReader isr = new InputStreamReader(inputStream);
                System.out.println("AFTER INput stream--------");
                BufferedReader br = new BufferedReader(isr);
                System.out.println("RECEIVED TEXT--------:" + br.readLine());
                br.close();

                System.out.println(Appconfig.socketArray.size() + ":" + Appconfig.sendCount);
                if (Appconfig.sendCount < Appconfig.socketArray.size()) {
                    System.out.println("Called New...");
                    socket = Appconfig.socketArray.get(Appconfig.sendCount);
                    OutputStream os = socket.getOutputStream();
                    PrintStream printStream = new PrintStream(os);
                    printStream.print(Appconfig.TEXTSTRING);
                    printStream.close();
                    Appconfig.sendCount++;
                } else {
                    Appconfig.sendCount = 0;
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            System.out.println("Called Again...");
                            ServerText.startScrollAgain();
                            System.out.println("Again...");
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socketServer != null) {
                try {
                    socketServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
