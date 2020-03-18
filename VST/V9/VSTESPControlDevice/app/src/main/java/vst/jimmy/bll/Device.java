package vst.jimmy.bll;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import vst.jimmy.db.SqliteAdapter;

/**
 * Created by tieub on 01/11/2017.
 */

public class Device implements Serializable {

    private static final String TABLE_NAME = "Device";

    private static final String ID = "DeviceId";
    private static final String NAME = "DeviceName";
    private static final String ADDRESS = "DeviceAddress";
    private static final String TYPE = "DeviceType";

    //region -- Variables --
    private String deviceId;
    private String deviceName;
    private String deviceAddress;
    private String deviceType;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

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
                    item.setDeviceType(cursor.getString(cursor.getColumnIndex(TYPE)));
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

    public static boolean insert(Context context, Device device){
        try {
            SqliteAdapter adapter = new SqliteAdapter(context);
            SQLiteDatabase db = adapter.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put( ID,device.getDeviceId());
            values.put( NAME,device.getDeviceName());
            values.put( ADDRESS, device.getAddress());
            values.put( TYPE, device.getDeviceType());

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
    public static boolean update(Context context, String oldId, Device device){
        try {
            SqliteAdapter adapter = new SqliteAdapter(context);
            SQLiteDatabase db = adapter.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ID, device.getDeviceId());
            values.put(NAME, device.getDeviceName());
            values.put( ADDRESS, device.getAddress());
            values.put( TYPE, device.getDeviceType());

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
    public static boolean delete(Context context, String id){
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
