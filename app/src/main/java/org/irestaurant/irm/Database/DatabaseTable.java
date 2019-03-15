package org.irestaurant.irm.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTable extends SQLiteOpenHelper {

    public DatabaseTable(Context context) {
        super(context, "Number.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table number(id integer primary key autoincrement, number text, status text)");
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
            contentValues.put("status",number.getStatus());
            long ins = db.insert("number",null,contentValues);
            result = ins>0;
        }catch (Exception e){
            result = false;
        }
        return result;
    }

    //    Lấy dữ liêu
    public List<Number> getallTable(){
        List<Number> ListNumber = new ArrayList<>();
        String selectQuery = "SELECT * FROM number" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                Number number = new Number();
                number.setId(cursor.getInt(0));
                number.setNumber(cursor.getString(1));
                ListNumber.add(number);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ListNumber;
    }

    public int deleteTable (int number){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("number","number = ?",new String[]{String.valueOf(number)});
    }
}
