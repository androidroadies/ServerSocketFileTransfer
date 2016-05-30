package com.example.androidserversocket;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import testhotspot.WifiApManager;
import wifi.api.WifiStatus;
import wifi.api.wifiAddresses;
import wifi.api.wifiHotSpots;
import wifi.datatransfer.WifiSocket;


/**
 * We use this class for recieve image from Server
 */
public class Client extends Activity {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear, btnJoinGroup;
    public static ImageView imageView;
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
    private Client mContext;
    private Button btnInformServer;
    public static Boolean isInform=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client);
        mContext = Client.this;

        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnInformServer =(Button)findViewById(R.id.client_btn_inform_server);
        btnInformServer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isInform=true;
            }
        });


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
        btnJoinGroup.setOnClickListener(new View.OnClickListener() {
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
                setimage(textResponse.getText().toString());

            }
        });
    }

    public static boolean isFromClientImage=false;
    OnClickListener buttonConnectOnClickListener =
            new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    isFromClientImage =true;
                    myClientTask1 = new MyClientTask1(
                            editTextAddress.getText().toString().trim(),
                            8080, ao);
                    myClientTask1.execute();
                }
            };

    MyClientTask1.OnPostCallComplete ao = new MyClientTask1.OnPostCallComplete() {

        @Override
        public void response(final String result) {
            if (MyClientTask1.list !=null && MyClientTask1.list.size()!=0){
                setImageWidthHeight(imageView,MyClientTask1.list.get(1));
            }
//            System.out.println("result final :" + result.toString());
            textResponse.setText(result);
            Client.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    textResponse.setText(result);

                }
            });
//            setimage(result.toString());

            byte[] decodedString = Base64.decode(result.toString(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            System.out.println("bitmap post:" + decodedByte);
            imageView.setImageBitmap(decodedByte);
//            imageView.post(new Runnable() {
//                @Override
//                public void run() {
//                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_fade_out);
//                    imageView.startAnimation(anim);
//                }
//            });

            if (decodedByte == null) {
                lin1.setVisibility(View.VISIBLE);
                lin2.setVisibility(View.INVISIBLE);
            } else {
                lin2.setVisibility(View.VISIBLE);
                lin1.setVisibility(View.INVISIBLE);
                imageView.setImageBitmap(decodedByte);
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

    public static void setImageWidthHeight(View v, String height) {
        ViewGroup.LayoutParams videoLayoutParams = v.getLayoutParams();
        videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        videoLayoutParams.height = Integer.parseInt(height);
        v.setLayoutParams(videoLayoutParams);
    }

    private void setimage(String str) {

        String str1 = "iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAAACXBIWXMAAAsTAAALEwEAmpwYAAAKT2lDQ1BQaG90b3Nob3AgSUNDIHByb2ZpbGUAAHjanVNnVFPpFj333vRCS4iAlEtvUhUIIFJCi4AUkSYqIQkQSoghodkVUcERRUUEG8igiAOOjoCMFVEsDIoK2AfkIaKOg6OIisr74Xuja9a89+bN/rXXPues852zzwfACAyWSDNRNYAMqUIeEeCDx8TG4eQuQIEKJHAAEAizZCFz/SMBAPh+PDwrIsAHvgABeNMLCADATZvAMByH/w/qQplcAYCEAcB0kThLCIAUAEB6jkKmAEBGAYCdmCZTAKAEAGDLY2LjAFAtAGAnf+bTAICd+Jl7AQBblCEVAaCRACATZYhEAGg7AKzPVopFAFgwABRmS8Q5ANgtADBJV2ZIALC3AMDOEAuyAAgMADBRiIUpAAR7AGDIIyN4AISZABRG8lc88SuuEOcqAAB4mbI8uSQ5RYFbCC1xB1dXLh4ozkkXKxQ2YQJhmkAuwnmZGTKBNA/g88wAAKCRFRHgg/P9eM4Ors7ONo62Dl8t6r8G/yJiYuP+5c+rcEAAAOF0ftH+LC+zGoA7BoBt/qIl7gRoXgugdfeLZrIPQLUAoOnaV/Nw+H48PEWhkLnZ2eXk5NhKxEJbYcpXff5nwl/AV/1s+X48/Pf14L7iJIEyXYFHBPjgwsz0TKUcz5IJhGLc5o9H/LcL//wd0yLESWK5WCoU41EScY5EmozzMqUiiUKSKcUl0v9k4t8s+wM+3zUAsGo+AXuRLahdYwP2SycQWHTA4vcAAPK7b8HUKAgDgGiD4c93/+8//UegJQCAZkmScQAAXkQkLlTKsz/HCAAARKCBKrBBG/TBGCzABhzBBdzBC/xgNoRCJMTCQhBCCmSAHHJgKayCQiiGzbAdKmAv1EAdNMBRaIaTcA4uwlW4Dj1wD/phCJ7BKLyBCQRByAgTYSHaiAFiilgjjggXmYX4IcFIBBKLJCDJiBRRIkuRNUgxUopUIFVIHfI9cgI5h1xGupE7yAAygvyGvEcxlIGyUT3UDLVDuag3GoRGogvQZHQxmo8WoJvQcrQaPYw2oefQq2gP2o8+Q8cwwOgYBzPEbDAuxsNCsTgsCZNjy7EirAyrxhqwVqwDu4n1Y8+xdwQSgUXACTYEd0IgYR5BSFhMWE7YSKggHCQ0EdoJNwkDhFHCJyKTqEu0JroR+cQYYjIxh1hILCPWEo8TLxB7iEPENyQSiUMyJ7mQAkmxpFTSEtJG0m5SI+ksqZs0SBojk8naZGuyBzmULCAryIXkneTD5DPkG+Qh8lsKnWJAcaT4U+IoUspqShnlEOU05QZlmDJBVaOaUt2ooVQRNY9aQq2htlKvUYeoEzR1mjnNgxZJS6WtopXTGmgXaPdpr+h0uhHdlR5Ol9BX0svpR+iX6AP0dwwNhhWDx4hnKBmbGAcYZxl3GK+YTKYZ04sZx1QwNzHrmOeZD5lvVVgqtip8FZHKCpVKlSaVGyovVKmqpqreqgtV81XLVI+pXlN9rkZVM1PjqQnUlqtVqp1Q61MbU2epO6iHqmeob1Q/pH5Z/YkGWcNMw09DpFGgsV/jvMYgC2MZs3gsIWsNq4Z1gTXEJrHN2Xx2KruY/R27iz2qqaE5QzNKM1ezUvOUZj8H45hx+Jx0TgnnKKeX836K3hTvKeIpG6Y0TLkxZVxrqpaXllirSKtRq0frvTau7aedpr1Fu1n7gQ5Bx0onXCdHZ4/OBZ3nU9lT3acKpxZNPTr1ri6qa6UbobtEd79up+6Ynr5egJ5Mb6feeb3n+hx9L/1U/W36p/VHDFgGswwkBtsMzhg8xTVxbzwdL8fb8VFDXcNAQ6VhlWGX4YSRudE8o9VGjUYPjGnGXOMk423GbcajJgYmISZLTepN7ppSTbmmKaY7TDtMx83MzaLN1pk1mz0x1zLnm+eb15vft2BaeFostqi2uGVJsuRaplnutrxuhVo5WaVYVVpds0atna0l1rutu6cRp7lOk06rntZnw7Dxtsm2qbcZsOXYBtuutm22fWFnYhdnt8Wuw+6TvZN9un2N/T0HDYfZDqsdWh1+c7RyFDpWOt6azpzuP33F9JbpL2dYzxDP2DPjthPLKcRpnVOb00dnF2e5c4PziIuJS4LLLpc+Lpsbxt3IveRKdPVxXeF60vWdm7Obwu2o26/uNu5p7ofcn8w0nymeWTNz0MPIQ+BR5dE/C5+VMGvfrH5PQ0+BZ7XnIy9jL5FXrdewt6V3qvdh7xc+9j5yn+M+4zw33jLeWV/MN8C3yLfLT8Nvnl+F30N/I/9k/3r/0QCngCUBZwOJgUGBWwL7+Hp8Ib+OPzrbZfay2e1BjKC5QRVBj4KtguXBrSFoyOyQrSH355jOkc5pDoVQfujW0Adh5mGLw34MJ4WHhVeGP45wiFga0TGXNXfR3ENz30T6RJZE3ptnMU85ry1KNSo+qi5qPNo3ujS6P8YuZlnM1VidWElsSxw5LiquNm5svt/87fOH4p3iC+N7F5gvyF1weaHOwvSFpxapLhIsOpZATIhOOJTwQRAqqBaMJfITdyWOCnnCHcJnIi/RNtGI2ENcKh5O8kgqTXqS7JG8NXkkxTOlLOW5hCepkLxMDUzdmzqeFpp2IG0yPTq9MYOSkZBxQqohTZO2Z+pn5mZ2y6xlhbL+xW6Lty8elQfJa7OQrAVZLQq2QqboVFoo1yoHsmdlV2a/zYnKOZarnivN7cyzytuQN5zvn//tEsIS4ZK2pYZLVy0dWOa9rGo5sjxxedsK4xUFK4ZWBqw8uIq2Km3VT6vtV5eufr0mek1rgV7ByoLBtQFr6wtVCuWFfevc1+1dT1gvWd+1YfqGnRs+FYmKrhTbF5cVf9go3HjlG4dvyr+Z3JS0qavEuWTPZtJm6ebeLZ5bDpaql+aXDm4N2dq0Dd9WtO319kXbL5fNKNu7g7ZDuaO/PLi8ZafJzs07P1SkVPRU+lQ27tLdtWHX+G7R7ht7vPY07NXbW7z3/T7JvttVAVVN1WbVZftJ+7P3P66Jqun4lvttXa1ObXHtxwPSA/0HIw6217nU1R3SPVRSj9Yr60cOxx++/p3vdy0NNg1VjZzG4iNwRHnk6fcJ3/ceDTradox7rOEH0x92HWcdL2pCmvKaRptTmvtbYlu6T8w+0dbq3nr8R9sfD5w0PFl5SvNUyWna6YLTk2fyz4ydlZ19fi753GDborZ752PO32oPb++6EHTh0kX/i+c7vDvOXPK4dPKy2+UTV7hXmq86X23qdOo8/pPTT8e7nLuarrlca7nuer21e2b36RueN87d9L158Rb/1tWeOT3dvfN6b/fF9/XfFt1+cif9zsu72Xcn7q28T7xf9EDtQdlD3YfVP1v+3Njv3H9qwHeg89HcR/cGhYPP/pH1jw9DBY+Zj8uGDYbrnjg+OTniP3L96fynQ89kzyaeF/6i/suuFxYvfvjV69fO0ZjRoZfyl5O/bXyl/erA6xmv28bCxh6+yXgzMV70VvvtwXfcdx3vo98PT+R8IH8o/2j5sfVT0Kf7kxmTk/8EA5jz/GMzLdsAAAAgY0hSTQAAeiUAAICDAAD5/wAAgOkAAHUwAADqYAAAOpgAABdvkl/FRgAAQKJJREFUeNrsfWmMnfV1/nP3fZl7Z/UsHs+MjVcYYxOwcWIbEIQlQKVAVdRWUSGtFLVIUVv1U6NI/dJF/zQ0JQ2olITEQZQYCDhAYhvb4A0bvBRixh7bYxvPvty5+37f/wf7OZz7+s7YGDBbXmk0d+7c+y6/c35nfc45FqfTWbFarSgUCrDZbLBYLCgUCrDb7bDZbKhUKiiXy3A4HKhUKigWiwAAu92OSqWCSqUCAHA4HLBarSgWi/Ke+bBarfLaMAwYhoGZDovFItfkZ2c6Lz9vt9tRLBZhsVhgtVrlu1artep6/L/VaoXFYjl/P2UAQDgchsfjgc1mg9/vh9VqRS6Xg2EYyOVycDqdsFqtiMfjcDgcAIBSsQir1Yp8Pg+bzYZsNguuqcfjAQDk83mUy2UYhgGn04lKpQKLxYKKYUEul6ta01KpBKvVCpvNhnK5jEqlAofDAbvdjnw+L3+TFhaLBR6PB5VKBblcTp5dr7vVapXrA0AwGMTJkydhLxQKFn5Bf6lUKqFUKskClUolVCoVOJ1OFItFlMtluN1uFAoFlMtluRlekDeoz1mpVKqYoBYRSaxKpYJCoSALY7FYZmUAwzBQLBaFiUulEux2O+x2OwqFQhWz8Zn0/bpcLjidTlgsFiSTSbjd7ioG9Hg8yOVySCaTwgSVSgXZbBZOhwOZTEbWxG63w+l0wjAMIbzb7YbX60WhUJC1LRRKcDidsNlssokAoFwuywbgPVitVpRKJXkOMrvb7RbakfjcuHptyFT8m4fdYrHAMAy43W75UKFQkN3Bk5VKJQAQ6WCxWGRnAIDNZqviMi66Joj54rWIWC6f24kkRj6fv6i00Ee5XIbNZpPn4e4yE1//rlQqSCbTCAYtcLvdCAaDCAQCsujpdFqem7uvVCqhWCzC5XKhcn6juFwuGIaBbDYrjMPdns/nUSgUkM8XABhCODJKOp1GqVQSSep2u+F0OpFMJqs2IteBki6Xy8Fisch6cRPxforFonyHTEuaAIDd5/MhlUoJ9/Af/CAfhDfM3c+L8eQkPDmX59CSRX++1k7UBOHu19JktoPMWiqVUC6XhZHInFqF8N55fZvNhmDQD4fDIc9QLpeRyWTgcDjg9XpRLpeFiblji8UiPB4PCoYB47z0AQC3232OMc7vylwuj1K5AofdCosFsFpt58V7BalUSlQOn6FYLCKdTlftaLMKLJVK8Hq9yGQyov4obchYlH5kXNKoSgKQg3gBm80Gl8slJyMRuJP4We5u6ilyod1uRzqdriImX7tcLhQKhQtEuSYIdx13WblcFi6f7eD9U4U4HA6Uy2Vo9WYm/AevDaRSKUQiERG7VAFkRDI0RT3FaSaTgdfjQbFYFAbhM9AWCAYDyOfz5yVk6bwUAKxWCyplo2qHc/dz45VKJWEsbTuRSa1WK5xOJ/L5fJU6M4xzz6Q3ncvlqroWANhtNhucTqcscrFYhNPplA84HA7Rf/yf3mXc8cViUc5BzjTvcrM4rrUbzRzL930+H3K5nCxyLQPT5XIhm80KsbQk0Ne58Aew288RnYZsNpuFzWZDKpVCMpmE1WpFOByGz+dDOp2Gy+WC3W5HqVTC1NSUGGlkmHPSwYtkMiXEt9lssNttMIxzEtPpdALFck27i8ar1+sVdUNjjrYAic51JS25BtyQ2WxW1Cs3pRjEAAxyR0dHB2KxmBC+UqkgkUjICbmDyYHaQCuXy/B4PPB4PKKHnU4nCoWCSAQtVbTRx3M5HA60tLQgm82KHeJ0OhGLxeQhZzME7XY76uvrqySXw+FAPB6X3cDrmX/7/efEvN/vRyAQqFJpXq8XY2NjKBQK8Pv9yGazwlw2mw2FfL7KQq+rq0MmmweMynlvwofx8YnzUqZOvp/PF+E8L23dbrdcMxKJwOVyIZPJyGbI5XJCSJfLJYxKepDBqJai0ajcj81mg8/nw/j4OFKpVLUXQM5Zt24d/vZv/1Z2mN/vx/T0NJ555hk8++yzole0mCOnA8D8+fPxne98B0uWLEE2m0UoFEK5XMbu3bvx1FNPob+/f1b9bbFYcMMNN+D73/8+EokESqWS7Oif/vSn+M1vfjMr8QFg4cKF+N73vodIJIJCoYC6ujrYbDY8//zzePzxxzE5OSkE10zMXeH3+/EXf/EXuPnmW2C325BMJhEMBjE1NYUf//jH2Lx5s+h4eh3c9dlsFk6nE7feeiv+/M//HD6fD5VKGW6PF/lcHs8++794+umnkclk5NoulwNenw+FQkGk57x58/DQQw9hyZIlcDqd8Pv9KJVKeOedd/Dkk0/i8OHDyGQyVWqCUpgMuXbtWjz88MPw+XyYnp5GIBBAqVTCz3/+czz77LPVkthisRjBYNB47rnnjFrH5s2bjba2NuO8pDCsVqsBwLBYLAYAw2azGQCM9evXGzMd99133wXf4WuLxWJYrVajubnZOHLkSM3vP/nkk4bP55PrWywW+dH39dBDD9X8fqFQMNatW2cAMFwul+FwOAybzWY4HA7D5/MZ0WjUaGiIGitXXmtkMqma5/h//+/fjJaWJqOlpcloaIgadXUho64uZESjdUY0EjYCfq8RDgWMR374g5rfP3XqpHH1siWG3WYxXE67YbPC8LidhtVqNRwOh+FwOAwAxo033jjjOj7wwANVz0ta2Gw2ed3U1GT8+te/rvn9bdu2Ga2trQYAIxQKGZOTk4aVhgV1NnUYd1smk6ky5GZzv/T3tNt3MQue30skEiJVtNWr3c3ZDt47r83vJ5PJqnswG4KUaDScGBgyjDLK5aKcWweStBTR9yb3UMzDqJRQqZy7l1QqVWXTmH+b11EbfHxP2z8z0YKGKj9PQ9JMS1Gb+JIedLk++LFWMQYJYP77i3bYv4yEN4eDz1nn1qoAiXYbaxGff3/emcL6Zd39ZADufrvdfkGuohbBv0jE/1IyAMV6NRPYJflVK+AyWwLrDxJgBhF7KYT4NBZxpmDQhdlB44Il0jv/QgYxLrAhLrz2h3/Wi53zo9LEqlOptQik9WKtGzJ7CNra5gJxZ2n9a442mv1ybV2bM1szPsx5Ec54uPZM9LnN93XuexYUiyXw9Pz8B+cw4HS6YLXa5DMOhxO5XP58bN+BYrGEYrH0AbUtlvOxNpxPqFXgcDhRMYxzPxVDYhDmiCjvoVgsypry+Wqlt2vRi+/rtPEFa2ZOMjB2TteBkT69+DyxzlzpVKZ2ZfSF9Q7S4pYRO7fbLecwx9Z1KlP/6HOQqcz4AS3eqev5eSZ7HA4XSqUyikVGEe0wDIu6BxssFivy+cL5z1Xg8XhhGEC+UITFakexVEa+wMW2w2L5gFmKxTKK5Qpcbi8cTjcMw4p8oSS5FIfDIVlMnRklM+v101gHSi9uYHPmVWcDuS6kqWEY59LBdrsd09PTskAM+wKQMDAvxNw2M1U8OS9KAAQPZrV443wonpdAi0qlgsnJyapr89APMZM4Y0qVkTqdtcvn8+Ib831m2Ox2OxwOB7LZLAKBADKZdNV3uR65XA7T09MoFovw+XyS+wiFQkgmk8jnc3C5XCrDaK06T6VSgQVANnsuw+h0ngu12+12IRQTX6lUCn6/X+6TBwEnOgzPPAE3oI6ZEA/BI5VKVQFkxA0sFot47rnnBFlSLpdl12zatAnJZPKCxSdyiPH+kydP4l//9V/R2NgIl8slu/j48eM4evRoFbaAjKBF1eTkJB599FEMDw/LDTIl+8ILL0iodCZASaVSwb59+/D4449LAoW7fd++fTh+/HjVLtE4B2IOJicn8dRTT2HZsmUiNn0+H5LJJLZv345SqQSn0ykMxhy/1+sV5ti+fTs6Ozslk0i7YteuXZJP4LMzsUMa2Gw2HD16FN///vdx9dVXy3M5HA6Mjo6iv7+/igb0Yog5sNvtyOVy2LRpkzDDOWY7hzn41a9+hUwmg0KhIM9gsdvtBr84a8DgPLeZX+sE0WwHRR2zVDrNyvTzJ31wR7lcLrjdblgsFvh8PlEDIyMjkvFMJBKoVCrwer0wDANTU1MIBoPCPFwzJlyoVijZZjPIzNA0rsul4B5qHRqrYaaDOVLJnIHf78fJkydhJRdpcadFl9abNN6YjeIuoCidSURrUVZrBzPR9HF4F06ns+pc+r49Ho/ofi46pUU6nYbP5xP96Pf74TufqMlmswiHw4hEIrKbaGTSfiFjMQVL2Ji+vs/nu2Bd+TlNTG131Xo2ft7hcAgqSGdP9fm59kxXU7qLGqHRddttt2HlypWCguEJKMp5EsaWefJCoQCfzyeLxc9RdNlstiomIVRLYwpLpRI8Ho8YMNoipq7mwl0MUkZm5Q72+/1VIFEiaQYHB2EYBubPn49UKiWQqnQ6LRhADcrUcXkuXi6XE8IXCoUqYA1BmszjkzDapiKsizu/FjZQG3zEY/J7lJy0I3i/lGJ67Ww2G/bu3Yuf//zn8ozCAA6HA6tXr8Ztt92GdDqNQCCAVCpVBXLgzuf75HbDMFBXVweXyyWQLOpOYgRo+BFtw7Qlia2J+1F93ks9Tp06hUKhgAULFnxpgmDNzc3YuHEjEonEBxKgXC6jUCggk8kgkUggFoshm82KVarFDTlVixlyJUUXUS3MmdNVpL+rsW+UGB6PpwrVS+lCSUPAic7cmVUJxWw2mxUQh4auEXQZCARgt9sRi8WqXE39We2x6Otxd1OinAN55sVroSQjBI1GI//mWvCaGgPIHa7XXMchKBG1EU0JbTbOuc7cUJlMBj6fT+yVKkiYGQRK65AE11EyGmwkjmDbz/upPAcvrgMWfFitFyn2eR7tRnHBtEujda4ZKk53kuekmCRTUmrRVuG90kbRaGctuonI4d86SEV1R5GtA1raBdMSjmpK++b8PtfMbHxr1cfv8lr6XPo7et1JU3MMoWY2sFaiQ59MB1g0gTUgcyajzXxufkcTnQugGcN8P0QNM9/Nxdb3RjWj0bDclZROPC+ZmUyjr0nfW7uuJBilWS27pFaU7lISTbOFzGut4WzXvpTzX1Y62JxQ0RaxOQypf2p5AGaYuBlkYYZv6SoXLTnsdrvg6KhW8vk8crmcGIL6oEund68OPHHHa1HKz5D5qK6ulN3ymcEDkJiaAbggsxl0tTJstRhLf0a/p8Ur9SmvTbHG961WKzzn4dojIyMYGBgQrKHdbsehQ4dgtVqxaNEiGIaBYDAIj8eDhoYGtLW1wev1yn0Rx8fr6tQx1cuXjgF0VI5WvhZLl6ICLhbaNYtQ2iTmKiOKZuq606dP48CBAzh79izsdjt8Ph88Ho+4oXV1dWhubhYPJp/PIxaLYWpqCseOHRMruaOjAwsXLkRPT0+VdGAQy+Vyfa6Jf9kMYDZYzEmLWkmbD6sCZromLWYSkwwRj8fxwgsvYHp6GjabDa2trVi9ejU8Hg/8fj/8fr/sXAaEAOCaa64Rnz6fzyOZTEpgZWpqCnv37sUrr7yClpYWrFmzBs3NzeINUdJcDK38hZQA5tQwXZ2LGTSX4utrFVMLjGG32+H1emG1WnHq1Cns2bMHw8PD6O3txVVXXYVoNIr6+noR44yJmzOQlCQMAgWDQTQ0NMjz0DVOJBIYGRnB888/D6fTiVWrVmHx4sWiHujGzhaC/UIxgLkQlMUK2iaYKXVrBlOYvQCK+UQiAbfbLckOj8cjqOFQKITR0VH87//+L3w+H1atWoUVK1ago6OjKhvIej4mZugBsOxMu1Hag2HEz+VyoampCXPmzEFPTw+6u7vR39+PXbt2Ye/evVi1ahUWLVqEXC4nhR3067Vb+GkdtRDI5tf2j3JiM1ijljFnfs/s7pltCupYFpZQxyaTSYRCIRSLRfzqV7/CiRMnsHbtWrS3t8MwDLS1tUndIVO/jJ1ns1lxFTXQQ99DMpmEx+OpiuGTUZlybWlpQUtLC5YuXYq+vj68+eabeP311/FXf/VXEovXrqUu7f7CQsJ0pY0Gg5h3PAk/k/+q1QpLo5nDttlsiEQiOHbsGJ588klUKhU88MADWLRoESKRCBoaGjA1NYVkMimZPiZ4mNEj+IOBFRqGvGY4HK4KRJljDpR6hUIB0WgUq1evxi233IKmpib88z//M0ZHRyVDyDWhS/qFtgH4ty7GpO9s/qll5Jklg5YETLb4fD688sorOH78OFauXImrr74aNpsN8XhcEi3M009PT0uNv2EYOHr0KI4dO4azZ8/KPXs8Hhw7dgyVSgW7d+8WMEkgEEB7ezsWLVqErq4ucTmpIoh+ovhsa2tDQ0MDli1bhqeeego9PT344z/+Y1GT6XRasoyfVXvgshnAjOHTWcKZ4Fu1fPtaqsJms0kAx+PxYMOGDZicnMSaNWvQ3d0todtQKCSuGSVGXV0dTp8+jddeew1jY2Nobm5GXV0dVq1ahfr6etn9J06cQKlUQltbm0T8isUihoaG8PLLL2NqagotLS3o7e3FNddcI1HFXC4naVwim7q7u/Htb38bu3btwo9+9CP8zd/8jYS+War9hZMAtcqkaHTNFB3T4MdaEoGBpGKxiEAggGKxiCeeeAKRSAR/+qd/Cr/fj3w+j3g8LiKZSBiLxYIdO3Zg37598Hg8IqLtdjtCoZCEbSmh+H0WkHo8HpRKJbS3t+O6665DPp/H2bNn8fvf/x579uzBokWLsG7dOvEs8vk8HA4HgsGgJH3Wrl2LAwcO4Ic//CEefPBBqTL+LEcKPxYVoC1nPnAtFTCTsaiDLIZhIBQKYWxsDC+++CI6OzuxevVqJBIJJJNJNDQ0IBqNChP4/X4cOnQIr732Grq6uvDAAw8gEomIrmdvA010ShDaG1arFalUCi6XC36/X4zEYDCIhQsXYmJiAm+99RYee+wxLFq0CGvWrKnCAFitVgQCARiGgXXr1mF6ehobNmzAgw8+eAHC+QthBM4UyfswvXxqnY/Zt4mJCbz66qtobGzE6tWr4XQ6EY1GEQqFkM1mkUwmEY1GMT09jR//+MfYuXMn7rnnHtxxxx2oq6urgljpZgrJZPKC0DJVFl1OqjL2S/J4PGhubsZdd92FO++8E8eOHcOjjz6K48ePCx6CfYFou9x9992YN28ennjiicuCeF1RBqBI1Nmzix10zTRmnbsrn89jcnJSxCLdKp0SJgqYlrJGFjscDrz++uvIZrO4++67pWJXN7Cqq6vD7t278d///d/o7e3FQw89hHnz5okrRsONvj93odvtFuyhx+OB1+tFsVhEPp+X5hUasElGob5vaWnBt771LVx33XX47W9/iy1btggTnOsH8EFvnptvvhmhUAgbNmyQLGOpVBIX9VLa3nzUo1bm0Pz6E6sMomdQSyow2KO7eVUqFbS1teH111/H6dOn8Z3vfEdi7mTMdDqNlpYWbNiwATt37sSf/dmfobe3F06nEwMDA1Wp5FrBKzN2kRgAGobaTTQDP+hleDwe3HjjjbjjjjuQSCTwP//zP3A4HEgmkyJNuInuu+8+BINB7NmzR6BaBIyY4fOf2zjATEyg3ULNBHqXMvJXLpfh8/mwa9cu7Nu3Dw8++CDGxsakXQ2bMjY3N+Pf/u3fkMvl8O1vfxtNTU1S09DY2Dir9KI/rotH8vm8RB1PnTqF999/H2fOnMHQ0BDGxsaqAkq0+Iks6uzsxO23345IJIJ/+Zd/ERuAWVG6kDfddBP6+vrQ399fhdGLxWIz9jv6XCSDZiO+RtXwgakGcrkcvF6vtH+hBZ9MJrFnzx7cfvvt8Hq9GB8fF08gGo3CarXiBz/4ARYsWICvf/3rEvELBoOy82azP8zGqW5Bc+rUKfT19Ukqmaqro6MDHR0d4iU4HA4JUVPK3HbbbXA4HHjkkUfw8MMPizHMXEN9fT16e3vx0ksv4U/+5E8k11BXV1czIPaFkQAMn2oJQBhWoVCQWgLaHm+++SYaGxtx/fXXY2RkBKFQSBY5GAxKRu7GG29EOp2G1WqF3+/HyMhIFRZwpoM2BPUxex+OjY3h9OnT8Pl8AnzVJW/BYBA+nw+ZTAbFYhHBYBDFYlGCUPl8Hrfeeit6enrwwx/+UNxY9g/K5XL4yle+gs7OThw8eFCM3c+KV2D9pCWAGSfARI3O0E1OTuLYsWNYv349hoeH4ff7JR1bX1+PZ599FrFYDHfffbdg+2KxGFKpFJqamhCLxQQEOptKot5nRjGVSuH06dOYnJw839AxJyiiVColXbny+Tx8Pl+VOvL7/ahUKgiFQkgkErj11lvR3NyMn/zkJ2II6rD4fffdh/7+fgwMDMBisWBqauozkUb+RBnAjKnnjifhuRMPHDiA5cuXIxqNinguFAro6enB9u3bcebMGdx1111VTR9ZO5dIJBAIBGbNvOmWrdS7brdb9D53skbr0kjVUDCn04lwOFzlIpbLZUSjUVgsFnzjG9+Az+fDtm3bRO2xaqdQKOCmm27C7t27EY/HUVdX95lIEllr+fMflwuiMQIkDhsw0kePx+M4ceIEvv71r+PEiRMwDAN+vx/hcBj9/f04fPgwvva1r4ldwLKyVCoFr9crbdRmc6to/ZubMA4NDcmO1rEIMrBOazOKyH6D5vgHIec0+oaGhqrgclarFb29vUilUjh58qQglz8TDKDz8BoPfzmHBojSECLCplKpwO12C+rG4/Hgueeewze/+U1MTU0hGo3C4XAglTrXXXP//v3o7u7G4sWLYRgGAoEPWq663W5pKKn7D9TCIdIGKJVKkiRiLsDv90s1LhkklUrJec29BnTFkO6ZTIOytbUV1113HZ5//vmqbumULnfddRdeffVVpFKpKvi2Obj2cTaGqIUHkJqHK81xunPl8ePHEYlEEA6HRd9yt7JyZ/HixVKoMltY+WLSiG1XbTYbBgYG8O6774pb6nK50NXVhXw+j3Q6XdVY4cN0KSfUrL29HaFQCPv37xcPhU02u7q6EIlEcPbs2Qu6lOg1Mrfv/1x5AbUSRzowxB1z8OBB9PT0SDCIOLtKpYJ33nkHjY2NgvIxw83MUS2zS2V+j1VHhUIBW7ZskexfKpVCe3s7Ojo6kMlkpMcA6xcvRU+bcRDNzc1YtGgRTpw4gVgsJtKVhuHatWuxf//+KgmiA2h6rT63RqD5YXTHbhZvjoyMYOHChVJ8QTE5ODiIWCyGJUuWiCg2t7CplZeYiTH0+7t27cLAwIBIg3w+j2uuuUZCxzqzOVs/glqgGD6/1WrF0qVL4fF4cOjQoao2LclkEgsXLsTg4GDNkvzLlXKfuTiA+aG4S4jLe/fdd9Ha2irl2ZVKRQo7+vr60N3djZaWFiQSCfEcNEzrw1TbMFY/NDSEzZs3S4awUqmgs7MTLS0tmJqaqipa1RM2LuXQldN8lra2NoyPj0uDDbqNpVIJixYtwoEDB6pEvrlvwOeWAWZTAdRr7733Hq699lqk02mBWdvtdoyNjWF8fBwrVqwQn93coXym3WJmDP13sVjEG2+8gXg8LnH5QqGAtWvXSp5Bj175MCKYzKJFerlcRnd3NyqVCs6cOVOVOSyXy7jpppuwZ8+eqtb7GhtJN/JzLwFqoYISiQTS6TTa29ulYpXt1kdHRxEKhdDS0oJMJiPFqGytMhujzdTV0+v14p133sHBgwcRDoelZ9CCBQvQ3NwsIWmGl3lPFNsXYwQt4RjaLhaLaGhoQDAYxOjoaJV0cTqdCAaDwtiUGmYVeiWAJNaL6dTL3fW1mIAI2aGhIUQikSp4OcO5ExMT6OzsrArMzNRw2mxA0e1iEIdGXCaTwbZt25BMJoXZ4vE4rr76ammIwbiEHup0qW1rzFE/3fFkzpw5GB8flwZY9AaKxSK6urokXlALQHNFjMBaeP3LaWZoDgCZxZl2x44fP44FCxaIqNXj0iYnJ9HV1SXZsmw2K2Hji4lE5vVZQs6eAdu2bUN/f78QNZvNorOzU/IHDodDppHwXumWfhgbgGJeH/Pnz0c+n8fU1FRVmtnhcKCrqwuvvvqqoJJo4xDE8qm4gTN10vy4ghE2mw3T09PSXo0MR7EcCASk6ocL5fP5qiqC+WMO0rD5Ay36YDCIs2fP4rXXXkMikZBxL62trbjxxhsFBq77G5hL3C5VFM9kmHq9Xni9XmEouoOVSgVz586t6mWo27nQu/jUbQDzw5ujSrX+PxPzMDKYSCQQDAYlt07RG4/HZXAjjUJdgm0e9aJfUw9zwpfVakU6ncZLL72E/v5+EcmxWAy33HILAoGAFIFSdWjG0kmsj6oKvV4vpqamJBNJacXxODQizUDbK2IDXOpFLmacXAojEHzJxAzTs4y4TU5OSkUQ4ddMpFysMQItb53QOXz4MLZv3w6XywWv14uhoSHcfPPNWLZsmcz2I2OS8LwfzQAflRDBYBDT09MCMNEeBvMLn4Qt9qGTQbP9zCbaa0mFmRggFovJTB9i5ql7KQH0aDbqbdoI2kgjsUgwDmj0+XyYmprCiy++KHV7p0+fRiQSwf333y8TxhgMIuSM16Br9nERoaGhAZlMpmoqC0e4eb1exOPxi6qTT10C1NrpMzFGrQZOtMzT6bR056BNoO0ASgDCsHQjazOi15xm1u3Z9u3bh61bt0qSJ5FI4I/+6I+kkpejV81j1WqVun9UCVBfX1/lyejxroS4Xymdf4EHwxe6maC23Gu1UtfgS62XzQ2lzP9jGbbT6URdXZ2kdPVnielnmznCxxjL57m1oaT9f4fDgWPHjmHjxo3SsjadTuPOO+/E+vXrxZ4gzEtn+Mypa/O1PgoDEHZOV5C/Gxoaqnopmxnh4zDAdSNQ8/lkdnAsFsPw8LCMVtM3o4mo9VMtBjDvev0/h8OBM2fOIJfLYWBgAMlkEoFAQFLFp0+fxrFjx6rarno8Him9pjRwOp1Sc6enm/M6v/71r3Ho0CHMnz9fhj4uWbIEAwMDKJVKiEaj8pq9hE6dOiXAkEqlAp/Ph5GREfT391f5+DTYKJVYCTWbEWixWDAwMID6+nrJNpIJGQcIh8PyfFwP2kCXJdpVeR5bww4ODla16AUAuy5sYDGjefebdbm5d52ZAXQJmJYGDodDBjGPj49XjYalMTQ6OipSgnEALjAjbTQe2fiY42oDgQAGBgbw0ksvIRQKYWJiAi6XC7fffjtaWlowOjqKYDCIrVu34sCBA+jt7cXExITYERwPz6ngqVQK8XhcrkGIOPMZDDKZu5ubM4VELiWTScRiMTGAPR6PVBAzRO31eiWewfnDl3PoLuXMsrK+4gIJYLVaEY1G0draKr60ztzVMuZmYoBaRqH26Z1OJ6anp7Fw4UIx9LizRkZG0NXVBZfLJf2IdYtV3QwyGo2en76ZRzgcRjweh8ViwYYNG5BIJNDa2oqJiQnMnz9fKnUmJiawfft2+P1+rFixAj09PVi8eDFcLhd2796NkZERNDY2AkBVcWgymZRCTxa2UF1ms9lZGYABnb6+PixevBhz584VW8Dj8WBsbAx+vx9Lly6tqlswF9pejvjXYBiXy4Xjx4/D4/Fgenr6g06hWq9fzIq/VAbQxRdmBmBNHaNm5HT6/Ayj0iahUcfv6J3IOr58Po/m5mY8/fTT2L59O9rb2zE+Pg6fz4d77rkH8+bNw7Fjx7Bv3z7YbDbceuutooJ8Pl9V7p+egG4sSXuEelQ3h2LU8WJ4AXY64TNoN1g3pmC09GLNsy8lBsE5AqQHbbCqdju6rQkXYragj3kieC0GMHsE5grgRCIhTY75W+bwni8c4S4hY2gDlYGVYDAoIrO/vx+PPPKIuJS5XA733nsvvvGNb+B3v/sdjh8/jrlz56K3txfBYFDiABpOTnuEYl3fAwtYNGqHxvJsSSqb7dwIWrN3RALE43F0dnZe0DxTX+dykVc6zUzb4gIVoG9UtxbXxkQtHX+pKsB8+P3+qubRul2r3+9HPB5HQ0ODIHZoqDEmQKZh3x/uxsceewzj4+Po7OxEX18f1q5di/Xr1+PZZ59FsVjETTfdhIULFyKdTov3kc/nZVcQpMIiFA3WIEG0O8pZwZpRZmIADsFgwoluJuMiRETRYGNI+HJjAZQihODz/mq5tnbdWHG2fHst/7+WvtdGoJnbCcJkrJ46lYsRDAYRi8WqgjNEBOkxMPl8Hn6/X4ybN998Ey+99BK8Xi8KhQKamprQ2dmJoaEh2Gw2rF69Gk1NTRgbG5PmT7FYDNFotCrSyGJVehZs7U5pwR1Pj4NIppm8AB7j4+MIh8Py7LpSqlgsor6+Xly0Wu72x5Gd1Zu8phegEy1mkWbu6atj87V+zMOc9N9sl6I7e9LHd7lcGBwcrLpZFmuwulcjcGlD/Pu//7swSjKZRDgcxp49e/DWW2/hqquuwtatW2XhS6WSjIGJRqNVTagpXWihb9++HQMDA9L3x+l0oqurC2vWrJFoprnphdkI4z0FAgGZqcAIKCVhMBi8JFjb5QbszL2N9XntmriXkuD5sDkDc88/wqKSySS8Xq8QNJfLoaWlBSdOnBC7IJFICMem02kZVkUJEo1GMTExga1bt2LBggWS1InFYmLpDg8Pi1HHCKTuok24mFZ9NAYnJiaq3DbaJqtXr0ahUBDVRG9Et3snoQFgdHQUDQ0NYgjzsyMjIzLanbaEDtZ81MYS5s6ttYJc9pmw4xfDqddKxtQCg9SCT4XDYUxNTaGpqUmsZOrSYrGIyclJNDY2ivHCmQTMFVQqFbS0tCAej1e5hFp00trW4270kCUSiGFn7hQahXRFGcHTUzsYmKJLykii7uNPpiEaKBKJSB6Ai3/y5Em0tLRUrbe5q+pHiQSaezDVauxxxYdHG4aBhoYGvPvuu7jmmmskkcOBBq2trYjH42hvb6/q2EErdnR0FI2NjRgfH0dzczPee+89pFIp2XUkBImvfWkyMa19pmInJyfhdrsRCASE+MQGxmKxqvNwUhgbVOk+xjoAw9/vvfcewuGwxBd0FLWvrw9r1qypCmvr9vZXIj9wRRmAufc5c+Zg165dYnyxysfj8aCxsRHvv/8+li5dKq6fHtfGiV4tLS147bXXsGXLFixfvhyNjY1SHkb7gS6kObQdCoUQj8fhdrulyDOVSmFwcFCKTAnYYA0fARvz5s0T41UDOdiHyOl0is1is9lw9uxZtLa2SlLK7XYjnU7D6XQK/M3s/l3JbOAVlwCMSzc0NGBgYABLliyRhE00GpUFGhwcRFtbG1KplIRpmcYFgP/4j/+A1+vFjTfeiDvvvFMCRhpRq4mvkTd+vx8DAwPweDzo6upCuVzGjh078PjjjyMQCAgxe3t7cdtttwkx6H6SSXTpe6FQkEgng2FnzpxBNptFW1tbVTzE4/Hg5MmT0s9Y9xQwZz+/cAzAyVxLlizBvn37sGLFCkxMTIh4rq+vR1NTE44ePYr29nax2iuVCurr6wXk0d3djY6ODqnYpaWtM5LavaVeJQMQPFJXVwe73Y5wOIzh4WFEo1EAEEBKXV2dpI35HSKUSfBisVjl0tJNPHDgAEKhEDo7O6UkLh6PIxQKYevWrVi2bJlsCnpgtZpkf6EYgEGUOXPmoFKpYHx8HACkgZPFYkFLSwvefvttnDp1Cm1tbeKePfbYYygUCrjhhhsQCATg8XgwOTkp7WCJ4jUTn2FQHZ+g66mbRbB/AMGqDAbpAVoklHkgNnc+jcDx8XFMTU1h+fLlVa4mYfHxeBwLFy4UxtRDpq5k8wjrp6ECuPBNTU3o7++XnD8NoJaWFjQ0NODIkSOIRqPo6+vDf/3XfyEUCmHFihWyg2OxGDwej7h3ZrFvZgRdf6inmTELygwkg1JUN8xN6CGRZChmCnU3MraoDYVCkuQpFAooFovw+/3Yv38/Ojo6pNsIJZMugGXXsk+cAS6nTdxHOWicZbNZ9PT04NChQ/B6vUilUjLVo1gsYsGCBfB4PHjkkUewe/duXH/99ViwYAEcDodE62iAJZNJaeHChdbQLqZEGQji5ym++T4Xnd1JdGFIrYifOalD6XP27FkcPXoUS5curRpgzf/v2rUL69evl14JXHM9zOpSYPAfJg4wk3FpvdLin/X/drsdnZ2d6OjowK5duxCJRCTownrBt99+G7lcDkuWLBHpQf2aTqcl26WBHLV+NMZev2fGOGjG0YUp7HGQTCZF3+uBE+Pj48LErGwOBoOYP3++qAnWBO7YsQMrV66E2+2+oDv5xeB2n3sVQB3LDlljY2O49tprMTw8jEwmA7fbDZ/Ph0OHDuHZZ59Fb28vvF4vzpw5g7q6OmkuxeyczjswW2curzaHuc2FmDpOXkt1cKezsaSO4TPCWFdXh+npafj9frz99tvo7+/H3XffXRXGZhBp27ZtWL58+QVJt0/r+FQkQLlcxvT0NABg3rx5cDqd2LFjB8LhMJ555hns2LEDX/va1xCJRNDe3o5kMomxsTEJtWoDLpvNSvxeY/rN+H5zuxrzUCoyl/mHYWhGGmkQEqvA5FQwGMTAwAB27tyJ++67T3IbrDqy2Wx44403pLElE1dfKgagIZbP51FfXw+n04n3338ft9xyC9LpNH7wgx8gk8lg/fr1YoU3NjaioaEBv//97zE9PS0WNQlPQ0xX9+oKH3Oxh7lx1Uw5Dx361hA0hqeZDSSoJJFIYOPGjbjlllswZ84ccRd53ng8joMHD2L58uWi7r60EiAYDMr8n2AwiP3794s3sHLlSumpWygUMDg4iObmZrS0tODo0aMYHh4WtI7W46ywrfWj/zdT9w3NHJphCN1iboAt4Any8Pv9GB8fx9NPP41rr70Wy5YtQzKZlBw/u5A/+eSTuPbaa9HR0YFCoSBVyl86CVAoFDA5OYn6+nrkcjls2LABBw8exNq1a2G1WrF3716Uy2WEQiEBXFQqFSxcuBBerxejo6OYmJiQQVVMIFG11GKAWggZc1yeIp+AzFrqgPqfOEC3241EIoENGzagt7cXN9xwg9wLA1OhUAibNm1CU1MTVq5cKdC2RCIxK5bwC8sAuVwOc+bMwaFDh/Cf//mfCIVCuP7665HL5aRY8vTp02LcMVM3MjKCq666Cn6/H++99x6Gh4cv6MA9U4n1TNPL9H01NjbKUCj+NDY2ChPSIEwmk2L8DQ8P46c//Smuv/56rFq1qgok6nQ6kcvlcPDgQYyMjOCrX/2qTFinDfFZaBRpv1wxTjHJAIgGH3KEC9OylUoFyWQSjY2NMgVkamoKX/va1+DxeDA1NQW32w2v14u2tjZMTk5ifHwcoVBIXDyK6fb2dlgsFhw9ehRjY2NYvHgxgsGg6GJNYB1aZdkYmYbEYBbxq1/9Kvbv31+FmCkWi4JJ4HnYMXTnzp3YsmULHnjgAVx11VUiIRhMcrlcOHXqFHbt2oWVK1eira1N2uVx7iFzC5/khpsJwXXZDKAbHJKDXS6XJGPI3US7ctE7Ojqwbds27Nu3D5FIBKtWrYLH48H4+LhU6RDpWyqV0NfXh2XLliEQCIgrRqOvtbUVDQ0NOHnyJN5++2309PSgtbVVVACNLxpvJJ7O2TMqSFfN6XSKH8/v6sQPCTw1NYVf/vKXsNls+Md//EfB23u9XoGLE5Ty6quvoqurC6tWrRLiE8nMdfu0pcBlSQDd6ZMFFJQErKphWjQUCiGTyeDRRx9FLpcTHH4qlZI4vsvlEuw9d2V7ezuOHDmC7u5uRCIR0d+BQEBi8cuWLcPExAROnTqFoaEhLFmyRFLCrMsnw+p6PAZ7dDcRYg4Y9tWDsPh6+/bt2LlzJ9auXYt169ZJ8yc2kyagpFwu42c/+xk6Ojpw5513VpWus+KJGMfPnQrQ4lEnUTSKlYjZ+vp69PX1YePGjWhsbMT8+fPFb6aaoEFUqVQQiUSQSqUQCoVk0Y4ePYquri40NDTAMAxpJs3IWnNzMwKBAPr6+rB161b09PRg7ty5sNvtQiBG3RiSJRiE/YB0EkZ7CswCvvXWW3jllVewdOlS/N3f/Z3UCRCNFI/HRVJks1ls2LABXV1duPfee0WFED6mY/yfBUa4bBVANeDz+aTUmaI3EonA4XBg06ZN2LFjB3p7e9Ha2opisYipqSmEQiFpAsEuGgRV6KOpqQmlUgknTpyA0+lEKBRCJBJBOp3G6OgoIpGINHlYs2YNzp49i+HhYezduxd+vx/t7e3SgJqVQwR8cvoHjUzqx0KhgEQigcHBQZw+fRqnTp1Cd3c3/umf/kkgZLzPxsZGTE5OCnGHhoawadMmzJ07F/feey/S6bToex60Pz4rg6QuSwIQ/EDXiw/q9/thsVhw4sQJbN68GdlsFjfffDNyuRwGBwfh8/kQjUalTJw7jzh85gHYRziRSKC5uRl+v1+w9TabDYFAAC6XC4lEQqaInz59GnV1dYLWpWp49913BZMXCAQEP3Dy5ElMTU3JTKJsNovJyUlMTk7KKNqrr74a9913n6iC6elpmWOcyWSQSqUQjUaRSqUEp7Bu3TqsXLlSIOVkdCZ4dLu4z4IbeFkSgHX4TOyw7VkqlcL//d//YcuWLWhtbUVXV5dUwwaDQSm+4GJwnh8LUt1uNwqFArxer3wvk8kIbOqdd97BnDlzxJInzJoIY1a+2Gw2tLS0oLW1VfLto6OjkjFMJpOYnJxEPp9HNBpFLpdDe3s7enp60NTUhEgkUjUmhnYBewwxkJNKpXD27Fn09/fjjTfewF//9V8jEomgWCwKvoF4RT1AUuMcP20pYK+VfdJlSrq8iwYgRZqOkZ85cwZvvPEGTp48iauvvhoej0cQttRz+XxeeuwXi0UZ80opQFg3VQnVA3f3V77yFQwMDODo0aMyEVQjgM2hX53pmzt3rtQlulwu+Hw+zJ07F9dff724mAxTEx9Ao1BXB4fDYYGslctlbNy4EcFgEN/73vdExDMlTZwDmZDPp8Ejn/ZQSTsJrUuk6JposIKuMeNO5kiVw4cP4/XXX4fb7cbKlSuRy+VkrAsJT2IQ28d4uh7eRC+AoAzWCrpcLqTTacHwxWIxjI+PIxaLYc6cOVUNJDTzEhzKFrRUM8ViERMTEwItJwOYW7VS2tG6d7lcmJqawtjYGHbv3g2bzYZ77rkHXV1dVeVhZHh6EDy/GZ94KYcuw/skUsX2WtgzPWePopARsVgsJnN0stkstmzZgoMHD6K7uxtz586VhMnFuolpcahxe7VGzvMz3KEulwtXXXUVYrEYTp48CYfDgba2Nvh8vqomEpRctPRpt1AC6P6E5oHXGt4di8VQLpfx/vvvY//+/Uin01i1apXMFGaT6SuF4/tEbQBdNcPeOvTrC4UCIpEI8vk8BgcHsWnTJhSLRaxZswa5XA7Dw8MyPm2mxdAcrXeD+W8GY+iSkbD0OKi/WR00OjoKwzBk/gAJQylANcF7MHcCo57Whu3Y2BhSqRQmJiZw9uxZFItFLF26FNddd50UhbDlnTm9/FmeFzwrA+j4utfrRTAYlO6aBDsODAxg+/btCIfDWLRoUVU9nRbFtYocdYhW/zaHLc2MwDi83+9HIBCQKt9QKIS2tjZEo1HpyHnixAkA5yqR/X4/nE4nHA6HdPWgccn5AHq8bCqVwvT0NKanpxGPx5FIJNDU1ITVq1dj4cKFVfkAIoFnklqfByawzxTp00QkDGtoaAg7d+7E4OAgFi5ciEqlIoYcjR6z7tOhVf5tJr7+n2Yas65kuFg3dx4bG4PP55Mxc93d3RKenZqaQiwWqwJxcOLXkSNHJBqpG0TbbDZhqtWrV6O1tbUqZcyWsoxhMDZQSzd/XH0Gr7gE4Gwfl8sl9XfHjh3Du+++C5fLhWuuuUbAGYRHEfPOZMls0z00c+jqYZ3fr4XaIdFpcBFEyQATjTR6KvPmzRPAKyeBMPScSqUwf/58LFmyBJVKBXV1dVIpxOBQLpfD9PS0xCt0Aycmk+gmfh53/6wSgOHaZDKJrVu34syZM+ju7hY3jpYxsXps0sTwqdli1cQ0u5f6b7P7qReUOXT2EiSSmQkYBqh4/9lsVqBbNApJvHA4jJaWFnR2dopKoHjXNgklj54HoDuSmbN5NFgvdffP1OK+1udmm4t4ucanXVu8uk6+XC7jyJEj2LdvH0qlEhYvXgyn04lMJlMFdhgbG4PH40EkEpGafT3+RS8cF0YnW+hm8fo09nT/XPr6uksX1ZSe3sUElTnJw2AVXUs9DYy5AfM4eZ09ZCMIPWKekDBek9lDDUE39+XT6o5eCbOMOtZCycrnoJTT56O60qNu9GxivZ7cKLrqWBhANy92u93IZDIYHx/HW2+9hWPHjiEajaK9vV2CN7rChjP+KAG4wIyVk5hEyfA9EoElVpppdFBIG4HmjlnattAVQAy8MJikO4Iz+cK/GeEzN6qgIWyuk9DBJh3F04jiWnOSqHbMUs7cEkarETKlRj/rbi1kdDKd2ca6WIGplIfzwn6/H1arFQcOHMChQ4eQy+XEtyb+LRgMioHHfj66ykbfLMUtdaRuh07ikgkImdZlWwzz0i3TzRNm6k9ITtdjaTm5UzdhIN5QewE6DsB4PVPLev4h5wEzLjI8PIzGxkZ5v1wuIxAISNjZ7AJrEEgul6uqiNKuMJ+NHcYZJifjskch8xt6QCXXUAfbyFz02KRDCIMmqVQKb7zxBg4fPgyr1YpFixZJqJfE0R0suZgs22azRp/Ph3A4jImJCdk15spdqhrdOpZ5chJDczbvcSb0rvbjKUUYqKK6CofDYsGz6DMajaK5ubnmDiG6qJYO14DUtrY2eV9n/fRrLclIfG071GpFa25MBaBqDVjmbv68vsda5zLXNNrZofP555+X4Qx+v19weeQuGn0EetAC13XynIlLV427kLueBiQNK8YbwuGwgCrIaOVyWcS1lgK1OmeYjSCem/F9PXfIarXKDj116hTeeustuT+KfEoSHThiFpTMmMlk5LUGjugIIp9V2yRavOvWeVRLfM28ATcDawt0X0WqAf19bWPp/gsWiwV79+69UAJQxLz//vs1ixE/bOz6Uj/PhbPb7YhGoxKv50Km02npBKZbw8+m0/R08NkOXQvJPIK2KShKqWu1J5DJZKrG67KMjcxBVcA2MpSYfC6CRXk93VeIn9O2AO+Dao2MSgmpCW9u5cvfOgV9AU4QgFGrs5eZiH6/X0SoHvPGC2q9X+tgupYtWLioui8fz+FwODA9PS2Sh1FJc/sUc9GjnhnExdTNHvVCMPrHH3YpMR98Vi22zQbqh9kgmknMkuLDgER0FzJ+X9/rxYy/QCCAkydPnrMBrFYrVqxYgb/8y7+UYA71+/PPPy8xf90kgR27KPIaGxvx93//9wgGg1X9/gcHB/GLX/wCAwMDsgN0Ny5iCjs6OrB69WqsXbtWjK9iqQS3y4UNGzZg//63ahCdf5+zpufMmYPvfve7CAaDyOVyonYOHDiAX/ziF9Lvh4ulwaOGYSAUCuHWW2/FbbfdVtXedXJyEj/72c9w6NAh0cN8fu5Q7uJly5bh4YcfrhqUyZZzL774okwfZ25F2wIsjf/mN7+J5cuXyyQRuqJPPPEEjhw5IsYtbTQdg7Db7bj55ptx//33V20Ym82GZ555Btu3b69uQuF0Og2fz2f85Cc/MWodGzduNKLRqAHAcDgchsViMSwWi+FyueQ1AOOGG26o+f1cLmfceeedBgDD6/UabrfbCIfDRiQSMdra2oyenh6ju7vbWLVqlbF3796a5/jJT/7LaJ3TYkTqwkZDfdSIRuqMurqwEQ4FjWDQb/j9PiMQ8Bvf+ta3an5/ZGTEuO666+QZnE6n4Xa7DZvNZlitVvnp7u42Dh48WPMc3/3udw2r1XqO286fx2q1GjabzbDb7QYAw+12G//wD/9Q8/tvvvmmMX/+fOO8xDWsVqthsVjkPDz36tWrjXg8XvMc999/v1yf1+T5+Lqurs544oknan7/l7/8pRGNRg2r1WoEg0FjYmLCsFJ8sTUKOYq6iA0Ozb5oPp+vCiwwB679W1qjFPmUNnRZ6N4QTfSBVVuBYZRRKZ/vqu12wmIxYBgVnGNcAzAqMIwKjEoFRqUEGIYMgNBVwtzt/J8GfNYaPjVnzpwqScPnYCKJYlQHWbQI1+3g9HkCgUCVta8xF7payWazSeNI0qBWrqFW+p5rTHpRzfE89fX1F0DR7RqsWCucqKdazhaanE0Pms+pXRHdOmWmeyiXK+fNlZmuc+H/dCj2UkfB6QCSOYdxqXre3IxZB24uRb/P9plL/f5MnUWYwPpYYOGXe5i7eOmQ5eUehmHAwOcHhPGZTgZ9HMdsk8ZooZv9bIvFwo180R1wpcer/oEBLpMJZpIAuvOmbo1qucTdXhPF9AdaXp5EvpIXI6HNyZ0PM6rdLAk+jzi8zxwDfByLV6sSdSbimdu3ztaxYyaJ8lHbqc90b7VQuJcapKnVjPmj5Oov9xlmo03NLmFEyGpLWFuSGiKtQRqMObNyl+4PM246mkZCM0DEkik9ks3n88GolOVaBGewHbtu76JtDbk/E6ybxKxFGLOlz+BVNBpFsVisgqoDH9Q7ahALn4fn168Z2WR0kfF4ekE6naylIa+np6HqMLXOfJrVKBthc011K3vSRrv0wPnx8RaLRRab5Uv03Qme1H6qDoEy1s3PM/HCbBgZhSlhPXeIrd54zuHhYVisNgAW2GwOBEN1kpmrNUTJDCXznI9FmDNpzDXM9H09O2B6elqg45ph2FTa3NSZ41+5wCR4IBCQ+UZEGtHF1PMANTCGhCd+go0puIE0oRmV1VlWwzAkV6HtLd4DYzf62e0kxMsvv4w5c+YI8RlM2bp1q+T9tZ/LDBqDQidOnMDTTz+NSCQiQaFCoYAzZ86gr6+vaucyfcxQKGfnPPfccwgFA0ilUgrNk8bmzZsFcFLL+j+XBCpi8+bNeOGFF6pwfclkEocPH0Z/f/+MYpzPMDQ0hEcffRR33HGHlKtxDvHvfvc72UnMiOpEDZ93//79eOWVV6RGgW1nfvOb30hbXD2ZRINrAODs2bP40Y9+JAhkdkWNx+Po7++v8vV1sIr0icfjePnll6XxNs9fKpXw29/+FqOjoxdEUAzu3GAwWFWdQ8mgq3x0hkl3wQI+qArWmD4OerDb7QiFQtKYWadUCed2uVwwKhW43c4qUTc+PgGXy1mV+bpwdnAFFqtNeg/pMa2pVEpqEmeSHszSsbf/1NRUVZx/amqq6p4o2bhz9RArIpR1kcrExITcA+sG9XcYKOIUUeIZ9LpPT09f0OSqFvrH4/FIoS43QqVSQSKRkM3l8Xhw6tSpcwxA8azhzro4hClS3VaWyRANfqDe16leDfhgZS+5mvBuYu7K5RIs5xM7XCwuOqHnMzWDBCyoGKhCA/HzZhDETAakeSHZ7oVqjhlDPUNQj6vXatLMrDr1y/ddLpeMx9Hi3Ty6z2xjmLORZF7zWD/iBohU0qrb5/Ph9OnTsHq9XmmVok+sQ6g07HR1jV5QFn16PB5BBuv6AuoxNmDWrWR4/nMP4kKpVKwCNrA8bXp6uiq+fgHxYFRhDpm9pCierUmUBmBS99OAov1C5LBmKOpXt7I9tLFHIgeDwSqQCglNQumexqxLYBm6uXJJbwjubKKc9OBvGth64qqWSlUjYzT36e5V5qncumGTjiuzc7duH0OpYcaqaQuUuhTA+WJTF4rFUlUnTTIHjbRau/acLrPAdX7BuMhsKctBU7VcMr3AGs1sNtJ0jJ+7iegcGleUjhodxJ6C+loazVwrl5DNZqsqiXk+vZM/kJrlmgOtuVnIVFwT7XUBgJ09epnbJvKX9Xc6kUB4lS7hJixaSw1t7Xs8HrHiyal8GJZR8wZdLhfyuazkBvTgY4fDUWUtXxgariB5vh0LVQ7z6ToLOZvPzIIR6nb+rpUIog2kLWvzZ7XBTDWi8x7m2gGW1nFtNfxdT3XVDETG1fTRDKt7I9WM3/h8PoNVMxp4aK54nQl/NxPkihzJnnmUChSRGjChW7vS/zZHCC0WJfoNXJD6YX+eWmPuLzUQczmInI87fP5Rz1nrHOb3DMNAOBzG0NAQ7I2NjQLmpJ5iH/vLPbh79OQu7kJd9FCrpbtZRM1UYlbrmG3Awh9CxdX0aW5uxuTkJCyFQqHEaJHWOZfLANqeME/wMBNxpt+XywCzDVj4AwNUS4SxsTHMnTsX/38AhICGSuOGW+sAAAAASUVORK5CYII=";
        byte[] decodedString = Base64.decode(str1, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        System.out.println("bitmap :" + decodedByte);

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

}
