package org.irestaurant.irm.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseFinger extends SQLiteOpenHelper {
    public DatabaseFinger(Context context) {
        super(context, "Finger.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table finger(id integer primary key autoincrement, phone text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists finger");
    }
    //    Thêm dữ liêu
    public boolean creat (Finger finger){
        boolean result =true;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("phone",finger.getPhone());
            long ins = db.insert("finger",null,contentValues);
            result = ins>0;
        }catch (Exception e){
            result = false;
        }
        return result;
    }

    public Boolean chkphone (){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from finger", new String[]{""});
        if (cursor.getCount()>0) return true;
        else return false;
    }

    //    Lấy dữ liêu
    public Finger Phone(){
        Finger finger = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("Select * from finger", new String[]{""});
            if (cursor.moveToFirst()){
                finger = new Finger();
                finger.setId(cursor.getInt(0));
                finger.setPhone(cursor.getString(1));
            }
        }catch (Exception e){
            finger = null;
        }
        return finger;
    }
    //    Xóa dữ liệu
    public void deleteFinger (){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from finger");
    }
}
