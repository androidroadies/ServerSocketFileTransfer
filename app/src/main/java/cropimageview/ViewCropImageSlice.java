/*
 * Copyright 2015 Cesar Diez Sanchez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cropimageview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;


import com.example.androidserversocket.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

public class ViewCropImageSlice extends Activity {

    static ImageView imageView1,imageView2,imageView3,imageView4;
//    ScrollTextView scrolltext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(new SampleView(this));
        setContentView(R.layout.crop_image);
//        new SampleView(this);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);

//        TextView tv = (TextView) this.findViewById(R.id.TextView03);
//        tv.setSelected(true);  // Set focus to the textview

//        scrolltext=(ScrollTextView) findViewById(R.id.scrolltext);
//
//        scrolltext.setText("123456789");
//        scrolltext.setTextColor(Color.WHITE);
//        scrolltext.setTextSize(80);
//        scrolltext.startScroll();


//        Thread socketServerThread = new Thread(new SocketServerThread());
//        socketServerThread.start();

//        scrolltext.setVisibility(View.INVISIBLE);

        SharedPreferences shre1 = PreferenceManager.getDefaultSharedPreferences(this);
        String previouslyEncodedImagep1 = shre1.getString("image_datap1", "");
        String previouslyEncodedImagep2 = shre1.getString("image_datap2", "");
        String previouslyEncodedImagep3 = shre1.getString("image_datap3", "");
        String previouslyEncodedImagep4 = shre1.getString("image_datap4", "");

        if( !previouslyEncodedImagep1.equalsIgnoreCase("") ){
            byte[] b1 = Base64.decode(previouslyEncodedImagep1, Base64.DEFAULT);
            Bitmap bitmapp1 = BitmapFactory.decodeByteArray(b1, 0, b1.length);
            imageView1.setImageBitmap(bitmapp1);
        }
        if( !previouslyEncodedImagep2.equalsIgnoreCase("") ){
            byte[] b2 = Base64.decode(previouslyEncodedImagep2, Base64.DEFAULT);
            Bitmap bitmapp2 = BitmapFactory.decodeByteArray(b2, 0, b2.length);
            imageView2.setImageBitmap(bitmapp2);
        }
        if( !previouslyEncodedImagep3.equalsIgnoreCase("") ){
            byte[] b3 = Base64.decode(previouslyEncodedImagep3, Base64.DEFAULT);
            Bitmap bitmapp3 = BitmapFactory.decodeByteArray(b3, 0, b3.length);
            imageView3.setImageBitmap(bitmapp3);
        }
        if( !previouslyEncodedImagep4.equalsIgnoreCase("") ){
            byte[] b4 = Base64.decode(previouslyEncodedImagep4, Base64.DEFAULT);
            Bitmap bitmapp4 = BitmapFactory.decodeByteArray(b4, 0, b4.length);
            imageView4.setImageBitmap(bitmapp4);
        }
//    final CropImageView cropImageView = new CropImageView(CropActivity.this);
//    final Resources res = getResources();
//    cropImageView.setImageDrawable(res.getDrawable(images[position]));
//    final CropImageView.CropType cropType = imageCrops[position];
//    cropImageView.setCropType(cropType);

//    ButterKnife.bind(this);


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.marqueetext, menu);
//        return true;
//    }

    //  @OnClick(R.id.main_crop_simple) public void onCenteredCropsClick() {
//    startActivity(new Intent(Cropimage.this, SimpleCropActivity.class));
//  }
//
//  @OnClick(R.id.main_crop_custom) public void onCustomCropsClick() {
//    startActivity(new Intent(Cropimage.this, CustomCropActivity.class));
//  }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            //                serverSocket = new ServerSocket(SocketServerPORT);
            ViewCropImageSlice.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
//                        info.setText("I'm waiting here: "
//                                + serverSocket.getLocalPort());
                }
            });

            while (true) {
//                    socket = serverSocket.accept();
//
//                    socketArray.add(count, socket);
//                    count++;
//                    message += "#" + count + " from " + socket.getInetAddress()
//                            + ":" + socket.getPort() + "\n";

                ViewCropImageSlice.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
//                            msg.setText(message);
//                        System.out.println("111 is pause" + scrolltext.isPaused());
//                        System.out.println("111 test3 ;");
                    }
                });

//					SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
//							socket, count);
//					socketServerReplyThread.run();

            }
        }

    }
    private static class SampleView extends View {

        // CONSTRUCTOR
        public SampleView(Context context) {
            super(context);
            setFocusable(true);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint = new Paint();

            canvas.drawColor(Color.YELLOW);


            // you need to insert a image flower_blue into res/drawable folder
            paint.setFilterBitmap(true);
            Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ball_horizontal);

            Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0,
                    bitmapOrg.getWidth() / 2, bitmapOrg.getHeight());

//            Bitmap realImage = BitmapFactory.decodeStream(stream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();

            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//            textEncode.setText(encodedImage);
            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit=shre.edit();
            edit.putString("image_data",encodedImage);
            edit.commit();

//            imageView1.setImageBitmap(croppedBmp);
//            int h = bitmapOrg.getHeight();
//            canvas.drawBitmap(bitmapOrg, 10, 10, paint);
//            canvas.drawBitmap(croppedBmp, 0, 0 + h + 10, paint);

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
