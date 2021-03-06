package com.mbh.mbutils.db.sharedprefs;

import com.mbh.mbutils.db.MBFileUtils;
import com.mbh.mbutils.thread.MBThreadUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created By MBH on 2016-06-21.
 */
public class MBJsonSharedPrefs implements IPreferences {

    private String filePath;
    private JSONObject mJSONObject;

    public MBJsonSharedPrefs(String filePath){
        this.filePath = filePath;
        try {
            if(!MBFileUtils.FileExists(filePath)){
                // create empty json object for first time implementation
                MBFileUtils.CreateFile(filePath, "{}");
            }
            String json = MBFileUtils.ReadFile(filePath);
            mJSONObject = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean contains(String key) {
        if(mJSONObject== null) return false;
        return mJSONObject.has(key);
    }

    @Override
    public int getInt(String key, int defValue) {
        return tryParseInt(getContentByKey(key), defValue);
    }

    private int tryParseInt(String strVal, int defValue){
        if(strVal == null) return defValue;
        try{return Integer.parseInt(strVal);}
        catch (Exception e){e.printStackTrace(); return defValue;}
    }

    @Override
    public String getString(String key, String defValue) {
        String value = getContentByKey(key);
        return value==null?defValue:value;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        String value = getContentByKey(key);
        return value==null?defValue:value.equals("t");
    }

    @Override
    public void putInt(String key, int value) {
        putContentByKey(key, value+"");
    }

    @Override
    public void put(String key, int value) {
        putInt(key, value);
    }

    @Override
    public void putString(String key, String value) {
        putContentByKey(key, value);
    }

    public void put(Map<String, Object> objectMap){
        for (Map.Entry<String, Object> entry:
        objectMap.entrySet()){
            if(entry.getKey() == null)
                continue;
            Object obj = entry.getValue();
            putContentByKey(entry.getKey(), obj!=null?obj.toString():"");
        }
    }

    @Override
    public void put(String key, String value) {
        putString(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        putContentByKey(key, value?"t":"f");
    }

    @Override
    public void put(String key, boolean value) {
        putBoolean(key, value);
    }

    public void commit() {
        if(mJSONObject == null) return;
        try {
            MBFileUtils.WriteToFile(filePath, mJSONObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void apply() {
        MBThreadUtils.DoOnBackground(new Runnable() {
            @Override
            public void run() {
                commit();
            }
        });
    }

    private String getContentByKey(String key) {
        if(mJSONObject == null) return null;
        if(!contains(key)) return null;
        try {
            return (String)mJSONObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void putContentByKey(String key, String content) {
        if(mJSONObject == null) return;
        try {
            // Put add new if not exists, updates otherwise
            mJSONObject.put(key, content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
