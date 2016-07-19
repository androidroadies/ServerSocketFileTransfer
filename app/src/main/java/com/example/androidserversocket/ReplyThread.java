package com.example.androidserversocket;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ReplyThread extends Thread {

    //    int cnt;
    String strPathSend;
    private Socket hostThreadSocket;
    private boolean isCalled=false;

    public ReplyThread(Socket socket, String strPath) {
        hostThreadSocket = socket;
//        cnt = c;
        strPathSend = strPath;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        String msgReply = strPathSend;
        Appconfig.TEXTSTRING = msgReply;
        System.out.println("111 reply thread");
        try {
//            if (Appconfig.sendCount!=0) {
//            if (!isCalled) {
//                isCalled = true;
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();
//            }
            new Thread(new ReceiveFromClient()).start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class ReceiveFromClient extends Thread {
        private ServerSocket socketServer;
        private Socket socket;

        @Override
        public void run() {
            super.run();
            try {
                socketServer = new ServerSocket(9000);
                System.out.println("OUTER-----------");
                while (true) {
                    socket = socketServer.accept();
                    InputStream inputStream = socket.getInputStream();
                    System.out.println("INput stream--------");
                    InputStreamReader isr = new InputStreamReader(inputStream);
                    System.out.println("AFTER INput stream--------");
                    BufferedReader br = new BufferedReader(isr);
                    System.out.println("RECEIVED TEXT--------:" + br.readLine());
                    br.close();
//                    if (br.readLine().equals("true")) {
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
                        Appconfig.sendCount = 1;
                        hostThreadSocket.close();
                        ReplyThread.currentThread().interrupt();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                System.out.println("Called Again...");
                                ServerText.startScrollAgain();
                                System.out.println("Again...");
                            }
                        });
                    }
//                        Appconfig.sendCount++;
//                        if (Appconfig.socketArray.size()==Appconfig.sendCount){}
//                            Appconfig.sendCount=0;
//                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}