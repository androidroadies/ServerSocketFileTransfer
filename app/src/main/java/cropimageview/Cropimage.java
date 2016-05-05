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
import android.content.Intent;
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

public class Cropimage extends Activity {

    static ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SampleView(this));
//        setContentView(
//                R.layout.crop_image);
//        new SampleView(this);
//        imageView1 = (ImageView) findViewById(R.id.imageView1);

//        SharedPreferences shre1 = PreferenceManager.getDefaultSharedPreferences(this);
//        String previouslyEncodedImage = shre1.getString("image_data", "");
//
//        System.out.println("test ;" + previouslyEncodedImage.toString());
//        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
//            byte[] b1 = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(b1, 0, b1.length);
//            imageView1.setImageBitmap(bitmap);
//        }

//    final CropImageView cropImageView = new CropImageView(CropActivity.this);
//    final Resources res = getResources();
//    cropImageView.setImageDrawable(res.getDrawable(images[position]));
//    final CropImageView.CropType cropType = imageCrops[position];
//    cropImageView.setCropType(cropType);

//    ButterKnife.bind(this);

        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
                R.drawable.ball_vertical);

//        cropimage(bitmapOrg);
    }

    private void cropimage(Bitmap bitmapOrg) {
        //=============================================================================================================================================

        // For split images in 4 part vertical L3.
        // Part1

        // (0, 0,  (image.size.width) * 1/2, (image.size.height) * 1/2) );

        Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0,bitmapOrg.getWidth() *1/2 , bitmapOrg.getHeight()*1/2);

        ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
        croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
        byte[] bP1 = baosP1.toByteArray();
        String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);

        // Part2

        // ((image.size.width) * 1/2, 0, (image.size.width) * 1/2, (image.size.height) * 1/2));

        Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1/2, 0, bitmapOrg.getWidth()*1/2 , bitmapOrg.getHeight()*1/2);

        ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
        croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
        byte[] bp2 = baosP2.toByteArray();
        String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
//
        // Part3

        // (0, (image.size.height) * 1/2, (image.size.width) * 1/2, (image.size.height) * 1/2));

        Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1/2, bitmapOrg.getWidth()*1/2 , bitmapOrg.getHeight()*1/2);

        ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
        croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
        byte[] bp3 = baosP3.toByteArray();
        String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);

        // Part4

        // ((image.size.width) * 1/2, (image.size.height) * 1/2, image.size.width/ 2.0, image.size.height));

        Bitmap croppedBmp4 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1/2, bitmapOrg.getHeight() * 1/2, bitmapOrg.getWidth()/2 , bitmapOrg.getHeight()/2);

        ByteArrayOutputStream baosP4 = new ByteArrayOutputStream();
        croppedBmp4.compress(Bitmap.CompressFormat.JPEG, 100, baosP4);
        byte[] bp4 = baosP4.toByteArray();
        String encodedImagep4 = Base64.encodeToString(bp4, Base64.DEFAULT);

//=============================================================================================================================================
        System.out.println("bitmap :" + bitmapOrg.getWidth());

//            Bitmap realImage = BitmapFactory.decodeStream(stream);

//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] b = baos.toByteArray();
//            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

//            textEncode.setText(encodedImage);

//            System.out.println("test2 ;" + encodedImage.toString());

        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit=shre.edit();
        edit.putString("image_datap1",encodedImagep1);
        edit.putString("image_datap2",encodedImageP2);
        edit.putString("image_datap3",encodedImagep3);
        edit.putString("image_datap4",encodedImagep4);
        edit.commit();

//            imageView1.setImageBitmap(croppedBmp);
//            int h = bitmapOrg.getHeight();
//            canvas.drawBitmap(bitmapOrg, 10, 10, paint);
//            canvas.drawBitmap(croppedBmp, 0, 0 + h + 10, paint);

        Intent in = new Intent(getApplicationContext(), ViewCropImageSlice.class);
        startActivity(in);

    }

    //  @OnClick(R.id.main_crop_simple) public void onCenteredCropsClick() {
//    startActivity(new Intent(Cropimage.this, SimpleCropActivity.class));
//  }
//
//  @OnClick(R.id.main_crop_custom) public void onCustomCropsClick() {
//    startActivity(new Intent(Cropimage.this, CustomCropActivity.class));
//  }
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
                    R.drawable.ball_vertical);
            // =============================================================================================================================================
            // For split images in 2 part vertical.
            // Part1

            // 0, 0, image.size.width , (image.size.height) * 1/2)

//            Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0,bitmapOrg.getWidth()  , bitmapOrg.getHeight()* 1/2);
//
//            ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
//            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
//            byte[] bP1 = baosP1.toByteArray();
//            String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // (0, (image.size.height) * 1/3, image.size.width, image.size.height/3.0));
//
//            Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1/2, bitmapOrg.getWidth() , bitmapOrg.getHeight()/2);
//
//            ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
//            croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
//            byte[] bp2 = baosP2.toByteArray();
//            String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);

//=============================================================================================================================================

//=============================================================================================================================================
            // For split images in 2 part Horizontal.
            // Part1

//            (0, 0,  (image.size.width) * 1/2, image.size.height)

            Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0,bitmapOrg.getWidth() * 1/2 , bitmapOrg.getHeight());

            ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
            byte[] bP1 = baosP1.toByteArray();
            String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);

            // Part2

            // (image.size.width) * 1/2, 0, image.size.width/2.0, image.size.height)

            Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1/2, 0, bitmapOrg.getWidth()/2 , bitmapOrg.getHeight());

            ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
            croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
            byte[] bp2 = baosP2.toByteArray();
            String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);

// =============================================================================================================================================
//=============================================================================================================================================
            // For split images in 3 part Horizontal.
            // Part1

//            (0, 0,  (image.size.width) * 1/3, image.size.height)

//            Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0,bitmapOrg.getWidth() * 1/3 , bitmapOrg.getHeight());
//
//            ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
//            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
//            byte[] bP1 = baosP1.toByteArray();
//            String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // (image.size.width) * 1/3, 0, image.size.width/3.0, image.size.height)
//
//            Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1/3, 0, bitmapOrg.getWidth()/3 , bitmapOrg.getHeight());
//
//            ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
//            croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
//            byte[] bp2 = baosP2.toByteArray();
//            String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
////
//            // Part3
//
//            // (image.size.width) * 2/3, 0, image.size.width/ 3.0, image.size.height)
//
//            Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 2/3, 0, bitmapOrg.getWidth()/3 , bitmapOrg.getHeight());
//
//            ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
//            croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
//            byte[] bp3 = baosP3.toByteArray();
//            String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);

// =============================================================================================================================================
            // For split images in 3 part vertical.
            // Part1

            // 0, 0, image.size.width , (image.size.height) * 1/3)

//            Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0,bitmapOrg.getWidth()  , bitmapOrg.getHeight()* 1/3);
//
//            ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
//            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
//            byte[] bP1 = baosP1.toByteArray();
//            String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // (0, (image.size.height) * 1/3, image.size.width, image.size.height/3.0));
//
//            Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1/3, bitmapOrg.getWidth() , bitmapOrg.getHeight()/3);
//
//            ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
//            croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
//            byte[] bp2 = baosP2.toByteArray();
//            String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
////
//            // Part3
//
//            // (0, (image.size.height) * 2/3, image.size.width, image.size.height/ 3.0));
//
//            Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight()* 2/3, bitmapOrg.getWidth() , bitmapOrg.getHeight()/3);
//
//            ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
//            croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
//            byte[] bp3 = baosP3.toByteArray();
//            String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);

//=============================================================================================================================================

            // For split images in 4 part vertical L1.
            // Part1

            // 0, 0, image.size.width , image.size.height/4)

//            Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0,bitmapOrg.getWidth()  , bitmapOrg.getHeight()/4);
//
//            ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
//            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
//            byte[] bP1 = baosP1.toByteArray();
//            String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // 0, (image.size.height) * 1/4, image.size.width, image.size.height/4.0));
//
//            Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1/4, bitmapOrg.getWidth() , bitmapOrg.getHeight()/4);
//
//            ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
//            croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
//            byte[] bp2 = baosP2.toByteArray();
//            String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
////
//            // Part3
//
//            // (0, (image.size.height) * 1/2, image.size.width, image.size.height/ 4.0));
//
//            Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight()* 1/2, bitmapOrg.getWidth() , bitmapOrg.getHeight()/4);
//
//            ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
//            croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
//            byte[] bp3 = baosP3.toByteArray();
//            String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);
//
//            // Part4
//
//            // (0, (image.size.height) * 3/4, image.size.width, image.size.height/ 4.0));
//
//            Bitmap croppedBmp4 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight()* 3/4, bitmapOrg.getWidth() , bitmapOrg.getHeight()/4);
//
//            ByteArrayOutputStream baosP4 = new ByteArrayOutputStream();
//            croppedBmp4.compress(Bitmap.CompressFormat.JPEG, 100, baosP4);
//            byte[] bp4 = baosP4.toByteArray();
//            String encodedImagep4 = Base64.encodeToString(bp4, Base64.DEFAULT);

//=============================================================================================================================================
//=============================================================================================================================================

            // For split images in 4 part vertical L2.
            // Part1

//            // 0, 0,  (image.size.width) * 1/4, image.size.height) );
//
//            Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0,bitmapOrg.getWidth() *1/4 , bitmapOrg.getHeight());
//
//            ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
//            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
//            byte[] bP1 = baosP1.toByteArray();
//            String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);
//
//            // Part2
//
//            // (image.size.width) * 1/4, 0, image.size.width/4.0, image.size.height));
//
//            Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1/4, 0, bitmapOrg.getWidth()/4 , bitmapOrg.getHeight());
//
//            ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
//            croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
//            byte[] bp2 = baosP2.toByteArray();
//            String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
////
//            // Part3
//
//            // (((image.size.width) * 1/2, 0, image.size.width/ 4.0, image.size.height));
//
//            Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1/2, 0, bitmapOrg.getWidth()/4 , bitmapOrg.getHeight());
//
//            ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
//            croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
//            byte[] bp3 = baosP3.toByteArray();
//            String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);
//
//            // Part4
//
//            // ((image.size.width) * 3/4, 0, image.size.width/ 4.0, image.size.height));
//
//            Bitmap croppedBmp4 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 3/4, 0, bitmapOrg.getWidth()/4 , bitmapOrg.getHeight());
//
//            ByteArrayOutputStream baosP4 = new ByteArrayOutputStream();
//            croppedBmp4.compress(Bitmap.CompressFormat.JPEG, 100, baosP4);
//            byte[] bp4 = baosP4.toByteArray();
//            String encodedImagep4 = Base64.encodeToString(bp4, Base64.DEFAULT);

//=============================================================================================================================================
            //=============================================================================================================================================

            // For split images in 4 part vertical L3.
            // Part1

            // (0, 0,  (image.size.width) * 1/2, (image.size.height) * 1/2) );

//            Bitmap croppedBmp = Bitmap.createBitmap(bitmapOrg, 0, 0,bitmapOrg.getWidth() *1/2 , bitmapOrg.getHeight()*1/2);
//
//            ByteArrayOutputStream baosP1 = new ByteArrayOutputStream();
//            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baosP1);
//            byte[] bP1 = baosP1.toByteArray();
//            String encodedImagep1 = Base64.encodeToString(bP1, Base64.DEFAULT);

            // Part2

            // ((image.size.width) * 1/2, 0, (image.size.width) * 1/2, (image.size.height) * 1/2));

//            Bitmap croppedBmp2 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1/2, 0, bitmapOrg.getWidth()*1/2 , bitmapOrg.getHeight()*1/2);
//
//            ByteArrayOutputStream baosP2 = new ByteArrayOutputStream();
//            croppedBmp2.compress(Bitmap.CompressFormat.JPEG, 100, baosP2);
//            byte[] bp2 = baosP2.toByteArray();
//            String encodedImageP2 = Base64.encodeToString(bp2, Base64.DEFAULT);
//
            // Part3

            // (0, (image.size.height) * 1/2, (image.size.width) * 1/2, (image.size.height) * 1/2));

//            Bitmap croppedBmp3 = Bitmap.createBitmap(bitmapOrg, 0, bitmapOrg.getHeight() * 1/2, bitmapOrg.getWidth()*1/2 , bitmapOrg.getHeight()*1/2);
//
//            ByteArrayOutputStream baosP3 = new ByteArrayOutputStream();
//            croppedBmp3.compress(Bitmap.CompressFormat.JPEG, 100, baosP3);
//            byte[] bp3 = baosP3.toByteArray();
//            String encodedImagep3 = Base64.encodeToString(bp3, Base64.DEFAULT);

            // Part4

            // ((image.size.width) * 1/2, (image.size.height) * 1/2, image.size.width/ 2.0, image.size.height));

//            Bitmap croppedBmp4 = Bitmap.createBitmap(bitmapOrg, bitmapOrg.getWidth() * 1/2, bitmapOrg.getHeight() * 1/2, bitmapOrg.getWidth()/2 , bitmapOrg.getHeight()/2);
//
//            ByteArrayOutputStream baosP4 = new ByteArrayOutputStream();
//            croppedBmp4.compress(Bitmap.CompressFormat.JPEG, 100, baosP4);
//            byte[] bp4 = baosP4.toByteArray();
//            String encodedImagep4 = Base64.encodeToString(bp4, Base64.DEFAULT);

//=============================================================================================================================================
            System.out.println("bitmap :" + bitmapOrg.getWidth());

//            Bitmap realImage = BitmapFactory.decodeStream(stream);

//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] b = baos.toByteArray();
//            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

//            textEncode.setText(encodedImage);

//            System.out.println("test2 ;" + encodedImage.toString());

            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor edit=shre.edit();
            edit.putString("image_datap1",encodedImagep1);
            edit.putString("image_datap2",encodedImageP2);
//            edit.putString("image_datap3",encodedImagep3);
//            edit.putString("image_datap4",encodedImagep4);
            edit.commit();

//            imageView1.setImageBitmap(croppedBmp);
//            int h = bitmapOrg.getHeight();
//            canvas.drawBitmap(bitmapOrg, 10, 10, paint);
//            canvas.drawBitmap(croppedBmp, 0, 0 + h + 10, paint);

            Intent in = new Intent(getContext(), ViewCropImageSlice.class);
            getContext().startActivity(in);

        }

    }

}
