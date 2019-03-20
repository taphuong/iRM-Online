package org.irestaurant.irm.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRevenue extends SQLiteOpenHelper {
    public DatabaseRevenue(Context context) {
        super(context, "Revenue.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table revenue(id integer primary key autoincrement, date text, rdate text, time text, number text, total text, discount text, totalat text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists revenue");
    }

    //    Thêm dữ liêu
    public boolean creat (Revenue revenue){
        boolean result =true;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("date",revenue.getDate());
            contentValues.put("rdate", revenue.getRdate());
            contentValues.put("time",revenue.getTime());
            contentValues.put("number",revenue.getNumber());
            contentValues.put("total",revenue.getTotal());
            contentValues.put("discount",revenue.getDiscount());
            contentValues.put("totalat",revenue.getTotalat());
            long ins = db.insert("revenue",null,contentValues);
            result = ins>0;
        }catch (Exception e){
            result = false;
        }
        return result;
    }

    //    Lấy dữ liêu
    public List<Revenue> getallRevenue(String sdate, String edate){
        List<Revenue> ListRevenue = new ArrayList<>();
        String selectQuery = "SELECT * FROM revenue WHERE rdate BETWEEN '"+sdate+"' AND '"+edate+"'" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                Revenue revenue = new Revenue();
                revenue.setId(cursor.getInt(0));
                revenue.setDate(cursor.getString(1));
                revenue.setRdate(cursor.getString(2));
                revenue.setTime(cursor.getString(3));
                revenue.setNumber(cursor.getString(4));
                revenue.setTotal(cursor.getString(5));
                revenue.setDiscount(cursor.getString(6));
                revenue.setTotalat(cursor.getString(7));
                ListRevenue.add(revenue);            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ListRevenue;
    }
}
