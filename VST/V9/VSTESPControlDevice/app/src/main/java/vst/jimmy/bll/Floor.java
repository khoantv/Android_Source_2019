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
 * Created by tieub on 30/11/2017.
 */

public class Floor implements Serializable {

    private static final String TABLE_NAME = "Floor";

    private static final String ID = "FloorId";
    private static final String NAME = "FloorName";
    private static final String IP = "FloorIp";
    private static final String PORT = "FloorPort";
//    private static final String PASSWORD = "PassWord";

    //region -- Variables --
    private String floorId;
    private String floorName;
    private String floorIp;
    private String floorPort;
    private String passWord;

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getID() {
        return ID;
    }

    public static String getNAME() {
        return NAME;
    }

    public static String getIP() {
        return IP;
    }

    public static String getPORT() {
        return PORT;
    }

//    public static String getPASSWORD() {
//        return PASSWORD;
//    }

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getFloorIp() {
        return floorIp;
    }

    public void setFloorIp(String floorIp) {
        this.floorIp = floorIp;
    }

    public String getFloorPort() {
        return floorPort;
    }

    public void setFloorPort(String floorPort) {
        this.floorPort = floorPort;
    }

//    public String getPassWord() {
//        return passWord;
//    }
//
//    public void setPassWord(String passWord) {
//        this.passWord = passWord;
//    }
    //endregion

    public static List<Floor> getListFloors(Context context)
    {
        // Khai báo một mảng động
        List<Floor> list = new ArrayList<Floor>();

        try
        {
            SqliteAdapter adapter = new SqliteAdapter(context);

            SQLiteDatabase db = adapter.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from "+ TABLE_NAME, null);
            if (cursor.moveToFirst())
            {
                do {
                    Floor item = new Floor();
                    item.setFloorId(cursor.getString(cursor.getColumnIndex(ID)));
                    item.setFloorName(cursor.getString(cursor.getColumnIndex(NAME)));
                    item.setFloorIp(cursor.getString(cursor.getColumnIndex(IP)));
                    item.setFloorPort(cursor.getString(cursor.getColumnIndex(PORT)));
//                    item.setPassWord(cursor.getString(cursor.getColumnIndex(PASSWORD)));
                    list.add(item);

                }while (cursor.moveToNext());
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(context, "Cannot get list floors.", Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    public static boolean insert(Context context, Floor floor){
        SqliteAdapter adapter = new SqliteAdapter(context);
        SQLiteDatabase db = adapter.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( ID, floor.getFloorId());
        values.put( NAME, floor.getFloorName());
        values.put( IP, floor.getFloorIp());
        values.put( PORT, floor.getFloorPort());
//        values.put( PASSWORD, floor.getPassWord());

        if(db.insert(TABLE_NAME, null, values) != -1)
            return true;
        else {
            return false;
        }
    }
    public static boolean update(Context context, String oldId, Floor floor){
        SqliteAdapter adapter = new SqliteAdapter(context);
        SQLiteDatabase db = adapter.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put( ID, floor.getFloorId());
        values.put( NAME, floor.getFloorName());
        values.put( IP, floor.getFloorIp());
        values.put( PORT, floor.getFloorPort());
//        values.put( PASSWORD, floor.getPassWord());

        long result= db.update(TABLE_NAME , values, ID + " = ?"  , new String[]{String.valueOf(oldId)});
        if(result == -1){
            return false ;
        }else {
            return true;
        }

    }
    public static boolean delete(Context context, String id){
        SqliteAdapter adapter = new SqliteAdapter(context);
        SQLiteDatabase db = adapter.getWritableDatabase();

        long result= db.delete(TABLE_NAME,ID + " = ?" ,new String[]{String.valueOf(id)});
        if(result == -1){
            return false ;
        }else {
            return true;
        }
    }
    public static boolean deleteAll(Context context){
        SqliteAdapter adapter = new SqliteAdapter(context);
        SQLiteDatabase db = adapter.getWritableDatabase();

        long result= db.delete(TABLE_NAME,null ,null);
        if(result == -1){
            return false ;
        }else {
            return true;
        }
    }
}
