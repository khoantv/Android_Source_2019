package kmt.jimmy.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tieub on 30/10/2017.
 */

public class SharepredPreferencesCls {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String SETTING_FILE = "setting_values";

    public SharepredPreferencesCls(Context context){
        this.sharedPref = context.getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
        this.editor = sharedPref.edit();
    }
    public void putString(String key, String value){
        this.editor.putString(key,value);
        this.editor.commit();
    }
    public String getString(String key){
        return this.sharedPref.getString(key,null);
    }

    public void putInt(String key, int value){
        this.editor.putInt(key,value);
        this.editor.commit();
    }
    public int getInt(String key){
        return this.sharedPref.getInt(key,-1);
    }
}
