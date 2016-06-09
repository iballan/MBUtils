package com.mbh.mbutils.ui.font;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by MBH on 30/07/15.
 */
public final class MBFontOverride {

    public static void changeDefaultFontSet(Context context, String fontAssetName) {
        // Assets
        // this SANS_SERIF should be also added to the app style

        String fontNameWithPath = "fonts/"+ fontAssetName;
        // Changing default fonts with our chosen font
//        setDefaultFont(context, "MONOSPACE", fontNameWithPath);
//        setDefaultFont(context, "SERIF", fontNameWithPath);
        setDefaultFont(context, "SANS_SERIF", fontNameWithPath);
    }

    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}