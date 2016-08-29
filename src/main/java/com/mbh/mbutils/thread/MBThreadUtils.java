package com.mbh.mbutils.thread;

import com.mbh.mbutils.root.MBRootUtils;

/**
 * Created By MBH on 2016-06-09.
 */
public class MBThreadUtils {
    public static void TryToSleepFor(long millis){
        if(millis>0){
            try {Thread.sleep(millis);} catch (InterruptedException e)
            {e.printStackTrace();}
        }
    }

    /**
     * Fire and forget for trivial tasks
     * @param runnable: the runnable that will run in background
     */
    public static void DoOnBackground(Runnable runnable){
        if(runnable==null) return;
        new Thread(runnable).start();
    }

    public static void TryToRebootTablet(){
        MBRootUtils.sudo(new String[]{"-c", "reboot"});
//        try {
//            Process proc = Runtime.getRuntime().exec();
//            proc.waitFor();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
