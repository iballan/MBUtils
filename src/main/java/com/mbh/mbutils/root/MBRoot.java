package com.mbh.mbutils.root;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created By MBH on 2016-06-09.
 */
public class MBRoot {

    public static boolean sudo(String[] strings) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            for (String s : strings) {
                outputStream.writeBytes(s + "\n");
                outputStream.flush();
            }
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
