package com.prolifixs.pixelstream.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Prolifixs on 2/8/2018.
 */

//responsible for conversion of images-to-bitmap, bitmap-to-bytes, and so on..
    //methods used heavily on "FirebaseStorageMethods.class in global utils section"

public class ImageManager {
    private static final String TAG = "ImageManager";

    //this method takes the chosen image by the user from phone memory and converts to a bitmap form
    public static Bitmap getBitMap(String imgUrl){
        File imageFile = new File(imgUrl);
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try{
            fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);

        }catch (FileNotFoundException e){
            Log.e(TAG, "getBitMap: FileNotFoundException: " + e.getMessage() );

        }finally {
            try {
                fis.close();
            }catch (IOException e){
                Log.e(TAG, "getBitMap: FileNotFoundException: " + e.getMessage() );

            }
        }return bitmap;
    }

    //convert from bitmap to byte, image quality
      //image quality : quality is greater than zero but less than 100 (Completely adjustable)
    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, stream);
        return stream.toByteArray();
    }
}
