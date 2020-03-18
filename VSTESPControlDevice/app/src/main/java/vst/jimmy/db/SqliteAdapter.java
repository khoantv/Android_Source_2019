package vst.jimmy.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tieub on 30/10/2017.
 */

public class SqliteAdapter extends SQLiteOpenHelper {

    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DevicesManager";
    private static final String TABLE_DEVICES = "Device";
    private static final String TABLE_FLOOR = "Floor";

    private static final String CREATE_TABLE_DEVICES = "CREATE TABLE [Device] (\n" +
            "[DeviceId] VARCHAR(2)  UNIQUE NOT NULL PRIMARY KEY,\n" +
            "[DeviceName] NVARCHAR(250)  NULL,\n" +
            "[DeviceAddress] VARCHAR(8)  NULL\n" +
            ")";

    private static final String CREATE_TABLE_FLOOR = "CREATE TABLE [Floor] (\n" +
            "[FloorId] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "[FloorName] NVARCHAR(200)  NULL,\n" +
            "[FloorIp] VARCHAR(20)  NULL,\n" +
            "[FloorPort] INTEGER  NULL,\n" +
//            "[PassWord] VARCHAR(200)  NULL\n" +
            ")";

    public SqliteAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_DEVICES);
        sqLiteDatabase.execSQL(CREATE_TABLE_FLOOR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOOR);
        onCreate(sqLiteDatabase);
    }
}
