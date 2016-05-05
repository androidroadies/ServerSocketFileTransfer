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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainclass);

        client = (Button) findViewById(R.id.client);
        server = (Button) findViewById(R.id.server);

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
