package mobi.ttg.arduinowificontrol;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by duong on 2/14/2016.
 */
public class Settings {
    public static final String IP_ADDRESS = "ip_address";
    public static final String PORT = "port";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String SETTING_FILE = "setting_values";

    public Settings(Context context){
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
