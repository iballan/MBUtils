package com.mbh.mbutils.firewall;

import android.content.pm.ApplicationInfo;

/**
 * Created by MBH on 11/16/2015.
 */
public class MBFireWallApp {
    /**
     * linux user id
     */
    int uid;
    /**
     * application names belonging to this user id
     */
    String names[];
    /**
     * indicates if this application is selected for wifi
     */
    boolean selected_wifi;
    /**
     * indicates if this application is selected for 3g
     */
    boolean selected_3g;
    /**
     * toString cache
     */
    String tostr;
    /**
     * application info
     */
    ApplicationInfo appinfo;
    /**
     * cached application icon
     */
    String packagename;
    /**
     * cached application icon
     */
//    Drawable cached_icon;
    /**
     * indicates if the icon has been loaded already
     */
//    boolean icon_loaded;
    /**
     * first time seem?
     */
    boolean firstseem;

    public MBFireWallApp() {
    }

    public MBFireWallApp(int uid, String name, boolean selected_wifi, boolean selected_3g) {
        this.uid = uid;
        this.names = new String[]{name};
        this.selected_wifi = selected_wifi;
        this.selected_3g = selected_3g;
    }

    /**
     * Screen representation of this application
     */
    @Override
    public String toString() {
        if (tostr == null) {
            final StringBuilder s = new StringBuilder();
            if (uid > 0) s.append(uid + ": ");
            for (int i = 0; i < names.length; i++) {
                if (i != 0) s.append(", ");
                s.append(names[i]);
            }
            s.append("\n");
            tostr = s.toString();
        }
        return tostr;
    }
}
