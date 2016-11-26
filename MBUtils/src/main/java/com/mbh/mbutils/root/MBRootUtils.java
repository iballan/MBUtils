package com.mbh.mbutils.root;

import com.mbh.mbutils.db.MBFileUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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

                Runtime.getRuntime().exec(
                        new String[]{
                                "su",
                                "-c",
                                "pm install -r " + absolutePath});
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }
}
