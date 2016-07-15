package com.example.androidserversocket;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;
import java.util.List;

import cropimageview.ScrollTextViewClient;
import testhotspot.WifiApManager;
import wifi.api.WifiStatus;
import wifi.api.wifiAddresses;
import wifi.api.wifiHotSpots;
import wifi.datatransfer.WifiSocket;


/**
 * We use this class for recieve image from Server
 */
public class ClientText extends Activity {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear, btnJoinGroup;
    ImageView imageView;
    MyClientTask1 myClientTask1;
    Bitmap decodedByte;

    wifiHotSpots hotutil;
    WifiStatus wifiStatus;
    BroadcastReceiver receiver;
    WifiSocket ws = new WifiSocket(this);
    WifiApManager wifiApManager;

    //    SimpleAsynTask mTask;
    wifiAddresses au;

    LinearLayout lin1, lin2;
    ScrollTextViewClient scrolltext;
    private TextView tvWaitText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_text);

        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);
        tvWaitText = (TextView) findViewById(R.id.client_text_tv_wait_text);
        imageView = (ImageView) findViewById(R.id.imageView);

        scrolltext = (ScrollTextViewClient) findViewById(R.id.scrolltext);

        lin1 = (LinearLayout) findViewById(R.id.lin1);
        lin2 = (LinearLayout) findViewById(R.id.lin2);

        lin1.setVisibility(View.VISIBLE);
        lin2.setVisibility(View.INVISIBLE);

        editTextAddress.setText("192.168.43.1");

        hotutil = new wifiHotSpots(getApplicationContext());
//        hotutil.isConnectedToAP();
        wifiStatus = new WifiStatus(getApplicationContext());
        wifiApManager = new WifiApManager(this);

//        mTask = new SimpleAsynTask();
        au = new wifiAddresses(getApplicationContext());

        btnJoinGroup = (Button) findViewById(R.id.btnJoinGroup);
        btnJoinGroup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                joinFriend(wifiStatus, hotutil);
//                hotutil.connectToHotspot("SSID", "");

                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

            }
        });

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                textResponse.setText("");
                System.out.println("text data :" + textResponse.getText().toString().trim());
//                setimage(textResponse.getText().toString());

            }
        });
    }

    public static boolean isFromClientText = false;
    OnClickListener buttonConnectOnClickListener =
            new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    lin1.setVisibility(View.GONE);
                    tvWaitText.setVisibility(View.VISIBLE);
                    isFromClientText = true;
                    myClientTask1 = new MyClientTask1(
                            editTextAddress.getText().toString().trim(),
                            8080, ao);
                    myClientTask1.execute();
                }
            };

    MyClientTask1.OnPostCallComplete ao = new MyClientTask1.OnPostCallComplete() {

        @Override
        public void response(final String result) {

            tvWaitText.setVisibility(View.GONE);
            System.out.println("result final :" + result.toString());
            textResponse.setText(result);

            scrolltext.setText(result);
            scrolltext.setTextColor(Color.BLACK);
            scrolltext.setTextSize(150);

            scrolltext.startScroll();

            if (result.toString() == null) {
                lin1.setVisibility(View.VISIBLE);
                lin2.setVisibility(View.INVISIBLE);
            } else {
                lin2.setVisibility(View.VISIBLE);
                lin1.setVisibility(View.INVISIBLE);
//                imageView.setImageBitmap(decodedByte);
            }

        }
    };


    /**
     * Using this methos user can find our Network and join automatically or you can connect our network from wifi. Our Netwrok name is " SSID ".
     *
     * @param wifiStatus
     * @param hotutil
     */
    public void joinFriend(final WifiStatus wifiStatus, final wifiHotSpots hotutil) {
        if (wifiStatus.checkWifi(wifiStatus.IS_WIFI_ON)) {
            hotutil.scanNetworks();
            List<ScanResult> results = hotutil.getHotspotsList();
            for (ScanResult result : results) {
                Toast.makeText(getApplicationContext(), result.SSID + " Mayank test" + result.level,
                        Toast.LENGTH_SHORT).show();

                System.out.println("SSID" + result.SSID + " ??");
                if (result.SSID.equalsIgnoreCase("SSID")) {

                    Toast.makeText(getApplicationContext(), result.SSID + " Found SSID" + result.level,
                            Toast.LENGTH_SHORT).show();

                    System.out.println("SSID" + result.SSID + " Mayank found");

                    hotutil.connectToHotspot("SSID", "");


                    try {
                        unregisterReceiver(receiver);
                        break;
                    } catch (Exception e) {
                        //error as trying to do unregistering twice?
                    }
                    //hotutil.stopScan();
                }
            }

        } else {
            if (hotutil.isWifiApEnabled())
                hotutil.startHotSpot(false);
            //start wifi.
            wifiStatus.checkWifi(wifiStatus.WIFI_ON);

            receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    final String action = intent.getAction();
                    if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                        List<ScanResult> results = hotutil.getHotspotsList();
                        for (ScanResult result : results) {
                            Toast.makeText(getApplicationContext(), result.SSID + " " + result.level,
                                    Toast.LENGTH_SHORT).show();
                            if (result.SSID.equalsIgnoreCase("SSID")) {
                                Toast.makeText(getApplicationContext(), "Found SSID", Toast.LENGTH_SHORT).show();//get final found message here.
//                                ws.sendMessage(au.getGatewayIPAddress(), 5000, "Message from Heaven receiver");
                                if (!hotutil.isConnectToHotSpotRunning)
                                    hotutil.connectToHotspot("SSID", "");
                                try {
                                    unregisterReceiver(receiver);
                                    break;
                                } catch (Exception e) {
                                    //trying to unregister twice? need vary careful about this.
                                }

                            }
                        }
                    }
                }

            };
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(receiver, mIntentFilter);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
