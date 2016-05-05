/* 
 * Copyright (C) 2013-2014 www.Andbrain.com 
 * Faster and more easily to create android apps
 * 
 * */
package wifi.api;


import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.androidserversocket.R;


public class showHotspotsList  extends Activity {
	ListView listView ;
	wifiHotSpots HU;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitymain);
		HU=new wifiHotSpots(this);
		listView = (ListView) findViewById(R.id.list);
		HU.showHotspotsList(listView );
	}
}
