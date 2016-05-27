package com.example.androidserversocket;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by multidots on 4/22/2016.
 */
public class MyClientTask1 extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";
    public static ArrayList<String > list=new ArrayList<>();
    private OnPostCallComplete onpostcallcomplete;

    MyClientTask1(String addr, int port, OnPostCallComplete onPostCallComplete) {
        dstAddress = addr;
        dstPort = port;
        this.onpostcallcomplete = onPostCallComplete;
    }
//    public MyClientPost(Context context, String progressMessage, OnPostCallComplete onPostCallComplete2) {
//        message = progressMessage;
//        this.context = context;
//        this.onpostcallcomplete = onPostCallComplete2;
//
//        if (!(message.equals(""))) {
//            dialog = new ProgressDialog(context);
//            dialog.setMessage(progressMessage);
//        }
//    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

//            ByteArrayOutputStream byteArrayOutputStream =
//                    new ByteArrayOutputStream(8192000);
//            byte[] buffer = new byte[8192000];
//
//            int bytesRead;
//            InputStream inputStream = socket.getInputStream();
//
//            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
//
//            StringBuilder total = new StringBuilder();
//            String line;
//            while ((line = r.readLine()) != null) {
//                total.append(line).append('\n');
//            }
//
////            Bitmap b = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
//            System.out.println("total :" + total);

            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream()); //Error Line!
            try {
                Object object = objectInput.readObject();
                list = (ArrayList<String>) object;
            }catch (Exception e){
                e.printStackTrace();
            }
//            response = total.toString();
            response = list.get(0);
            System.out.println("height received :"+list.get(1));
                /*
				 * notice:
				 * inputStream.read() will block if no data return
				 */
//            while ((bytesRead = inputStream.read(buffer)) != -1){
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                response += byteArrayOutputStream.toString("UTF-8");
//            }
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
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
        return null;
    }

    protected void onPostExecute(final Void result) {
//        textResponse.setText(response);
//        System.out.println("response :" + response.toString());

//        MyClientTask1 myClientTask = new MyClientTask1(
//                "192.168.43.157",
//                8080);
//        myClientTask.execute();

//        new Handler().postDelayed(new Runnable(){
//            @Override
//            public void run() {
//					startActivity(myintent);
//					finish();
        try {
            onpostcallcomplete.response(response);

        } catch (JSONException e) {
            e.printStackTrace();
        }
//            }
//        }, 1000);

        super.onPostExecute(result);
    }

    public interface OnPostCallComplete {
        void response(String result) throws JSONException;
    }

}
