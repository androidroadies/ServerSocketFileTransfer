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

import cropimageview.ScrollTextView;
import testhotspot.WifiApManager;
import wifi.api.WifiStatus;
import wifi.api.wifiAddresses;
import wifi.api.wifiHotSpots;
import wifi.datatransfer.WifiSocket;


/** We use this class for recieve image from Server*/
public class ClientText extends Activity {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear,btnJoinGroup;
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

    LinearLayout lin1,lin2;
    ScrollTextView scrolltext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_text);

        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);
        imageView = (ImageView) findViewById(R.id.imageView);

        scrolltext=(ScrollTextView) findViewById(R.id.scrolltext);


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

    OnClickListener buttonConnectOnClickListener =
            new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    myClientTask1 = new MyClientTask1(
                            editTextAddress.getText().toString().trim(),
                            8080, ao);
                    myClientTask1.execute();
                }
            };

    MyClientTask1.OnPostCallComplete ao = new MyClientTask1.OnPostCallComplete() {

        @Override
        public void response(final String result) {

//            System.out.println("result final :" + result.toString());
            textResponse.setText(result);

            scrolltext.setText(result);
            scrolltext.setTextColor(Color.BLACK);
            scrolltext.setTextSize(80);

//            scrolltext.startScroll();

//            ClientText.this.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    textResponse.setText(result);
//
//                }
//            });
//            setimage(result.toString());

//			byte[] decodedString = Base64.decode(result.toString(), Base64.DEFAULT);
//			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//			System.out.println("bitmap post:"  + decodedByte);
//			imageView.setImageBitmap(decodedByte);

            if (result.toString() == null){
                lin1.setVisibility(View.VISIBLE);
                lin2.setVisibility(View.INVISIBLE);
            }else {
                lin2.setVisibility(View.VISIBLE);
                lin1.setVisibility(View.INVISIBLE);
//                imageView.setImageBitmap(decodedByte);
            }

            }
        };

        public class MyClientTask extends AsyncTask<String, String, String> {

            String dstAddress;
            int dstPort;
            String response = "";

            MyClientTask(String addr) {
                dstAddress = addr;
//			dstPort = port;
            }

            @Override
            protected String doInBackground(String... arg0) {

                Socket socket = null;
                try {
                    byte[] decodedString = Base64.decode(dstAddress, Base64.DEFAULT);

                    decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    System.out.println("bitmap :" + decodedByte);
                } catch (Exception e) {
                    System.out.println("exception :" + e);
                }

//			try {
//				socket = new Socket(dstAddress, dstPort);
//
//				ByteArrayOutputStream byteArrayOutputStream =
//		                new ByteArrayOutputStream(8192000);
//				byte[] buffer = new byte[8192000];
//
//				System.out.println("test1 ;");
//				int bytesRead;
//				InputStream inputStream = socket.getInputStream();
//
//				/*
//				 * notice:
//				 * inputStream.read() will block if no data return
//				 */
//	            while ((bytesRead = inputStream.read(buffer)) != -1){
//	                byteArrayOutputStream.write(buffer, 0, bytesRead);
//	                response += byteArrayOutputStream.toString("UTF-8");
//	            }
//
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				response = "UnknownHostException: " + e.toString();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				response = "IOException: " + e.toString();
//			}finally{
//				if(socket != null){
//					try {
//						socket.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                imageView.setImageBitmap(decodedByte);
                //setimage(response.toString());

//			new Handler().postDelayed(new Runnable(){
//				@Override
//				public void run() {
////					startActivity(myintent);
////					finish();
////					setimage(response.toString());
//				}
//			}, 8000);

//			textResponse.setText(response);
                super.onPostExecute(result);
//			MyClientTask1 myClientTask = new MyClientTask1(
//					"192.168.43.157",
//					8080);
//			myClientTask.execute();
            }

        }

        private void setimage(String str) {

//        String str1 =";
//            byte[] decodedString = Base64.decode(str1, Base64.DEFAULT);
//            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//            System.out.println("bitmap :" + decodedByte);

//		new Handler().postDelayed(new Runnable(){
//			@Override
//			public void run() {
//				if (decodedByte == null){
//					byte[] decodedString1 = Base64.decode(textResponse.getText().toString(), Base64.NO_WRAP);
//					Bitmap decodedByte1 = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
//					System.out.println("bitmap in:"  + decodedByte1);
//					imageView.setImageBitmap(decodedByte1);
//				}
//			}
//		}, 3000);


            imageView.setImageBitmap(decodedByte);
        }

    /**
     * Using this methos user can find our Network and join automatically or you can connect our network from wifi. Our Netwrok name is " SSID ".
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

    }
