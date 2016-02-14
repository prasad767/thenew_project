package com.visa.vts.certificationapp.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Chandrakanta_Sahoo on 11/25/2015.
 */
public class Preference {
    private Context context;
    private static Preference preference;


    private Preference(Context context){
        this.context = context;
    }


    public static Preference getInstance(Context context){
        if(preference==null) {
            preference = new Preference(context);
        }
        return preference;
    }

    public static Preference getInstance(){
        return preference;
    }

    public boolean saveString(String key, String value) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String retrieveString(String key) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public boolean saveBoolean(String key, boolean value) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean retrieveBoolean(String key) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(key, true);
    }

}
