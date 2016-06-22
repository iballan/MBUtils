package com.mbh.mbutils.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created By MBH on 2016-06-15.
 */
public class MBuiUtils {

    public static Bitmap Base64ToBitmap(String base64Pic){
        try {
            byte[] imageAsBytes = Base64.decode(base64Pic, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(
                    imageAsBytes,
                    0,
                    imageAsBytes.length
            );
        } catch (Exception exc){
            exc.printStackTrace();
            return null;
        }
    }
}
