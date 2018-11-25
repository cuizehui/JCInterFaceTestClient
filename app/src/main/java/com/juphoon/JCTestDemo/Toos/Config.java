package com.juphoon.JCTestDemo.Toos;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Config {

    public final static String LAST_CONNECT_IP = "last_connect_ip";

    public static void setLastConnectIp(Context context, String ip) {
        saveData(context, LAST_CONNECT_IP, ip);
    }

    public static String getLastConnectIp(Context context) {
        return (String) getData(context, LAST_CONNECT_IP, "");
    }

    private static void saveData(Context context, String key, Object data) {
        String type = data.getClass().getSimpleName();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) data);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) data);
        } else if ("String".equals(type)) {
            editor.putString(key, (String) data);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) data);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) data);
        }
        editor.commit();
    }

    private static Object getData(Context context, String key, Object defValue) {
        String type = defValue.getClass().getSimpleName();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if ("Integer".equals(type)) {
            return sharedPreferences.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return sharedPreferences.getBoolean(key, (Boolean) defValue);
        } else if ("String".equals(type)) {
            return sharedPreferences.getString(key, (String) defValue);
        } else if ("Float".equals(type)) {
            return sharedPreferences.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return sharedPreferences.getLong(key, (Long) defValue);
        }
        return null;
    }
}

