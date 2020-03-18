package mobi.ttg.arduinowificontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by duong on 2/15/2016.
 */
public class Database extends SQLiteOpenHelper {

    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "devicesManager";

    private static final String TABLE_DEVICES = "devices";
    private static final String KEY_ID = "id";
    private static final String KEY_DEVICE_NAME = "deviceName";
    private static final String KEY_DEVICE_DES = "deviceDes";
    private static final String KEY_DEVICE_COMAND = "deviceComand";
    private static final String KEY_DEVICE_TURN_ON = "isTurnOn";

    private static final String CREATE_TABLE_DEVICES = "CREATE TABLE "
            + TABLE_DEVICES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DEVICE_NAME
            + " TEXT," + KEY_DEVICE_DES + " TEXT," + KEY_DEVICE_COMAND + " TEXT," + KEY_DEVICE_TURN_ON
            + " INTEGER" + ")";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public long addDevice(DeviceItem deviceItem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DEVICE_NAME, deviceItem.getName());
        values.put(KEY_DEVICE_DES, deviceItem.getDes());
        values.put(KEY_DEVICE_COMAND, deviceItem.getCommand());
        values.put(KEY_DEVICE_TURN_ON, deviceItem.isOn());
        return db.insert(TABLE_DEVICES, null, values);
    }
    public ArrayList<DeviceItem> getAllDevices(){
        ArrayList<DeviceItem> listDeviceItem = new ArrayList<DeviceItem>();
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                DeviceItem deviceItem = new DeviceItem();
                deviceItem.setId(c.getLong((c.getColumnIndex(KEY_ID))));
                deviceItem.setName(c.getString((c.getColumnIndex(KEY_DEVICE_NAME))));
                deviceItem.setDes(c.getString((c.getColumnIndex(KEY_DEVICE_DES))));
                deviceItem.setCommand(c.getString((c.getColumnIndex(KEY_DEVICE_COMAND))));
                deviceItem.setOn((c.getInt(c.getColumnIndex(KEY_DEVICE_TURN_ON))==1) ? true: false);
                listDeviceItem.add(deviceItem);
            } while (c.moveToNext());
        }
        return  listDeviceItem;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DEVICES);
        for(int i=1; i<9; i++){
            String sql = "INSERT INTO "+TABLE_DEVICES+" ("+KEY_DEVICE_NAME+", "+KEY_DEVICE_DES+", "+KEY_DEVICE_COMAND+", "+KEY_DEVICE_TURN_ON+") VALUES ('DEVICE "+i+"', 'Device "+i+"', '"+i+"', 0);";
            db.execSQL(sql);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICES);
        onCreate(db);
    }
}
