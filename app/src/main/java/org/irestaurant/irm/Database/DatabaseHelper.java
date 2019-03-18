package org.irestaurant.irm.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static String NAME = "name",PHONE = "phone", PASSWORD = "password", RESNAME = "resname", RESPHONE = "resphone", RESADDRESS = "resaddress";
    public static int REGISTED = 0;


    public DatabaseHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table user(id integer primary key autoincrement,name text, phone text, password text, resname text, resphone text, resaddress text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
    }



    public Boolean chkphone (String phone){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from user where phone = ?", new String[]{phone});
        if (cursor.getCount()>0) return false;
        else return true;
    }

    public User finduser (int id){
        User user = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("Select * from user where id = ? ", new String[]{String.valueOf(id)});
            if (cursor.moveToFirst()){
                user = new User();
                user.setId(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setPhone(cursor.getString(2));
                user.setPassword(cursor.getString(3));
            }
        }catch (Exception e){
            user = null;
        }
        return user;
    }

    public boolean creat (User user){
        boolean result =true;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME,user.getName());
            contentValues.put(PHONE,user.getPhone());
            contentValues.put(PASSWORD,user.getPassword());
            contentValues.put(RESNAME, user.getResname());
            contentValues.put(RESPHONE, user.getResphone());
            contentValues.put(RESADDRESS, user.getResaddress());
            long ins = db.insert("user",null,contentValues);
            result = ins>0;
        }catch (Exception e){
            result = false;
        }
        return result;
    }


    public int updateUser (User user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME,user.getName());
        contentValues.put(PHONE,user.getPhone());
        contentValues.put(PASSWORD,user.getPassword());
        contentValues.put(RESNAME, user.getResname());
        contentValues.put(RESPHONE, user.getResphone());
        contentValues.put(RESADDRESS, user.getResaddress());
        return db.update("user",contentValues,"id = ?", new String[]{String.valueOf(user.getId())});
    }

    public User userLogin(String phone, String password){
        User user = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("Select * from user where phone = ? and password = ?", new String[]{phone,password});
            if (cursor.moveToFirst()){
                user = new User();
                user.setId(cursor.getInt(0));
                user.setName(cursor.getString(1));
                user.setPhone(cursor.getString(2));
                user.setPassword(cursor.getString(3));
                user.setResname(cursor.getString(4));
                user.setResphone(cursor.getString(5));
                user.setResaddress(cursor.getString(6));
            }
        }catch (Exception e){
            user = null;
        }
        return user;
    }
}
