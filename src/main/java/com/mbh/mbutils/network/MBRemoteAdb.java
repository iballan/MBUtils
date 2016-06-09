package com.mbh.mbutils.network;


import com.mbh.mbutils.BuildConfig;
import com.mbh.mbutils.root.MBRootUtils;

/**
 * Created by mballan on 19.09.2015.
 */
public class MBRemoteAdb {

    /**
     * I have added this in order to open the wireless adb and lan adb to connect remotely and debug
     */
    public static void debugIfConfigDebug() {
        if (BuildConfig.DEBUG) {
            suduOpenADBForNetwork();
        }
    }

    public static boolean suduOpenADBForNetwork() {
        String[] openAdbCommands = new String[]{"setprop service.adb.tcp.port 5555",
                "stop adbd",
                "start adbd"
        };
        return MBRootUtils.sudo(openAdbCommands);
    }

    public static boolean suduCloseADBForNetwork() {
        String[] closeAdbCommands = new String[]{"setprop service.adb.tcp.port -1",
                "stop adbd",
                "start adbd"
        };
        return MBRootUtils.sudo(closeAdbCommands);
    }


}
