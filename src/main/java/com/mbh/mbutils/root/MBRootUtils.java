package com.mbh.mbutils.root;

import com.mbh.mbutils.db.MBFileUtils;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created By MBH on 2016-06-09.
 */
public class MBRootUtils {

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


    public static boolean installApkFromPath(String absolutePath) {
        if (absolutePath != null && !absolutePath.isEmpty() &&
                MBFileUtils.FileExists(absolutePath)) {
            try {
                final String command = "pm install -r " + absolutePath;
                Process proc = Runtime.getRuntime().exec(new String[]{
                        "su",
                        "-c",
                        command});
                proc.waitFor();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
