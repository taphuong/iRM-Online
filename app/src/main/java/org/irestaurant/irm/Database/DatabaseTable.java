package org.irestaurant.irm.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseTable extends SQLiteOpenHelper {

    public DatabaseTable(Context context) {
        super(context, "Number.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table number(id integer primary key autoincrement, number text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists number");
    }

    public boolean creat (Number number){
        boolean result =true;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("number",number.getNumber());
            long ins = db.insert("number",null,contentValues);
            result = ins>0;
        }catch (Exception e){
            result = false;
        }
        return result;
    }
    public void addTable(Number number){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number.getNumber());
        //Neu de null thi khi value bang null thi loi
        db.insert("number",null,values);
        db.close();
    }
}
