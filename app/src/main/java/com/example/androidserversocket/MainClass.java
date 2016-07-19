package com.example.androidserversocket;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import testhotspot.WifiApManager;
import wifi.api.WifiStatus;
import wifi.api.wifiAddresses;
import wifi.api.wifiHotSpots;
import wifi.datatransfer.WifiSocket;

/**
 * Created by multidots on 4/22/2016.
 */
public class MainClass extends Activity {

    Button client, server;
    Button clientText, serverText;

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
        setContentView(R.layout.mainclass);

        Appconfig.sendCount=1;

        client = (Button) findViewById(R.id.client);
        server = (Button) findViewById(R.id.server);
        clientText = (Button) findViewById(R.id.clientText);
        serverText = (Button) findViewById(R.id.serverText);

        hotutil = new wifiHotSpots(getApplicationContext());
        wifiStatus = new WifiStatus(getApplicationContext());
        wifiApManager = new WifiApManager(this);
        au = new wifiAddresses(getApplicationContext());

        clientText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientText.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out));
                Intent in = new Intent(getApplicationContext(), ClientText.class);
                startActivity(in);

            }
        });

        serverText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverText.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out));
                hotutil.setHotSpot("SSID", "");
                inviteFriend(hotutil);

                Intent inser = new Intent(getApplicationContext(), ServerText.class);
                startActivity(inser);


            }
        });
        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out));
                Intent in = new Intent(getApplicationContext(), Client.class);
                startActivity(in);

            }
        });

        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out));
                hotutil.setHotSpot("SSID", "");
                inviteFriend(hotutil);

                Intent inser = new Intent(getApplicationContext(), Server.class);
                startActivity(inser);


            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Appconfig.sendCount=1;
        if (Appconfig.socketArray.size()>0)
            Appconfig.socketArray.clear();
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
}
