package com.mbh.mbutils.ui;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.widget.Toast;


/**
 * Created By MBH on 2016-06-11.
 */
public class MBToaster {
    Handler uiHandler;
    Context context;

    public MBToaster(Context context, Handler uiHandler) {
        if(context == null)
            throw new RuntimeException("MBToaster Constructor: Context cannot be null");
        if(uiHandler == null)
            throw new RuntimeException("MBToaster Constructor: UIHandler cannot be null");
        this.uiHandler = uiHandler;
        this.context = context;
    }

    public void shortToast(String string){
        showToast(string, true);
    }

    public void longToast(String string){
        showToast(string, false);
    }

    public void shortToast(@StringRes int strRes){
        showToast(getStringFromContext(strRes), true);
    }

    public void longToast(@StringRes int strRes){
        showToast(getStringFromContext(strRes), false);
    }

    public String getStringFromContext(@StringRes int strRes){
        return context.getString(strRes);
    }

    private void showToast(final String string, final boolean isShort){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, string,
                        isShort?Toast.LENGTH_SHORT:Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}
