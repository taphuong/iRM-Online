package org.irestaurant.irm.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseOrdered extends SQLiteOpenHelper {
    public DatabaseOrdered(Context context) {
        super(context, "Ordered.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table ordered(id integer primary key autoincrement, number text, foodname text, amount text, status text, date text, price text, total text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists ordered");
    }

//    Thêm dữ liêu
    public boolean creat (Ordered ordered){
        boolean result =true;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("number",ordered.getNumber());
            contentValues.put("foodname",ordered.getFoodname());
            contentValues.put("amount",ordered.getAmount());
            contentValues.put("status",ordered.getStatus());
            contentValues.put("date",ordered.getDate());
            contentValues.put("price",ordered.getPrice());
            contentValues.put("total", ordered.getTotal());
            long ins = db.insert("ordered",null,contentValues);
            result = ins>0;
        }catch (Exception e){
            result = false;
        }
        return result;
    }

    //    Lấy dữ liêu
    public List<Ordered> getallOrdered(String nb){
        List<Ordered> ListOrdered = new ArrayList<>();
        String selectQuery = "SELECT * FROM ordered WHERE number = '"+nb.trim()+"' AND status = 'notyet' " ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                Ordered ordered = new Ordered();
                ordered.setId(cursor.getInt(0));
                ordered.setNumber(cursor.getString(1));
                ordered.setFoodname(cursor.getString(2));
                ordered.setAmount(cursor.getString(3));
                ordered.setStatus(cursor.getString(4));
                ordered.setDate(cursor.getString(5));
                ordered.setPrice(cursor.getString(6));
                ordered.setTotal(cursor.getString(7));
                ListOrdered.add(ordered);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ListOrdered;
    }

    //    Cập nhật dữ liệu
    public int updateOrdered (Ordered ordered, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", ordered.getNumber());
        contentValues.put("foodname", ordered.getFoodname());
        contentValues.put("amount", ordered.getAmount());
        contentValues.put("status", ordered.getStatus());
        contentValues.put("date", ordered.getDate());
        contentValues.put("price", ordered.getPrice());
        contentValues.put("total", ordered.getTotal());
        int number = db.update("ordered",contentValues,"id = ?",new String[]{id});
        return number;
    }

    //    Xóa dữ liệu
    public int deleteOrdered (int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("ordered","id = ?",new String[]{String.valueOf(id)});
    }
}
