package com.example.androidserversocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ReplyThread extends Thread {

    //    int cnt;
    String strPathSend;
    private Socket hostThreadSocket;
    private boolean isCalled = false;

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
            outputStream = hostThreadSocket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(msgReply);
            printStream.flush();
            printStream.close();

            new Thread(new ReceiveFromClient()).start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}