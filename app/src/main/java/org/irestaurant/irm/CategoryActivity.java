package org.irestaurant.irm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.irestaurant.irm.Database.Food;
import org.irestaurant.irm.Database.FoodGroupAdapter;
import org.irestaurant.irm.Interface.IOnFoodGroupClickListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView lvGroup;
    List<Food> foodGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        lvGroup = findViewById(R.id.lv_group);
        foodGroupList = new ArrayList<>();
        FoodGroupAdapter foodGroupAdapter = new FoodGroupAdapter(foodGroupList);
        foodGroupAdapter.setIOnFoodGroupClickListener(new IOnFoodGroupClickListener() {
            @Override
            public void onFoodGroupClickListener(String foodGroup, int postion) {
                if (postion != -1){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", foodGroup);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
        lvGroup.setHasFixedSize(true);
        lvGroup.setLayoutManager(new LinearLayoutManager(this));
        lvGroup.setAdapter(foodGroupAdapter);
    }
}
