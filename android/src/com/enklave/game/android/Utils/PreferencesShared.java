package com.enklave.game.android.Utils;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferencesShared {
    private final SharedPreferences.Editor editor;
    private final SharedPreferences preferences;
    //private Context context;

    public PreferencesShared(Context con,String name) {
        preferences = con.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    public void putLong(String key,long value){
        editor.putLong(key,value);
    }
    public void putString(String key,String value){
        editor.putString(key, value);
    }
    public boolean commit(){
        return editor.commit();
    }
    public String getString(String key, String value){
        return preferences.getString(key, value);
    }
    public long getLong(String key,long value){
        return preferences.getLong(key, value);
    }
    public boolean contains(String s){
        return preferences.contains(s);
    }
}
