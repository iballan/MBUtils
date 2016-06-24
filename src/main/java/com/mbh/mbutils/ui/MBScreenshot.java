package com.mbh.mbutils.ui;

import android.graphics.Bitmap;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created By MBH on 2016-06-23.
 */
public class MBScreenshot {


    public static Bitmap takeScreenShot(View u){ //}, NestedScrollView z) {
        u.setDrawingCacheEnabled(true);
//        int totalHeight = z.getChildAt(0).getHeight();
//        int totalWidth = z.getChildAt(0).getWidth();
//        Log.d("yoheight", "" + totalHeight);
//        Log.d("yowidth", "" + totalWidth);
        u.layout(0, 0, u.getWidth(), u.getHeight());
        u.buildDrawingCache();
        Bitmap b = Bitmap.createBitmap(u.getDrawingCache());
        u.setDrawingCacheEnabled(false);
        u.destroyDrawingCache();
        return b;
    }

    public static void SendScreenShot(String serverIP, int port, File file) {
        try {
            Socket client = new Socket(serverIP, port);
            OutputStream outputStream = client.getOutputStream();
            byte[] buffer = new byte[1024];
            FileInputStream in = new FileInputStream(file);
            int rByte;
            while((rByte = in.read(buffer, 0, 1024))!= -1){
                outputStream.write(buffer, 0, rByte);
            }
            outputStream.flush();
            outputStream.close();
            client.close();
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void TakeScreenShotThenSaveAndSend(View view, String dir, String name,
                                                     String serverIP, int port){
        Bitmap bitmapScreenShot = takeScreenShot(view);
        File file = MBFileUtils.SaveBitmapToFile(bitmapScreenShot, dir, name);
        SendScreenShot(serverIP, port, file);
    }
}
