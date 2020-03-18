package kmt.jimmy.bll;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kmt.jimmy.db.SqliteAdapter;
import kmt.jimmy.espcontrol.MainActivity;

/**
 * Created by jimmy on 01/11/2017.
 */

public class Device implements Serializable {

    private static final String TABLE_NAME = "Device";

    private static final String ID = "DeviceId";
    private static final String NAME = "DeviceName";
    private static final String ADDRESS = "DeviceAddress";

    //region -- Variables --
    private String deviceId;
    private String deviceName;
    private String deviceAddress;

    public String getAddress() {
        return deviceAddress;
    }

    public void setAddress(String address) {
        this.deviceAddress = address;
    }
    //    private float level;
//    private String code;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    //endregion

    public static List<Device> getListDevices(Context context)
    {
        // Khai báo một mảng động
        List<Device> list = new ArrayList<Device>();
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try
        {
            SqliteAdapter adapter = new SqliteAdapter(context);

            db = adapter.getWritableDatabase();
            cursor = db.rawQuery("select * from "+ TABLE_NAME, null);
            if (cursor.moveToFirst())
            {
                do {
                    Device item = new Device();

                    item.setDeviceId(cursor.getString(cursor.getColumnIndex(ID)));
                    item.setDeviceName(cursor.getString(cursor.getColumnIndex(NAME)));
                    item.setAddress(cursor.getString(cursor.getColumnIndex(ADDRESS)));
                    list.add(item);

                }while (cursor.moveToNext());
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(context, "Cannot get list devices.", Toast.LENGTH_SHORT).show();
        }
        finally {
            cursor.close();
            db.close();
        }
        return list;
    }

    public static boolean insert(MainActivity context, Device device){
        try {
            SqliteAdapter adapter = new SqliteAdapter(context);
            SQLiteDatabase db = adapter.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put( ID,device.getDeviceId());
            values.put( NAME,device.getDeviceName());
            values.put( ADDRESS, device.getAddress());

            if(db.insert(TABLE_NAME, null, values) != -1)
                return true;
            else {
                return false;
            }
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    public static boolean update(MainActivity context, String oldId, Device device){
        try {
            SqliteAdapter adapter = new SqliteAdapter(context);
            SQLiteDatabase db = adapter.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ID, device.getDeviceId());
            values.put(NAME, device.getDeviceName());
            values.put( ADDRESS, device.getAddress());

            long result= db.update(TABLE_NAME , values, ID +" = ?"  , new String[]{String.valueOf(oldId)});
            if(result == -1){
                return false ;
            }else {
                return true;
            }
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    public static boolean delete(MainActivity context, String id){
        try {
            SqliteAdapter adapter = new SqliteAdapter(context);
            SQLiteDatabase db = adapter.getWritableDatabase();

            long result= db.delete(TABLE_NAME,ID + " = ?" ,new String[]{String.valueOf(id)});
            if(result == -1){
                return false ;
            }else {
                return true;
            }
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public static boolean deleteAll(Context context){
        try {
            SqliteAdapter adapter = new SqliteAdapter(context);
            SQLiteDatabase db = adapter.getWritableDatabase();

            long result= db.delete(TABLE_NAME,null ,null);
            if(result == -1){
                return false ;
            }else {
                return true;
            }
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
