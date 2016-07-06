package com.example.androidserversocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ReplyThread extends Thread {

    private Socket hostThreadSocket;
    //    int cnt;
    String strPathSend;

    public ReplyThread(Socket socket, String strPath) {
        hostThreadSocket = socket;
//        cnt = c;
        strPathSend = strPath;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        String msgReply = strPathSend;
        Appconfig.TEXTSTRING=msgReply;
        System.out.println("111 reply thread");
        try {
            outputStream = hostThreadSocket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(msgReply);
            printStream.close();

//            new SendToServer().start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class SendToServer extends Thread {
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
                    String res=br.readLine().toString();
                    if (res.equals("true")) {
                        hostThreadSocket=Appconfig.socketArray.get(Appconfig.sendCount);
                        OutputStream os= hostThreadSocket.getOutputStream();
                        PrintStream printStream = new PrintStream(os);
                        printStream.print(Appconfig.TEXTSTRING);
                        printStream.close();
                        Appconfig.sendCount++;
                        if (Appconfig.socketArray.size()==Appconfig.sendCount)
                            Appconfig.sendCount=0;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}