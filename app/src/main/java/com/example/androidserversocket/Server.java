package com.example.androidserversocket;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import testhotspot.WifiApManager;
import wifi.api.WifiStatus;
import wifi.api.wifiAddresses;
import wifi.api.wifiHotSpots;
import wifi.datatransfer.WifiSocket;

/**
 * We use this class for send images to our network connected devices means Client
 */

public class Server extends Activity {

    final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
    final int PICK_IMAGE_FROM_GALLARY = 5;
    final int PICK_MULTIPLE_IMAGE_FROM_GALLARY = 2;
    TextView info, infoip, msg;
    String message = "";
    ServerSocket serverSocket;
    Button btnServerSend, btnStartSlideShow, btnSlideShow, btnSelectPhoto, btnCreateGroup, btnOneDevice, btnTwoDevice, btnThreeDevice;
    Button btnL1, btnL2, btnL3, btnDone;
    ImageView img1, img2, img3, imageView;
    Boolean isDevice1 = false, isDevice2 = false, isDevice3 = false;
    Boolean isLayout1 = false, isLayout2 = false, isLayout3 = false;
    Socket socket;
    //    ArrayList<Socket> socketArray = new ArrayList<Socket>();
    Context context;
    String path = "";

    RelativeLayout relmain1, rel1, rel2, rel3;
    LinearLayout linmain1, linmain2;

    wifiHotSpots hotutil;
    WifiStatus wifiStatus;
    BroadcastReceiver receiver;
    WifiSocket ws = new WifiSocket(this);
    WifiApManager wifiApManager;

    //    SimpleAsynTask mTask;
    wifiAddresses au;
    private TextView tvPleaseSelectDevice;
    private ArrayList<String> imagesEncodedList;
    private String imageEncoded;
    private boolean isSlideShow = false;
    private ArrayList<String> serverSlideImg;
    private ArrayList<Object> clientSlideImg1;
    private ArrayList<Object> clientSlideImg2;
    private ArrayList<Object> clientSlideImg3;
    private int i = 0;
    private int received=0;

    /**
     * For setting up image width and height
     *
     * @param v
     * @param height
     */
    public static void setImageWidthHeight(View v, int height) {
        ViewGroup.LayoutParams videoLayoutParams = v.getLayoutParams();
        videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        videoLayoutParams.height = height;
        v.setLayoutParams(videoLayoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context = Server.this;

        hotutil = new wifiHotSpots(getApplicationContext());
        wifiStatus = new WifiStatus(getApplicationContext());
        wifiApManager = new WifiApManager(this);
        au = new wifiAddresses(getApplicationContext());

        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        tvPleaseSelectDevice = (TextView) findViewById(R.id.server_tv_please_select_device);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);

        setImageWidthHeight(img1);
        setImageWidthHeight(img2);
        setImageWidthHeight(img3);

        imageView = (ImageView) findViewById(R.id.imageView);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) Server.this).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        int heightPixels = displayMetrics.heightPixels;

        if (heightPixels > 1000 && heightPixels < 1300) {
            setImageWidthHeight(imageView, 1050);
        } else if (heightPixels > 854 && heightPixels < 1000) {
            setImageWidthHeight(imageView, 820);
        } else if (heightPixels > 750 && heightPixels < 855) {
            setImageWidthHeight(imageView, 750);
        } else {
            setImageWidthHeight(imageView, heightPixels - 450);
        }
        rel1 = (RelativeLayout) findViewById(R.id.rel1);
        rel2 = (RelativeLayout) findViewById(R.id.rel2);
        rel3 = (RelativeLayout) findViewById(R.id.rel3);

        rel1.setVisibility(View.GONE);
        rel2.setVisibility(View.GONE);
        rel3.setVisibility(View.GONE);

        relmain1 = (RelativeLayout) findViewById(R.id.relMain1);
        linmain1 = (LinearLayout) findViewById(R.id.linMain1);
        linmain2 = (LinearLayout) findViewById(R.id.linMain2);

        relmain1.setVisibility(View.VISIBLE);
        linmain1.setVisibility(View.GONE);
        linmain2.setVisibility(View.GONE);

        serverSlideImg = new ArrayList<>();
        clientSlideImg1 = new ArrayList<>();
        clientSlideImg2 = new ArrayList<>();
        clientSlideImg3 = new ArrayList<>();
        clientSlideImg1.add("0");
        clientSlideImg2.add("0");
        clientSlideImg3.add("0");

        btnSlideShow = (Button) findViewById(R.id.btnSlideShow);
        btnSlideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSlideShow = true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_MULTIPLE_IMAGE_FROM_GALLARY);
//                relmain1.setVisibility(View.VISIBLE);
//                linmain1.setVisibility(View.GONE);
//                linmain2.setVisibility(View.GONE);

            }
        });

        btnStartSlideShow = (Button) findViewById(R.id.btnStartSlideShow);
        btnStartSlideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < Appconfig.socketArray.size(); i++) {
                    if (Appconfig.socketArray.size() == 1) {
                        SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(0), "1");
                        socketServerReplyThread.run();
                    }else if (Appconfig.socketArray.size()==2){
                        SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(0), "1");
                        socketServerReplyThread.run();

                        socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(1), "2");
                        socketServerReplyThread.run();
                    }else if (Appconfig.socketArray.size()==3){
                        SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(0), "1");
                        socketServerReplyThread.run();

                        socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(1), "2");
                        socketServerReplyThread.run();

                        socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(2), "3");
                        socketServerReplyThread.run();
                    }
                }

                linmain1.setVisibility(View.GONE);
                linmain2.setVisibility(View.VISIBLE);
                relmain1.setVisibility(View.GONE);

                /*CountDownTimer continueAnimation = new CountDownTimer(5000, 2500) {
                    public void onTick(long millisUntilFinished) {
                        ObjectAnimator.ofFloat(imageView, View.ALPHA, 1.0f, 0.2f).setDuration(3000).start();
                        ObjectAnimator.ofFloat(imageView, View.ALPHA, 0.2f, 1.0f).setDuration(3000).start();
                        if (i==serverSlideImg.size()){
                            i=0;
                        }
                        byte[] b2 = Base64.decode(serverSlideImg.get(i), Base64.DEFAULT);
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(b2, 0, b2.length);
                        imageView.setImageBitmap(bitmap);
                        i++;
                    }

                    public void onFinish() {
                        //    showNotification();
                        start();// here, when your CountDownTimer has finished , we start it again :)
                    }
                };
                try {
                    Thread.sleep(5000);
                    continueAnimation.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                String[] images=new String[serverSlideImg.size()];
                for (int i = 0; i < serverSlideImg.size(); i++) {
                    images[i]=serverSlideImg.get(i);
                }
                try {
                    Thread.sleep(5000);
                    animate(imageView,images,0,true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Layout 1 for Horizontal device.
        btnL1 = (Button) findViewById(R.id.btnOneLayout);
        btnL1.setBackgroundColor(Color.GRAY);
        btnL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnL1.setBackgroundColor(getResources().getColor(R.color.btn_choose_action));
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
        btnL2.setBackgroundColor(Color.GRAY);
        btnL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnL1.setBackgroundColor(Color.GRAY);
                btnL2.setBackgroundColor(getResources().getColor(R.color.btn_choose_action));
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
        btnL3.setBackgroundColor(Color.GRAY);
        btnL3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnL1.setBackgroundColor(Color.GRAY);
                btnL2.setBackgroundColor(Color.GRAY);
                btnL3.setBackgroundColor(getResources().getColor(R.color.btn_choose_action));

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

        btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relmain1.setVisibility(View.GONE);
                linmain1.setVisibility(View.GONE);
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

                tvPleaseSelectDevice.setVisibility(View.VISIBLE);
            }
        });

        btnOneDevice = (Button) findViewById(R.id.btnOneDevice);
        btnOneDevice.setBackgroundColor(Color.GRAY);
        btnOneDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnOneDevice.setBackgroundColor(getResources().getColor(R.color.btn_choose_action));
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
        btnTwoDevice.setBackgroundColor(Color.GRAY);
        btnTwoDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnOneDevice.setBackgroundColor(Color.GRAY);
                btnTwoDevice.setBackgroundColor(getResources().getColor(R.color.btn_choose_action));
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
        btnThreeDevice.setBackgroundColor(Color.GRAY);
        btnThreeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnOneDevice.setBackgroundColor(Color.GRAY);
                btnTwoDevice.setBackgroundColor(Color.GRAY);
                btnThreeDevice.setBackgroundColor(getResources().getColor(R.color.btn_choose_action));

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
                isSlideShow = false;
                picPhoto();
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

                        System.out.println("array :" + Appconfig.socketArray.get(i));

                        if (Appconfig.socketArray.size() == 1) {
                            SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(0), previouslyEncodedImagep1);
                            socketServerReplyThread.run();

                            linmain1.setVisibility(View.GONE);
                            linmain2.setVisibility(View.VISIBLE);
                            relmain1.setVisibility(View.GONE);

                            if (!previouslyEncodedImagep2.equalsIgnoreCase("")) {
                                byte[] b2 = Base64.decode(previouslyEncodedImagep2, Base64.DEFAULT);
                                Bitmap bitmapp2 = BitmapFactory.decodeByteArray(b2, 0, b2.length);
                                imageView.setImageBitmap(bitmapp2);

//                                Display display = getWindowManager().getDefaultDisplay();
//                                DisplayMetrics metrics = new DisplayMetrics();
//
//                                display.getMetrics(metrics);
//
//                                int widthScreen = metrics.widthPixels;
//                                int heightScreen = metrics.heightPixels;
//
//                                imageView.getLayoutParams().height = (int) (heightScreen * 0.65);//it set the height of image 10% of your screen
//                                imageView.getLayoutParams().width = widthScreen ;


                                /*imageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                                        imageView.startAnimation(anim);
                                    }
                                });*/
                            }

                        }
                        if (Appconfig.socketArray.size() == 2) {
                            SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(0), previouslyEncodedImagep1);
                            socketServerReplyThread.run();

                            socketServerReplyThread = new SocketServerReplyThread(Appconfig.socketArray.get(1), previouslyEncodedImagep2);
                            socketServerReplyThread.run();

                            linmain1.setVisibility(View.GONE);
                            linmain2.setVisibility(View.VISIBLE);
                            relmain1.setVisibility(View.GONE);

                            if (!previouslyEncodedImagep3.equalsIgnoreCase("")) {
                                byte[] b3 = Base64.decode(previouslyEncodedImagep3, Base64.DEFAULT);
                                Bitmap bitmapp3 = BitmapFactory.decodeByteArray(b3, 0, b3.length);
                                imageView.setImageBitmap(bitmapp3);

//                                Display display = getWindowManager().getDefaultDisplay();
//                                DisplayMetrics metrics = new DisplayMetrics();
//
//                                display.getMetrics(metrics);
//
//                                int widthScreen = metrics.widthPixels;
//                                int heightScreen = metrics.heightPixels;
//
//                                imageView.getLayoutParams().height = (int) (heightScreen * 0.65);//it set the height of image 10% of your screen
//                                imageView.getLayoutParams().width = widthScreen;

                                /* imageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                                        imageView.startAnimation(anim);
                                    }
                                });*/
                            }
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


                            if (!previouslyEncodedImagep4.equalsIgnoreCase("")) {
                                byte[] b4 = Base64.decode(previouslyEncodedImagep4, Base64.DEFAULT);
                                Bitmap bitmapp4 = BitmapFactory.decodeByteArray(b4, 0, b4.length);
                                imageView.setImageBitmap(bitmapp4);

//                                Display display = getWindowManager().getDefaultDisplay();
//                                DisplayMetrics metrics = new DisplayMetrics();
//
//                                display.getMetrics(metrics);
//
//                                int widthScreen = metrics.widthPixels;
//                                int heightScreen = metrics.heightPixels;
//
//                                imageView.getLayoutParams().height = (int) (heightScreen * 0.65);//it set the height of image 10% of your screen
//                                imageView.getLayoutParams().width = widthScreen;

                                /*imageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                                        imageView.startAnimation(anim);
                                    }
                                });*/
                            }

                        }
                    }


                } else {
                    Toast.makeText(context, "Please Connect Your Device First.!", Toast.LENGTH_LONG).show();
                }
            }
        });
        infoip.setText(getIpAddress());
//		infoip.setText("192.168.1.51");

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }
    private void animate(final ImageView imageView, final String[] images, final int imageIndex, final boolean forever) {

        //imageView <-- The View which displays the images
        //images[] <-- Holds R references to the images to display
        //imageIndex <-- index of the first image to show in images[]
        //forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

        int fadeInDuration = 500; // Configure time values here
        int timeBetween = 3000;
        int fadeOutDuration = 1000;

        imageView.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        byte[] b = Base64.decode(images[imageIndex], Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(b, 0, b.length);
        imageView.setImageBitmap(bitmap);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1,forever); //Calls itself until it gets to the end of the array
                }
                else {
                    if (forever == true){
                        animate(imageView, images, 0,forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
    }
    public void setImageWidthHeight(View v) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) Server.this).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int cachedHeight = (int) (width * 9 / 16);
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = cachedHeight;
        v.setLayoutParams(params);
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

            } else if (requestCode == PICK_MULTIPLE_IMAGE_FROM_GALLARY) {
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);

                        // Get the cursor
                        imageEncoded = Utils.getPath(getApplicationContext(), uri);
                        Bitmap myBitmap = BitmapFactory.decodeFile(imageEncoded);
//                        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
//                        byte[] bP1 = baosP1.toByteArray();
//                        String encodedImage= Base64.encodeToString(bP1, Base64.DEFAULT);
//                        imagesEncodedList.add(encodedImage);
//                        cursor.close();
                        System.out.println("SLIDE IMAGE:" + imageEncoded);

//
                        System.out.println("device1" + isDevice1);
                        System.out.println("device2" + isDevice2);
                        System.out.println("device3" + isDevice3);

                        System.out.println("isLayout1" + isLayout1);
                        System.out.println("isLayout2" + isLayout2);
                        System.out.println("isLayout3" + isLayout3);

                        if (isDevice1 && isLayout1 == true) {
                            cropHorizontal2Part(myBitmap);//2 part
                            btnSelectPhoto.setVisibility(View.GONE);
                            btnStartSlideShow.setVisibility(View.VISIBLE);
                        }
                        if (isDevice1 && isLayout2 == true) {
                            cropvertical2Part(myBitmap);//2 part
                            btnStartSlideShow.setVisibility(View.VISIBLE);
                        }
                        if (isDevice2 && isLayout1 == true) {
                            cropHorizontal(myBitmap);//3 part
                            btnSelectPhoto.setVisibility(View.GONE);
                            btnStartSlideShow.setVisibility(View.VISIBLE);
                        }
                        if (isDevice2 && isLayout2 == true) {
                            cropvertical(myBitmap);//3 part
                            btnSelectPhoto.setVisibility(View.GONE);
                            btnStartSlideShow.setVisibility(View.VISIBLE);
                        }
                        if (isDevice3 && isLayout1 == true) {
                            cropL2(myBitmap);//4 part
                            btnSelectPhoto.setVisibility(View.GONE);
                            btnStartSlideShow.setVisibility(View.VISIBLE);
                        }
                        if (isDevice3 && isLayout2 == true) {
                            cropL1(myBitmap);//4 part
                            btnSelectPhoto.setVisibility(View.GONE);
                            btnStartSlideShow.setVisibility(View.VISIBLE);
                        }
                        if (isDevice3 && isLayout3 == true) {
                            cropL3(myBitmap);//4 part
                            btnSelectPhoto.setVisibility(View.GONE);
                            btnStartSlideShow.setVisibility(View.VISIBLE);
                        }
                    }
                } else {

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

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap finalCroppedBmp = Bitmap.createBitmap(croppedBmp, 0, 0, croppedBmp.getWidth(), croppedBmp.getHeight(), matrix, true);

        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        finalCroppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // (0, (image.size.height) * 1/3, image.size.width, image.size.height/3.0));
//
        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1 / 2, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 2);

        Bitmap finalCroppedBmp2 = Bitmap.createBitmap(croppedBmp2, 0, 0, croppedBmp2.getWidth(), croppedBmp2.getHeight(), matrix, true);

        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        finalCroppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);

//=============================================================================================================================================
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_datap1", encodedImagep1);
        edit.putString("image_datap2", encodedImageP2);

        edit.commit();

        clientSlideImg1.add(encodedImagep1);
        serverSlideImg.add(encodedImageP2);
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

        clientSlideImg1.add(encodedImagep1);
        serverSlideImg.add(encodedImageP2);
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

        clientSlideImg1.add(encodedImagep1);
        clientSlideImg2.add(encodedImageP2);
        clientSlideImg3.add(encodedImagep3);
        serverSlideImg.add(encodedImagep4);
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

        clientSlideImg1.add(encodedImagep1);
        clientSlideImg2.add(encodedImageP2);
        clientSlideImg3.add(encodedImagep3);
        serverSlideImg.add(encodedImagep4);
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
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap finalCroppedBmp = Bitmap.createBitmap(croppedBmp, 0, 0, croppedBmp.getWidth(), croppedBmp.getHeight(), matrix, true);
        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        finalCroppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // 0, (image.size.height) * 1/4, image.size.width, image.size.height/4.0));
//
        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1 / 4, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 4);
        Bitmap finalCroppedBmp2 = Bitmap.createBitmap(croppedBmp2, 0, 0, croppedBmp2.getWidth(), croppedBmp2.getHeight(), matrix, true);
        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        finalCroppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
////
//            // Part3
//
//            // (0, (image.size.height) * 1/2, image.size.width, image.size.height/ 4.0));
//
        Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1 / 2, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 4);
        Bitmap finalCroppedBmp3 = Bitmap.createBitmap(croppedBmp3, 0, 0, croppedBmp3.getWidth(), croppedBmp3.getHeight(), matrix, true);
        ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
        finalCroppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
        byte[] bp3 = baosP3.toByteArray();
        String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);
//
//            // Part4
//
//            // (0, (image.size.height) * 3/4, image.size.width, image.size.height/ 4.0));
//
        Bitmap croppedBmp4 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 3 / 4, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 4);
        Bitmap finalCroppedBmp4 = Bitmap.createBitmap(croppedBmp4, 0, 0, croppedBmp4.getWidth(), croppedBmp4.getHeight(), matrix, true);
        ByteArrayOutputStream baosP4 = new ByteArrayOutputStream();
        finalCroppedBmp4.compress(Bitmap.CompressFormat.JPEG, 100, baosP4);
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

        clientSlideImg1.add(encodedImagep1);
        clientSlideImg2.add(encodedImageP2);
        clientSlideImg3.add(encodedImagep3);
        serverSlideImg.add(encodedImagep4);
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

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap finalCroppedBmp = Bitmap.createBitmap(croppedBmp, 0, 0, croppedBmp.getWidth(), croppedBmp.getHeight(), matrix, true);
        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        finalCroppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // (0, (image.size.height) * 1/3, image.size.width, image.size.height/3.0));
//
        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1 / 3, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 3);

        Bitmap finalCroppedBmp2 = Bitmap.createBitmap(croppedBmp2, 0, 0, croppedBmp2.getWidth(), croppedBmp2.getHeight(), matrix, true);
        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        finalCroppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
////
//            // Part3
//
//            // (0, (image.size.height) * 2/3, image.size.width, image.size.height/ 3.0));
//
        Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 2 / 3, bitmapOrg.getWidth(), bitmapOrg.getHeight() / 3);

        Bitmap finalCroppedBmp3 = Bitmap.createBitmap(croppedBmp3, 0, 0, croppedBmp3.getWidth(), croppedBmp3.getHeight(), matrix, true);
        ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
        finalCroppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
        byte[] bp3 = baosP3.toByteArray();
        String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);

//=============================================================================================================================================
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_datap1", encodedImagep1);
        edit.putString("image_datap2", encodedImageP2);
        edit.putString("image_datap3", encodedImagep3);
        edit.commit();

        clientSlideImg1.add(encodedImagep1);
        clientSlideImg2.add(encodedImageP2);
        serverSlideImg.add(encodedImagep3);
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

        clientSlideImg1.add(encodedImagep1);
        clientSlideImg2.add(encodedImageP2);
        serverSlideImg.add(encodedImagep3);
//        Intent in = new Intent(getApplicationContext(), ViewCropImageSlice.class);
//        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        String receivedMessage = "";
        int count = 0;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                Server.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        info.setText("I'm waiting here: "
                                + serverSocket.getLocalPort());
                        System.out.println("test2 server;");
                    }
                });


                while (true) {
                    socket = serverSocket.accept();

                    Appconfig.socketArray.add(count, socket);
                    count++;
                    message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + " is now connected.\n";
//                    //                    for receiving message from client
//                    InputStream is = socket.getInputStream();
//                    InputStreamReader isr = new InputStreamReader(is);
//                    BufferedReader br = new BufferedReader(isr);
//                    receivedMessage = br.readLine();
//                    System.out.println("Message received..."+receivedMessage);

                    Server.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            msg.setText(message);
                            tvPleaseSelectDevice.setVisibility(View.GONE);
                            relmain1.setVisibility(View.GONE);
                            linmain2.setVisibility(View.GONE);
                            linmain1.setVisibility(View.VISIBLE);
                        }
                    });
//                    InputStream inputStream = socket.getInputStream();
//                    InputStreamReader isr = new InputStreamReader(inputStream);
//                    BufferedReader br = new BufferedReader(isr);
//                    final String receivedMessage = br.readLine();
//
//                    Server.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(Server.this,""+receivedMessage+" by client",Toast.LENGTH_SHORT).show();
//                        }
//                    });

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

//	private void storeSliceinPref() {
//		SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//		SharedPreferences.Editor edit=shre.edit();
//		edit.putString("image_datap1",encodedImagep1);
//		edit.putString("image_datap2",encodedImageP2);
//		edit.putString("image_datap3",encodedImagep3);
//		edit.putString("image_datap4",encodedImagep4);
//		edit.commit();
//
//		Intent in = new Intent(getApplicationContext(), ViewCropImageSlice.class);
//		startActivity(in);
//	}

    public class SocketServerReplyThread extends Thread {

        String strPathSend;
        private Socket hostThreadSocket;
//		int cnt;

        SocketServerReplyThread(Socket socket, String strPath) {//int c
            hostThreadSocket = socket;
            strPathSend = strPath;
//			cnt = c;
        }

        @Override
        public void run() {

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
//            String msgReply = encodedString;//REmove comment
            String msgReply = strPathSend;

            System.out.println("test3 ;");
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) Server.this).getWindowManager().getDefaultDisplay()
                    .getMetrics(displayMetrics);
            int heightPixels = displayMetrics.heightPixels;
            System.out.println("height of server:" + heightPixels);
            if (heightPixels > 855 && heightPixels < 1300) {
                heightPixels = 800;
            } else if (heightPixels > 750 && heightPixels < 855) {
                heightPixels = 800;
            } else {
                heightPixels = 800;
            }
            try {
                ArrayList<String> list = new ArrayList<>();
                list.add(("1"));
                list.add(msgReply);
                list.add(String.valueOf(heightPixels));
                ObjectOutputStream objectOutput = new ObjectOutputStream(hostThreadSocket.getOutputStream());
                if (isSlideShow) {
                    System.out.println("IN SLIDE SHOW SOCKET");
                    if (strPathSend.equals("1")) {
                        System.out.println("11111");
                        clientSlideImg1.add(String.valueOf(heightPixels));
                        objectOutput.writeObject(clientSlideImg1);
                    }else if (strPathSend.equals("2")){
                        System.out.println("2222");
                        clientSlideImg2.add(String.valueOf(heightPixels));
                        objectOutput.writeObject(clientSlideImg2);
                    }else{
                        System.out.println("333");
                        clientSlideImg3.add(String.valueOf(heightPixels));
                        objectOutput.writeObject(clientSlideImg3);
                    }
                } else {
                    objectOutput.writeObject(list);
                }

//                while (true) {
//                    if (received==Appconfig.socketArray.size()){
//                        break;
//                    }
//                    socket = serverSocket.accept();
//                    InputStream inputStream = socket.getInputStream();
//                    System.out.println("INput stream--------");
//                    InputStreamReader isr = new InputStreamReader(inputStream);
//                    System.out.println("AFTER INput stream--------");
//                    BufferedReader br = new BufferedReader(isr);
//                    System.out.println("RECEIVED TEXT--------:" + br.readLine());
//                    br.close();
//                    received++;
//                }
//                outputStream = hostThreadSocket.getOutputStream();
//                PrintStream printStream = new PrintStream(outputStream);
//                printStream.print(msgReply);
//                printStream.close();
//                message += msgReply;
//                Server.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        msg.setText(message);
//                    }
//                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            Server.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    msg.setText(message);

                }
            });

        }


    }
}
