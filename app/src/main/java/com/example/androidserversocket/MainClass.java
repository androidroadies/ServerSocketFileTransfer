package com.example.androidserversocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by multidots on 4/22/2016.
 */
public class MainClass extends Activity{

    Button client,server;
    Button clientText,serverText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainclass);

        client = (Button) findViewById(R.id.client);
        server = (Button) findViewById(R.id.server);
        clientText = (Button) findViewById(R.id.clientText);
        serverText = (Button) findViewById(R.id.serverText);

        clientText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getApplicationContext(),ClientText.class);
                startActivity(in);

            }
        });

        serverText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent inser = new Intent(getApplicationContext(),ServerText.class);
                startActivity(inser);


            }
        });
        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getApplicationContext(),Client.class);
                startActivity(in);

            }
        });

        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent inser = new Intent(getApplicationContext(),Server.class);
                startActivity(inser);


            }
        });


    }
}
