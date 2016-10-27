package com.mbh.mbutils.ui;

import android.os.Build;

import com.mbh.mbutils.thread.MBThreadUtils;

/**
 * Created By MBH on 2016-06-09.
 */
public class MBTaskbarUtils {

    public static void removeTaskBarAsync() {
        MBThreadUtils.DoOnBackground(new Runnable() {
            @Override
            public void run() {
                removeTaskBar();
            }
        });
    }
    public static boolean removeTaskBar() {
        try {
            // REQUIRES ROOT
            String ProcID = "79"; // HONEYCOMB AND OLDER

            // v.RELEASE //4.0.3
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                ProcID = "42"; // ICS AND NEWER
            }

            // REQUIRES ROOT
            Process proc = Runtime.getRuntime().exec(
                    new String[]{
                            "su",
                            "-c",
                            "service call activity " + ProcID
                                    + " s16 com.android.systemui"}); // WAS 79
            proc.waitFor();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean addTaskBar() {
        // oncreate_addTaskBar
        try {
            // REQUIRES ROOT
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79"; // HONEYCOMB AND OLDER

            // v.RELEASE //4.0.3
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                ProcID = "42"; // ICS AND NEWER
            }

            // REQUIRES ROOT
            Process proc = Runtime
                    .getRuntime()
                    .exec(new String[]{"su", "-c",
                            "am startservice -n com.android.systemui/.SystemUIService"}); // WAS
            // 79
            proc.waitFor();

//            CoreStatic.m_task_bar_active = true;
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void addTaskBarAsync(){
        MBThreadUtils.DoOnBackground(new Runnable() {
            @Override
            public void run() {
                addTaskBar();
            }
        });
    }
}
