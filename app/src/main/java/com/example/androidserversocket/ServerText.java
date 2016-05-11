package com.example.androidserversocket;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

//import cropimageview.ScrollTextView;
import cropimageview.ScrollTextView;
import testhotspot.WifiApManager;
import wifi.api.WifiStatus;
import wifi.api.wifiAddresses;
import wifi.api.wifiHotSpots;
import wifi.datatransfer.WifiSocket;

/**
 * We use this class for send images to our network connected devices means Client
 */

public class ServerText extends Activity {

    TextView info, infoip, msg;
    String message = "";
    ServerSocket serverSocket;
    Button btnServerSend, btnChoose, btnSelectPhoto, btnCreateGroup, btnOneDevice, btnTwoDevice, btnThreeDevice;
    Button btnL1, btnL2, btnL3, btnDone;
    ImageView img1, img2, img3,imageView;

    Boolean isDevice1 = false, isDevice2 = false, isDevice3 = false;
    Boolean isLayout1 = false, isLayout2 = false, isLayout3 = false;
    Socket socket;
//    ArrayList<Socket> socketArray = new ArrayList<Socket>();
    Context context;
    final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
    final int PICK_IMAGE_FROM_GALLARY = 5;
    String path = "";

    RelativeLayout relmain1, rel1, rel2, rel3;
    LinearLayout linmain1,linmain2;

    wifiHotSpots hotutil;
    WifiStatus wifiStatus;
    BroadcastReceiver receiver;
    WifiSocket ws = new WifiSocket(this);
    WifiApManager wifiApManager;

    //    SimpleAsynTask mTask;
    wifiAddresses au;

    ScrollTextView scrolltext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_text);

        context = ServerText.this;

        scrolltext=(ScrollTextView) findViewById(R.id.scrolltext);

//        scrolltext.setTextColor(Color.WHITE);
//        scrolltext.setTextSize(80);
//        scrolltext.startScroll();
//
        hotutil = new wifiHotSpots(getApplicationContext());
        wifiStatus = new WifiStatus(getApplicationContext());
        wifiApManager = new WifiApManager(this);
        au = new wifiAddresses(getApplicationContext());

        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);


        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);

        imageView = (ImageView) findViewById(R.id.imageView);

        rel1 = (RelativeLayout) findViewById(R.id.rel1);
        rel2 = (RelativeLayout) findViewById(R.id.rel2);
        rel3 = (RelativeLayout) findViewById(R.id.rel3);

        rel1.setVisibility(View.GONE);
        rel2.setVisibility(View.GONE);
        rel3.setVisibility(View.GONE);

        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                relmain1.setVisibility(View.VISIBLE);
                linmain1.setVisibility(View.GONE);
                linmain2.setVisibility(View.GONE);

            }
        });
        // Layout 1 for Horizontal device.
        btnL1 = (Button) findViewById(R.id.btnOneLayout);
        btnL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnL1.setBackgroundColor(Color.CYAN);
                btnL2.setBackgroundColor(Color.GRAY);
                btnL3.setBackgroundColor(Color.GRAY);

                rel1.setVisibility(View.VISIBLE);
                rel2.setVisibility(View.GONE);
                rel3.setVisibility(View.GONE);

                if (isDevice1 == true) {
                    img1.setBackgroundResource(R.drawable.h_2);
                    isLayout1 = true;
                    isLayout2 = false;
                    isLayout3 = false;
                }

                if (isDevice2 == true) {
                    img1.setBackgroundResource(R.drawable.h_3);
                    isLayout1 = true;
                    isLayout2 = false;
                    isLayout3 = false;
                }
                if (isDevice3 == true) {
                    img1.setBackgroundResource(R.drawable.h_4);
                    isLayout1 = true;
                    isLayout2 = false;
                    isLayout3 = false;
                }
            }
        });
        // Layout 1 for Vertical device.
        btnL2 = (Button) findViewById(R.id.btnTwoLayout);
        btnL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnL1.setBackgroundColor(Color.GRAY);
                btnL2.setBackgroundColor(Color.CYAN);
                btnL3.setBackgroundColor(Color.GRAY);

                rel1.setVisibility(View.GONE);
                rel2.setVisibility(View.VISIBLE);
                rel3.setVisibility(View.GONE);

                if (isDevice1 == true) {
                    img2.setBackgroundResource(R.drawable.v_2);
                    isLayout2 = true;
                    isLayout1 = false;
                    isLayout3 = false;
                }
                if (isDevice2 == true) {
                    img2.setBackgroundResource(R.drawable.v_3);
                    isLayout2 = true;
                    isLayout1 = false;
                    isLayout3 = false;
                }
                if (isDevice3 == true) {
                    img2.setBackgroundResource(R.drawable.v_4);
                    isLayout2 = true;
                    isLayout1 = false;
                    isLayout3 = false;
                }
            }
        });
        btnL3 = (Button) findViewById(R.id.btnThreeLayout);
        btnL3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnL1.setBackgroundColor(Color.GRAY);
                btnL2.setBackgroundColor(Color.GRAY);
                btnL3.setBackgroundColor(Color.CYAN);

                rel1.setVisibility(View.GONE);
                rel2.setVisibility(View.GONE);
                rel3.setVisibility(View.VISIBLE);

                if (isDevice3 == true) {
                    img3.setBackgroundResource(R.drawable.screen_4);
                    isLayout3 = true;
                    isLayout1 = false;
                    isLayout2 = false;
                }

            }
        });


        btnL1.setVisibility(View.GONE);
        btnL2.setVisibility(View.GONE);
        btnL3.setVisibility(View.GONE);

        relmain1 = (RelativeLayout) findViewById(R.id.relMain1);
        linmain1 = (LinearLayout) findViewById(R.id.linMain1);
        linmain2 = (LinearLayout) findViewById(R.id.linMain2);


        linmain1.setVisibility(View.VISIBLE);
        linmain2.setVisibility(View.GONE);
        relmain1.setVisibility(View.GONE);

        btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relmain1.setVisibility(View.GONE);
                linmain1.setVisibility(View.VISIBLE);
                linmain2.setVisibility(View.GONE);

                rel1.setVisibility(View.GONE);
                rel2.setVisibility(View.GONE);
                rel3.setVisibility(View.GONE);

                btnL1.setVisibility(View.GONE);
                btnL2.setVisibility(View.GONE);
                btnL3.setVisibility(View.GONE);

                btnOneDevice.setBackgroundColor(Color.GRAY);
                btnTwoDevice.setBackgroundColor(Color.GRAY);
                btnThreeDevice.setBackgroundColor(Color.GRAY);

            }
        });

        btnOneDevice = (Button) findViewById(R.id.btnOneDevice);
        btnOneDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnOneDevice.setBackgroundColor(Color.CYAN);
                btnTwoDevice.setBackgroundColor(Color.GRAY);
                btnThreeDevice.setBackgroundColor(Color.GRAY);

                btnL1.setVisibility(View.VISIBLE);
                btnL2.setVisibility(View.VISIBLE);
                btnL3.setVisibility(View.GONE);

                btnL1.setBackgroundColor(Color.GRAY);
                btnL2.setBackgroundColor(Color.GRAY);
                btnL3.setBackgroundColor(Color.GRAY);

                isDevice1 = true;
                isDevice2 = false;
                isDevice3 = false;

                rel1.setVisibility(View.GONE);
                rel2.setVisibility(View.GONE);
                rel3.setVisibility(View.GONE);

            }
        });
        btnTwoDevice = (Button) findViewById(R.id.btnTwoDevice);
        btnTwoDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnOneDevice.setBackgroundColor(Color.GRAY);
                btnTwoDevice.setBackgroundColor(Color.CYAN);
                btnThreeDevice.setBackgroundColor(Color.GRAY);

                btnL1.setVisibility(View.VISIBLE);
                btnL2.setVisibility(View.VISIBLE);
                btnL3.setVisibility(View.GONE);


                btnL1.setBackgroundColor(Color.GRAY);
                btnL2.setBackgroundColor(Color.GRAY);
                btnL3.setBackgroundColor(Color.GRAY);


                isDevice1 = false;
                isDevice2 = true;
                isDevice3 = false;

                rel1.setVisibility(View.GONE);
                rel2.setVisibility(View.GONE);
                rel3.setVisibility(View.GONE);

            }
        });
        btnThreeDevice = (Button) findViewById(R.id.btnThreeDevice);
        btnThreeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnOneDevice.setBackgroundColor(Color.GRAY);
                btnTwoDevice.setBackgroundColor(Color.GRAY);
                btnThreeDevice.setBackgroundColor(Color.CYAN);

                btnL1.setVisibility(View.VISIBLE);
                btnL2.setVisibility(View.VISIBLE);
                btnL3.setVisibility(View.VISIBLE);


                btnL1.setBackgroundColor(Color.GRAY);
                btnL2.setBackgroundColor(Color.GRAY);
                btnL3.setBackgroundColor(Color.GRAY);


                isDevice1 = false;
                isDevice2 = false;
                isDevice3 = true;

                rel1.setVisibility(View.GONE);
                rel2.setVisibility(View.GONE);
                rel3.setVisibility(View.GONE);

            }
        });


        btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hotutil.setHotSpot("SSID", "");
                inviteFriend(hotutil);
            }
        });

        btnSelectPhoto = (Button) findViewById(R.id.btnSelectPhoto);
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                picPhoto();
                pictext();
            }
        });

        btnServerSend = (Button) findViewById(R.id.btnServerSend);
        btnServerSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("socket " + Appconfig.socketArray.size());

                if (Appconfig.socketArray.size() > 0) {
                    SharedPreferences shre1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    String previouslyEncodedImagep1 = shre1.getString("image_datap1", "");
                    String previouslyEncodedImagep2 = shre1.getString("image_datap2", "");
                    String previouslyEncodedImagep3 = shre1.getString("image_datap3", "");
                    String previouslyEncodedImagep4 = shre1.getString("image_datap4", "");
//

                    for (int i = 0; i < Appconfig.socketArray.size(); i++) {


                        if (Appconfig.socketArray.size() == 1) {
                            SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(0), previouslyEncodedImagep1);
                            socketServerReplyThread.run();

                            linmain1.setVisibility(View.GONE);
                            linmain2.setVisibility(View.VISIBLE);
                            relmain1.setVisibility(View.GONE);

                            scrolltext.setText(previouslyEncodedImagep1);
                            scrolltext.setTextColor(Color.BLACK);
                            scrolltext.setTextSize(80);
                            scrolltext.startScroll();

//                            if( !previouslyEncodedImagep2.equalsIgnoreCase("") ){
//                                byte[] b2 = Base64.decode(previouslyEncodedImagep2, Base64.DEFAULT);
//                                Bitmap bitmapp2 = BitmapFactory.decodeByteArray(b2, 0, b2.length);
//                                imageView.setImageBitmap(bitmapp2);
//                            }

                        }
                        if (Appconfig.socketArray.size() == 2) {
                            SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(0), previouslyEncodedImagep1);
                            socketServerReplyThread.run();

                            socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(1), previouslyEncodedImagep2);
                            socketServerReplyThread.run();

                            linmain1.setVisibility(View.GONE);
                            linmain2.setVisibility(View.VISIBLE);
                            relmain1.setVisibility(View.GONE);

//                            if( !previouslyEncodedImagep3.equalsIgnoreCase("") ){
//                                byte[] b3 = Base64.decode(previouslyEncodedImagep3, Base64.DEFAULT);
//                                Bitmap bitmapp3 = BitmapFactory.decodeByteArray(b3, 0, b3.length);
//                                imageView.setImageBitmap(bitmapp3);
//                            }
                        }
                        if (Appconfig.socketArray.size() == 3) {
                            SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(0), previouslyEncodedImagep1);
                            socketServerReplyThread.run();

                            socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(1), previouslyEncodedImagep2);
                            socketServerReplyThread.run();

                            socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(2), previouslyEncodedImagep3);
                            socketServerReplyThread.run();

                            linmain1.setVisibility(View.GONE);
                            linmain2.setVisibility(View.VISIBLE);
                            relmain1.setVisibility(View.GONE);


//                            if( !previouslyEncodedImagep4.equalsIgnoreCase("") ){
//                                byte[] b4 = Base64.decode(previouslyEncodedImagep4, Base64.DEFAULT);
//                                Bitmap bitmapp4 = BitmapFactory.decodeByteArray(b4, 0, b4.length);
//                                imageView.setImageBitmap(bitmapp4);
//                            }

                        }
                    }



                }else {
                    Toast.makeText(context,"Please Connect Your Device First.!",Toast.LENGTH_LONG).show();
                }
            }
        });
        infoip.setText(getIpAddress());
//		infoip.setText("192.168.1.51");

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    private void pictext() {

    }

    /**
     * Start our hotspoy here and ask other user to join our Network Default ip is 192.168.43.1
     *
     * @param hotutil
     */
    public void inviteFriend(wifiHotSpots hotutil) {
        /*
        if(hotutil.setHotSpot("SSID",""))
		{
			hotutil.startHotSpot(true);
		}
		*/
        wifiApManager.setWifiApEnabled(null, true);
        hotutil.setAndStartHotSpot(true, "SSID");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                ServerText.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        info.setText("I'm waiting here: "
                                + serverSocket.getLocalPort());
                        System.out.println("test2 server text;");
                    }
                });

                while (true) {
                    socket = serverSocket.accept();

                    Appconfig.socketArray.add(count, socket);
                    count++;
                    message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n";

                    ServerText.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            msg.setText(message);
                        }
                    });

//					SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
//							socket, count);
//					socketServerReplyThread.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

//    private class SocketServerReplyThread extends Thread {
//
//        private Socket hostThreadSocket;
//        String strPathSend;
////		int cnt;
//
//        SocketServerReplyThread(Socket socket, String strPath) {//int c
//            hostThreadSocket = socket;
//            strPathSend = strPath;
////			cnt = c;
//        }
//
//        @Override
//        public void run() {
//
//
//            byte[] bytes;
//            byte[] buffer = new byte[8192000];
//            int bytesRead;
//            ByteArrayOutputStream output = new ByteArrayOutputStream();
//            try {
//                InputStream inputStream = new FileInputStream(path);//You can get an inputStream using any IO API
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    output.write(buffer, 0, bytesRead);
//                    System.out.println("buffer :" + buffer.length);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            bytes = output.toByteArray();
//
//            String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
//
//            OutputStream outputStream;
////            String msgReply = encodedString;//REmove comment
//            String msgReply = strPathSend;
//
//            System.out.println("test3 ;");
//            try {
//                outputStream = hostThreadSocket.getOutputStream();
//                PrintStream printStream = new PrintStream(outputStream);
//                printStream.print(msgReply);
//                printStream.close();
//
//                message += msgReply;
//
//                ServerText.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        msg.setText(message);
//
//                    }
//                });
//
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                message += "Something wrong! " + e.toString() + "\n";
//            }
//
//            ServerText.this.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    msg.setText(message);
//
//                }
//            });
//
//        }
//
//
//    }
private class SocketServerReplyThread extends Thread {

    private Socket hostThreadSocket;
//    int cnt;
    String strPathSend;

    SocketServerReplyThread(Socket socket, String strPath) {
        hostThreadSocket = socket;
//        cnt = c;
        strPathSend = strPath;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        String msgReply = "Hello from Android, you are #";

        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_datap1", msgReply);
        edit.putString("image_datap2", msgReply);
        edit.putString("image_datap3", msgReply);
        edit.putString("image_datap4", msgReply);
        edit.commit();
        try {
            outputStream = hostThreadSocket.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(msgReply);
            printStream.close();//Remove comment

            message += "replayed: " + msgReply + "\n";

            ServerText.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    msg.setText(message);
                }
            });

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            message += "Something wrong! " + e.toString() + "\n";
        }

        ServerText.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                msg.setText(message);
            }
        });
    }

}
    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    void picPhoto() {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_choose_image);
        dialog.setTitle(getResources().getString(R.string.choice));

        Button camera = (Button) dialog.findViewById(R.id.usecamera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent in= new Intent();
//                in.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(in,CHOOSE_FROM_CAMERA);
//                dialog.dismiss();
                Utils.captureImage(context, CAMERA_CAPTURE_IMAGE_REQUEST_CODE, "Back");
                dialog.dismiss();

            }
        });
        Button gallery = (Button) dialog.findViewById(R.id.usegallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent in= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(in, CHOOSE_FROM_GALLERY);
//                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_FROM_GALLARY);
                dialog.dismiss();
            }
        });

        Button dialogCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        // if button is clicked, close the custom dialog
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == PICK_IMAGE_FROM_GALLARY) {
                path = Utils.getPath(this, data.getData());
                Appconfig.mediaPath1 = path;
                System.out.println("path :" + Appconfig.mediaPath1);
                if (Appconfig.mediaPath1.length() > 0) {
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//						img_acteditfamily_profile.setImageBitmap(myBitmap);

                        // For split images in 3 part Horizontal.
                        // Part1

                        System.out.println("device1" + isDevice1);
                        System.out.println("device2" + isDevice2);
                        System.out.println("device3" + isDevice3);

                        System.out.println("isLayout1" + isLayout1);
                        System.out.println("isLayout2" + isLayout2);
                        System.out.println("isLayout3" + isLayout3);

                        if (isDevice1 && isLayout1 == true) {
                            cropHorizontal2Part(myBitmap);//2 part
                            btnServerSend.setVisibility(View.VISIBLE);
                        }
                        if (isDevice1 && isLayout2 == true) {
                            cropvertical2Part(myBitmap);//2 part
                            btnServerSend.setVisibility(View.VISIBLE);
                        }
//                        cropHorizontal2Part(myBitmap);//2 part
//                        cropvertical2Part(myBitmap);//2 part

                        if (isDevice2 && isLayout1 == true) {
                            cropHorizontal(myBitmap);//3 part
                            btnServerSend.setVisibility(View.VISIBLE);
                        }
                        if (isDevice2 && isLayout2 == true) {
                            cropvertical(myBitmap);//3 part
                            btnServerSend.setVisibility(View.VISIBLE);
                        }
//                        cropHorizontal(myBitmap);//3 part
//                        cropvertical(myBitmap);//3 part

                        if (isDevice3 && isLayout1 == true) {

                            cropL2(myBitmap);//4 part
//                            cropL1(myBitmap);//4 part
                            btnServerSend.setVisibility(View.VISIBLE);

                        }
                        if (isDevice3 && isLayout2 == true) {
//                            cropL2(myBitmap);//4 part
                            cropL1(myBitmap);//4 part
                            btnServerSend.setVisibility(View.VISIBLE);
                        }

                        if (isDevice3 && isLayout3 == true) {
                            cropL3(myBitmap);//4 part
                            btnServerSend.setVisibility(View.VISIBLE);
                        }
//                        else {
//
//                            Toast.makeText(context,"Choose Device First.!",Toast.LENGTH_LONG).show();
//                        }

//                        cropL1(myBitmap);//4 part
//                        cropL2(myBitmap);//4 part
//                        cropL3(myBitmap);//4 part
                    }

                }
            } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
                path = Utils.getPath(this, Utils.fileUri);
                Appconfig.mediaPath1 = path;
                System.out.println("path :" + Appconfig.mediaPath1);
//                Appconfig.background_color = "";
                if (Appconfig.mediaPath1.length() > 0) {
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//						img_acteditfamily_profile.setImageBitmap(myBitmap);
                    }

                }

            }
        }
    }

    private void cropvertical2Part(Bitmap bitmapOrg) {
        // =============================================================================================================================================
        // For split images in 2 part vertical.
        // Part1

        // 0, 0, image.size.width , (image.size.height) * 1/2)

        Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight() * 1 / 2);

        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // (0, (image.size.height) * 1/3, image.size.width, image.size.height/3.0));
//
        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1 / 2, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 2);

        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);

//=============================================================================================================================================
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_datap1", encodedImagep1);
        edit.putString("image_datap2", encodedImageP2);

        edit.commit();

//        Intent in = new Intent(getApplicationContext(), ViewCropImageSlice.class);
//        startActivity(in);

    }

    private void cropHorizontal2Part(Bitmap bitmapOrg) {
        //=============================================================================================================================================
        // For split images in 2 part Horizontal.
        // Part1

//            (0, 0,  (image.size.width) * 1/2, image.size.height)

        Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth() * 1 / 2, bitmapOrg.getHeight());

        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);

        // Part2

        // (image.size.width) * 1/2, 0, image.size.width/2.0, image.size.height)

        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1 / 2, 0, bitmapOrg.getWidth() / 2, bitmapOrg.getHeight());

        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);

// =============================================================================================================================================
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_datap1", encodedImagep1);
        edit.putString("image_datap2", encodedImageP2);
        edit.commit();

//        Intent in = new Intent(getApplicationContext(), ViewCropImageSlice.class);
//        startActivity(in);
    }

    /**
     * This method use for slice image for 4 devices.
     *
     * @param bitmapOrg
     */
    private void cropL3(Bitmap bitmapOrg) {
        //=============================================================================================================================================

        // For split images in 4 part vertical L3.
        // Part1

        // (0, 0,  (image.size.width) * 1/2, (image.size.height) * 1/2) );

        Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth() * 1 / 2, bitmapOrg.getHeight() * 1 / 2);

        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);

        // Part2

        // ((image.size.width) * 1/2, 0, (image.size.width) * 1/2, (image.size.height) * 1/2));

        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1 / 2, 0, bitmapOrg.getWidth() * 1 / 2, bitmapOrg.getHeight() * 1 / 2);

        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
//
        // Part3

        // (0, (image.size.height) * 1/2, (image.size.width) * 1/2, (image.size.height) * 1/2));

        Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1 / 2, bitmapOrg.getWidth() * 1 / 2, bitmapOrg.getHeight() * 1 / 2);

        ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
        croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
        byte[] bp3 = baosP3.toByteArray();
        String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);

        // Part4

        // ((image.size.width) * 1/2, (image.size.height) * 1/2, image.size.width/ 2.0, image.size.height));

        Bitmap croppedBmp4 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1 / 2, bitmapOrg.getHeight() * 1 / 2, bitmapOrg.getWidth() / 2, bitmapOrg.getHeight() / 2);

        ByteArrayOutputStream baosP4 = new ByteArrayOutputStream();
        croppedBmp4.compress(Bitmap.CompressFormat.JPEG, 100, baosP4);
        byte[] bp4 = baosP4.toByteArray();
        String encodedImagep4 = Base64.encodeToString(bp4, Base64.DEFAULT);

//=============================================================================================================================================
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_datap1", encodedImagep1);
        edit.putString("image_datap2", encodedImageP2);
        edit.putString("image_datap3", encodedImagep3);
        edit.putString("image_datap4", encodedImagep4);
        edit.commit();

//        Intent in = new Intent(getApplicationContext(), ViewCropImageSlice.class);
//        startActivity(in);

    }


    /**
     * This method use for slice image for 4 devices.
     *
     * @param bitmapOrg
     */

    private void cropL2(Bitmap bitmapOrg) {
        //=============================================================================================================================================

        // For split images in 4 part vertical L2.
        // Part1

//            // 0, 0,  (image.size.width) * 1/4, image.size.height) );
//
        Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth() * 1 / 4, bitmapOrg.getHeight());

        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // (image.size.width) * 1/4, 0, image.size.width/4.0, image.size.height));
//
        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1 / 4, 0, bitmapOrg.getWidth() / 4, bitmapOrg.getHeight());

        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
////
//            // Part3
//
//            // (((image.size.width) * 1/2, 0, image.size.width/ 4.0, image.size.height));
//
        Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1 / 2, 0, bitmapOrg.getWidth() / 4, bitmapOrg.getHeight());

        ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
        croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
        byte[] bp3 = baosP3.toByteArray();
        String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);
//
//            // Part4
//
//            // ((image.size.width) * 3/4, 0, image.size.width/ 4.0, image.size.height));
//
        Bitmap croppedBmp4 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 3 / 4, 0, bitmapOrg.getWidth() / 4, bitmapOrg.getHeight());

        ByteArrayOutputStream baosP4 = new ByteArrayOutputStream();
        croppedBmp4.compress(Bitmap.CompressFormat.JPEG, 100, baosP4);
        byte[] bp4 = baosP4.toByteArray();
        String encodedImagep4 = Base64.encodeToString(bp4, Base64.DEFAULT);

//=============================================================================================================================================
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_datap1", encodedImagep1);
        edit.putString("image_datap2", encodedImageP2);
        edit.putString("image_datap3", encodedImagep3);
        edit.putString("image_datap4", encodedImagep4);
        edit.commit();

//        Intent in = new Intent(getApplicationContext(), ViewCropImageSlice.class);
//        startActivity(in);
    }


    /**
     * This method use for slice image for 4 devices.
     *
     * @param bitmapOrg
     */
    private void cropL1(Bitmap bitmapOrg) {
        //=============================================================================================================================================

        // For split images in 4 part vertical L1.
        // Part1

        // 0, 0, image.size.width , image.size.height/4)

        Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 4);

        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // 0, (image.size.height) * 1/4, image.size.width, image.size.height/4.0));
//
        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1 / 4, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 4);

        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
////
//            // Part3
//
//            // (0, (image.size.height) * 1/2, image.size.width, image.size.height/ 4.0));
//
        Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1 / 2, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 4);

        ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
        croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
        byte[] bp3 = baosP3.toByteArray();
        String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);
//
//            // Part4
//
//            // (0, (image.size.height) * 3/4, image.size.width, image.size.height/ 4.0));
//
        Bitmap croppedBmp4 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 3 / 4, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 4);

        ByteArrayOutputStream baosP4 = new ByteArrayOutputStream();
        croppedBmp4.compress(Bitmap.CompressFormat.JPEG, 100, baosP4);
        byte[] bp4 = baosP4.toByteArray();
        String encodedImagep4 = Base64.encodeToString(bp4, Base64.DEFAULT);

//=============================================================================================================================================
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_datap1", encodedImagep1);
        edit.putString("image_datap2", encodedImageP2);
        edit.putString("image_datap3", encodedImagep3);
        edit.putString("image_datap4", encodedImagep4);
        edit.commit();

//        Intent in = new Intent(getApplicationContext(), ViewCropImageSlice.class);
//        startActivity(in);
    }


    /**
     * This method use for slice image for 3 devices.
     *
     * @param bitmapOrg
     */
    private void cropvertical(Bitmap bitmapOrg) {
        // =============================================================================================================================================
        // For split images in 3 part vertical.
        // Part1

        // 0, 0, image.size.width , (image.size.height) * 1/3)

        Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight() * 1 / 3);

        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // (0, (image.size.height) * 1/3, image.size.width, image.size.height/3.0));
//
        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1 / 3, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 3);

        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
////
//            // Part3
//
//            // (0, (image.size.height) * 2/3, image.size.width, image.size.height/ 3.0));
//
        Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 2 / 3, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 3);

        ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
        croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
        byte[] bp3 = baosP3.toByteArray();
        String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);

//=============================================================================================================================================
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_datap1", encodedImagep1);
        edit.putString("image_datap2", encodedImageP2);
        edit.putString("image_datap3", encodedImagep3);
        edit.commit();

//        Intent in = new Intent(getApplicationContext(), ViewCropImageSlice.class);
//        startActivity(in);
    }


    /**
     * This method use for slice image for 3 devices.
     *
     * @param bitmapOrg
     */
    private void cropHorizontal(Bitmap bitmapOrg) {

        //=============================================================================================================================================
        // For split images in 3 part Horizontal.
        // Part1

//            (0, 0,  (image.size.width) * 1/3, image.size.height)

        Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth() * 1 / 3, bitmapOrg.getHeight());

        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // (image.size.width) * 1/3, 0, image.size.width/3.0, image.size.height)
//
        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1 / 3, 0, bitmapOrg.getWidth() / 3, bitmapOrg.getHeight());

        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
////
//            // Part3
//
//            // (image.size.width) * 2/3, 0, image.size.width/ 3.0, image.size.height)
//
        Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 2 / 3, 0, bitmapOrg.getWidth() / 3, bitmapOrg.getHeight());

        ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
        croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
        byte[] bp3 = baosP3.toByteArray();
        String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);

// =============================================================================================================================================
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_datap1", encodedImagep1);
        edit.putString("image_datap2", encodedImageP2);
        edit.putString("image_datap3", encodedImagep3);
        edit.commit();

//        Intent in = new Intent(getApplicationContext(), ViewCropImageSlice.class);
//        startActivity(in);
    }

}
