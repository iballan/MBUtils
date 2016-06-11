package com.mbh.mbutils.network;

/**
 * CreatedBy MBH on 2016-06-09.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mballan on 16.09.2015.
 */

public class NetworkUtils {

    public static boolean IsNetworkConnected(Context context) {
        if (context == null)
            return false;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return IsNetworkConnected(cm);
    }

    public static boolean IsNetworkConnected(ConnectivityManager cm) {
        if (cm == null)
            return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }

    public static String GenerateRandomMacAddressStartsWith00() {
        String[] Mac = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        Random rd = new Random();
        rd.nextInt(15);
        String result = "00:";

        for (int i = 0; i < 6; i++) {
            String a = Mac[rd.nextInt(15)];
            String b = Mac[rd.nextInt(15)];
            result += a + b;
            if (i < 4) {
                result += ":";
            }
        }
        return result;
    }

    public static String GenerateRandomMacAddress() {
        String[] Mac = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        Random rd = new Random();
        rd.nextInt(15);
        String result = "";

        for (int i = 0; i < 6; i++) {
            String a = Mac[rd.nextInt(15)];
            String b = Mac[rd.nextInt(15)];
            result += a + b;
            if (i < 5) {
                result += ":";
            }
        }
        return result;
    }

    public static boolean IsWiFiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    public static boolean IsETHConnected(Context context) {
        try {
            if (context == null)
                return false;
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo etherNet = connManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
            if (etherNet == null)
                return false;
            return etherNet.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean SetRandomMacAddress() {
        String macAddress = GenerateRandomMacAddress();
        String[] command = {"ip link set eth0 down",
                "ip link set eth0 address " + macAddress + "",
                "ip link set eth0 up"};
        Process p;
        try {
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            for (String tmpCmd : command) {
                os.writeBytes(tmpCmd + " \n ");
            }
            os.writeBytes("exit \n ");
            os.flush();
            p.waitFor();
            return true;
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void SetMacAddressIfLanConnected(Context context, final String newMacAddress,
                                                   final OnChangeCompleted onCompleted) {
        if (NetworkUtils.IsETHConnected(context)) {
            SetMacAddress(newMacAddress, onCompleted);
        } else {
            if (onCompleted != null)
                onCompleted.onFail("Lan is not connected");
        }
    }

    public static void SetMacAddress(final String macAddress, final OnChangeCompleted onCompleted) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int successTimes = 0;
                int retryCount = 10; // retry 15 times
                int i = 0;
                while (i < retryCount) {
                    try {
                        if (SetMacAddress(macAddress)) {
                            successTimes++;
//                            if(successTimes > 2) {
                            if (onCompleted != null) {
                                onCompleted.onSuccess(macAddress);
                            }
                            return;
//                            }
                        }
                        Thread.sleep(150);
                    } catch (Exception e) {
                    }
                    i++;
                }
                if (onCompleted != null)
                    onCompleted.onFail("Could not change Mac Address in 15 times");
            }
        }).start();
    }

    public static boolean SetMacAddress(String macAddress) {
//		MacAddress = GenerateRandomMacAddress();
        String[] command = {"ip link set eth0 down",
                "ip link set eth0 address " + macAddress + "",
                "ip link set eth0 up"};
        Process p;
        try {
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            for (String tmpCmd : command) {
                os.writeBytes(tmpCmd + " \n ");
            }
            os.writeBytes("exit \n ");
            os.flush();
            p.waitFor();
            return true;
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean ValidateMacAddress(String macAddress) {

        Pattern p = Pattern.compile("^([a-fA-F0-9][:-]){5}[a-fA-F0-9][:-]$");
        Matcher m = p.matcher(macAddress);
        return m.find();

    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("EMSEM", ex.toString());
        }
        return null;
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    public static String getEth0MACAddress() {
        return getMACAddress("eth0");
    }

    public static String getWlanMACAddress() {
        return getMACAddress("wlan0");
    }


    public static String getNetmask() {
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);

            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                short prefix = address.getNetworkPrefixLength();
                if (prefix == 8) {
                    return "255.0.0.0";
                }
                if (prefix == 16) {
                    return "255.255.0.0";
                }
                if (prefix == 24) {
                    return "255.255.255.0";
                }
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
    public static void OpenEthernetInterface(){

        try {

//            if(isEthOn()){
//                Runtime.getRuntime().exec("ifconfig eth0 down");
//
//            }
//            else{
//                Runtime.getRuntime().exec("ifconfig eth0 up");
//            }
            Runtime.getRuntime().exec("ifconfig eth0 up");

        } catch (IOException e) {
            Log.e("OLE","Runtime Error: "+e.getMessage());
            e.printStackTrace();
        }
//        String [] command = new String[]{"ip link set eth0 up"};
//        Process p;
//        try {
//            p = Runtime.getRuntime().exec("su");
//            DataOutputStream os = new DataOutputStream(p.getOutputStream());
//            for (String tmpCmd : command) {
//                os.writeBytes(tmpCmd + " \n ");
//            }
//            os.writeBytes("exit \n ");
//            os.flush();
//            p.waitFor();
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
    }
    public static abstract class OnChangeCompleted {
        public abstract void onSuccess(String macAddress);

        public void onFail(String reason) {
        }
    }
//    public static void OpenWifiInterface(Context context, boolean open) {
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        wifiManager.setWifiEnabled(open);
//    }
}
