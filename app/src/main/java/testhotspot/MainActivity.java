package testhotspot;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.androidserversocket.R;

import java.util.ArrayList;
import java.util.List;

import wifi.api.WifiStatus;
import wifi.api.wifiAddresses;
import wifi.api.wifiHotSpots;
import wifi.datatransfer.WifiSocket;

public class MainActivity extends Activity {

    private Button invite, send,receive;
    private Button join;
    private static int result_lavel = 0;

    wifiHotSpots hotutil;
    WifiStatus wifiStatus;
    BroadcastReceiver receiver;
    WifiSocket ws = new WifiSocket(this);
    WifiApManager wifiApManager;

//    SimpleAsynTask mTask;
    wifiAddresses au;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_action);

        hotutil = new wifiHotSpots(getApplicationContext());
//        hotutil.isConnectedToAP();
        wifiStatus = new WifiStatus(getApplicationContext());
//        invite = (Button) findViewById(R.id.Invite);
//        join = (Button) findViewById(R.id.Join);
//        send = (Button) findViewById(R.id.Send);
//                receive = (Button) findViewById(R.id.receive);
        wifiApManager = new WifiApManager(this);

//        mTask = new SimpleAsynTask();
        au = new wifiAddresses(getApplicationContext());
//        scan();

        WifiInfo connectionInfo = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();

//        String ssid = connectionInfo.getIpAddress();
//        ssid = ssid == null ? C2391a.f8921b : ssid.replaceAll("\"", C2391a.f8921b);
//        boolean z2 = ssid.startsWith("ADY") || ssid.startsWith("ADH");
//        String bssid = connectionInfo.getBSSID();
//        C0536a.m6113a(f3499a, "connected to wifi " + ssid + " BSSID: " + connectionInfo.getBSSID());

        System.out.println("info :"+ connectionInfo);

//        final Runnable mTask = (Runnable) new SimpleAsynTask();
//        ws.receiveMessage(5000, 10, new Runnable() {
//
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//
//                    public void run() {
//
//                        Toast.makeText(getApplicationContext(), ":::"+ws.receivedMessage+":::",
//                                Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//                Log.i("Receive text :: ",":::"+ws.receivedMessage+":::" );
//                Toast.makeText(getApplicationContext(), ws.receivedMessage, Toast.LENGTH_LONG).show();
//
//            }
//        });

        invite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
//				if(hotutil.setHotSpot("SSID",""))
//				{
//					hotutil.startHotSpot(true);
//				}
                hotutil.setHotSpot("SSID","");
                inviteFriend(hotutil);
            }
        });
        join.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                joinFriend(wifiStatus, hotutil);
                hotutil.addWifiNetwork("ssid22", "", "OPEN"); //Trird argument can be "WEP","OPEN","WAP"
            }
        });

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

//                Toast.makeText(getApplicationContext(), "GatWay IP:"
//                                + au.getGatewayIPAddress() + "\n" + "GatWay MAC" + au.getGatWayMacAddress() + "\n" + "Device IP" + au.getDeviceIPAddress() + "\n" + "Device MAC" + au.getDeviceMacAddress(),
//                        Toast.LENGTH_SHORT).show();
//                System.out.println("GatWay IP:"+ au.getGatewayIPAddress() );
//
                if (wifiStatus.checkWifi(wifiStatus.CONECT_HOTSPOT)) {
                    Toast.makeText(getApplicationContext(), "Yes  Device is Coneceting To Hostspot", Toast.LENGTH_LONG).show();
                    ws.sendMessage(au.getGatewayIPAddress(), 5000, "Message from Heaven");
                } else {
                    Toast.makeText(getApplicationContext(), "No  Device is Not Coneceting To Hostspot", Toast.LENGTH_LONG).show();
                    ws.sendMessage(au.getGatewayIPAddress(), 5000, "Message from Heaven");
                }



//                Intent serviceIntent = new Intent(getApplicationContext(), FileTransferService.class);
//                serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
//                serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, "/storage/emulated/0/com.example.android.wifidirect/wifip2pshared-1461225067807.jpg");
//                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
//                        au.getGatewayIPAddress());
//                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 5000);
//                getApplicationContext().startService(serviceIntent);




            }
        });

        receive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                wifiAddresses au = new wifiAddresses(getApplicationContext());
//                Toast.makeText(getApplicationContext(), "GatWay IP:"
//                                + au.getGatewayIPAddress() + "\n" + "GatWay MAC" + au.getGatWayMacAddress() + "\n" + "Device IP" + au.getDeviceIPAddress() + "\n" + "Device MAC" + au.getDeviceMacAddress(),
//                        Toast.LENGTH_SHORT).show();
//                System.out.println("GatWay IP:"+ au.getGatewayIPAddress() );


//                if (wifiStatus.checkWifi(wifiStatus.CONECT_HOTSPOT)) {
//                    Toast.makeText(getApplicationContext(), "Yes  Device is Coneceting To Hostspot", Toast.LENGTH_LONG).show();
////                    ws.sendMessage("192.168.43.213", 5000, "Message from Heaven");
                    ws.receiveMessage(5000, 10, new Runnable() {

                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                public void run() {

//                                    Toast.makeText(getApplicationContext(), ":::"+ws.receivedMessage+":::",
//                                            Toast.LENGTH_SHORT).show();
                                    System.out.println("message received finally :" + ws.receivedMessage);

                                }
                            });
                            Log.i("Receive text :: ",":::"+ws.receivedMessage+":::" );
//                            Toast.makeText(getApplicationContext(), ws.receivedMessage, Toast.LENGTH_LONG).show();

                        }
                    });
//                } else {
//                    Toast.makeText(getApplicationContext(), "No  Device is Not Coneceting To Hostspot", Toast.LENGTH_LONG).show();
//                    ws.receiveMessage(5000, 10, new Runnable() {
//
//                        @Override
//                        public void run() {
//                            runOnUiThread(new Runnable() {
//
//                                public void run() {
//
////                                    Toast.makeText(getApplicationContext(), ":::"+ws.receivedMessage+":::",
////                                            Toast.LENGTH_SHORT).show();
//                                    System.out.println("message received finally :" + ws.receivedMessage);
//
//                                }
//                            });
//                            Log.i("Receive text :: ",":::"+ws.receivedMessage+":::" );
////                            Toast.makeText(getApplicationContext(), ws.receivedMessage, Toast.LENGTH_LONG).show();
//
//                        }
//                    });
//                }
            }
        });

    }

    private void scan() {
        wifiApManager.getClientList(false, new FinishScanListener() {

            @Override
            public void onFinishScan(final ArrayList<ClientScanResult> clients) {

//                textView1.setText("WifiApState: " + wifiApManager.getWifiApState() + "\n\n");
//                textView1.append("Clients: \n");
                for (ClientScanResult clientScanResult : clients) {
//                    textView1.append("####################\n");
//                    textView1.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
//                    textView1.append("Device: " + clientScanResult.getDevice() + "\n");
//                    textView1.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
//                    textView1.append("isReachable: " + clientScanResult.isReachable() + "\n");
                    if(au.pingCmd(clientScanResult.getIpAddr())){
                        Toast.makeText(getApplicationContext(), "This IP is Live",
                                Toast.LENGTH_SHORT).show();
                        ws.sendMessage(clientScanResult.getIpAddr(), 5000, "Message from Heaven");
                        System.out.println("ip live" + clientScanResult.getIpAddr() +"result :" + au.getPingResulta(clientScanResult.getIpAddr()));
                    }else{
                        System.out.println("ip not live" + clientScanResult.getIpAddr());
                        Toast.makeText(getApplicationContext(), "No This IP Not Live",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
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

    public void joinFriend(final WifiStatus wifiStatus, final wifiHotSpots hotutil) {
        if (wifiStatus.checkWifi(wifiStatus.IS_WIFI_ON)) {
            hotutil.scanNetworks();
            List<ScanResult> results = hotutil.getHotspotsList();
            for (ScanResult result : results) {
                Toast.makeText(getApplicationContext(), result.SSID + " Mayank test" + result.level,
                        Toast.LENGTH_SHORT).show();

                System.out.println("SSID" + result.SSID + " Mayank test");
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
