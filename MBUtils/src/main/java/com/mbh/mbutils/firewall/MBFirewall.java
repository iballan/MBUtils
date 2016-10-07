package com.mbh.mbutils.firewall;

import android.content.Context;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by MBH on 11/16/2015. Fixing Google service packets problem
 */
public class MBFirewall {
    private final static String TAG = "MBFirewall";
    protected Context context;

    public MBFirewall(Context context) {
        this.context = context;
    }

    public void prepareApplications() {
        if (Api.applications == null)
            Api.getApps(context);
    }

    public void prepareRules() {
        if (Api.applications == null)
            prepareApplications();
        LinkedList<Integer> allowedUids = new LinkedList<>();
        for (int i = 0; i < Api.applications.length; i++) {
            MBFireWallApp app = Api.applications[i];
            if (app.appinfo == null) {
                continue;
            }
            if (app.appinfo.packageName.toLowerCase().contains("emse")) {
                allowedUids.add(app.uid);
                app.selected_3g = true;
                app.selected_wifi = true;
            }
        }

        if (allowedUids.size() == 0) {
            log("UIDs cannot be 0, or there is not emse apps on this tablet");
        }
    }

    public boolean isEnabled() {
        return Api.isEnabled(context);
    }

    public void disable() {
        boolean enabled = isEnabled();
        if (enabled) {
            if (Api.purgeIptables(context, true))
                Api.setEnabled(context, false);
            else {
                log("Could not be disabled!");
            }
        }
    }

    public boolean enable() {
        if (Api.applications == null) {
            prepareApplications();
            prepareRules();
        }
        // Not enabled, enable it now
        Api.setEnabled(context, true);
        if (applyOrSaveRules()) {
            //Api.setEnabled(context, true);
            return true;
        }
        Api.setEnabled(context, false);
        // could not be enabled for some reasons!!!
        log("Could not be enabled");
        return false;
    }

    public void saveRules() {
        Api.saveRules(context);
    }

    /**
     * Purge iptable rules, showing a visual indication
     */
    public boolean purgeRules() {
        if (Api.hasRootAccess(context, true)) {
            if (Api.purgeIptables(context, true)) {
                log("All Rules are Purged");
                return true;
            } else {
                log("Could NOT Purge Rules");
            }
        } else {
            log("NO ROOT ACCESS");
        }
        return false;
    }

    /**
     * Apply or save iptable rules, showing a visual indication
     */
    private boolean applyOrSaveRules() {
        final boolean enabled = Api.isEnabled(context);
        if (enabled) {
            log("Applying rules.");
            if (Api.hasRootAccess(context, true) && Api.applyIptablesRules(context, true)) {
                log("Rules applied");
                return true;
            } else {
                log("Failed - Disabling firewall.");
                Api.setEnabled(context, false);
            }
        } else {
            log("Saving rules.");
            Api.saveRules(context);
        }
        return false;
    }

    /**
     * Clear logs
     */
    public void clearLog() {
        if (!Api.hasRootAccess(context, true)) return;
        if (Api.clearLog(context)) {
            log("Log had been cleared");
        } else {
            log("Log cannot be cleared");
        }
    }

    public String getLog() {
        return Api.getLog(context);
    }

    public String getRules() {
        if (!Api.hasRootAccess(context, true)) return "Not Rooted";
        return Api.getIptablesRules(context);
    }

    public void log(String logMsg) {
        Log.d(TAG, logMsg);
    }
}
