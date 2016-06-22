package com.mbh.mbutils.db.sharedprefs;

/**
 * Created By MBH on 2016-06-21.
 */
public interface IPreferences {
    boolean contains(String key);

    int getInt(String key, int defValue);
    String getString(String key, String defValue);
    boolean getBoolean(String key, boolean defValue);

    void putInt(String key, int value);
    void putBoolean(String key, boolean value);
    void putString(String key, String value);

    // easiness of use
    void put(String key, String value);
    void put(String key, int value);
    void put(String key, boolean value);
}
