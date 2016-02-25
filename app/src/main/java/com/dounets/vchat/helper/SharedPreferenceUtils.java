package com.dounets.vchat.helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dounets.vchat.App;

public class SharedPreferenceUtils {

    public static void saveString(String key,String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(key, value);
        prefEditor.commit();

    }
    public static String getString(String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        String content = pref.getString(key, null);

        return content;
    }
    
    public static void deleteString(String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.remove(key);
        prefEditor.commit();
    }

    public static void saveInt(String key,int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putInt(key, value);
        prefEditor.commit();

    }
    public static int getInt(String key, int defaultValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        int content = pref.getInt(key,defaultValue);

        return content;
    }

    public static void deleteInt(String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.remove(key);
        prefEditor.commit();
    }

}
