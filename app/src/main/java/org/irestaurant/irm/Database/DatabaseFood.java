package org.irestaurant.irm.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseFood extends SQLiteOpenHelper {
    public DatabaseFood(Context context) {
        super(context, "Food.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table food(id integer primary key autoincrement, foodname text, foodprice text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists food");
    }

//    Thêm dữ liêu
    public boolean creat (Food food){
        boolean result =true;
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("foodname",food.getFoondname());
            contentValues.put("foodprice",food.getFoodprice());
            long ins = db.insert("food",null,contentValues);
            result = ins>0;
        }catch (Exception e){
            result = false;
        }
        return result;
    }

//    Lấy dữ liêu
    public List<Food> getallFood(){
        List<Food> ListFood = new ArrayList<>();
        String selectQuery = "SELECT * FROM food ORDER BY foodname" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do {
                Food food = new Food();
                food.setId(cursor.getInt(0));
                food.setFoondname(cursor.getString(1));
                food.setFoodprice(cursor.getString(2));
                ListFood.add(food);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ListFood;
    }

//    Cập nhật dữ liệu
    public int updateFood (Food food){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("foodname", food.getFoondname());
        contentValues.put("foodprice", food.getFoodprice());
        int number = db.update("food",contentValues,"id = ?",new String[]{String.valueOf(food.getId())});
        return number;
    }

//    Xóa dữ liệu
    public int deleteFood (int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("food","id = ?",new String[]{String.valueOf(id)});
    }
}
